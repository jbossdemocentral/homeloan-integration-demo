package mortgages;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Applicant implements Serializable {
	private static final long serialVersionUID = 1L;

	private Date dob;
	private Date applicationDate;
	private int creditScore;
	private String name;
	private boolean approved;

	/**
	 * @return the calculated age from the DOB
	 */
	public int getAge() {

		Calendar dob = Calendar.getInstance();
		dob.setTime(this.getDob());
		Calendar now = Calendar.getInstance();

		int age = now.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

		// IF THE CURRENT MONTH IS LESS THAN THE DOB MONTH
		// THEN REDUCE THE DOB BY 1 AS THEY HAVE NOT HAD THEIR
		// BIRTHDAY YET THIS YEAR
		if (now.get(Calendar.MONTH) < dob.get(Calendar.MONTH)) {
			age = age - 1;
		}

		// IF THE MONTH IN THE DOB IS EQUAL TO THE CURRENT MONTH
		// THEN CHECK THE DAY TO FIND OUT IF THEY HAVE HAD THEIR
		// BIRTHDAY YET. IF THE CURRENT DAY IS LESS THAN THE DAY OF THE DOB
		// THEN REDUCE THE DOB BY 1 AS THEY HAVE NOT HAD THEIR
		// BIRTHDAY YET THIS YEAR
		if (now.get(Calendar.MONTH) == dob.get(Calendar.MONTH)
				&& now.get(Calendar.DATE) < dob.get(Calendar.DATE)) {
			age = age - 1;
		}

		// THE AGE VARIBALE WILL NOW CONTAIN THE CORRECT AGE
		// DERIVED FROMTHE GIVEN DOB

		return age;
	}

	/**
	 * @return the dob
	 */
	public Date getDob() {
		return dob;
	}

	/**
	 * @param dob
	 *            the dob to set
	 */
	public void setDob(Date dob) {
		this.dob = dob;
	}

	/**
	 * @return the applicationDate
	 */
	public Date getApplicationDate() {
		return applicationDate;
	}

	/**
	 * @param applicationDate
	 *            the applicationDate to set
	 */
	public void setApplicationDate(Date applicationDate) {
		this.applicationDate = applicationDate;
	}

	/**
	 * @return the creditScore
	 */
	public int getCreditScore() {
		return creditScore;
	}

	/**
	 * @param creditScore
	 *            the creditScore to set
	 */
	public void setCreditScore(int creditScore) {
		this.creditScore = creditScore;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": Name=" + this.getName()
				+ " Age=" + this.getAge() + " CreditScore=" + this.creditScore
				+ " ApplicationDate=" + this.applicationDate + " Approved=" + this.approved 
				+ " DOB=" + this.dob;
	}

}
