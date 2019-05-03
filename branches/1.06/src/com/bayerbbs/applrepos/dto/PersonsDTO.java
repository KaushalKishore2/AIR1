package com.bayerbbs.applrepos.dto;

public class PersonsDTO {

	/**
	 * The DTO (Data transfer object) for the database table "PERSONS"
	 * 
	 * @author evafl
	 *
	 */
	
	private Long personId;
	private String cwid;
	private String persNr;
	private String lastname;
	private String firstname;
	
	public Long getPersonId() {
		return personId;
	}
	public void setPersonId(Long personId) {
		this.personId = personId;
	}
	public String getCwid() {
		return cwid;
	}
	public void setCwid(String cwid) {
		this.cwid = cwid;
	}
	public String getPersNr() {
		return persNr;
	}
	public void setPersNr(String persNr) {
		this.persNr = persNr;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	
	
	/**
	 * returns the full name with cwid for display usage
	 * 
	 * Hasenclever, Juergen (HAS)
	 * 
	 * @return
	 */
	public String getDisplayNameFull() {
		StringBuffer sb = new StringBuffer();

		if (null != getLastname()) {
			sb.append(getLastname());
		}

		
		if (null != getFirstname()) {
			if (0 != sb.length()) {
				sb.append(", ");
			}
			sb.append(getFirstname());
		}

		
		if (null != getCwid()) {
			if (0 != sb.length()) {
				sb.append(" ");
			}
			
			sb.append("(");
			sb.append(getCwid());
			sb.append(")");
		}
			
		return sb.toString();
	}
	
}
