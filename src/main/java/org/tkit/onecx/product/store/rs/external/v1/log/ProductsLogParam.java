package org.tkit.onecx.product.store.rs.external.v1.log;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogParam;

import gen.org.tkit.onecx.product.store.rs.external.v1.model.LoadProductRequestDTOv1;
import gen.org.tkit.onecx.product.store.rs.external.v1.model.ProductItemLoadSearchCriteriaDTOv1;
import gen.org.tkit.onecx.product.store.rs.external.v1.model.ProductItemSearchCriteriaDTOv1;

@ApplicationScoped
public class ProductsLogParam implements LogParam {

    @Override
    public List<Item> getClasses() {
        return List.of(
                item(10, ProductItemLoadSearchCriteriaDTOv1.class, x -> {
                    ProductItemLoadSearchCriteriaDTOv1 d = (ProductItemLoadSearchCriteriaDTOv1) x;
                    return ProductItemLoadSearchCriteriaDTOv1.class.getSimpleName() + "[" + d.getPageNumber() + ","
                            + d.getPageSize()
                            + "]";
                }),
                item(10, LoadProductRequestDTOv1.class, x -> LoadProductRequestDTOv1.class.getSimpleName()),
                item(10, ProductItemSearchCriteriaDTOv1.class, x -> {
                    ProductItemSearchCriteriaDTOv1 d = (ProductItemSearchCriteriaDTOv1) x;
                    return ProductItemSearchCriteriaDTOv1.class.getSimpleName() + "[" + d.getPageNumber() + ","
                            + d.getPageSize()
                            + "]";
                }));
    }
}
