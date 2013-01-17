package org.jbpm.homeloan;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.builder.ResourceType;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.homeloan.prequalification.ApplicationType;
import org.jbpm.homeloan.prequalification.ApplicationType.Borrowers;
import org.jbpm.homeloan.prequalification.AssetsLiabilitiesType;
import org.jbpm.homeloan.prequalification.AssetsLiabilitiesType.CashDeposits;
import org.jbpm.homeloan.prequalification.BorrowerType;
import org.jbpm.homeloan.prequalification.BorrowerType.EmploymentInformation;
import org.jbpm.homeloan.prequalification.CashDepositType;
import org.jbpm.homeloan.prequalification.EmploymentType;
import org.jbpm.homeloan.prequalification.ObjectFactory;
import org.jbpm.test.JbpmJUnitTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This is a sample file to test a process.
 */
public class ProcessTest extends JbpmJUnitTestCase {
    private static final boolean USE_RESOURCES_FROM_GUVNOR = false;
    private static final String GUVNOR_URL = "http://localhost:8080/jboss-brms";
    private static final String GUVNOR_USER_NAME = "admin";
    private static final String GUVNOR_PASSWORD = "admin";
    private static final String[] GUVNOR_PACKAGES = { "mortgages" };

    private static final String LOCAL_PROCESS_NAME = "HomeLoan.bpmn2";
    private static final String LOCAL_RULES_NAME = "mortgages.drl";

    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String DOB_ADULT = "01/01/1970";
    private static final String DOB_MINOR = "01/01/1999";
    private static final String SSN_HIGH_SCORE = "789-12-3456";
    private static final String SSN_LOW_SCORE = "567-89-1234";

    private static final BigDecimal AMOUNT_OK = BigDecimal.valueOf(150000);
    private static final BigDecimal AMOUNT_TOO_LOW = BigDecimal.valueOf(10000);
    private static final BigDecimal AMOUNT_TOO_HIGH = BigDecimal.valueOf(250000);
    private static final BigDecimal INTEREST_RATE = BigDecimal.valueOf(5.3);
    private static final BigInteger NUMBER_OF_MONTHS_20_YR = BigInteger.valueOf(20 * 12);
    private static final BigInteger NUMBER_OF_MONTHS_30_YR = BigInteger.valueOf(30 * 12);
    private static final BigInteger NUMBER_OF_MONTHS_INVALID = BigInteger.valueOf(25 * 12);
    private static final BigDecimal CASH_DEPOSIT_OK = BigDecimal.valueOf(1000);
    private static final BigDecimal CASH_DEPOSIT_TOO_HIGH = BigDecimal.valueOf(20000);

    private static final BigDecimal MONTHLY_INCOME = BigDecimal.valueOf(5000);

    private static StatefulKnowledgeSession ksession;

    @BeforeClass
    public static void setUpOnce() throws Exception {
        // Make sure the ESB services are running before starting the tests.
        boolean found = false;
        try {
            found = exists(CreditReportNodeWorkItemHandler.ENDPOINT_ADDRESS + "?wsdl");
            // && exists(PrequalificationNodeWorkItemHandler.ENDPOINT_ADDRESS + "?wsdl");
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        if (!found) {
            fail("ESB services not found! Please start them before running the tests.");
        }
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        // Set up the knowledge session with the process and handlers.
        KnowledgeBase kbase = null;
        if (USE_RESOURCES_FROM_GUVNOR) {
            kbase = createKnowledgeBaseGuvnor(false, GUVNOR_URL, GUVNOR_USER_NAME, GUVNOR_PASSWORD, GUVNOR_PACKAGES);
        } else {
            // Use the local files.
            final Map<String, ResourceType> resources = new HashMap<String, ResourceType>();
            resources.put(LOCAL_PROCESS_NAME, ResourceType.BPMN2);
            resources.put(LOCAL_RULES_NAME, ResourceType.DRL);
            kbase = createKnowledgeBase(resources);
        }
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("CreditReportNode", new CreditReportNodeWorkItemHandler());
        // ksession.getWorkItemManager().registerWorkItemHandler("PrequalificationNode", new PrequalificationNodeWorkItemHandler());
    }

    @Override
    @After
    public void tearDown() throws Exception {
        ksession.dispose();

        super.tearDown();
    }

    @Test
    public void approvedFor20yrs() {
        final Map<String, Object> parms = new HashMap<String, Object>();
        parms.put("application", getValidApplication());

        System.out.println("=================================================");
        System.out.println("= Starting Process Approved (20 yrs) Test Case. =");
        System.out.println("=================================================");
        final ProcessInstance processInstance = ksession.startProcess("mortgages.HomeLoan", parms);
        ksession.fireAllRules();

        // Check whether the process instance has completed successfully.
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "Read application", "Credit Report Node", "Initialise rules input", "Prequalification Rules",
                "Process rules output", "Communicate acceptance");
    }

