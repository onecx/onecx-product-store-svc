package org.tkit.onecx.product.store.rs.external.v1.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import org.tkit.onecx.product.store.domain.daos.ImageDAO;
import org.tkit.onecx.product.store.domain.models.Image;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.image.rs.internal.model.RefTypeDTO;
import gen.org.tkit.onecx.product.store.rs.external.v1.ImagesApi;

@LogService
@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class ImagesRestControllerV1 implements ImagesApi {

    @Inject
    ImageDAO imageDAO;

    @Override
    public Response getProductLogo(String refId) {
        Image image = imageDAO.findByRefIdAndRefType(refId, RefTypeDTO.LOGO.toString());
        if (image == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(image.getImageData(), image.getMimeType())
                .header(HttpHeaders.CONTENT_LENGTH, image.getLength()).build();
    }

}
