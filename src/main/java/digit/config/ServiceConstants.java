package digit.config;


import org.springframework.stereotype.Component;


@Component
public class ServiceConstants {

    public static final String EXTERNAL_SERVICE_EXCEPTION = "External Service threw an Exception: ";
    public static final String SEARCHER_SERVICE_EXCEPTION = "Exception while fetching from searcher: ";

    public static final String IDGEN_ERROR = "IDGEN ERROR";
    public static final String NO_IDS_FOUND_ERROR = "No ids returned from idgen Service";

    public static final String ERROR_WHILE_FETCHING_FROM_MDMS = "Exception occurred while fetching category lists from mdms: ";

    public static final String RES_MSG_ID = "uief87324";
    public static final String SUCCESSFUL = "successful";
    public static final String FAILED = "failed";

    public static final String URL = "url";
    public static final String URL_SHORTENING_ERROR_CODE = "URL_SHORTENING_ERROR";
    public static final String URL_SHORTENING_ERROR_MESSAGE = "Unable to shorten url: ";

    public static final String DOB_FORMAT_Y_M_D = "yyyy-MM-dd";
    public static final String DOB_FORMAT_D_M_Y = "dd/MM/yyyy";
    public static final String ILLEGAL_ARGUMENT_EXCEPTION_CODE = "IllegalArgumentException";
    public static final String OBJECTMAPPER_UNABLE_TO_CONVERT = "ObjectMapper not able to convertValue in userCall";
    public static final String DOB_FORMAT_D_M_Y_H_M_S = "dd-MM-yyyy HH:mm:ss";
    public static final String CREATED_DATE = "createdDate";
    public static final String LAST_MODIFIED_DATE = "lastModifiedDate";
    public static final String DOB = "dob";
    public static final String PWD_EXPIRY_DATE = "pwdExpiryDate";
    public static final String INVALID_DATE_FORMAT_CODE = "INVALID_DATE_FORMAT";
    public static final String INVALID_DATE_FORMAT_MESSAGE = "Failed to parse date format in user";
    public static final String CITIZEN_UPPER = "CITIZEN";
    public static final String CITIZEN_LOWER = "Citizen";
    public static final String USER = "user";

    public static final String PARSING_ERROR = "PARSING ERROR";
    public static final String FAILED_TO_PARSE_BUSINESS_SERVICE_SEARCH = "Failed to parse response of workflow business service search";
    public static final String BUSINESS_SERVICE_NOT_FOUND = "BUSINESSSERVICE_NOT_FOUND";
    public static final String THE_BUSINESS_SERVICE = "The businessService ";
    public static final String NOT_FOUND = " is not found";
    public static final String TENANTID = "?tenantId=";
    public static final String BUSINESS_SERVICES = "&businessServices=";


    public static final String PARSING_ERROR_MESSAGE = "PARSING_ERROR";
    public static final String PARSING_ERROR_DESCRIPTION = "Failed to parse workflow search response";
    public static final String MODULE_NAME = "birth-services";
    public static final String BUSINESS_SERVICE = "BTR";
    public static final String PAY_ACTION = "PAY";
    public static final String PAYMENT_COMMENT = "Payment for birth registration processed";
    public static final String USER_NOT_FOUND_MSG="No user found for the uuids";
    public static final String USER_NOT_FOUND="USER_NOT_FOUND";
    public static final String INVALIC_ACCOUNT_ID_MSG="No user exist for the given accountId";
    public static final String INVALID_ACCOUNT_ID="INVALID_ACCOUNTID";
    public static final String KAFKA_SAVE_BIRTH_APPLICATION = "save-birth-application";
    public static final String KAFKA_UPDATE_BIRTH_APPLICATION = "update-birth-application";
    public static final String BIRTH_APP_ERROR_CODE = "EG_BT_APP_ERR";
    public static final String TENANT_ID_MANDATORY = "tenantId is mandatory for creating birth registration applications";
    public static final String REGISTRATION_CREATE = "/registration/v1/_create";
    public static final String REGISTRATION_SEARCH = "/v1/registration/_search";
    public static final String REGISTRATION_UPDATE = "/registration/v1/_update";

}
