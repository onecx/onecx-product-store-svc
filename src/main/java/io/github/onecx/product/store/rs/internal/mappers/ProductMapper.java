package io.github.onecx.product.store.rs.internal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.product.store.rs.internal.model.ProductDTO;
import gen.io.github.onecx.product.store.rs.internal.model.ProductPageResultDTO;
import gen.io.github.onecx.product.store.rs.internal.model.ProductSearchCriteriaDTO;
import io.github.onecx.product.store.domain.criteria.ProductSearchCriteria;
import io.github.onecx.product.store.domain.models.Product;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface ProductMapper {

    ProductSearchCriteria map(ProductSearchCriteriaDTO data);

    @Mapping(target = "removeStreamItem", ignore = true)
    ProductPageResultDTO mapPageResult(PageResult<Product> page);

    @Mapping(target = "version", source = "modificationCount")
    ProductDTO map(Product data);
}
