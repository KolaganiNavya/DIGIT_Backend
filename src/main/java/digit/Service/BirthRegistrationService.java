package digit.Service;

import digit.enrichment.BirthApplicationEnrichment;
import digit.kafka.Producer;
import digit.repository.BirthRegistrationRepository;
import digit.validators.BirthApplicationValidator;
import digit.web.models.BirthApplicationSearchCriteria;
import digit.web.models.BirthRegistrationApplication;
import digit.web.models.BirthRegistrationRequest;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static digit.config.ServiceConstants.KAFKA_SAVE_BIRTH_APPLICATION;
import static digit.config.ServiceConstants.KAFKA_UPDATE_BIRTH_APPLICATION;

@Service
@Slf4j
public class BirthRegistrationService {

    @Autowired
    private BirthApplicationValidator validator;

    @Autowired
    private BirthApplicationEnrichment enrichmentUtil;

    @Autowired
    private digit.Service.UserService userService;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private BirthRegistrationRepository birthRegistrationRepository;

    @Autowired
    private Producer producer;

    /**
     * Registers a new Birth Registration request by performing validation, enrichment, user service interaction,
     * workflow update, and finally pushing the application to Kafka for persistence.
     *
     * @param birthRegistrationRequest The request containing the birth registration details.
     * @return A list of enriched and validated BirthRegistrationApplication objects.
     */
    public List<BirthRegistrationApplication> registerBtRequest(BirthRegistrationRequest birthRegistrationRequest) {
        // Validate applications
        validator.validateBirthApplication(birthRegistrationRequest);

        // Enrich applications
        enrichmentUtil.enrichBirthApplication(birthRegistrationRequest);

        // Enrich/Upsert user in upon birth registration
        userService.callUserService(birthRegistrationRequest);

        // Initiate workflow for the new application
        workflowService.updateWorkflowStatus(birthRegistrationRequest);

        // Push the application to the topic for persister to listen and persist
        producer.push(KAFKA_SAVE_BIRTH_APPLICATION, birthRegistrationRequest);

        // Return the response back to user
        return birthRegistrationRequest.getBirthRegistrationApplications();
    }

    /**
     * Searches for Birth Registration Applications based on the provided search criteria.
     *
     * @param requestInfo The request information containing the user details.
     * @param birthApplicationSearchCriteria The criteria used to filter the birth registration applications.
     * @return A list of birth registration applications matching the search criteria.
     */
    public List<BirthRegistrationApplication> searchBtApplications(RequestInfo requestInfo, BirthApplicationSearchCriteria birthApplicationSearchCriteria) {
        // Fetch applications from database according to the given search criteria
        List<BirthRegistrationApplication> applications = birthRegistrationRepository.getApplications(birthApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        // Enrich mother and father of applicant objects
        applications.forEach(application -> {
            enrichmentUtil.enrichFatherApplicantOnSearch(application);
            enrichmentUtil.enrichMotherApplicantOnSearch(application);

        });
        // Otherwise return the found applications
        return applications;
    }

    /**
     * Updates an existing Birth Registration Application. This method validates if the application exists,
     * performs enrichment, updates workflow status, and sends an update request to Kafka.
     *
     * @param birthRegistrationRequest The request containing the updated birth registration details.
     * @return The updated BirthRegistrationApplication.
     */
    public BirthRegistrationApplication updateBtApplication(BirthRegistrationRequest birthRegistrationRequest) {
        // Validate whether the application that is being requested for update indeed exists
        BirthRegistrationApplication existingApplication = validator.validateApplicationExistence(birthRegistrationRequest.getBirthRegistrationApplications().get(0));
        existingApplication.setWorkflow(birthRegistrationRequest.getBirthRegistrationApplications().get(0).getWorkflow());
        log.info(existingApplication.toString());
        birthRegistrationRequest.setBirthRegistrationApplications(Collections.singletonList(existingApplication));

        // Enrich application upon update
        enrichmentUtil.enrichBirthApplicationUponUpdate(birthRegistrationRequest);

        // update workflow for the application
        workflowService.updateWorkflowStatus(birthRegistrationRequest);

        // Just like create request, update request will be handled asynchronously by the persister
        producer.push(KAFKA_UPDATE_BIRTH_APPLICATION, birthRegistrationRequest);

        return birthRegistrationRequest.getBirthRegistrationApplications().get(0);
    }
}