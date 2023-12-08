package io.github.onecx.product.store.rs.operator.mfe.v1.mappers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
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
    @Mapping(target = "endpoints", qualifiedByName = "updateList")
    void update(@MappingTarget Microfrontend mfe, UpdateMfeRequestMDTOv1 dto);

    @Named("updateList")
    default Set<UIEndpoint> updateList(List<UpdateMfeUIEndpointRequestMDTOv1> listToUpdate) {
        var list = new HashSet<UIEndpoint>();

        if (listToUpdate != null) {
            for (var mf : listToUpdate) {
                list.add(map(mf));
            }
        }

        return list;
    }
}
