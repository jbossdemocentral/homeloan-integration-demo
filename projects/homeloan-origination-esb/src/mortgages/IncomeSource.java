package mortgages;

public class IncomeSource {

	private int monthlyAmount;
	private boolean selfEmployed;

	/**
	 * @return the monthlyAmount
	 */
	public int getMonthlyAmount() {
		return monthlyAmount;
	}

	/**
	 * @param monthlyAmount
	 *            the monthlyAmount to set
	 */
	public void setMonthlyAmount(int monthlyAmount) {
		this.monthlyAmount = monthlyAmount;
	}

	/**
	 * @return the selfEmployed
	 */
	public boolean isSelfEmployed() {
		return selfEmployed;
	}

	/**
	 * @param selfEmployed
	 *            the selfEmployed to set
	 */
	public void setSelfEmployed(boolean selfEmployed) {
		this.selfEmployed = selfEmployed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": monthlyAmount="
				+ this.monthlyAmount + " selfEmployed=" + this.selfEmployed;
	}

}