package io.github.onecx.product.store.rs.internal.controllers;

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

import gen.io.github.onecx.product.store.rs.internal.ProductsInternalApi;
import gen.io.github.onecx.product.store.rs.internal.model.CreateProductDTO;
import gen.io.github.onecx.product.store.rs.internal.model.ProductSearchCriteriaDTO;
import gen.io.github.onecx.product.store.rs.internal.model.RestExceptionDTO;
import gen.io.github.onecx.product.store.rs.internal.model.UpdateProductDTO;
import io.github.onecx.product.store.domain.daos.ProductDAO;
import io.github.onecx.product.store.rs.internal.mappers.InternalExceptionMapper;
import io.github.onecx.product.store.rs.internal.mappers.ProductMapper;

@Path("/internal/products")
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

    @Override
    public Response createProduct(CreateProductDTO createProductDTO) {
        return null;
    }

    @Override
    public Response deleteProduct(String id) {
        return null;
    }

    @Override
    public Response getProduct(String id) {
        return null;
    }

    @Override
    public Response searchProducts(ProductSearchCriteriaDTO productSearchCriteriaDTO) {
        var criteria = mapper.map(productSearchCriteriaDTO);
        var result = dao.findProductsByCriteria(criteria);
        return Response.ok(mapper.mapPageResult(result)).build();
    }

    @Override
    public Response updateProduct(String id, UpdateProductDTO updateProductDTO) {
        return null;
    }

    @ServerExceptionMapper
    public RestResponse<RestExceptionDTO> exception(ConstraintException ex) {
        return exceptionMapper.exception(ex);
    }

    @ServerExceptionMapper
    public RestResponse<RestExceptionDTO> exception(DAOException ex) {
        return exceptionMapper.exception(ex);
    }

    @ServerExceptionMapper
    public RestResponse<RestExceptionDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }
}
