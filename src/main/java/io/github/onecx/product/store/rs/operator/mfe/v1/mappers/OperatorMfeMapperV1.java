package io.github.onecx.product.store.rs.operator.mfe.v1.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.product.store.rs.operator.mfe.v1.model.*;
import io.github.onecx.product.store.domain.models.Microfrontend;
import io.github.onecx.product.store.domain.models.UIEndpoint;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface OperatorMfeMapperV1 {

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appId", ignore = true)
    @Mapping(target = "operator", constant = "true")
    Microfrontend create(UpdateMfeRequestMDTOv1 dto);

    @Mapping(target = "id", ignore = true)
    UIEndpoint map(UpdateMfeUIEndpointRequestMDTOv1 dto);

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appId", ignore = true)
    @Mapping(target = "operator", constant = "true")
    void update(@MappingTarget Microfrontend mfe, UpdateMfeRequestMDTOv1 dto);
}
