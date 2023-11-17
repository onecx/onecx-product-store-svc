package io.github.onecx.product.store.rs.external.v1.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.io.github.onecx.product.store.rs.external.v1.ProductsApi;
import gen.io.github.onecx.product.store.rs.external.v1.model.ProblemDetailResponseDTOv1;
import gen.io.github.onecx.product.store.rs.external.v1.model.ProductSearchCriteriaDTOv1;
import io.github.onecx.product.store.domain.daos.MicrofrontendDAO;
import io.github.onecx.product.store.domain.daos.ProductDAO;
import io.github.onecx.product.store.rs.external.v1.mappers.ExceptionMapperV1;
import io.github.onecx.product.store.rs.external.v1.mappers.ProductMapperV1;

@Path("/v1/products")
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

    @Inject
    MicrofrontendDAO microfrontendDAO;

    @Override
    public Response getProductByName(String name) {
        var product = dao.findProductByName(name);
        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var microfrontends = microfrontendDAO.findByProductName(product.getName());

        var dto = mapper.map(product, microfrontends);

        return Response.ok(dto).build();
    }

    @Override
    public Response searchProducts(ProductSearchCriteriaDTOv1 productSearchCriteriaDTOV1) {
        var criteria = mapper.map(productSearchCriteriaDTOV1);
        var result = dao.findProductsByCriteria(criteria);
        return Response.ok(mapper.mapPageResult(result)).build();
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTOv1> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }
}
