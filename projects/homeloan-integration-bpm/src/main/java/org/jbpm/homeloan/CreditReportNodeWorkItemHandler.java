package org.jbpm.homeloan;

import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.jbpm.homeloan.creditreport.CreditQuery;
import org.jbpm.homeloan.creditreport.CreditReport;
import org.jbpm.homeloan.creditreport.CreditReportPortType;
import org.jbpm.homeloan.creditreport.CreditReportService;
import org.jbpm.homeloan.creditreport.ObjectFactory;
import org.jbpm.homeloan.prequalification.ApplicationType;
import org.jbpm.homeloan.prequalification.BorrowerType;

/**
 * WorkItem handler for retrieval of a credit report.
 */
public class CreditReportNodeWorkItemHandler implements WorkItemHandler {
    // This is the service endpoint on the ESB.
    static final String ENDPOINT_ADDRESS = "http://localhost:8080/JBHomeLoans/CreditReport";

    /** {@inheritDoc} */
    @Override
    public void executeWorkItem(final WorkItem item, final WorkItemManager itemMgr) {
        // Get the input.
        final ApplicationType application = (ApplicationType) item.getParameter("application");
        final BorrowerType borrower = application.getBorrowers().getBorrower().get(0);

        // Map to service input.
        final CreditQuery creditQuery = new ObjectFactory().createCreditQuery();
        creditQuery.setFirstName(borrower.getFirstName());
        creditQuery.setLastName(borrower.getLastName());
        creditQuery.setDob(borrower.getDOB());
        creditQuery.setSsn(borrower.getSSN());

        // Call Web Service.
        final CreditReportPortType port = new CreditReportService().getCreditReportSoap();
        ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, ENDPOINT_ADDRESS);
        final CreditReport creditReport = port.creditReport(creditQuery);

        // Map from service output and signal that the work item is completed.
        final Map<String, Object> output = new HashMap<String, Object>();
        output.put("creditScore", creditReport.getScore());
        itemMgr.completeWorkItem(item.getId(), output);
    }

    /** {@inheritDoc} */
    @Override
    public void abortWorkItem(final WorkItem item, final WorkItemManager itemMgr) {
        System.err.println("Abort called on Credit Report Node");
    }
}
