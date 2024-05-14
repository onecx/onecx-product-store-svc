package org.tkit.onecx.product.store.rs.external.v1.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import org.tkit.onecx.product.store.domain.daos.ImageDAO;
import org.tkit.onecx.product.store.domain.models.Image;

import gen.org.tkit.onecx.product.store.rs.external.v1.ImagesApi;
import gen.org.tkit.onecx.product.store.rs.external.v1.model.RefTypeDTOv1;
import org.tkit.quarkus.log.cdi.LogService;

@LogService
@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class ImagesRestControllerV1 implements ImagesApi {

    @Inject
    ImageDAO imageDAO;

    @Override
    public Response getImage(String refId, RefTypeDTOv1 refType) {
        Image image = imageDAO.findByRefIdAndRefType(refId, refType.toString());
        if (image == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(image.getImageData(), image.getMimeType())
                .header(HttpHeaders.CONTENT_LENGTH, image.getLength()).build();
    }

}