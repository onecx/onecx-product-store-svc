package org.tkit.onecx.product.store.domain.criteria;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

@RegisterForReflection
@Getter
@Setter
public class ProductSearchCriteria {

    private List<String> productNames;

    private String displayName;

    private String name;

    private List<String> providers;

    private List<String> classifications;

    private Integer pageNumber;

    private Integer pageSize;
}
