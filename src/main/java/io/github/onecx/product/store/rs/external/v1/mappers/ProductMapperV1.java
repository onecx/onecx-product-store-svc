package io.github.onecx.product.store.rs.external.v1.mappers;

import java.util.List;
import java.util.stream.Stream;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.product.store.rs.external.v1.model.*;
import io.github.onecx.product.store.domain.criteria.ProductSearchCriteria;
import io.github.onecx.product.store.domain.models.Microfrontend;
import io.github.onecx.product.store.domain.models.Product;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface ProductMapperV1 {

    ProductSearchCriteria map(ProductSearchCriteriaDTOV1 data);

    @Mapping(target = "removeStreamItem", ignore = true)
    ProductPageResultDTOV1 mapPageResult(PageResult<Product> page);

    ProductItemDTOV1 maPageItems(Product data);

    @Mapping(target = "removeMicrofrontendsItem", ignore = true)
    @Mapping(target = "microfrontends", ignore = true)
    ProductDTOV1 map(Product data);

    default ProductDTOV1 map(Product product, Stream<Microfrontend> microfrontends) {
        var p = map(product);
        p.setMicrofrontends(items(microfrontends));
        return p;
    }

    List<MicrofrontendDTOV1> items(Stream<Microfrontend> microfrontends);

    MicrofrontendDTOV1 map(Microfrontend data);
}
