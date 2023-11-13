package io.github.onecx.product.store.rs.operator.mfe.v1.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import gen.io.github.onecx.product.store.rs.operator.mfe.v1.OperatorMfeApi;
import gen.io.github.onecx.product.store.rs.operator.mfe.v1.model.UpdateMfeRequestDTOV1;
import io.github.onecx.product.store.domain.daos.MicrofrontendDAO;
import io.github.onecx.product.store.rs.operator.mfe.v1.mappers.OperatorMfeMapperV1;

@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
@Path("/operator/mfe/v1/{mfeId}")
public class OperatorMfeRestControllerV1 implements OperatorMfeApi {

    @Inject
    MicrofrontendDAO dao;

    @Inject
    OperatorMfeMapperV1 mapper;

    @Override
    public Response createOrUpdateMfe(String mfeId, UpdateMfeRequestDTOV1 dto) {

        var mfe = dao.findByMfeId(mfeId);
        if (mfe == null) {
            mfe = mapper.create(dto);
            mfe.setMfeId(mfeId);
            dao.create(mfe);
            return Response.status(Response.Status.CREATED).build();
        }
        mapper.update(mfe, dto);
        dao.update(mfe);
        return Response.status(Response.Status.OK).build();
    }
}
