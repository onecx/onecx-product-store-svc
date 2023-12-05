package io.github.onecx.product.store.rs.external.v1.log;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogParam;

import gen.io.github.onecx.product.store.rs.external.v1.model.ProductSearchCriteriaDTOv1;

@ApplicationScoped
public class ProductsLogParam implements LogParam {

    @Override
    public List<Item> getClasses() {
        return List.of(
                item(10, ProductSearchCriteriaDTOv1.class, x -> {
                    ProductSearchCriteriaDTOv1 d = (ProductSearchCriteriaDTOv1) x;
                    return ProductSearchCriteriaDTOv1.class.getSimpleName() + "[" + d.getPageNumber() + "," + d.getPageSize()
                            + "]";
                }));
    }
}
