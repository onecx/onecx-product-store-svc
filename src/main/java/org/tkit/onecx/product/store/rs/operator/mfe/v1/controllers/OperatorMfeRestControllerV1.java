package org.tkit.onecx.product.store.rs.operator.mfe.v1.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.product.store.domain.daos.MicrofrontendDAO;
import org.tkit.onecx.product.store.rs.operator.mfe.v1.mappers.OperatorMfeExceptionMapperV1;
import org.tkit.onecx.product.store.rs.operator.mfe.v1.mappers.OperatorMfeMapperV1;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.product.store.rs.operator.mfe.v1.OperatorMfeApi;
import gen.org.tkit.onecx.product.store.rs.operator.mfe.v1.model.ProblemDetailResponseMDTOv1;
import gen.org.tkit.onecx.product.store.rs.operator.mfe.v1.model.UpdateMfeRequestMDTOv1;

@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
@LogService
public class OperatorMfeRestControllerV1 implements OperatorMfeApi {

    @Inject
    MicrofrontendDAO dao;

    @Inject
    OperatorMfeMapperV1 mapper;

    @Inject
    OperatorMfeExceptionMapperV1 exceptionMapper;

    @Override
    public Response createOrUpdateMfe(String productName, String appId, UpdateMfeRequestMDTOv1 dto) {

        var mfe = dao.findByProductAppExposedModule(productName, appId, dto.getExposedModule());
        if (mfe == null) {
            mfe = mapper.create(dto);
            mfe.setAppId(appId);
            mfe.setProductName(productName);
            dao.create(mfe);
            return Response.status(Response.Status.CREATED).build();
        }
        mapper.update(mfe, dto);
        dao.update(mfe);
        return Response.status(Response.Status.OK).build();
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseMDTOv1> exception(ConstraintException ex) {
        return exceptionMapper.exception(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseMDTOv1> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }
}
