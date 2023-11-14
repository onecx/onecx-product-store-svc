package io.github.onecx.product.store.domain.criteria;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

@RegisterForReflection
@Getter
@Setter
public class ProductSearchCriteria {

    private String name;

    private Integer pageNumber;

    private Integer pageSize;
}
