package digit.repository.rowmapper;

import digit.web.models.*;
import org.egov.common.contract.models.Address;
import org.egov.common.contract.models.AuditDetails;
import org.egov.common.contract.models.Workflow;
import org.egov.common.contract.request.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

// The BirthApplicationRowMapper class is responsible for mapping a ResultSet (from a SQL query) to a list of BirthRegistrationApplication objects.
@Component
public class BirthApplicationRowMapper implements ResultSetExtractor<List<BirthRegistrationApplication>> {

    /**
     * Extracts data from the ResultSet and maps it to a list of BirthRegistrationApplication objects.
     *
     * @param rs The ResultSet containing the data to be mapped to BirthRegistrationApplication objects.
     * @return A list of BirthRegistrationApplication objects with the extracted data.
     * @throws SQLException if a database access error occurs.
     * @throws DataAccessException if there is any issue during data extraction.
     */
    public List<BirthRegistrationApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String,BirthRegistrationApplication> birthRegistrationApplicationMap = new LinkedHashMap<>();

        while (rs.next()){
            String uuid = rs.getString("bapplicationnumber");
            BirthRegistrationApplication birthRegistrationApplication = birthRegistrationApplicationMap.get(uuid);

            if(birthRegistrationApplication == null) {

                Long lastModifiedTime = rs.getLong("blastModifiedTime");
                if (rs.wasNull()) {
                    lastModifiedTime = null;
                }

                // Create User objects for father and mother using the UUIDs from the result set.
                User father = User.builder().uuid(rs.getString("bfatherid")).build();
                User mother = User.builder().uuid(rs.getString("bmotherid")).build();

                // Create AuditDetails object to store metadata about creation and last modification
                AuditDetails auditdetails = AuditDetails.builder()
                        .createdBy(rs.getString("bcreatedBy"))
                        .createdTime(rs.getLong("bcreatedTime"))
                        .lastModifiedBy(rs.getString("blastModifiedBy"))
                        .lastModifiedTime(lastModifiedTime)
                        .build();

                // Create workflow object for workflow related information
                Workflow workflow=Workflow.builder().action(rs.getString("waction"))
                        .comments(rs.getString("wcomment"))
                        .assignes(Arrays.asList())
                        .rating(rs.getInt("wrating"))
                        .build();

                // Create the BirthRegistrationApplication object using the extracted data.
                birthRegistrationApplication = BirthRegistrationApplication.builder()
                        .applicationNumber(rs.getString("bapplicationnumber"))
                        .tenantId(rs.getString("btenantid"))
                        .id(rs.getString("bid"))
                        .babyFirstName(rs.getString("bbabyfirstname"))
                        .babyLastName(rs.getString("bbabylastname"))
                        .father(father)
                        .mother(mother)
                        .doctorName(rs.getString("bdoctorname"))
                        .hospitalName(rs.getString("bhospitalname"))
                        .placeOfBirth(rs.getString("bplaceofbirth"))
                        .timeOfBirth(rs.getInt("btimeofbirth"))
                        .auditDetails(auditdetails)
                        .workflow(workflow)
                        .build();
            }
            
            addChildrenToProperty(rs, birthRegistrationApplication);
            birthRegistrationApplicationMap.put(uuid, birthRegistrationApplication);
        }
        return new ArrayList<>(birthRegistrationApplicationMap.values());
    }

    /**
     * Adds additional properties (e.g., address) to the BirthRegistrationApplication object.
     *
     * @param rs The ResultSet containing the data to be added.
     * @param birthRegistrationApplication The BirthRegistrationApplication object to enrich with additional properties.
     * @throws SQLException if a database access error occurs.
     */
    private void addChildrenToProperty(ResultSet rs, BirthRegistrationApplication birthRegistrationApplication)
            throws SQLException {
        addAddressToApplication(rs, birthRegistrationApplication);
    }

    //extract the address and associate it with the BirthRegistrationApplication.

    /**
     * Extracts address data from the ResultSet and associates it with the BirthRegistrationApplication object.
     *
     * @param rs The ResultSet containing address-related data.
     * @param birthRegistrationApplication The BirthRegistrationApplication object to associate the address with.
     * @throws SQLException if a database access error occurs.
     */
    private void addAddressToApplication(ResultSet rs, BirthRegistrationApplication birthRegistrationApplication) throws SQLException {
        Address address = Address.builder()
                .tenantId(rs.getString("atenantid"))
                .address(rs.getString("aaddress"))
                .city(rs.getString("acity"))
                .pinCode(rs.getString("apincode"))
                .build();

        BirthApplicationAddress birthApplicationAddress= BirthApplicationAddress.builder()
                .id(rs.getString("aid"))
                .tenantId(rs.getString("atenantid"))
                .applicantAddress(address)
                .build();

        // Set the created BirthApplicationAddress object to the BirthRegistrationApplication.
        birthRegistrationApplication.setAddress(birthApplicationAddress);

    }

}