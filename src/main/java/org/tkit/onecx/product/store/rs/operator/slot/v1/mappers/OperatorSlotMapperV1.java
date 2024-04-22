package org.tkit.onecx.product.store.rs.operator.slot.v1.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.tkit.onecx.product.store.domain.models.Slot;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.product.store.rs.operator.slot.v1.model.UpdateSlotRequestSlotDTOv1;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface OperatorSlotMapperV1 {

    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "operator", constant = "true")
    @Mapping(target = "appId", ignore = true)
    @Mapping(target = "undeployed", qualifiedByName = "undeployed")
    @Mapping(target = "deprecated", qualifiedByName = "deprecated")
    Slot create(UpdateSlotRequestSlotDTOv1 dto);

    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "operator", constant = "true")
    @Mapping(target = "appId", ignore = true)
    @Mapping(target = "undeployed", qualifiedByName = "undeployed")
    @Mapping(target = "deprecated", qualifiedByName = "deprecated")
    void update(@MappingTarget Slot slot, UpdateSlotRequestSlotDTOv1 dto);

    @Named("deprecated")
    @SuppressWarnings("java:S2447")
    default Boolean setDeprecated(Boolean value) {
        if (value == null || !value) {
            return null;
        }
        return true;
    }

    @Named("undeployed")
    @SuppressWarnings("java:S2447")
    default Boolean setUndeployed(Boolean value) {
        if (value == null || !value) {
            return null;
        }
        return true;
    }
}
