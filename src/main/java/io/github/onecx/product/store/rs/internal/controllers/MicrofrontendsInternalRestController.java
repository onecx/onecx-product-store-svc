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

import gen.io.github.onecx.product.store.rs.internal.MicrofrontendsInternalApi;
import gen.io.github.onecx.product.store.rs.internal.model.CreateMicrofrontendDTO;
import gen.io.github.onecx.product.store.rs.internal.model.MicrofrontendSearchCriteriaDTO;
import gen.io.github.onecx.product.store.rs.internal.model.RestExceptionDTO;
import gen.io.github.onecx.product.store.rs.internal.model.UpdateMicrofrontendDTO;
import io.github.onecx.product.store.domain.daos.MicrofrontendDAO;
import io.github.onecx.product.store.rs.internal.mappers.InternalExceptionMapper;
import io.github.onecx.product.store.rs.internal.mappers.MicrofrontendMapper;

@Path("/internal/microfrontends")
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

    @Override
    public Response createMicrofrontend(CreateMicrofrontendDTO createMicrofrontendDTO) {
        return null;
    }

    @Override
    public Response deleteMicrofrontend(String id) {
        return null;
    }

    @Override
    public Response getMicrofrontend(String id) {
        return null;
    }

    @Override
    public Response searchMicrofrontends(MicrofrontendSearchCriteriaDTO microfrontendSearchCriteriaDTO) {
        var criteria = mapper.map(microfrontendSearchCriteriaDTO);
        var result = dao.findMicrofrontendsByCriteria(criteria);
        return Response.ok(mapper.mapPageResult(result)).build();
    }

    @Override
    public Response updateMicrofrontend(String id, UpdateMicrofrontendDTO updateMicrofrontendDTO) {
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
