package io.github.onecx.product.store.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.product.store.rs.internal.model.*;
import io.github.onecx.product.store.AbstractTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(ProductsInternalRestController.class)
@WithDBData(value = "data/test-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class ProductsInternalRestControllerTest extends AbstractTest {

    @Test
    void createProductTest() {

        // create product
        var createProductDTO = new CreateProductRequestDTO();
        createProductDTO.setName("test01");
        createProductDTO.setVersion("test01");
        createProductDTO.setBasePath("basePath");

        var dto = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(createProductDTO)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(ProductDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isNotNull().isEqualTo(createProductDTO.getName());
        assertThat(dto.getBasePath()).isNotNull().isEqualTo(createProductDTO.getBasePath());

        // create theme without body
        var exception = given()
                .when()
                .contentType(APPLICATION_JSON)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("CONSTRAINT_VIOLATIONS");
        assertThat(exception.getDetail()).isEqualTo("createProduct.createProductRequestDTO: must not be null");

        // create theme with existing name
        exception = given().when()
                .contentType(APPLICATION_JSON)
                .body(createProductDTO)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("PERSIST_ENTITY_FAILED");
        assertThat(exception.getDetail()).isEqualTo(
                "could not execute statement [ERROR: duplicate key value violates unique constraint 'ui_product_base_path'  Detail: Key (base_path)=(basePath) already exists.]");
    }

    @Test
    void deleteProductTest() {
        // delete product
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "p1")
                .delete("{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        // check if product exists
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "p1")
                .get("{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

        // delete product
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "p1")
                .delete("{id}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void getProductTest() {
        var dto = given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "p1")
                .get("{id}")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(ProductDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("product1");
        assertThat(dto.getDescription()).isEqualTo("description");

        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "___")
                .get("{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void searchProducts_shouldReturnProductListFullAttributeCheck_whenSearchCriteriaMatches() {

        /*
         * var createProductDTO = new CreateProductRequestDTO();
         * createProductDTO.setName("testFullAttributes01");
         * createProductDTO.setVersion("1.0.0");
         * createProductDTO.setBasePath("/app3");
         * createProductDTO
         * .setImageUrl("https://prod.ucwe.capgemini.com/wp-content/uploads/2023/11/world-cloud-report-banner1_2023.jpg");
         * createProductDTO.setDescription("some text");
         * createProductDTO.setDisplayName("display me");
         * createProductDTO.setIconName("sun");
         * Set<String> classificationSet = new HashSet<>();
         * classificationSet.add("Themes");
         * classificationSet.add("Menu");
         * createProductDTO.setClassifications(classificationSet);
         *
         * var dto = given()
         * .when()
         * .contentType(APPLICATION_JSON)
         * .body(createProductDTO)
         * .post()
         * .then()
         * .statusCode(CREATED.getStatusCode())
         * .extract()
         * .body().as(ProductDTO.class);
         *
         * assertThat(dto).isNotNull();
         * assertThat(dto.getName()).isNotNull().isEqualTo(createProductDTO.getName());
         * assertThat(dto.getBasePath()).isNotNull().isEqualTo(createProductDTO.getBasePath());
         */

        var criteria = new ProductSearchCriteriaDTO();
        criteria.setName("product1");
        criteria.setPageNumber(0);
        criteria.setPageSize(100);

        var data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProductPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(1);
        assertThat(data.getStream()).isNotNull().hasSize(1);

        var criteria2 = new ProductSearchCriteriaDTO();
        criteria2.setName("product1");
        criteria2.setPageNumber(0);
        criteria2.setPageSize(1);

        var data2 = given()
                .contentType(APPLICATION_JSON)
                .body(criteria2)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProductPageResultDTO.class);

        assertThat(data2).isNotNull();
        assertThat(data2.getTotalElements()).isEqualTo(1);
        assertThat(data2.getStream()).isNotNull().hasSize(1);
        assertThat(data2.getStream().get(0).getClassifications()).isNotNull().hasSize(2);

    }

    @Test
    void searchProductsTest() {
        var criteria = new ProductSearchCriteriaDTO();

        var data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(ProductPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(2);
        assertThat(data.getStream()).isNotNull().hasSize(2);

        /*
         * criteria.setName("product1");
         *
         * data = given()
         * .contentType(APPLICATION_JSON)
         * .body(criteria)
         * .post("/search")
         * .then()
         * .statusCode(OK.getStatusCode())
         * .contentType(APPLICATION_JSON)
         * .extract()
         * .as(ProductPageResultDTO.class);
         *
         * assertThat(data).isNotNull();
         * assertThat(data.getTotalElements()).isEqualTo(1);
         * assertThat(data.getStream()).isNotNull().hasSize(1);
         *
         * criteria.setName(" ");
         *
         * data = given()
         * .contentType(APPLICATION_JSON)
         * .body(criteria)
         * .post("/search")
         * .then()
         * .statusCode(OK.getStatusCode())
         * .contentType(APPLICATION_JSON)
         * .extract()
         * .as(ProductPageResultDTO.class);
         *
         * assertThat(data).isNotNull();
         * assertThat(data.getTotalElements()).isEqualTo(2);
         * assertThat(data.getStream()).isNotNull().hasSize(2);
         */
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
                .as(ProblemDetailResponseDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getDetail()).isEqualTo("searchProducts.productSearchCriteriaDTO: must not be null");
    }

    @Test
    void updateProductTest() {
        var updateDto = new UpdateProductRequestDTO();
        updateDto.setName("test01");
        updateDto.setVersion("0.0.0");
        updateDto.setDescription("description-update");
        updateDto.setBasePath("basePath");

        given()
                .contentType(APPLICATION_JSON)
                .body(updateDto)
                .when()
                .pathParam("id", "does-not-exists")
                .put("{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        given()
                .contentType(APPLICATION_JSON)
                .body(updateDto)
                .when()
                .pathParam("id", "p1")
                .put("{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        var dto = given().contentType(APPLICATION_JSON)
                .body(updateDto)
                .when()
                .pathParam("id", "p1")
                .get("{id}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(ProductDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getDescription()).isEqualTo(updateDto.getDescription());
    }

    @Test
    void updateProductWithoutBodyTest() {

        var exception = given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", "update_create_new")
                .put("{id}")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        Assertions.assertNotNull(exception);
        Assertions.assertEquals("CONSTRAINT_VIOLATIONS", exception.getErrorCode());
        Assertions.assertEquals("updateProduct.updateProductRequestDTO: must not be null",
                exception.getDetail());
        Assertions.assertNotNull(exception.getInvalidParams());
        Assertions.assertEquals(1, exception.getInvalidParams().size());
    }
}
