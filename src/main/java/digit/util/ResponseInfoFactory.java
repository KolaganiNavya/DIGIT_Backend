package digit.util;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.stereotype.Component;

import static digit.config.ServiceConstants.*;

@Component
public class ResponseInfoFactory {

    /**
     * This method creates a ResponseInfo object from the provided RequestInfo and success flag.
     * @param requestInfo The RequestInfo object containing metadata about the request (e.g., API ID, version, timestamp, etc.).
     * @param success A Boolean flag indicating whether the operation was successful or not.
     * @return A ResponseInfo object that contains the response details corresponding to the request.
     */
    public ResponseInfo createResponseInfoFromRequestInfo(final RequestInfo requestInfo, final Boolean success) {

        final String apiId = requestInfo != null ? requestInfo.getApiId() : "";
        final String ver = requestInfo != null ? requestInfo.getVer() : "";
        Long ts = null;
        if(requestInfo!=null)
            ts = requestInfo.getTs();
        final String resMsgId = RES_MSG_ID; // FIXME : Hard-coded
        final String msgId = requestInfo != null ? requestInfo.getMsgId() : "";
        final String responseStatus = success ? SUCCESSFUL : FAILED;

        return ResponseInfo.builder().apiId(apiId).ver(ver).ts(ts).resMsgId(resMsgId).msgId(msgId).resMsgId(resMsgId)
                .status(responseStatus).build();
    }

}