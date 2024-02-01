package org.tkit.onecx.product.store.rs.operator.product.v1.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.product.store.domain.daos.ProductDAO;
import org.tkit.onecx.product.store.rs.operator.product.v1.mappers.OperatorProductExceptionMapperV1;
import org.tkit.onecx.product.store.rs.operator.product.v1.mappers.OperatorProductMapperV1;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.product.store.rs.operator.product.v1.OperatorProductApi;
import gen.org.tkit.onecx.product.store.rs.operator.product.v1.model.ProblemDetailResponsePDTOv1;
import gen.org.tkit.onecx.product.store.rs.operator.product.v1.model.UpdateProductRequestPDTOv1;

@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
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
    public Response createOrUpdateProduct(String name, UpdateProductRequestPDTOv1 dto) {

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
    public RestResponse<ProblemDetailResponsePDTOv1> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }
}
