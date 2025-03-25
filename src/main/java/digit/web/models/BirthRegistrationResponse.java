package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.egov.common.contract.response.ResponseInfo; // ✅ Correct import
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Contract class to send response. Array of items are used in case of search results or response for create, whereas a single item is used for update.
 */
@Schema(description = "Contract class to send response. Array of items are used in case of search results or response for create, whereas a single item is used for update")
@Validated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BirthRegistrationResponse {

    @JsonProperty("ResponseInfo")
    @Valid
    private ResponseInfo responseInfo;

    @JsonProperty("BirthRegistrationApplications")
    @Valid
    private List<@Valid BirthRegistrationApplication> birthRegistrationApplications = new ArrayList<>();

    public BirthRegistrationResponse addBirthRegistrationApplicationsItem(BirthRegistrationApplication item) {
        birthRegistrationApplications.add(item);
        return this;
    }
}