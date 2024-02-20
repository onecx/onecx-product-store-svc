package org.tkit.onecx.product.store.rs.operator.ms.v1.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.product.store.domain.daos.MicroserviceDAO;
import org.tkit.onecx.product.store.rs.operator.ms.v1.mappers.OperatorMsExceptionMapperV1;
import org.tkit.onecx.product.store.rs.operator.ms.v1.mappers.OperatorMsMapperV1;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.product.store.rs.operator.mfe.v1.model.ProblemDetailResponseMDTOv1;
import gen.org.tkit.onecx.product.store.rs.operator.ms.v1.OperatorMsApi;
import gen.org.tkit.onecx.product.store.rs.operator.ms.v1.model.UpdateMsRequestMsDTOv1;

@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
@LogService
public class OperatorMsRestControllerV1 implements OperatorMsApi {

    @Inject
    MicroserviceDAO dao;

    @Inject
    OperatorMsMapperV1 mapper;

    @Inject
    OperatorMsExceptionMapperV1 exceptionMapper;

    @Override
    public Response createOrUpdateMs(String productName, String appId, UpdateMsRequestMsDTOv1 dto) {

        var ms = dao.findByAppId(appId);
        if (ms == null) {
            ms = mapper.create(dto);
            ms.setAppId(appId);
            ms.setProductName(productName);
            dao.create(ms);
            return Response.status(Response.Status.CREATED).build();
        }
        mapper.update(ms, dto);
        dao.update(ms);
        return Response.status(Response.Status.OK).build();
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseMDTOv1> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }
}
