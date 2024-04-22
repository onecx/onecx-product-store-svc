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
import org.tkit.onecx.product.store.domain.daos.SlotDAO;
import org.tkit.onecx.product.store.rs.internal.mappers.InternalExceptionMapper;
import org.tkit.onecx.product.store.rs.internal.mappers.SlotMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.product.store.rs.internal.SlotsInternalApi;
import gen.org.tkit.onecx.product.store.rs.internal.model.CreateSlotRequestDTO;
import gen.org.tkit.onecx.product.store.rs.internal.model.ProblemDetailResponseDTO;
import gen.org.tkit.onecx.product.store.rs.internal.model.SlotSearchCriteriaDTO;
import gen.org.tkit.onecx.product.store.rs.internal.model.UpdateSlotRequestDTO;

@LogService
@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class SlotInternalRestController implements SlotsInternalApi {

    @Inject
    InternalExceptionMapper exceptionMapper;

    @Inject
    SlotMapper mapper;

    @Inject
    SlotDAO dao;

    @Context
    UriInfo uriInfo;

    @Override
    public Response createSlot(CreateSlotRequestDTO createSlotRequestDTO) {
        var item = mapper.create(createSlotRequestDTO);
        item = dao.create(item);
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(item.getId()).build())
                .entity(mapper.map(item))
                .build();
    }

    @Override
    public Response deleteSlot(String id) {
        dao.deleteQueryById(id);
        return Response.noContent().build();
    }

    @Override
    public Response getSlot(String id) {
        var item = dao.findById(id);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(mapper.map(item)).build();
    }

    @Override
    public Response searchSlots(SlotSearchCriteriaDTO slotSearchCriteriaDTO) {
        var criteria = mapper.map(slotSearchCriteriaDTO);
        var result = dao.findByCriteria(criteria);
        return Response.ok(mapper.mapPageResult(result)).build();
    }

    @Override
    public Response updateSlot(String id, UpdateSlotRequestDTO updateSlotRequestDTO) {
        var item = dao.findById(id);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        mapper.update(updateSlotRequestDTO, item);
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
