package digit.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import digit.repository.ServiceRequestRepository;
import digit.config.Configuration;
import org.egov.common.contract.idgen.IdGenerationRequest;
import org.egov.common.contract.idgen.IdGenerationResponse;
import org.egov.common.contract.idgen.IdRequest;
import org.egov.common.contract.idgen.IdResponse;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static digit.config.ServiceConstants.*;

@Component
public class IdgenUtil {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ServiceRequestRepository restRepo;

    @Autowired
    private Configuration configs;

    /**
     * Fetches a list of unique IDs by calling the ID generation service.
     * @param requestInfo The request information (metadata about the request).
     * @param tenantId The tenant ID associated with the IDs to be generated.
     * @param idName The name of the ID to be generated (e.g., "BIRTH_REGISTRATION").
     * @param idformat The format of the ID (e.g., "BTR-{{counter}}").
     * @param count The number of IDs to generate.
     * @return A list of generated IDs.
     */
    public List<String> getIdList(RequestInfo requestInfo, String tenantId, String idName, String idformat, Integer count) {
        List<IdRequest> reqList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            reqList.add(IdRequest.builder().idName(idName).format(idformat).tenantId(tenantId).build());
        }

        IdGenerationRequest request = IdGenerationRequest.builder().idRequests(reqList).requestInfo(requestInfo).build();
        StringBuilder uri = new StringBuilder(configs.getIdGenHost()).append(configs.getIdGenPath());
        IdGenerationResponse response = mapper.convertValue(restRepo.fetchResult(uri, request), IdGenerationResponse.class);

        List<IdResponse> idResponses = response.getIdResponses();

        if (CollectionUtils.isEmpty(idResponses))
            throw new CustomException(IDGEN_ERROR, NO_IDS_FOUND_ERROR);

        return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
    }
}