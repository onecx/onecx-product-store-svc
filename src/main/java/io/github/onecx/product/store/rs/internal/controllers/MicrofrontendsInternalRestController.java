package io.github.onecx.product.store.rs.internal.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import gen.io.github.onecx.product.store.rs.internal.MicrofrontendsInternalApi;
import gen.io.github.onecx.product.store.rs.internal.model.CreateMicrofrontendDTO;
import gen.io.github.onecx.product.store.rs.internal.model.MicrofrontendSearchCriteriaDTO;
import gen.io.github.onecx.product.store.rs.internal.model.RestExceptionDTO;
import gen.io.github.onecx.product.store.rs.internal.model.UpdateMicrofrontendDTO;
import io.github.onecx.product.store.rs.internal.mappers.InternalExceptionMapper;

@Path("/internal/microfrontends")
@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class MicrofrontendsInternalRestController implements MicrofrontendsInternalApi {

    @Inject
    InternalExceptionMapper exceptionMapper;

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
        return null;
    }

    @Override
    public Response updateMicrofrontend(String id, UpdateMicrofrontendDTO updateMicrofrontendDTO) {
        return null;
    }

    @ServerExceptionMapper
    public RestResponse<RestExceptionDTO> exception(Exception ex) {
        return exceptionMapper.exception(ex);
    }

    @ServerExceptionMapper
    public RestResponse<RestExceptionDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }
}