    @Test
    public void approvedFor30yrs() {
        final ApplicationType application = getValidApplication();
        application.setNumberOfMonths(NUMBER_OF_MONTHS_30_YR);
        final Map<String, Object> parms = new HashMap<String, Object>();
        parms.put("application", application);

        System.out.println("=================================================");
        System.out.println("= Starting Process Approved (30 yrs) Test Case. =");
        System.out.println("=================================================");
        final ProcessInstance processInstance = ksession.startProcess("mortgages.HomeLoan", parms);
        ksession.fireAllRules();

        // Check whether the process instance has completed successfully.
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "Read application", "Credit Report Node", "Initialise rules input", "Prequalification Rules",
                "Process rules output", "Communicate acceptance");
    }

    @Test
    public void underAge() {
        final ApplicationType application = getValidApplication();
        application.getBorrowers().getBorrower().get(0).setDOB(DOB_MINOR);
        final Map<String, Object> parms = new HashMap<String, Object>();
        parms.put("application", application);

        System.out.println("====================================================");
        System.out.println("= Starting Process Rejected (under age) Test Case. =");
        System.out.println("====================================================");
        final ProcessInstance processInstance = ksession.startProcess("mortgages.HomeLoan", parms);
        ksession.fireAllRules();

        // Check whether the process instance has completed successfully.
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "Read application", "Credit Report Node", "Initialise rules input", "Prequalification Rules",
                "Process rules output", "Communicate rejection");
    }

    @Test
    public void badCreditCheck() {
        final ApplicationType application = getValidApplication();
        application.getBorrowers().getBorrower().get(0).setSSN(SSN_LOW_SCORE);
        final Map<String, Object> parms = new HashMap<String, Object>();
        parms.put("application", application);

        System.out.println("=====================================================");
        System.out.println("= Starting Process Rejected (bad credit) Test Case. =");
        System.out.println("=====================================================");
        final ProcessInstance processInstance = ksession.startProcess("mortgages.HomeLoan", parms);
        ksession.fireAllRules();

        // Check whether the process instance has completed successfully.
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "Read application", "Credit Report Node", "Initialise rules input", "Prequalification Rules",
                "Process rules output", "Communicate rejection");
    }

    @Test
    public void amountTooLow() {
        final ApplicationType application = getValidApplication();
        application.setAmount(AMOUNT_TOO_LOW);
        final Map<String, Object> parms = new HashMap<String, Object>();
        parms.put("application", application);

        System.out.println("=====================================================");
        System.out.println("= Starting Process Rejected (amount low) Test Case. =");
        System.out.println("=====================================================");
        final ProcessInstance processInstance = ksession.startProcess("mortgages.HomeLoan", parms);
        ksession.fireAllRules();

        // Check whether the process instance has completed successfully.
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "Read application", "Credit Report Node", "Initialise rules input", "Prequalification Rules",
                "Process rules output", "Communicate rejection");
    }

    @Test
    public void amountTooHigh() {
        final ApplicationType application = getValidApplication();
        application.setAmount(AMOUNT_TOO_HIGH);
        final Map<String, Object> parms = new HashMap<String, Object>();
        parms.put("application", application);

        System.out.println("======================================================");
        System.out.println("= Starting Process Rejected (amount high) Test Case. =");
        System.out.println("======================================================");
        final ProcessInstance processInstance = ksession.startProcess("mortgages.HomeLoan", parms);
        ksession.fireAllRules();

        // Check whether the process instance has completed successfully.
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "Read application", "Credit Report Node", "Initialise rules input", "Prequalification Rules",
                "Process rules output", "Communicate rejection");
    }

    @Test
    public void durationInvalid() {
        final ApplicationType application = getValidApplication();
        application.setNumberOfMonths(NUMBER_OF_MONTHS_INVALID);
        final Map<String, Object> parms = new HashMap<String, Object>();
        parms.put("application", application);

        System.out.println("===========================================================");
        System.out.println("= Starting Process Rejected (invalid duration) Test Case. =");
        System.out.println("===========================================================");
        final ProcessInstance processInstance = ksession.startProcess("mortgages.HomeLoan", parms);
        ksession.fireAllRules();

        // Check whether the process instance has completed successfully.
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "Read application", "Credit Report Node", "Initialise rules input", "Prequalification Rules",
                "Process rules output", "Communicate rejection");
    }

    @Test
    public void cashDepositTooHigh() {
        final ApplicationType application = getValidApplication();
        application.getBorrowers().getAssetsLiabilities().getCashDeposits().getCashDeposit().get(0).setAmount(CASH_DEPOSIT_TOO_HIGH);
        final Map<String, Object> parms = new HashMap<String, Object>();
        parms.put("application", application);

        System.out.println("=======================================================");
        System.out.println("= Starting Process Rejected (deposit high) Test Case. =");
        System.out.println("=======================================================");
        final ProcessInstance processInstance = ksession.startProcess("mortgages.HomeLoan", parms);
        ksession.fireAllRules();

        // Check whether the process instance has completed successfully.
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "Read application", "Credit Report Node", "Initialise rules input", "Prequalification Rules",
                "Process rules output", "Communicate rejection");
    }

    @Test
    public void emptyRequest() {
        final Map<String, Object> parms = new HashMap<String, Object>();

        System.out.println("========================================================");
        System.out.println("= Starting Process Approved (empty request) Test Case. =");
        System.out.println("========================================================");
        final ProcessInstance processInstance = ksession.startProcess("mortgages.HomeLoan", parms);
        ksession.fireAllRules();

        // Check whether the process instance has completed successfully.
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "Read application", "Credit Report Node", "Initialise rules input", "Prequalification Rules",
                "Process rules output", "Communicate acceptance");
    }

    /**
     * Check whether a given URL hosts a WSDL (or at least some other resource).
     * 
     * @param wsdlLocation
     *            The URL on which the WSDL is supposed to be hosted.
     * @return Whether the WSDL is hosted at the given URL.
     * @throws Exception
     *             If the given URL is not well-formed, no connection could be set up or no response code could be retrieved.
     */
    private static boolean exists(final String wsdlLocation) throws Exception {
        HttpURLConnection.setFollowRedirects(false);
        final HttpURLConnection connection = (HttpURLConnection) new URL(wsdlLocation).openConnection();
        return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
    }

    /**
     * Retrieve a loan application for testing purposes.
     * 
     * @return An application that leads to a positive decision outcome.
     */
    private static ApplicationType getValidApplication() {
        final ApplicationType application = new ObjectFactory().createApplicationType();
        final Borrowers borrowers = new ObjectFactory().createApplicationTypeBorrowers();
        application.setBorrowers(borrowers);
        final BorrowerType borrower = new ObjectFactory().createBorrowerType();
        borrowers.getBorrower().add(borrower);
        borrower.setFirstName(FIRST_NAME);
        borrower.setLastName(LAST_NAME);
        borrower.setDOB(DOB_ADULT);
        borrower.setSSN(SSN_HIGH_SCORE);

        application.setAmount(AMOUNT_OK);
        application.setInterestRate(INTEREST_RATE);
        application.setNumberOfMonths(NUMBER_OF_MONTHS_20_YR);

        final AssetsLiabilitiesType assetsLiabilities = new ObjectFactory().createAssetsLiabilitiesType();
        borrowers.setAssetsLiabilities(assetsLiabilities);
        final CashDeposits cashDeposits = new ObjectFactory().createAssetsLiabilitiesTypeCashDeposits();
        assetsLiabilities.setCashDeposits(cashDeposits);
        final CashDepositType cashDeposit = new ObjectFactory().createCashDepositType();
        cashDeposits.getCashDeposit().add(cashDeposit);
        cashDeposit.setAmount(CASH_DEPOSIT_OK);

        final EmploymentInformation employmentInformation = new ObjectFactory().createBorrowerTypeEmploymentInformation();
        borrower.setEmploymentInformation(employmentInformation);
        final EmploymentType employment = new ObjectFactory().createEmploymentType();
        employmentInformation.getEmployment().add(employment);
        employment.setMonthlyIncome(MONTHLY_INCOME);
        employment.setIsSelfEmployed(Boolean.FALSE);

        return application;
    }
}
