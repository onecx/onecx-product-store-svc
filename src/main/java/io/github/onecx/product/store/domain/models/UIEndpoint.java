package io.github.onecx.product.store.domain.models;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "UI_ENDPOINT", indexes = {
        @Index(columnList = "MICROFRONTEND_ID", name = "UI_ENDPOINT_MICROFRONTEND_ID")
}, uniqueConstraints = {
        @UniqueConstraint(name = "UI_ENDPOINT_PATH", columnNames = { "MICROFRONTEND_ID", "PATH" })
})
public class UIEndpoint implements Serializable {

    @Id
    @Column(name = "GUID")
    private String id = UUID.randomUUID().toString();

    @Column(name = "PATH")
    private String path;

    @Column(name = "NAME")
    private String name;

}
