package org.tkit.onecx.product.store.domain.models;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PRODUCT_CLASSIFICATION", uniqueConstraints = {
        @UniqueConstraint(name = "PRODUCT_CLASSIFICATION_VALUE", columnNames = { "PRODUCT_ID", "VALUE" })
})
public class ProductClassification implements Serializable {

    @Id
    @Column(name = "GUID")
    private String id = UUID.randomUUID().toString();

    @Column(name = "VALUE")
    private String value;

    @Column(name = "PRODUCT_ID", insertable = false, updatable = false)
    private String productId;
}
