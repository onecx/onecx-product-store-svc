package io.github.onecx.product.store.rs.internal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.product.store.rs.internal.model.*;
import io.github.onecx.product.store.domain.criteria.ProductSearchCriteria;
import io.github.onecx.product.store.domain.models.Product;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface ProductMapper {

    ProductSearchCriteria map(ProductSearchCriteriaDTO data);

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "operator", constant = "false")
    Product create(CreateProductRequestDTO dto);

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "operator", constant = "false")
    void update(UpdateProductRequestDTO dto, @MappingTarget Product product);

    @Mapping(target = "removeStreamItem", ignore = true)
    ProductPageResultDTO mapPageResult(PageResult<Product> page);

    @Mapping(target = "removeClassificationsItem", ignore = true)
    ProductAbstractDTO mapProductAbstract(Product data);

    @Mapping(target = "removeClassificationsItem", ignore = true)
    ProductDTO map(Product data);
}
