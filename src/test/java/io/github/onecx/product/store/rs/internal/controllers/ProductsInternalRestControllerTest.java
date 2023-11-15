package io.github.onecx.product.store.rs.internal.controllers;

import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import io.github.onecx.product.store.AbstractTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(ProductsInternalRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class ProductsInternalRestControllerTest extends AbstractTest {

    @Test
    void createProductTest() {

    }

    @Test
    void deleteProductTest() {

    }

    @Test
    void getProductTest() {

    }

    @Test
    void searchProductsTest() {

    }

    @Test
    void updateProductTest() {

    }
}
