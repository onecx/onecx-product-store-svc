package org.tkit.onecx.product.store.rs.operator.product.v1.mappers;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.tkit.onecx.product.store.domain.models.Product;
import org.tkit.onecx.product.store.domain.models.ProductClassification;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.product.store.rs.operator.product.v1.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface OperatorProductMapperV1 {

    @Mapping(target = "productClassifications", ignore = true)
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
    @Mapping(target = "classifications", qualifiedByName = "updateList")
    @Mapping(target = "undeployed", qualifiedByName = "undeployed")
    Product create(UpdateProductRequestPDTOv1 dto);

    @Mapping(target = "productClassifications", ignore = true)
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
    @Mapping(target = "classifications", qualifiedByName = "updateList")
    @Mapping(target = "undeployed", qualifiedByName = "undeployed")
    void update(@MappingTarget Product product, UpdateProductRequestPDTOv1 dto);

    @Named("undeployed")
    @SuppressWarnings("java:S2447")
    default Boolean setUndeployed(Boolean value) {
        if (value == null || !value) {
            return null;
        }
        return true;
    }

    @Mapping(target = "value", source = "classification")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productId", ignore = true)
    ProductClassification mapClassificationString(String classification);

    @Named("updateList")
    default Set<ProductClassification> updateList(Set<String> listToUpdate) {
        var list = new HashSet<ProductClassification>();

        if (listToUpdate != null) {
            for (var cf : listToUpdate) {
                list.add(mapClassificationString(cf));
            }
        }
        return list;
    }
}
