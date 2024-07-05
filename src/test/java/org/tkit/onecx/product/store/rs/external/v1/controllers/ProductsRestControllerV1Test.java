package org.tkit.onecx.product.store.rs.external.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.tkit.quarkus.security.test.SecurityTestUtils.getKeycloakClientToken;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.product.store.AbstractTest;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.product.store.rs.external.v1.model.*;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(ProductsRestControllerV1.class)
@WithDBData(value = "data/test-v1.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
@GenerateKeycloakClient(clientName = "testClient", scopes = { "ocx-ps:read" })
class ProductsRestControllerV1Test extends AbstractTest {

    @Test
    void getProductByNameTest() {
        var dto = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParams("name", "product1")
                .get("{name}")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProductDTOv1.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("product1");
        assertThat(dto.getMicrofrontends()).isNotNull().hasSize(2);

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParams("name", "does-not-exists")
                .get("{name}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

    }

    @Test
    void searchProductsTest() {

        var criteria = new ProductItemSearchCriteriaDTOv1();
        var data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProductItemPageResultDTOv1.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(2);
        assertThat(data.getStream()).isNotNull().hasSize(2);
        assertThat(data.getStream().get(0).getClassifications()).isEqualTo("search");
    }

    @Test
    void searchProductsByCriteriaTest() {

        var criteria = new ProductItemSearchCriteriaDTOv1();
        criteria.productNames(List.of("product1", "product2"));
        var data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProductItemPageResultDTOv1.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(2);
        assertThat(data.getStream()).isNotNull().hasSize(2);
    }

    @Test
    void searchProductsNoBodyTest() {
        var data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .post("/search")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProblemDetailResponseDTOv1.class);

        assertThat(data).isNotNull();
        assertThat(data.getDetail()).isEqualTo("searchProductsByCriteria.productItemSearchCriteriaDTOv1: must not be null");
    }

    @Test
    void loadProductsByCriteriaTest() {
        ProductItemLoadSearchCriteriaDTOv1 criteriaDTOv1 = new ProductItemLoadSearchCriteriaDTOv1();
        criteriaDTOv1.setProductNames(List.of("product1"));

        var data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(criteriaDTOv1)
                .post("/load")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProductsLoadResultDTOv1.class);

        assertThat(data).isNotNull();
        assertThat(data.getStream()).hasSize(1);
        assertThat(data.getStream().get(0).getMicrofrontends()).hasSize(2);
        assertThat(data.getStream().get(0).getMicroservices()).hasSize(2);
        assertThat(data.getStream().get(0).getMicrofrontends()).hasSize(2);
        assertThat(data.getStream().get(0).getDescription()).isEqualTo("description");
        assertThat(data.getStream().get(0).getImageUrl()).isEqualTo("/image");
        assertThat(data.getStream().get(0).getBasePath()).isEqualTo("/product1");
        assertThat(data.getStream().get(0).getClassifications()).isEqualTo("search");
    }

    @Test
    void loadProductsByCriteriaEmptyProductNameTest() {
        ProductItemLoadSearchCriteriaDTOv1 criteriaDTOv1 = new ProductItemLoadSearchCriteriaDTOv1();
        criteriaDTOv1.setProductNames(List.of(""));

        var data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(criteriaDTOv1)
                .post("/load")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProductsLoadResultDTOv1.class);

        assertThat(data).isNotNull();
        assertThat(data.getStream()).isEmpty();
    }

    @Test
    void loadProductsByEmptyCriteriaTest() {
        ProductItemLoadSearchCriteriaDTOv1 criteriaDTOv1 = new ProductItemLoadSearchCriteriaDTOv1();

        var data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(criteriaDTOv1)
                .post("/load")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProductsLoadResultDTOv1.class);

        assertThat(data).isNotNull();
        assertThat(data.getStream()).hasSize(2);
    }

    @Test
    void loadProductsByCriteriaEmptyListTest() {
        ProductItemLoadSearchCriteriaDTOv1 criteriaDTOv1 = new ProductItemLoadSearchCriteriaDTOv1();
        List<String> emptyList = new ArrayList<>();
        criteriaDTOv1.setProductNames(emptyList);
        var data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(criteriaDTOv1)
                .post("/load")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProductsLoadResultDTOv1.class);

        assertThat(data).isNotNull();
        assertThat(data.getStream()).hasSize(2);
    }

    @Test
    void loadProductsByNamesTest() {
        var data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(new LoadProductRequestDTOv1().productNames(List.of("product1", "product2")))
                .post("/load/shell")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(LoadProductResponseDTOv1.class);

        assertThat(data).isNotNull();
        assertThat(data.getProducts()).isNotNull().hasSize(2);
        var p1 = data.getProducts().stream().filter(x -> "product1".equals(x.getName())).findFirst().orElse(null);
        assertThat(p1).isNotNull();
        assertThat(p1.getMicrofrontends()).isNotNull().hasSize(2);
        assertThat(p1.getMicrofrontends().get(0).getEndpoints()).hasSize(2);
        var p2 = data.getProducts().stream().filter(x -> "product2".equals(x.getName())).findFirst().orElse(null);
        assertThat(p2).isNotNull();
        assertThat(p2.getMicrofrontends()).isNotNull().isEmpty();
    }

}
