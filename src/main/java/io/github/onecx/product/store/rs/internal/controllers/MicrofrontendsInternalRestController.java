package io.github.onecx.product.store.rs.internal.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import gen.io.github.onecx.product.store.rs.internal.MicrofrontendsInternalApi;
import gen.io.github.onecx.product.store.rs.internal.model.CreateMicrofrontendDTO;
import gen.io.github.onecx.product.store.rs.internal.model.MicrofrontendSearchCriteriaDTO;
import gen.io.github.onecx.product.store.rs.internal.model.UpdateMicrofrontendDTO;

@Path("/internal/microfrontends")
@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class MicrofrontendsInternalRestController implements MicrofrontendsInternalApi {
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
}
