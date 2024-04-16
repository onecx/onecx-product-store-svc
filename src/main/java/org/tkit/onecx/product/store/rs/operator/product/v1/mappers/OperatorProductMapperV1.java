package org.tkit.onecx.product.store.rs.operator.product.v1.mappers;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.tkit.onecx.product.store.domain.models.Product;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.product.store.rs.operator.product.v1.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface OperatorProductMapperV1 {

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "operator", constant = "true")
    @Mapping(target = "classifications", qualifiedByName = "toString")
    @Mapping(target = "undeployed", qualifiedByName = "undeployed")
    Product create(UpdateProductRequestPDTOv1 dto);

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "operator", constant = "true")
    @Mapping(target = "classifications", qualifiedByName = "toString")
    @Mapping(target = "undeployed", qualifiedByName = "undeployed")
    void update(@MappingTarget Product product, UpdateProductRequestPDTOv1 dto);

    @Named("toString")
    default String setToString(Set<String> classifications) {
        if (classifications != null && !classifications.isEmpty()) {
            return String.join(",", classifications);
        }
        return "";
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
