package digit.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import digit.config.BTRConfiguration;
import digit.repository.ServiceRequestRepository;
import digit.web.models.*;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.models.Workflow;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.models.RequestInfoWrapper;
import org.egov.common.contract.request.User;
import org.egov.common.contract.workflow.*;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static digit.config.ServiceConstants.*;

@Component
@Slf4j
public class WorkflowService {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ServiceRequestRepository repository;

    @Autowired
    private BTRConfiguration config;

    /**
     * Updates the workflow status for each birth registration application.
     *
     * @param birthRegistrationRequest The request to update of workflow
     */
    public void updateWorkflowStatus(BirthRegistrationRequest birthRegistrationRequest) {
        birthRegistrationRequest.getBirthRegistrationApplications().forEach(application -> {
            ProcessInstance processInstance = getProcessInstanceForBTR(application, birthRegistrationRequest.getRequestInfo());
            ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(birthRegistrationRequest.getRequestInfo(), Collections.singletonList(processInstance));
            callWorkFlow(workflowRequest);
        });
    }

    /**
     * Makes a call to the workflow service to transition the workflow to the next state.
     *
     * @param workflowReq The Workflow Request
     * @return the State in which the application is present
     */
    public State callWorkFlow(ProcessInstanceRequest workflowReq) {

        ProcessInstanceResponse response = null;
        StringBuilder url = new StringBuilder(config.getWfHost().concat(config.getWfTransitionPath()));
        Object optional = repository.fetchResult(url, workflowReq);
        response = mapper.convertValue(optional, ProcessInstanceResponse.class);
        return response.getProcessInstances().get(0).getState();
    }

    /**
     * Constructs a process instance for the birth registration application.
     *
     * @param application Contains the birth registration application
     * @param requestInfo contians the request Info from the request
     * @return returns Process Instance
     */
    private ProcessInstance getProcessInstanceForBTR(BirthRegistrationApplication application, RequestInfo requestInfo) {
        Workflow workflow = application.getWorkflow();

        ProcessInstance processInstance = new ProcessInstance();
        processInstance.setBusinessId(application.getApplicationNumber());
        processInstance.setAction(workflow.getAction());
        processInstance.setModuleName(MODULE_NAME);
        processInstance.setTenantId(application.getTenantId());
        processInstance.setBusinessService(BUSINESS_SERVICE);
        processInstance.setDocuments(workflow.getDocuments());
        processInstance.setComment(workflow.getComments());

        if(!CollectionUtils.isEmpty(workflow.getAssignes())){
            List<User> users = new ArrayList<>();

            workflow.getAssignes().forEach(uuid -> {
                User user = new User();
                user.setUuid(uuid);
                users.add(user);
            });

            processInstance.setAssignes(users);
        }

        return processInstance;

    }

    /**
     * Fetches the current workflow state for a given business ID and tenant.
     *
     * @param requestInfo Contains requestInfo from the Request
     * @param tenantId TenantId of the application
     * @param businessId Business Id to get the workflow
     * @return
     */
    public ProcessInstance getCurrentWorkflow(RequestInfo requestInfo, String tenantId, String businessId) {

        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

        StringBuilder url = getSearchURLWithParams(tenantId, businessId);

        Object res = repository.fetchResult(url, requestInfoWrapper);
        ProcessInstanceResponse response = null;

        try{
            response = mapper.convertValue(res, ProcessInstanceResponse.class);
        }
        catch (Exception e){
            throw new CustomException(PARSING_ERROR_MESSAGE,PARSING_ERROR_DESCRIPTION);
        }

        if(response!=null && !CollectionUtils.isEmpty(response.getProcessInstances()) && response.getProcessInstances().get(0)!=null)
            return response.getProcessInstances().get(0);

        return null;
    }

    /**
     * Gets the business service associated with a birth registration application.
     *
     * @param application The Birth Registration Application
     * @param requestInfo The RequestInfo
     * @return
     */
    private BusinessService getBusinessService(BirthRegistrationApplication application, RequestInfo requestInfo) {
        String tenantId = application.getTenantId();
        StringBuilder url = getSearchURLWithParams(tenantId, "BTR");
        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
        Object result = repository.fetchResult(url, requestInfoWrapper);
        BusinessServiceResponse response = null;
        try {
            response = mapper.convertValue(result, BusinessServiceResponse.class);
        } catch (IllegalArgumentException e) {
            throw new CustomException(PARSING_ERROR, FAILED_TO_PARSE_BUSINESS_SERVICE_SEARCH);
        }

        if (CollectionUtils.isEmpty(response.getBusinessServices()))
            throw new CustomException(BUSINESS_SERVICE_NOT_FOUND, THE_BUSINESS_SERVICE + BUSINESS_SERVICE + NOT_FOUND);

        return response.getBusinessServices().get(0);
    }

    /**
     * Builds the URL for searching business services with the provided tenant ID and business service name.
     *
     * @param tenantId
     * @param businessService
     * @return
     */
    private StringBuilder getSearchURLWithParams(String tenantId, String businessService) {

        StringBuilder url = new StringBuilder(config.getWfHost());
        url.append(config.getWfBusinessServiceSearchPath());
        url.append(TENANTID);
        url.append(tenantId);
        url.append(BUSINESS_SERVICES);
        url.append(businessService);
        return url;
    }

    /**
     * Prepares a process instance request for a birth registration payment.
     *
     * @param updateRequest
     * @return
     */
    public ProcessInstanceRequest getProcessInstanceForBirthRegistrationPayment(BirthRegistrationRequest updateRequest) {

        BirthRegistrationApplication application = updateRequest.getBirthRegistrationApplications().get(0);

        //// Create a new process instance for the payment action
        ProcessInstance process = ProcessInstance.builder()
                .businessService(BUSINESS_SERVICE)
                .businessId(application.getApplicationNumber())
                .comment(PAYMENT_COMMENT)
                .moduleName(MODULE_NAME)
                .tenantId(application.getTenantId())
                .action(PAY_ACTION)
                .build();

        return ProcessInstanceRequest.builder()
                .requestInfo(updateRequest.getRequestInfo())
                .processInstances(Arrays.asList(process))
                .build();

    }
}