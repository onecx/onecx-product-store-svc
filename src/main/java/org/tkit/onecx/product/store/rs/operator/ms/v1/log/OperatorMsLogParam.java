package org.tkit.onecx.product.store.rs.operator.ms.v1.log;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogParam;

import gen.org.tkit.onecx.product.store.rs.operator.ms.v1.model.UpdateMsRequestMsDTOv1;

@ApplicationScoped
public class OperatorMsLogParam implements LogParam {

    @Override
    public List<Item> getClasses() {
        return List.of(
                item(10, UpdateMsRequestMsDTOv1.class,
                        x -> x.getClass().getSimpleName() + ":" + ((UpdateMsRequestMsDTOv1) x).getName()));
    }
}
