package io.github.onecx.product.store.rs.external.v1.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.log.cdi.LogService;

import gen.io.github.onecx.product.store.rs.external.v1.ProductsApi;
import gen.io.github.onecx.product.store.rs.external.v1.model.ProductSearchCriteriaDTOV1;
import gen.io.github.onecx.product.store.rs.external.v1.model.RestExceptionDTOV1;
import io.github.onecx.product.store.domain.daos.ProductDAO;
import io.github.onecx.product.store.rs.external.v1.mappers.ExceptionMapperV1;
import io.github.onecx.product.store.rs.external.v1.mappers.ProductMapperV1;

@Path("/v1/products/search")
@LogService
@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class ProductsRestControllerV1 implements ProductsApi {

    @Inject
    ExceptionMapperV1 exceptionMapper;

    @Inject
    ProductMapperV1 mapper;

    @Inject
    ProductDAO dao;

    @Override
    public Response searchProducts(ProductSearchCriteriaDTOV1 productSearchCriteriaDTOV1) {
        var criteria = mapper.map(productSearchCriteriaDTOV1);
        var result = dao.findProductsByCriteria(criteria);
        return Response.ok(mapper.mapPageResult(result)).build();
    }

    @ServerExceptionMapper
    public RestResponse<RestExceptionDTOV1> exception(ConstraintException ex) {
        return exceptionMapper.exception(ex);
    }

    @ServerExceptionMapper
    public RestResponse<RestExceptionDTOV1> exception(DAOException ex) {
        return exceptionMapper.exception(ex);
    }

    @ServerExceptionMapper
    public RestResponse<RestExceptionDTOV1> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }
}
