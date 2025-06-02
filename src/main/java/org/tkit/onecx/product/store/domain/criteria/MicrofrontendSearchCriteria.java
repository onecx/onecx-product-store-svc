package org.tkit.onecx.product.store.domain.criteria;

import org.tkit.onecx.product.store.domain.models.Microfrontend;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RegisterForReflection
public class MicrofrontendSearchCriteria {

    private String appName;

    private String productName;

    private Integer pageNumber;

    private Integer pageSize;

    private Microfrontend.Type type;
}
