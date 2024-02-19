package org.tkit.onecx.product.store.rs.internal.mappers;

import org.mapstruct.*;
import org.tkit.onecx.product.store.domain.criteria.MicroserviceSearchCriteria;
import org.tkit.onecx.product.store.domain.models.Microservice;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.product.store.rs.internal.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface MicroserviceMapper {

    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    Microservice create(CreateMicroserviceRequestDTO dto);

    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    void update(UpdateMicroserviceRequestDTO dto, @MappingTarget Microservice data);

    MicroserviceSearchCriteria map(MicroserviceSearchCriteriaDTO dto);

    @Mapping(target = "removeStreamItem", ignore = true)
    MicroservicePageResultDTO mapPageResult(PageResult<Microservice> page);

    MicroservicePageItemDTO mapPageItem(Microservice data);

    MicroserviceDTO map(Microservice data);
}
