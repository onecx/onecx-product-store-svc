package org.tkit.onecx.product.store.rs.operator.ms.v1.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.tkit.onecx.product.store.domain.models.Microservice;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.product.store.rs.operator.ms.v1.model.UpdateMsRequestMsDTOv1;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface OperatorMsMapperV1 {

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
    Microservice create(UpdateMsRequestMsDTOv1 dto);

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
    void update(@MappingTarget Microservice ms, UpdateMsRequestMsDTOv1 dto);
}
