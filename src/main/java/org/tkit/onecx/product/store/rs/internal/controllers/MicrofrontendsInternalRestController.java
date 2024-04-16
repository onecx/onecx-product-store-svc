package org.tkit.onecx.product.store.rs.internal.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.product.store.domain.daos.MicrofrontendDAO;
import org.tkit.onecx.product.store.domain.models.Microfrontend;
import org.tkit.onecx.product.store.rs.internal.mappers.InternalExceptionMapper;
import org.tkit.onecx.product.store.rs.internal.mappers.MicrofrontendMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.product.store.rs.internal.MicrofrontendsInternalApi;
import gen.org.tkit.onecx.product.store.rs.internal.model.CreateMicrofrontendRequestDTO;
import gen.org.tkit.onecx.product.store.rs.internal.model.MicrofrontendSearchCriteriaDTO;
import gen.org.tkit.onecx.product.store.rs.internal.model.ProblemDetailResponseDTO;
import gen.org.tkit.onecx.product.store.rs.internal.model.UpdateMicrofrontendRequestDTO;

@LogService
@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class MicrofrontendsInternalRestController implements MicrofrontendsInternalApi {

    @Inject
    InternalExceptionMapper exceptionMapper;

    @Inject
    MicrofrontendMapper mapper;

    @Inject
    MicrofrontendDAO dao;

    @Context
    UriInfo uriInfo;

    @Override
    public Response createMicrofrontend(CreateMicrofrontendRequestDTO createMicrofrontendDTO) {
        var item = mapper.create(createMicrofrontendDTO);
        item = dao.create(item);
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(item.getId()).build())
                .entity(mapper.map(item))
                .build();
    }

    @Override
    @Transactional
    public Response deleteMicrofrontend(String id) {
        var mfe = dao.findById(id);
        if (mfe != null) {
            dao.delete(mfe);
        }
        return Response.noContent().build();
    }

    @Override
    public Response getMicrofrontend(String id) {
        var item = dao.findById(id);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(mapper.map(item)).build();
    }

    @Override
    @Transactional
    public Response searchMicrofrontends(MicrofrontendSearchCriteriaDTO microfrontendSearchCriteriaDTO) {
        var criteria = mapper.map(microfrontendSearchCriteriaDTO);
        var result = dao.findMicrofrontendsByCriteria(criteria);
        return Response.ok(mapper.mapPageResult(result)).build();
    }

    @Override
    public Response updateMicrofrontend(String id, UpdateMicrofrontendRequestDTO updateMicrofrontendDTO) {
        Microfrontend item = dao.findById(id);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        mapper.update(updateMicrofrontendDTO, item);
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
