package io.github.onecx.product.store.rs.internal.controllers;

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
import org.tkit.quarkus.jpa.exceptions.ConstraintException;
import org.tkit.quarkus.log.cdi.LogService;

import gen.io.github.onecx.product.store.rs.internal.ProductsInternalApi;
import gen.io.github.onecx.product.store.rs.internal.model.*;
import io.github.onecx.product.store.domain.criteria.MicrofrontendSearchCriteria;
import io.github.onecx.product.store.domain.daos.MicrofrontendDAO;
import io.github.onecx.product.store.domain.daos.ProductDAO;
import io.github.onecx.product.store.domain.models.Microfrontend;
import io.github.onecx.product.store.domain.models.Product;
import io.github.onecx.product.store.rs.internal.mappers.InternalExceptionMapper;
import io.github.onecx.product.store.rs.internal.mappers.ProductMapper;

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
            MicrofrontendSearchCriteria criteria = new MicrofrontendSearchCriteria();
            criteria.setProductName(product.getName());
            criteria.setPageNumber(0);
            criteria.setPageSize(1);
            List<Microfrontend> productRelatedMfes = microfrontendDAO.findMicrofrontendsByCriteria(criteria).getStream()
                    .toList();

            microfrontendDAO.delete(productRelatedMfes);
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
    public Response updateProduct(String id, UpdateProductRequestDTO updateProductDTO) {
        Product item = dao.findById(id);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

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
