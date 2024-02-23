package org.tkit.onecx.product.store.rs.internal.controllers;

import java.util.List;

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
import org.tkit.onecx.product.store.domain.models.Microfrontend;
import org.tkit.onecx.product.store.domain.models.Microservice;
import org.tkit.onecx.product.store.domain.models.Product;
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
    @Transactional
    public Response deleteProduct(String id) {
        var product = dao.findById(id);
        if (product != null) {
            List<Microfrontend> productRelatedMfes = microfrontendDAO.loadByProductName(product.getName()).toList();
            List<Microservice> productRelatedMs = microserviceDAO.loadByProductName(product.getName()).toList();

            microfrontendDAO.delete(productRelatedMfes);
            microserviceDAO.delete(productRelatedMs);
            dao.deleteQueryById(id);
        }
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
    public Response searchProducts(ProductSearchCriteriaDTO productSearchCriteriaDTO) {
        var criteria = mapper.map(productSearchCriteriaDTO);
        var result = dao.findProductsByCriteria(criteria);
        return Response.ok(mapper.mapPageResult(result)).build();
    }

    @Override
    @Transactional
    public Response updateProduct(String id, UpdateProductRequestDTO updateProductDTO) {
        Product item = dao.findById(id);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        //mfe update
        List<Microfrontend> mfes = microfrontendDAO.loadByProductName(item.getName()).toList();
        mfes.forEach(microfrontend -> microfrontend.setProductName(updateProductDTO.getName()));
        microfrontendDAO.update(mfes);

        //ms update
        List<Microservice> ms = microserviceDAO.loadByProductName(item.getName()).toList();
        ms.forEach(microservice -> microservice.setProductName(updateProductDTO.getName()));
        microserviceDAO.update(ms);

        mapper.update(updateProductDTO, item);
        dao.update(item);

        return Response.noContent().build();
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
