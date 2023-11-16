package io.github.onecx.product.store.rs.internal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.product.store.rs.internal.model.*;
import io.github.onecx.product.store.domain.criteria.MicrofrontendSearchCriteria;
import io.github.onecx.product.store.domain.models.Microfrontend;

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
    Microfrontend create(CreateMicrofrontendDTO dto);

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "operator", constant = "false")
    void update(UpdateMicrofrontendDTO dto, @MappingTarget Microfrontend data);

    MicrofrontendSearchCriteria map(MicrofrontendSearchCriteriaDTO dto);

    @Mapping(target = "removeStreamItem", ignore = true)
    MicrofrontendPageResultDTO mapPageResult(PageResult<Microfrontend> page);

    @Mapping(target = "version", source = "modificationCount")
    MicrofrontendDTO map(Microfrontend data);
}
