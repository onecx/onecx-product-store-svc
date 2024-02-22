package org.tkit.onecx.product.store.domain.criteria;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

@RegisterForReflection
@Getter
@Setter
public class ProductLoadCriteria {

    private List<String> productNames;

    private Integer pageNumber;

    private Integer pageSize;
}
