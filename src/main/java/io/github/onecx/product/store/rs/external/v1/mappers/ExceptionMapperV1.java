package io.github.onecx.product.store.rs.external.v1.mappers;

import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;
import org.tkit.quarkus.log.cdi.LogService;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.product.store.rs.external.v1.model.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper(uses = { OffsetDateTimeMapper.class })
public abstract class ExceptionMapperV1 {

    @LogService(log = false)
    public RestResponse<ProblemDetailResponseDTOv1> constraint(ConstraintViolationException ex) {
        var dto = exception("CONSTRAINT_VIOLATIONS", ex.getMessage());
        dto.setInvalidParams(createErrorValidationResponse(ex.getConstraintViolations()));
        return RestResponse.status(Response.Status.BAD_REQUEST, dto);
    }

    @LogService(log = false)
    public RestResponse<ProblemDetailResponseDTOv1> exception(ConstraintException ce) {
        var e = exception(ce.getMessageKey().name(), ce.getConstraints());
        e.setParams(map(ce.namedParameters));
        return RestResponse.status(Response.Status.BAD_REQUEST, e);
    }

    public List<ProblemDetailParamDTOv1> map(Map<String, Object> params) {
        if (params == null) {
            return List.of();
        }
        return params.entrySet().stream().map(e -> {
            var item = new ProblemDetailParamDTOv1();
            item.setKey(e.getKey());
            if (e.getValue() != null) {
                item.setValue(e.getValue().toString());
            }
            return item;
        }).toList();
    }

    @Mapping(target = "invalidParams", ignore = true)
    @Mapping(target = "removeInvalidParamsItem", ignore = true)
    @Mapping(target = "removeParamsItem", ignore = true)
    @Mapping(target = "params", ignore = true)
    public abstract ProblemDetailResponseDTOv1 exception(String errorCode, String detail);

    public abstract List<ProblemDetailInvalidParamDTOv1> createErrorValidationResponse(
            Set<ConstraintViolation<?>> constraintViolation);

    @Mapping(target = "name", source = "propertyPath")
    @Mapping(target = "message", source = "message")
    public abstract ProblemDetailInvalidParamDTOv1 createError(ConstraintViolation<?> constraintViolation);

    public String mapPath(Path path) {
        return path.toString();
    }
}
