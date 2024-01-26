package io.github.onecx.product.store.rs.internal.mappers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mapstruct.*;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.product.store.rs.internal.model.*;
import io.github.onecx.product.store.domain.criteria.MicrofrontendSearchCriteria;
import io.github.onecx.product.store.domain.models.Microfrontend;
import io.github.onecx.product.store.domain.models.UIEndpoint;

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

    MicrofrontendPageItemDTO mapPageItem(Microfrontend data);

    @Mapping(target = "removeEndpointsItem", ignore = true)
    MicrofrontendDTO map(Microfrontend data);
}
