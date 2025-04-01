package org.tkit.onecx.product.store.rs.internal.controllers;

import java.util.HashMap;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.product.store.domain.daos.MicrofrontendDAO;
import org.tkit.onecx.product.store.domain.daos.MicroserviceDAO;
import org.tkit.onecx.product.store.domain.daos.ProductDAO;
import org.tkit.onecx.product.store.domain.models.Microservice;
import org.tkit.onecx.product.store.domain.models.Product;
import org.tkit.onecx.product.store.domain.services.ProductService;
import org.tkit.onecx.product.store.rs.internal.mappers.InternalExceptionMapper;
import org.tkit.onecx.product.store.rs.internal.mappers.ProductMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.product.store.rs.internal.ProductsInternalApi;
import gen.org.tkit.onecx.product.store.rs.internal.model.*;

@LogService
@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class ProductsInternalRestController implements ProductsInternalApi {

    @Inject
    InternalExceptionMapper exceptionMapper;

    @Inject
    ProductMapper mapper;

    @Inject
    ProductDAO dao;

    @Context
    UriInfo uriInfo;

    @Inject
    MicrofrontendDAO microfrontendDAO;

    @Inject
    MicroserviceDAO microserviceDAO;

    @Inject
    ProductService productService;

    @Override
    public Response createProduct(CreateProductRequestDTO createProductDTO) {
        var item = mapper.create(createProductDTO);
        item = dao.create(item);
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(item.getId()).build())
                .entity(mapper.map(item))
                .build();
    }

    @Override
    public Response deleteProduct(String id) {
        productService.deleteProduct(id);

        return Response.noContent().build();
    }

    @Override
    public Response getProduct(String id) {
        var item = dao.findById(id);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(mapper.map(item)).build();
    }

    @Override
    public Response getProductByName(String name) {
        var item = dao.findProductByName(name);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(mapper.map(item)).build();
    }

    @Override
    public Response getProductSearchCriteria() {
        var providers = dao.findAllProviders();
        var classifications = dao.findAllClassifications();

        return Response.status(Response.Status.OK.getStatusCode()).entity(mapper.mapCriteriaLists(providers, classifications))
                .build();
    }

    @Override
    public Response searchProducts(ProductSearchCriteriaDTO productSearchCriteriaDTO) {
        var criteria = mapper.map(productSearchCriteriaDTO);

        var pageResult = mapper.mapPageResult(dao.findProductsByCriteria(criteria));

        var productNames = pageResult.getStream().stream().map(ProductAbstractDTO::getName).toList();

        var microservices = microserviceDAO.findByProductNames(productNames).stream().collect(
                Collectors.groupingBy(Microservice::getProductName, HashMap::new,
                        Collectors.mapping(x -> x, Collectors.toList())));

        pageResult.getStream().forEach(productAbstractDTO -> {
            if (microservices.get(productAbstractDTO.getName()) != null) {
                productAbstractDTO.getApplications()
                        .addAll(mapper.mapMsToAppAbstracts(microservices.get(productAbstractDTO.getName())));
            }
        });
        return Response.ok(pageResult).build();
    }

    @Override
    public Response updateProduct(String id, UpdateProductRequestDTO updateProductDTO) {
        Product item = dao.findById(id);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        String oldProductName = item.getName();

        mapper.update(updateProductDTO, item);
        item = productService.updateProductAndRelatedMfeAndMs(oldProductName, item);
        return Response.status(Response.Status.OK).entity(mapper.map(item)).build();
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> exception(ConstraintException ex) {
        return exceptionMapper.exception(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }
}
