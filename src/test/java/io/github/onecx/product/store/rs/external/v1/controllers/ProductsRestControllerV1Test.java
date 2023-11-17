package io.github.onecx.product.store.rs.external.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.product.store.rs.external.v1.model.ProblemDetailResponseDTOv1;
import gen.io.github.onecx.product.store.rs.external.v1.model.ProductDTOv1;
import gen.io.github.onecx.product.store.rs.external.v1.model.ProductPageResultDTOv1;
import gen.io.github.onecx.product.store.rs.external.v1.model.ProductSearchCriteriaDTOv1;
import io.github.onecx.product.store.AbstractTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(ProductsRestControllerV1.class)
@WithDBData(value = "data/testdata-v1.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class ProductsRestControllerV1Test extends AbstractTest {

    @Test
    void getProductByNameTest() {
        var dto = given()
                .contentType(APPLICATION_JSON)
                .pathParams("name", "product1")
                .get("{name}")
                .then().log().all()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProductDTOv1.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("product1");
        assertThat(dto.getMicrofrontends()).isNotNull().hasSize(2);

        given()
                .contentType(APPLICATION_JSON)
                .pathParams("name", "does-not-exists")
                .get("{name}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

    }

    @Test
    void searchProductsTest() {

        var criteria = new ProductSearchCriteriaDTOv1();

        var data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProductPageResultDTOv1.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(2);
        assertThat(data.getStream()).isNotNull().hasSize(2);
    }

    @Test
    void searchProductsNoBodyTest() {
        var data = given()
                .contentType(APPLICATION_JSON)
                .post("/search")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProblemDetailResponseDTOv1.class);

        assertThat(data).isNotNull();
        assertThat(data.getDetail()).isEqualTo("searchProducts.productSearchCriteriaDTOv1: must not be null");
    }
}
