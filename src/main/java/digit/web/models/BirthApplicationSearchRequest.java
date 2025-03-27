package digit.web.models;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;

/**
 * BirthApplicationSearchCriteria
 */
@Validated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BirthApplicationSearchRequest {

    /// RequestInfo is a metadata object containing request-related details
    @JsonProperty("RequestInfo")
    @Valid
    private RequestInfo requestInfo = null;

    // BirthApplicationSearchCriteria holds the actual search filters
    @JsonProperty("BirthApplicationSearchCriteria")
    @Valid
    private BirthApplicationSearchCriteria birthApplicationSearchCriteria = null;

}
