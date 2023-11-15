package io.github.onecx.product.store.rs.internal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.product.store.rs.internal.model.*;
import io.github.onecx.product.store.domain.criteria.MicrofrontendSearchCriteria;
import io.github.onecx.product.store.domain.models.Microfrontend;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface MicrofrontendMapper {

    MicrofrontendSearchCriteria map(MicrofrontendSearchCriteriaDTO dto);

    @Mapping(target = "removeStreamItem", ignore = true)
    MicrofrontendPageResultDTO mapPageResult(PageResult<Microfrontend> page);

    @Mapping(target = "version", source = "modificationCount")
    MicrofrontendDTO map(Microfrontend data);
}
