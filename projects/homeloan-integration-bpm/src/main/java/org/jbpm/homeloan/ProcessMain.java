package org.jbpm.homeloan;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXB;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.homeloan.prequalification.ApplicationType;

/**
 * Before launching this process start file, it is assumed you have the BRMS server started and the BPEL project deployed.
 */
public class ProcessMain {
    public static final void main(final String[] args) throws Exception {
        // Load up the knowledge base from local(!) resources.
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("HomeLoan.bpmn2"), ResourceType.BPMN2);
        kbuilder.add(ResourceFactory.newClassPathResource("mortgages.drl"), ResourceType.DRL);
        final KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        // Register work items.
        ksession.getWorkItemManager().registerWorkItemHandler("CreditReportNode", new CreditReportNodeWorkItemHandler());

        // Read the input from the src/test/resources directory.
        final Map<String, Object> params = new HashMap<String, Object>();
        final ApplicationType application = JAXB.unmarshal(new File("src/test/resources/application.xml"), ApplicationType.class);
        params.put("application", application);

        // Start a new process instance.
        ksession.startProcess("mortgages.HomeLoan", params);
        ksession.fireAllRules();
    }
}
