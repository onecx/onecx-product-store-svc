package org.tkit.onecx.product.store.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "SLOT", uniqueConstraints = {
        @UniqueConstraint(name = "SLOT_PRODUCT_NAME_APP_ID", columnNames = { "NAME", "PRODUCT_NAME", "APP_ID" })
})
@SuppressWarnings("java:S2160")
public class Slot extends TraceableEntity {

    @Column(name = "PRODUCT_NAME")
    private String productName;

    @Column(name = "APP_ID")
    private String appId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "OPERATOR", nullable = false)
    private boolean operator;

    @Column(name = "UNDEPLOYED")
    private Boolean undeployed;

    @Column(name = "DEPRECATED")
    private Boolean deprecated;
}
