package org.jbpm.homeloan;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import mortgages.Applicant;
import mortgages.IncomeSource;
import mortgages.LoanApplication;

import org.jbpm.homeloan.prequalification.ApplicationType;
import org.jbpm.homeloan.prequalification.BorrowerType;
import org.jbpm.homeloan.prequalification.EmploymentType;
import org.jbpm.homeloan.prequalification.ObjectFactory;
import org.jbpm.homeloan.prequalification.PreQualificationDecisionResponseType;

/**
 * Utility to convert between the model of the process and that of the rules.
 * <p>
 * The conversions here are exactly the same ones as in the smooks configuration of the homeloan-origination-esb project.
 */
public final class ModelConverter {
    /* From process to rules (smooks-config_xml2java.xml): */

    public static Applicant getApplicant(final ApplicationType application, final String creditScore) {
        final BorrowerType borrower = application.getBorrowers().getBorrower().get(0);
        Date dobDate = null;
        try {
            dobDate = new SimpleDateFormat("MM/dd/yyyy").parse(borrower.getDOB());
        } catch (final ParseException parseEx) {
            throw new IllegalArgumentException("Unable to determine DoB for applicant.", parseEx);
        }

        final Applicant applicant = new Applicant();
        applicant.setName(borrower.getLastName());
        applicant.setDob(dobDate);
        applicant.setCreditScore(Integer.parseInt(creditScore));
        return applicant;
    }

    public static LoanApplication getLoanApplication(final ApplicationType application) {
        final LoanApplication loanApplication = new LoanApplication();
        loanApplication.setAmount(application.getAmount().intValue());
        loanApplication.setApprovedRate(application.getInterestRate());
        loanApplication.setLengthYears(application.getNumberOfMonths().divide(BigInteger.valueOf(12)).intValue());
        loanApplication.setDeposit(application.getBorrowers().getAssetsLiabilities().getCashDeposits().getCashDeposit().get(0).getAmount().intValue());
        return loanApplication;
    }

    public static IncomeSource getIncomeSource(final ApplicationType application) {
        final BorrowerType borrower = application.getBorrowers().getBorrower().get(0);
        final EmploymentType employment = borrower.getEmploymentInformation().getEmployment().get(0);

        final IncomeSource incomeSource = new IncomeSource();
        incomeSource.setMonthlyAmount(employment.getMonthlyIncome().intValue());
        incomeSource.setSelfEmployed(employment.isIsSelfEmployed());
        return incomeSource;
    }

    /* From rules to process (smooks-config_java2xml.xml): */

    public static PreQualificationDecisionResponseType getPreQualificationDecisionResponse(final LoanApplication loanApplication) {
        final PreQualificationDecisionResponseType decisionResponse = new ObjectFactory().createPreQualificationDecisionResponseType();
        decisionResponse.setApproved(loanApplication.isApproved());
        decisionResponse.setApprovedRate(loanApplication.getApprovedRate().floatValue());
        decisionResponse.setExplanation(loanApplication.getExplanation());
        decisionResponse.setInsuranceCost(BigDecimal.valueOf(loanApplication.getInsuranceCost()));
        return decisionResponse;
    }
}
