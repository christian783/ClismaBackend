package io.app.clisma_backend.model;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import io.app.clisma_backend.service.EmissionRecordService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import org.springframework.web.servlet.HandlerMapping;


/**
 * Validate that the vehicleDetectionId value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = EmissionRecordVehicleDetectionIdUnique.EmissionRecordVehicleDetectionIdUniqueValidator.class
)
public @interface EmissionRecordVehicleDetectionIdUnique {

    String message() default "{Exists.emissionRecord.vehicle-detection-id}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class EmissionRecordVehicleDetectionIdUniqueValidator implements ConstraintValidator<EmissionRecordVehicleDetectionIdUnique, Long> {

        private final EmissionRecordService emissionRecordService;
        private final HttpServletRequest request;

        public EmissionRecordVehicleDetectionIdUniqueValidator(
                final EmissionRecordService emissionRecordService,
                final HttpServletRequest request) {
            this.emissionRecordService = emissionRecordService;
            this.request = request;
        }

        @Override
        public boolean isValid(final Long value, final ConstraintValidatorContext cvContext) {
            if (value == null) {
                // no value present
                return true;
            }
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables =
                    ((Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            final String currentId = pathVariables.get("id");
            if (currentId != null && value.equals(emissionRecordService.get(Long.parseLong(currentId)).getVehicleDetectionId())) {
                // value hasn't changed
                return true;
            }
            return !emissionRecordService.vehicleDetectionIdExists(value);
        }

    }

}
