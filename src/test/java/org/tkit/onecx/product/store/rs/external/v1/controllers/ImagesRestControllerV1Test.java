package org.tkit.onecx.product.store.rs.external.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;

import jakarta.ws.rs.core.HttpHeaders;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.product.store.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.product.store.rs.external.v1.model.RefTypeDTOv1;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(ImagesRestControllerV1.class)
@WithDBData(value = "data/test-v1.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class ImagesRestControllerV1Test extends AbstractTest {
    private static final String MEDIA_TYPE_IMAGE_PNG = "image/png";

    @Test
    void getImageTest() {

        var refId = "product1";
        var refType = RefTypeDTOv1.LOGO;

        given()
                .contentType(APPLICATION_JSON)
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .get()
                .then()
                .statusCode(OK.getStatusCode())
                .header(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE_IMAGE_PNG);

    }

    @Test
    void getImageTest_shouldReturnNotFound_whenImagesDoesNotExist() {

        var refId = "test-image";
        var refType = RefTypeDTOv1.LOGO;

        given()
                .contentType(APPLICATION_JSON)
                .pathParam("refId", refId + "_not_exists")
                .pathParam("refType", refType)
                .get()
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }
}
