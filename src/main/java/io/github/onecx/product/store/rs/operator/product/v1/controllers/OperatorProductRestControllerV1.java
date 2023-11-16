package io.github.onecx.product.store.rs.operator.product.v1.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;
import org.tkit.quarkus.log.cdi.LogService;

import gen.io.github.onecx.product.store.rs.operator.product.v1.OperatorProductApi;
import gen.io.github.onecx.product.store.rs.operator.product.v1.model.RestExceptionDTOV1;
import gen.io.github.onecx.product.store.rs.operator.product.v1.model.UpdateProductRequestDTOV1;
import io.github.onecx.product.store.domain.daos.ProductDAO;
import io.github.onecx.product.store.rs.operator.product.v1.mappers.OperatorProductExceptionMapperV1;
import io.github.onecx.product.store.rs.operator.product.v1.mappers.OperatorProductMapperV1;

@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
@Path("/operator/product/v1/update/{name}")
@LogService
public class OperatorProductRestControllerV1 implements OperatorProductApi {

    @Inject
    ProductDAO dao;

    @Inject
    OperatorProductMapperV1 mapper;

    @Inject
    OperatorProductExceptionMapperV1 exceptionMapper;

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public Response createOrUpdateProduct(String name, UpdateProductRequestDTOV1 dto) {

        var product = dao.findProductByName(name);
        if (product == null) {

            product = mapper.create(dto);
            product.setName(name);
            dao.create(product);

            return Response.status(Response.Status.CREATED).build();
        }

        mapper.update(product, dto);
        dao.update(product);
        return Response.status(Response.Status.OK).build();
    }

    @ServerExceptionMapper
    public Response exception(ConstraintException ex) {
        return exceptionMapper.exception(ex);
    }

    @ServerExceptionMapper
    public RestResponse<RestExceptionDTOV1> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }
}
