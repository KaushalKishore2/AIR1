package com.bayerbbs.applrepos.service;

import com.bayerbbs.air.error.ErrorCodeManager;
import com.bayerbbs.applrepos.constants.AirKonstanten;

public class ApplicationEditParameterOutput {

	private String result;
	
	private String displayMessage;	// one message, that should be displayed to the user
	
	private String messages[];
	
	private Long applicationId;
	
	private Long ciId;
	
	private Integer tableId;
	
	public ApplicationEditParameterOutput() {
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String[] getMessages() {
		return messages;
	}

	public void setMessages(String[] messages) {
		this.messages = messages;
	}

	public String getDisplayMessage() {
		return displayMessage;
	}

	public void setDisplayMessage(String displayMessage) {
		this.displayMessage = displayMessage;
	}

	public Long getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}
	

	public void setErrorMessage(String code) {
		setErrorMessage(code, null);
	}

		
	public void setErrorMessage(String code, String replacement) {
		setResult(AirKonstanten.RESULT_ERROR);
		ErrorCodeManager errorCodeManager = new ErrorCodeManager();
		String detailedMessage = errorCodeManager.getErrorMessage(code, replacement);
		setMessages(new String[] { detailedMessage });		
	}

	/**
	 * @return the ciId
	 */
	public Long getCiId() {
		return ciId;
	}

	/**
	 * @param ciId the ciId to set
	 */
	public void setCiId(Long ciId) {
		this.ciId = ciId;
	}

	/**
	 * @return the tableId
	 */
	public Integer getTableId() {
		return tableId;
	}

	/**
	 * @param tableId the tableId to set
	 */
	public void setTableId(Integer tableId) {
		this.tableId = tableId;
	}

	
}
