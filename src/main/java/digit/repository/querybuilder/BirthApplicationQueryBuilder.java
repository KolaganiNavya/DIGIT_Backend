package digit.repository.querybuilder;

import digit.web.models.BirthApplicationSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

//this class constructs a SELECT query that includes filters for tenantID, status, application number based on input criteria
@Component
public class BirthApplicationQueryBuilder {

    //query to select birth registration fields
    private static final String BASE_BTR_QUERY = " SELECT btr.id as bid, btr.tenantid as btenantid, btr.applicationnumber as bapplicationnumber, btr.babyfirstname as bbabyfirstname, btr.babylastname as bbabylastname, btr.fatherid as bfatherid, btr.motherid as bmotherid, btr.doctorname as bdoctorname, btr.hospitalname as bhospitalname, btr.placeofbirth as bplaceofbirth, btr.timeofbirth as btimeofbirth, btr.createdby as bcreatedby, btr.lastmodifiedby as blastmodifiedby, btr.createdtime as bcreatedtime, btr.lastmodifiedtime as blastmodifiedtime, ";

    //query to select address and workflow fields
    private static final String ADDRESS_SELECT_QUERY = " add.id as aid, add.tenantid as atenantid, add.type as atype, add.address as aaddress, add.city as acity, add.pincode as apincode, add.registrationid as aregistrationid , wf.action as waction, wf.comment as wcomment, wf.assignee as wassignee, wf.rating as wrating ";

    //joins with registration, address and workflow tables
    private static final String FROM_TABLES = " FROM eg_bt_registration btr LEFT JOIN eg_bt_address add ON btr.id = add.registrationid left JOIN eg_wf_processinstance_v2 wf ON btr.applicationnumber = wf.businessid ";

    //ordering of result by creation time in descending order
    private final String ORDERBY_CREATEDTIME = " ORDER BY btr.createdtime DESC ";

    /**
     * It adds the necessary filters based on the provided criteria and prepares the SQL statement accordingly.
     *
     * @param criteria The search criteria containing tenantId, status, applicationNumber, and other filters.
     * @param preparedStmtList The list to store the prepared statement parameters.
     * @return The constructed SQL query string.
     */
    public String getBirthApplicationSearchQuery(BirthApplicationSearchCriteria criteria, List<Object> preparedStmtList){
        StringBuilder query = new StringBuilder(BASE_BTR_QUERY);
        query.append(ADDRESS_SELECT_QUERY);
        query.append(FROM_TABLES);

        if(!ObjectUtils.isEmpty(criteria.getTenantId())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" btr.tenantid = ? ");
            preparedStmtList.add(criteria.getTenantId());
        }
        if(!ObjectUtils.isEmpty(criteria.getStatus())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" btr.status = ? ");
            preparedStmtList.add(criteria.getStatus());
        }
        if(!CollectionUtils.isEmpty(criteria.getIds())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" btr.id IN ( ").append(createQuery(criteria.getIds())).append(" ) ");
            addToPreparedStatement(preparedStmtList, criteria.getIds());
        }
        if(!ObjectUtils.isEmpty(criteria.getApplicationNumber())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" btr.applicationnumber = ? ");
            preparedStmtList.add(criteria.getApplicationNumber());
        }
        // order birth registration applications based on their createdtime in latest first manner
        query.append(ORDERBY_CREATEDTIME);
        return query.toString();
    }

    /**
     * appends WHERE or AND based on whether the prepared statement is empty
     *
     * @param query the query being built
     * @param preparedStmtList The list of prepared statement parameters
     */
    private void addClauseIfRequired(StringBuilder query, List<Object> preparedStmtList){
        if(preparedStmtList.isEmpty()){
            query.append(" WHERE ");
        }else{
            query.append(" AND ");
        }
    }

    /**
     * build a string with placeholders for the IN clause based on the size of the IDs list.
     *
     * @param ids contains ids
     * @return String with placeholders
     */

    private String createQuery(List<String> ids) {
        StringBuilder builder = new StringBuilder();
        int length = ids.size();
        for (int i = 0; i < length; i++) {
            builder.append(" ?");
            if (i != length - 1)
                builder.append(",");
        }
        return builder.toString();
    }

    /**
     * add the IDs to the prepared statement list.
     *
     * @param preparedStmtList contains the prepared Statement
     * @param ids contains id that needs to be added.
     */
    private void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids) {
        ids.forEach(id -> {
            preparedStmtList.add(id);
        });
    }
}