package digit.validators;

import digit.repository.BirthRegistrationRepository;
import digit.web.models.BirthApplicationSearchCriteria;
import digit.web.models.BirthRegistrationApplication;
import digit.web.models.BirthRegistrationRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import static digit.config.ServiceConstants.BIRTH_APP_ERROR_CODE;
import static digit.config.ServiceConstants.TENANT_ID_MANDATORY;

@Component
public class BirthApplicationValidator {

    @Autowired
    private BirthRegistrationRepository repository;

    /**
     * Validates the birth registration request to ensure that mandatory fields are provided.
     *
     * @param birthRegistrationRequest The birth registration request to be validated.
     * @throws CustomException if any mandatory field is missing.
     */
    public void validateBirthApplication(BirthRegistrationRequest birthRegistrationRequest) {
        birthRegistrationRequest.getBirthRegistrationApplications().forEach(application -> {
            if(ObjectUtils.isEmpty(application.getTenantId()))
                throw new CustomException(BIRTH_APP_ERROR_CODE,TENANT_ID_MANDATORY);
        });
    }

    /**
     * Checks if a birth registration application already exists in the repository based on its application number.
     *
     * @param birthRegistrationApplication The application to check for existence.
     * @return The existing birth registration application if found.
     * @throws CustomException if no application is found with the given application number.
     */
    public BirthRegistrationApplication validateApplicationExistence(BirthRegistrationApplication birthRegistrationApplication) {
        return repository.getApplications(BirthApplicationSearchCriteria.builder().applicationNumber(birthRegistrationApplication.getApplicationNumber()).build()).get(0);
    }
}