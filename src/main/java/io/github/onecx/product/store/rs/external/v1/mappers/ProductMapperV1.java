package io.github.onecx.product.store.rs.external.v1.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.product.store.rs.external.v1.model.ProductDTOV1;
import gen.io.github.onecx.product.store.rs.external.v1.model.ProductPageResultDTOV1;
import gen.io.github.onecx.product.store.rs.external.v1.model.ProductSearchCriteriaDTOV1;
import io.github.onecx.product.store.domain.criteria.ProductSearchCriteria;
import io.github.onecx.product.store.domain.models.Product;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface ProductMapperV1 {

    ProductSearchCriteria map(ProductSearchCriteriaDTOV1 data);

    @Mapping(target = "removeStreamItem", ignore = true)
    ProductPageResultDTOV1 mapPageResult(PageResult<Product> page);

    ProductDTOV1 map(Product data);
}
