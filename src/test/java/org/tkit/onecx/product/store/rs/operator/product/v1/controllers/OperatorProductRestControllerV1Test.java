package org.tkit.onecx.product.store.rs.operator.product.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.product.store.AbstractTest;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.product.store.rs.operator.product.v1.model.UpdateProductRequestPDTOv1;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(OperatorProductRestControllerV1.class)
@WithDBData(value = "data/test-operator-product.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class OperatorProductRestControllerV1Test extends AbstractTest {

    @Test
    void createOrUpdateProductTest() {

        var dto = new UpdateProductRequestPDTOv1();
        dto.setVersion("0.0.0");
        dto.basePath("/new_product");

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("name", "new_product_name")
                .body(dto)
                .put()
                .then().statusCode(CREATED.getStatusCode());

    }

    @Test
    void createOrUpdateProductUpdateTest() {

        var dto = new UpdateProductRequestPDTOv1();
        dto.basePath("/new_product");
        dto.setVersion("0.0.0");

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("name", "product1")
                .body(dto)
                .put()
                .then().statusCode(OK.getStatusCode());

    }

    @Test
    void createOrUpdateProductExistingBaseUrlTest() {

        var dto = new UpdateProductRequestPDTOv1();
        dto.basePath("/product1");

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("name", "new_product_name")
                .body(dto)
                .put()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());

    }

    @Test
    void updateProductExistingBaseUrlTest() {

        var dto = new UpdateProductRequestPDTOv1();
        dto.basePath("/product1");

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("name", "product2")
                .body(dto)
                .put()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());

    }

    @Test
    void createOrUpdateProductEmptyBodyTest() {
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("name", "new_product_name")
                .put()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());

    }
}
