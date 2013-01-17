package org.jbpm.homeloan;

import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.jbpm.homeloan.prequalification.ApplicationType;
import org.jbpm.homeloan.prequalification.ObjectFactory;
import org.jbpm.homeloan.prequalification.PreQualDecisionServicePortType;
import org.jbpm.homeloan.prequalification.PreQualDecisionServiceService;
import org.jbpm.homeloan.prequalification.PreQualificationDecisionRequestType;
import org.jbpm.homeloan.prequalification.PreQualificationDecisionResponseType;

/**
 * WorkItem handler for determining the prequalification.
 */
public class PrequalificationNodeWorkItemHandler implements WorkItemHandler {
    // This is the service endpoint on the ESB.
    static final String ENDPOINT_ADDRESS = "http://localhost:8080/homeloan-origination-esb/ebws/homeloan-origination-demo/PreQualDecisionService";

    /** {@inheritDoc} */
    @Override
    public void executeWorkItem(final WorkItem item, final WorkItemManager itemMgr) {
        // Get the input.
        final ApplicationType application = (ApplicationType) item.getParameter("application");
        final int creditScore = Integer.parseInt((String) item.getParameter("creditScore"));

        // Map to service input.
        final PreQualificationDecisionRequestType preQualDecisionRequest = new ObjectFactory().createPreQualificationDecisionRequestType();
        preQualDecisionRequest.setApplication(application);
        preQualDecisionRequest.setCreditScore(creditScore);

        // Call Web Service.
        final PreQualDecisionServicePortType port = new PreQualDecisionServiceService().getPreQualDecisionServicePortType();
        ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, ENDPOINT_ADDRESS);
        final PreQualificationDecisionResponseType preQualDecisionResponse = port.preQualDecisionServiceOp(preQualDecisionRequest);

        // Map from service output and signal that the work item is completed.
        final Map<String, Object> output = new HashMap<String, Object>();
        output.put("prequalResponse", preQualDecisionResponse);
        itemMgr.completeWorkItem(item.getId(), output);
    }

    /** {@inheritDoc} */
    @Override
    public void abortWorkItem(final WorkItem item, final WorkItemManager itemMgr) {
        System.err.println("Abort called on Prequalification Node");
    }
}
