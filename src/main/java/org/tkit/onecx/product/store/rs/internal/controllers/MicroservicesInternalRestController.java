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
import org.tkit.onecx.product.store.domain.daos.MicroserviceDAO;
import org.tkit.onecx.product.store.domain.models.Microservice;
import org.tkit.onecx.product.store.rs.internal.mappers.InternalExceptionMapper;
import org.tkit.onecx.product.store.rs.internal.mappers.MicroserviceMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.product.store.rs.internal.MicroservicesInternalApi;
import gen.org.tkit.onecx.product.store.rs.internal.model.CreateMicroserviceRequestDTO;
import gen.org.tkit.onecx.product.store.rs.internal.model.MicroserviceSearchCriteriaDTO;
import gen.org.tkit.onecx.product.store.rs.internal.model.ProblemDetailResponseDTO;
import gen.org.tkit.onecx.product.store.rs.internal.model.UpdateMicroserviceRequestDTO;

@LogService
@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class MicroservicesInternalRestController implements MicroservicesInternalApi {

    @Inject
    InternalExceptionMapper exceptionMapper;

    @Inject
    MicroserviceMapper mapper;

    @Inject
    MicroserviceDAO dao;

    @Context
    UriInfo uriInfo;

    @Override
    public Response createMicroservice(CreateMicroserviceRequestDTO createMicroserviceDTO) {
        var item = mapper.create(createMicroserviceDTO);
        item = dao.create(item);
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(item.getId()).build())
                .entity(mapper.map(item))
                .build();
    }

    @Override
    @Transactional
    public Response deleteMicroservice(String id) {
        var mfe = dao.findById(id);
        if (mfe != null) {
            dao.delete(mfe);
        }
        return Response.noContent().build();
    }

    @Override
    public Response getMicroservice(String id) {
        var item = dao.findById(id);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(mapper.map(item)).build();
    }

    @Override
    public Response getMicroserviceByAppId(String appId) {
        var item = dao.findByAppId(appId);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(mapper.map(item)).build();
    }

    @Override
    @Transactional
    public Response searchMicroservice(MicroserviceSearchCriteriaDTO microserviceSearchCriteriaDTO) {
        var criteria = mapper.map(microserviceSearchCriteriaDTO);
        var result = dao.findMicroservicesByCriteria(criteria);
        return Response.ok(mapper.mapPageResult(result)).build();
    }

    @Override
    public Response updateMicroservice(String id, UpdateMicroserviceRequestDTO updateMicroserviceDTO) {
        Microservice item = dao.findById(id);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        mapper.update(updateMicroserviceDTO, item);
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
