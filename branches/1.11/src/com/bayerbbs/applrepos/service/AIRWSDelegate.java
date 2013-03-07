package com.bayerbbs.applrepos.service;

import com.bayerbbs.applrepos.dto.ItsecUserOptionDTO;
import com.bayerbbs.applrepos.dto.RolePersonDTO;

@javax.jws.WebService(targetNamespace = "http://service.applrepos.bayerbbs.com/", serviceName = "AIRWSService", portName = "AIRWSPort")
public class AIRWSDelegate {

	com.bayerbbs.applrepos.service.AIRWS aIRWS = new com.bayerbbs.applrepos.service.AIRWS();

	public ApplicationParamOutput findCIsByOrganisationUnit(
			ApplicationParameterInput anwParamInp) {
		return aIRWS.findCIsByOrganisationUnit(anwParamInp);
	}

	public ItsecUserOptionDTO[] getItsecUserOption(
			ItsecUserOptionParameter parameter) {
		return aIRWS.getItsecUserOption(parameter);
	}

	public RolePersonDTO[] getRolePerson(ItsecUserOptionParameter parameter) {
		return aIRWS.getRolePerson(parameter);
	}

	public RolePersonDTO[] getRolePersonBusinessEssentialEditor(
			ItsecUserOptionParameter parameter) {
		return aIRWS.getRolePersonBusinessEssentialEditor(parameter);
	}

	public ApplicationEditParameterOutput saveUserOption(
			UserOptionParameterInput editInput) {
		return aIRWS.saveUserOption(editInput);
	}

}