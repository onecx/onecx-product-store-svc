package org.tkit.onecx.product.store.rs.internal.controllers;

import static jakarta.transaction.Transactional.TxType.NOT_SUPPORTED;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.*;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.product.store.domain.daos.ImageDAO;
import org.tkit.onecx.product.store.domain.models.Image;
import org.tkit.onecx.product.store.rs.internal.mappers.ExceptionMapper;
import org.tkit.onecx.product.store.rs.internal.mappers.ImageMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.image.rs.internal.ImagesInternalApi;
import gen.org.tkit.onecx.image.rs.internal.model.RefTypeDTO;
import gen.org.tkit.onecx.product.store.rs.internal.model.ProblemDetailResponseDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@LogService
@ApplicationScoped
@Transactional(value = NOT_SUPPORTED)
public class ImagesInternalRestController implements ImagesInternalApi {

    @Inject
    ExceptionMapper exceptionMapper;

    @Inject
    ImageDAO imageDAO;

    @Context
    UriInfo uriInfo;

    @Context
    HttpHeaders httpHeaders;

    @Inject
    ImageMapper imageMapper;

    @Override
    public Response deleteImage(String refId, RefTypeDTO refType) {
        imageDAO.deleteQueryByRefIdAndRefType(refId, refType);

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Override
    @Transactional
    public Response getImage(String refId, RefTypeDTO refType) {
        Image image = imageDAO.findByRefIdAndRefType(refId, refType.toString());
        if (image == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(image.getImageData(), image.getMimeType())
                .header(HttpHeaders.CONTENT_LENGTH, image.getLength()).build();
    }

    @Override
    public Response uploadImage(Integer contentLength, String refId, RefTypeDTO refType, byte[] body) {
        Image image = imageDAO.findByRefIdAndRefType(refId, refType.toString());

        var contentType = httpHeaders.getMediaType();
        contentType = new MediaType(contentType.getType(), contentType.getSubtype());

        if (image == null) {
            image = imageMapper.create(refId, refType.toString(), contentType.toString(), contentLength, body);
            image = imageDAO.create(image);
        } else {
            imageMapper.update(image, contentLength, contentType.toString(), body);
            image = imageDAO.update(image);
        }
        var imageInfoDTO = imageMapper.map(image);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(imageInfoDTO.getId()).build())
                .entity(imageInfoDTO)
                .build();
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }

}
