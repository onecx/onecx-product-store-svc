package org.tkit.onecx.product.store.rs.operator.slot.v1.log;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.log.cdi.LogParam;

import gen.org.tkit.onecx.product.store.rs.operator.slot.v1.model.UpdateSlotRequestSlotDTOv1;

@ApplicationScoped
public class OperatorSlotLogParam implements LogParam {

    @Override
    public List<LogParam.Item> getClasses() {
        return List.of(
                item(10, UpdateSlotRequestSlotDTOv1.class,
                        x -> x.getClass().getSimpleName() + ":" + ((UpdateSlotRequestSlotDTOv1) x).getName()));
    }
}
