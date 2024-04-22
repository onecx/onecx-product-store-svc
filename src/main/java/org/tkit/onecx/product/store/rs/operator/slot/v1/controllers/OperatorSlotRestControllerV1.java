package org.tkit.onecx.product.store.rs.operator.slot.v1.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.product.store.domain.daos.SlotDAO;
import org.tkit.onecx.product.store.rs.operator.slot.v1.mappers.OperatorSlotExceptionMapperV1;
import org.tkit.onecx.product.store.rs.operator.slot.v1.mappers.OperatorSlotMapperV1;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.product.store.rs.operator.slot.v1.OperatorSlotApi;
import gen.org.tkit.onecx.product.store.rs.operator.slot.v1.model.ProblemDetailResponseSlotDTOv1;
import gen.org.tkit.onecx.product.store.rs.operator.slot.v1.model.UpdateSlotRequestSlotDTOv1;

@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
@LogService
public class OperatorSlotRestControllerV1 implements OperatorSlotApi {

    @Inject
    SlotDAO dao;

    @Inject
    OperatorSlotMapperV1 mapper;

    @Inject
    OperatorSlotExceptionMapperV1 exceptionMapper;

    @Override
    public Response createOrUpdateSlot(String productName, String appId,
            UpdateSlotRequestSlotDTOv1 updateSlotRequestSlotDTOv1) {
        var ms = dao.findByProductNameAppId(productName, appId, updateSlotRequestSlotDTOv1.getName());
        if (ms == null) {
            ms = mapper.create(updateSlotRequestSlotDTOv1);
            ms.setAppId(appId);
            ms.setProductName(productName);
            dao.create(ms);
            return Response.status(Response.Status.CREATED).build();
        }
        mapper.update(ms, updateSlotRequestSlotDTOv1);
        dao.update(ms);
        return Response.status(Response.Status.OK).build();
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseSlotDTOv1> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }
}
