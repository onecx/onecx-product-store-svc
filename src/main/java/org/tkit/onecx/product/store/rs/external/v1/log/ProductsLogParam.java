package org.tkit.onecx.product.store.rs.external.v1.log;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogParam;

import gen.org.tkit.onecx.product.store.rs.external.v1.model.ProductItemSearchCriteriaDTOv1;

@ApplicationScoped
public class ProductsLogParam implements LogParam {

    @Override
    public List<Item> getClasses() {
        return List.of(
                item(10, ProductItemSearchCriteriaDTOv1.class, x -> {
                    ProductItemSearchCriteriaDTOv1 d = (ProductItemSearchCriteriaDTOv1) x;
                    return ProductItemSearchCriteriaDTOv1.class.getSimpleName() + "[" + d.getPageNumber() + ","
                            + d.getPageSize()
                            + "]";
                }));
    }
}
