package org.tkit.onecx.product.store.rs.operator.product.v1.log;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogParam;

import gen.org.tkit.onecx.product.store.rs.operator.product.v1.model.UpdateProductRequestPDTOv1;

@ApplicationScoped
public class OperatorProductLogParam implements LogParam {

    @Override
    public List<Item> getClasses() {
        return List.of(
                item(10, UpdateProductRequestPDTOv1.class, x -> x.getClass().getSimpleName()));
    }
}
