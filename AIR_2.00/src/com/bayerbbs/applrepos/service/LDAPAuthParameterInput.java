package com.bayerbbs.applrepos.service;

public class LDAPAuthParameterInput {

	private String cwid;
	
	private String password;
	
	private String hiddenCwid;//changes for CR Kerboros Implementation C0000275214
	
	private String token;

	public String getCwid() {
		return cwid;
	}

	public void setCwid(String cwid) {
		this.cwid = cwid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getHiddenCwid() {
		return hiddenCwid;
	}

	public void setHiddenCwid(String hiddenCwid) {
		this.hiddenCwid = hiddenCwid;
	}

}
