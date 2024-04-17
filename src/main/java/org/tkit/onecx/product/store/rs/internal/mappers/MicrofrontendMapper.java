package org.tkit.onecx.product.store.rs.internal.mappers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mapstruct.*;
import org.tkit.onecx.product.store.domain.criteria.MicrofrontendSearchCriteria;
import org.tkit.onecx.product.store.domain.models.Microfrontend;
import org.tkit.onecx.product.store.domain.models.UIEndpoint;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.product.store.rs.internal.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface MicrofrontendMapper {

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "operator", constant = "false")
    @Mapping(target = "deprecated", qualifiedByName = "deprecated")
    @Mapping(target = "undeployed", qualifiedByName = "undeployed")
    Microfrontend create(CreateMicrofrontendRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    UIEndpoint map(CreateUIEndpointDTO dto);

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "operator", constant = "false")
    @Mapping(target = "endpoints", qualifiedByName = "updateList")
    @Mapping(target = "deprecated", qualifiedByName = "deprecated")
    @Mapping(target = "undeployed", qualifiedByName = "undeployed")
    void update(UpdateMicrofrontendRequestDTO dto, @MappingTarget Microfrontend data);

    @Mapping(target = "id", ignore = true)
    UIEndpoint map(UpdateUIEndpointDTO dto);

    @Named("updateList")
    default Set<UIEndpoint> updateList(List<UpdateUIEndpointDTO> listToUpdate) {
        var list = new HashSet<UIEndpoint>();

        if (listToUpdate != null) {
            for (var mf : listToUpdate) {
                list.add(map(mf));
            }
        }

        return list;
    }

    MicrofrontendSearchCriteria map(MicrofrontendSearchCriteriaDTO dto);

    @Mapping(target = "removeStreamItem", ignore = true)
    MicrofrontendPageResultDTO mapPageResult(PageResult<Microfrontend> page);

    @Mapping(target = "deprecated", qualifiedByName = "get-deprecated")
    @Mapping(target = "undeployed", qualifiedByName = "get-undeployed")
    @Mapping(target = "removeEndpointsItem", ignore = true)
    MicrofrontendPageItemDTO mapPageItem(Microfrontend data);

    @Mapping(target = "deprecated", qualifiedByName = "get-deprecated")
    @Mapping(target = "undeployed", qualifiedByName = "get-undeployed")
    @Mapping(target = "removeEndpointsItem", ignore = true)
    MicrofrontendDTO map(Microfrontend data);

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
}
