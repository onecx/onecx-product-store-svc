package org.tkit.onecx.product.store.domain.models;

import jakarta.persistence.*;

import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "MICROSERVICE", uniqueConstraints = {
        @UniqueConstraint(name = "MICROSERVICE_APP_ID", columnNames = { "PRODUCT_NAME", "APP_ID" })
}, indexes = {
        @Index(name = "MICROSERVICE_PRODUCT_NAME", columnList = "PRODUCT_NAME")
})
@SuppressWarnings("java:S2160")
public class Microservice extends TraceableEntity {

    @Column(name = "APP_ID")
    private String appId;

    @Column(name = "VERSION")
    private String version;

    @Column(name = "NAME")
    private String name;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "PRODUCT_NAME")
    private String productName;

}
