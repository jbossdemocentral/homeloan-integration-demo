package mortgages;

import java.io.Serializable;
import java.math.BigDecimal;

public class LoanApplication implements Serializable {
	private static final long serialVersionUID = 1L;

	private int amount;
	private boolean approved;
	private int deposit;
	private BigDecimal approvedRate;
	private int lengthYears;
	private String explanation;
	private int insuranceCost;

	public LoanApplication() {
		this.setApproved(false);
	}

	/**
	 * @return the amount
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/**
	 * @return the approved
	 */
	public boolean isApproved() {
		return approved;
	}

	/**
	 * @param approved
	 *            the approved to set
	 */
	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	/**
	 * @return the deposit
	 */
	public int getDeposit() {
		return deposit;
	}

	/**
	 * @param deposit
	 *            the deposit to set
	 */
	public void setDeposit(int deposit) {
		this.deposit = deposit;
	}

	/**
	 * @return the approvedRate
	 */
	public BigDecimal getApprovedRate() {
		return approvedRate;
	}

	/**
	 * @param approvedRate
	 *            the approvedRate to set
	 */
	public void setApprovedRate(BigDecimal approvedRate) {
		this.approvedRate = approvedRate;
	}

	/**
	 * @return the lengthYears
	 */
	public int getLengthYears() {
		return lengthYears;
	}

	/**
	 * @param lengthYears
	 *            the lengthYears to set
	 */
	public void setLengthYears(int lengthYears) {
		this.lengthYears = lengthYears;
	}

	/**
	 * @return the explanation
	 */
	public String getExplanation() {
		return explanation;
	}

	/**
	 * @param explanation
	 *            the explanation to set
	 */
	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	/**
	 * @return the insuranceCost
	 */
	public int getInsuranceCost() {
		return insuranceCost;
	}

	/**
	 * @param insuranceCost
	 *            the insuranceCost to set
	 */
	public void setInsuranceCost(int insuranceCost) {
		this.insuranceCost = insuranceCost;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": Approved=" + this.approved
				+ " Explanation=" + this.explanation + " Amount=" + this.amount
				+ " Deposit=" + this.deposit + " ApprovedRate="
				+ this.approvedRate + " lenghtYears=" + this.lengthYears
				+ " InsuranceCost=" + this.insuranceCost;
	}
}
