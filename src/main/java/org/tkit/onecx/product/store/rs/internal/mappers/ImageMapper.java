package org.tkit.onecx.product.store.rs.internal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.tkit.onecx.product.store.domain.models.Image;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.image.rs.internal.model.ImageInfoDTO;

@Mapper(uses = OffsetDateTimeMapper.class)
public interface ImageMapper {

    ImageInfoDTO map(Image image);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "refId", source = "refId")
    @Mapping(target = "refType", source = "refType")
    Image create(String refId, String refType, String mimeType, Integer length, byte[] imageData);

    @Mapping(target = "refType", ignore = true)
    @Mapping(target = "refId", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    void update(@MappingTarget Image image, Integer length, String mimeType, byte[] imageData);
}
