package io.github.onecx.product.store.rs.external.v1.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import gen.io.github.onecx.product.store.rs.external.v1.ProductsApi;

@Path("/internal/products/search")
@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class ProductsRestControllerV1 implements ProductsApi {
    @Override
    public Response searchProducts(Object body) {
        return null;
    }
}
