package org.tkit.onecx.product.store.domain.criteria;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RegisterForReflection
public class SlotSearchCriteria {

    private String appId;

    private String name;

    private String productName;

    private Integer pageNumber;

    private Integer pageSize;
}
