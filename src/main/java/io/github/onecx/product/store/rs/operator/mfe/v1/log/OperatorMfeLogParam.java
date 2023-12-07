package io.github.onecx.product.store.rs.operator.mfe.v1.log;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogParam;

import gen.io.github.onecx.product.store.rs.operator.mfe.v1.model.UpdateMfeRequestMDTOv1;

@ApplicationScoped
public class OperatorMfeLogParam implements LogParam {

    @Override
    public List<Item> getClasses() {
        return List.of(
                item(10, UpdateMfeRequestMDTOv1.class,
                        x -> x.getClass().getSimpleName() + ":" + ((UpdateMfeRequestMDTOv1) x).getAppName()));
    }
}
