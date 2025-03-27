package digit.web.controllers;

import digit.Service.BirthRegistrationService;
import digit.util.ResponseInfoFactory;
import digit.web.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.egov.common.contract.request.RequestInfo;
import java.util.Collections;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;

import static digit.config.ServiceConstants.*;

@Controller
@RequestMapping("")
public class BirthApiController {

    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;
    private final BirthRegistrationService birthRegistrationService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    // Constructor for injecting dependencies into the controller
    @Autowired
    public BirthApiController(ObjectMapper objectMapper, HttpServletRequest request, BirthRegistrationService birthRegistrationService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.birthRegistrationService = birthRegistrationService;
    }

    /**
     * Handles the creation of new birth registration applications.
     *
     * @param birthRegistrationRequest The request containing the details of the new birth registration application(s).
     * @return A ResponseEntity containing the response with the created birth registration applications.
     */
    @RequestMapping(value = REGISTRATION_CREATE, method = RequestMethod.POST)
    public ResponseEntity<BirthRegistrationResponse> v1RegistrationCreatePost(
            @ApiParam(value = "Details for the new Birth Registration Application(s) + RequestInfo meta data.", required = true)
            @Valid @RequestBody BirthRegistrationRequest birthRegistrationRequest) {

        List<BirthRegistrationApplication> applications = birthRegistrationService.registerBtRequest(birthRegistrationRequest);
        RequestInfo requestInfo = birthRegistrationRequest.getRequestInfo();

        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfo, true); // ✅ Fix

        BirthRegistrationResponse response = BirthRegistrationResponse.builder()
                .birthRegistrationApplications(applications)
                .responseInfo(responseInfo) // ✅ Fix: Use ResponseInfo
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Handles the search for existing birth registration applications.
     *
     * @param birthApplicationSearchRequest The search request containing criteria and meta data.
     * @return A ResponseEntity containing the response with the list of birth registration applications that match the search criteria.
     */
    @RequestMapping(value = REGISTRATION_SEARCH, method = RequestMethod.POST)
    public ResponseEntity<BirthRegistrationResponse> v1RegistrationSearchPost(
            @ApiParam(value = "Details for the new Birth Registration Application(s) + RequestInfo meta data.", required = true)
            @Valid @RequestBody BirthApplicationSearchRequest birthApplicationSearchRequest) {

        List<BirthRegistrationApplication> applications = birthRegistrationService.searchBtApplications(
                birthApplicationSearchRequest.getRequestInfo(),
                birthApplicationSearchRequest.getBirthApplicationSearchCriteria()
        );
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(birthApplicationSearchRequest.getRequestInfo(), true); // ✅ Fix
        System.out.println(applications);
        System.out.println(responseInfo);
        BirthRegistrationResponse response = BirthRegistrationResponse.builder()
                .birthRegistrationApplications(applications)
                .responseInfo(responseInfo) // ✅ Fix: Use ResponseInfo
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Handles the update of existing birth registration applications.
     *
     * @param birthRegistrationRequest The request containing the updated details of the birth registration application.
     * @return A ResponseEntity containing the response with the updated birth registration application.
     */
    @RequestMapping(value = REGISTRATION_UPDATE, method = RequestMethod.POST)
    public ResponseEntity<BirthRegistrationResponse> v1RegistrationUpdatePost(
            @ApiParam(value = "Details for the new Birth Registration Application(s) + RequestInfo meta data.", required = true)
            @Valid @RequestBody BirthRegistrationRequest birthRegistrationRequest) {

        BirthRegistrationApplication application = birthRegistrationService.updateBtApplication(birthRegistrationRequest);

        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(birthRegistrationRequest.getRequestInfo(), true); // ✅ Fix

        BirthRegistrationResponse response = BirthRegistrationResponse.builder()
                .birthRegistrationApplications(Collections.singletonList(application))
                .responseInfo(responseInfo) // ✅ Fix: Use ResponseInfo
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}