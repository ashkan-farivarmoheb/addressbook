package com.reece.addressbook.validation;

import com.reece.addressbook.common.HeaderRestrictions;
import com.reece.addressbook.common.RequestHttpHeadersEnum;
import com.reece.addressbook.exception.InvalidHeaderContentException;
import com.reece.addressbook.exception.MandatoryHeaderMissingException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RequestHttpHeaderValidator implements ConstraintValidator<ValidRequestHeader, HttpHeaders> {
    private RequestHttpHeadersEnum mandatoryHeaders;

    @Override
    public void initialize(ValidRequestHeader constraintAnnotation) {
        mandatoryHeaders = constraintAnnotation.mandatoryHeaders();
    }

    @Override
    public boolean isValid(final HttpHeaders requestHeaders, final ConstraintValidatorContext context) {
        if (mandatoryHeaders != null) {
            validateHeadersPresent(requestHeaders);
            validateHeadersContent(requestHeaders);
        }
        return true;
    }

    private void validateHeadersPresent(HttpHeaders requestHeaders) {
        final List<String> mandatoryHeadersString = mandatoryHeaders.getHeaders();
        if (!CollectionUtils.isEmpty(mandatoryHeadersString)) {
            String[] missingHeaders = mandatoryHeadersString
                    .stream()
                    .filter(header -> StringUtils.isBlank(requestHeaders.getFirst(header)))
                    .toArray(String[]::new);
            if (missingHeaders.length != 0) {
                throw new MandatoryHeaderMissingException(missingHeaders);
            }
        }
    }

    private void validateHeadersContent(HttpHeaders headers) {
        Map<String, String> invalidHeaderMap = new HashMap<>();

        Arrays.stream(HeaderRestrictions.values())
                .forEach(restriction -> {
                    String headerName = restriction.getHeaderName();
                    String headerValue = headers.getFirst(headerName);
                    boolean invalid = restriction.validate(headerValue, invalidHeaderMap);
                    if (invalid) {
                        invalidHeaderMap.put(headerName, headerValue);
                    }
                });
        if (!CollectionUtils.isEmpty(invalidHeaderMap)) {
            throw new InvalidHeaderContentException(invalidHeaderMap);
        }
    }
}
