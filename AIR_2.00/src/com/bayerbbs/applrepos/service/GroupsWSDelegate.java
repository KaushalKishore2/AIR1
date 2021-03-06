package com.bayerbbs.applrepos.service;

import com.bayerbbs.applrepos.dto.GroupsDTO;

@javax.jws.WebService(targetNamespace = "http://service.applrepos.bayerbbs.com/", serviceName = "GroupsWSService", portName = "GroupsWSPort")
public class GroupsWSDelegate {

	com.bayerbbs.applrepos.service.GroupsWS groupsWS = new com.bayerbbs.applrepos.service.GroupsWS();

//	public GroupsDTO[] getGroupsList() {
//		return groupsWS.getGroupsList();
//	}

	public GroupsDTO[] findGroups(GroupsParameterInput input) {
		return groupsWS.findGroups(input);
	}

}