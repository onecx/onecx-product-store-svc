package io.github.onecx.product.store.domain.criteria;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RegisterForReflection
public class MicrofrontendSearchCriteria {

    private String displayName;

    private String productName;

    private Integer pageNumber;

    private Integer pageSize;
}
