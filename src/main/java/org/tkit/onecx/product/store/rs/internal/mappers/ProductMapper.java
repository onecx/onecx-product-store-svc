package org.tkit.onecx.product.store.rs.internal.mappers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mapstruct.*;
import org.tkit.onecx.product.store.domain.criteria.ProductSearchCriteria;
import org.tkit.onecx.product.store.domain.models.*;
import org.tkit.onecx.product.store.domain.wrapper.ProductLoadResultWrapper;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.product.store.rs.internal.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface ProductMapper {

    @Mapping(target = "productNames", ignore = true)
    ProductSearchCriteria map(ProductSearchCriteriaDTO data);

    @Mapping(target = "productClassifications", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "operator", constant = "false")
    @Mapping(target = "undeployed", qualifiedByName = "undeployed")
    @Mapping(target = "classifications", qualifiedByName = "updateList")
    Product create(CreateProductRequestDTO dto);

    @Mapping(target = "productClassifications", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "operator", constant = "false")
    @Mapping(target = "undeployed", qualifiedByName = "undeployed")
    @Mapping(target = "classifications", qualifiedByName = "updateList")
    void update(UpdateProductRequestDTO dto, @MappingTarget Product product);

    @Mapping(target = "removeStreamItem", ignore = true)
    ProductPageResultDTO mapPageResult(PageResult<Product> page);

    @Mapping(target = "applications", ignore = true)
    @Mapping(target = "removeApplicationsItem", ignore = true)
    @Mapping(target = "undeployed", qualifiedByName = "get-undeployed")
    @Mapping(target = "classifications", ignore = true)
    ProductAbstractDTO mapProductAbstract(Product data);

    @Mapping(target = "undeployed", qualifiedByName = "get-undeployed")
    @Mapping(target = "classifications", qualifiedByName = "toString")
    ProductDTO map(Product data);

    @Named("get-undeployed")
    default Boolean getUndeployed(Boolean value) {
        if (value == null) {
            return false;
        }
        return value;
    }

    @Named("undeployed")
    @SuppressWarnings("java:S2447")
    default Boolean setUndeployed(Boolean value) {
        if (value == null || !value) {
            return null;
        }
        return true;
    }

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "value", source = "classification")
    @Mapping(target = "id", ignore = true)
    ProductClassification mapClassificationString(String classification);

    @Named("updateList")
    default Set<ProductClassification> updateList(String listToUpdate) {
        var list = new HashSet<ProductClassification>();
        if (listToUpdate != null) {
            var listOfString = listToUpdate.split(",");
            for (var cf : listOfString) {
                list.add(mapClassificationString(cf));
            }
        }
        return list;
    }

    List<ApplicationAbstractDTO> mapMfeToAppAbstracts(List<Microfrontend> microfrontend);

    List<ApplicationAbstractDTO> mapMsToAppAbstracts(List<Microservice> microservice);

    @Mapping(target = "deprecated", ignore = true)
    @Mapping(target = "appName", source = "name")
    ApplicationAbstractDTO mapMsToAppAbstract(Microservice microservice);

    @Named("toString")
    default String setToString(Set<ProductClassification> classifications) {
        if (classifications != null && !classifications.isEmpty()) {
            return classifications.stream()
                    .map(ProductClassification::getValue)
                    .collect(Collectors.joining(","));
        }
        return "";
    }

    @Mapping(target = "removeProvidersItem", ignore = true)
    @Mapping(target = "removeClassificationsItem", ignore = true)
    ProductCriteriaDTO mapCriteriaLists(Stream<String> providers, Stream<String> classifications);

    @Mapping(target = "providers", ignore = true)
    @Mapping(target = "classifications", ignore = true)
    @Mapping(target = "displayName", ignore = true)
    @Mapping(target = "name", ignore = true)
    ProductSearchCriteria mapLoadCriteria(ProductLoadSearchCriteriaDTO productLoadSearchCriteriaDTO);

    @Mapping(target = "removeStreamItem", ignore = true)
    ProductsLoadResultDTO mapPageResultWrapper(PageResult<ProductLoadResultWrapper> page);

    @Mapping(target = "version", source = "appVersion")
    MicrofrontendAbstractDTO map(Microfrontend mfe);

    @Mapping(target = "appName", source = "name")
    MicroserviceAbstractDTO mapAbstract(Microservice mfe);

    default LoadProductDTO map(ProductLoadResultWrapper wrapper) {
        LoadProductDTO result = mapProduct(wrapper.getProduct());
        result.setMicrofrontends(mapMfeList(wrapper.getMicrofrontends()));
        result.setMicroservices(mapMsList(wrapper.getMicroservices()));
        result.setSlots(mapSlotList(wrapper.getSlots()));
        return result;
    }

    List<SlotAbstractDTO> mapSlotList(List<Slot> slots);

    List<MicroserviceAbstractDTO> mapMsList(List<Microservice> microservices);

    List<MicrofrontendAbstractDTO> mapMfeList(List<Microfrontend> microfrontends);

    @Mapping(target = "slots", ignore = true)
    @Mapping(target = "removeSlotsItem", ignore = true)
    @Mapping(target = "microfrontends", ignore = true)
    @Mapping(target = "removeMicrofrontendsItem", ignore = true)
    @Mapping(target = "microservices", ignore = true)
    @Mapping(target = "removeMicroservicesItem", ignore = true)
    LoadProductDTO mapProduct(Product product);
}
