package org.tkit.onecx.product.store.rs.internal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.tkit.onecx.product.store.domain.criteria.SlotSearchCriteria;
import org.tkit.onecx.product.store.domain.models.Slot;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.product.store.rs.internal.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface SlotMapper {

    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "operator", constant = "false")
    @Mapping(target = "undeployed", qualifiedByName = "undeployed")
    @Mapping(target = "deprecated", qualifiedByName = "deprecated")
    Slot create(CreateSlotRequestDTO dto);

    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "undeployed", qualifiedByName = "undeployed")
    @Mapping(target = "deprecated", ignore = true)
    @Mapping(target = "operator", ignore = true)
    void update(UpdateSlotRequestDTO dto, @MappingTarget Slot data);

    SlotSearchCriteria map(SlotSearchCriteriaDTO dto);

    @Mapping(target = "removeStreamItem", ignore = true)
    SlotPageResultDTO mapPageResult(PageResult<Slot> page);

    @Mapping(target = "undeployed", qualifiedByName = "get-undeployed")
    @Mapping(target = "deprecated", qualifiedByName = "get-deprecated")
    SlotPageItemDTO mapPageItem(Slot data);

    @Mapping(target = "undeployed", qualifiedByName = "get-undeployed")
    @Mapping(target = "deprecated", qualifiedByName = "get-deprecated")
    SlotDTO map(Slot data);

    @Named("get-undeployed")
    default Boolean getUndeployed(Boolean value) {
        if (value == null) {
            return false;
        }
        return value;
    }

    @Named("undeployed")
    @SuppressWarnings("java:S2447")
    default Boolean setUndeployed(Boolean value) {
        if (value == null || !value) {
            return null;
        }
        return true;
    }

    @Named("get-deprecated")
    default Boolean getDeprecated(Boolean value) {
        if (value == null) {
            return false;
        }
        return value;
    }

    @Named("deprecated")
    @SuppressWarnings("java:S2447")
    default Boolean setDeprecated(Boolean value) {
        if (value == null || !value) {
            return null;
        }
        return true;
    }
}
