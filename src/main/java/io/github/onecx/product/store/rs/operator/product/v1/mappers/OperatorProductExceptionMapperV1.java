package io.github.onecx.product.store.rs.operator.product.v1.mappers;

import java.util.List;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;
import org.tkit.quarkus.jpa.exceptions.DAOException;
import org.tkit.quarkus.log.cdi.LogService;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.product.store.rs.operator.product.v1.model.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper(uses = { OffsetDateTimeMapper.class })
public abstract class OperatorProductExceptionMapperV1 {

    @LogService(log = false)
    public RestResponse<RestExceptionDTOV1> constraint(ConstraintViolationException ex) {
        var dto = exception("CONSTRAINT_VIOLATIONS", ex.getMessage());
        dto.setValidations(createErrorValidationResponse(ex.getConstraintViolations()));
        return RestResponse.status(Response.Status.BAD_REQUEST, dto);
    }

    @LogService(log = false)
    public Response exception(ConstraintException ce) {
        var e = exception(ce.getMessageKey().name(), ce.getConstraints(), ce.parameters);
        e.setNamedParameters(ce.namedParameters);
        return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
    }

    @LogService(log = false)
    public RestResponse<RestExceptionDTOV1> exception(DAOException de) {
        return RestResponse.status(Response.Status.BAD_REQUEST,
                exception(de.getMessageKey().name(), de.getMessage(), de.parameters));
    }

    @Mapping(target = "removeParametersItem", ignore = true)
    @Mapping(target = "namedParameters", ignore = true)
    @Mapping(target = "removeNamedParametersItem", ignore = true)
    @Mapping(target = "parameters", ignore = true)
    @Mapping(target = "validations", ignore = true)
    @Mapping(target = "removeValidationsItem", ignore = true)
    public abstract RestExceptionDTOV1 exception(String errorCode, String message);

    @Mapping(target = "removeParametersItem", ignore = true)
    @Mapping(target = "namedParameters", ignore = true)
    @Mapping(target = "removeNamedParametersItem", ignore = true)
    @Mapping(target = "validations", ignore = true)
    @Mapping(target = "removeValidationsItem", ignore = true)
    public abstract RestExceptionDTOV1 exception(String errorCode, String message, List<Object> parameters);

    public abstract List<ValidationConstraintDTOV1> createErrorValidationResponse(
            Set<ConstraintViolation<?>> constraintViolation);

    @Mapping(target = "parameter", source = "propertyPath")
    @Mapping(target = "message", source = "message")
    public abstract ValidationConstraintDTOV1 createError(ConstraintViolation<?> constraintViolation);

    public String mapPath(Path path) {
        return path.toString();
    }
}
