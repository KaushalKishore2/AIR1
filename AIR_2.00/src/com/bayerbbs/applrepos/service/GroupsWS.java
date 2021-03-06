package com.bayerbbs.applrepos.service;

import com.bayerbbs.applrepos.dto.GroupsDTO;
import com.bayerbbs.applrepos.hibernate.GroupHbn;

public class GroupsWS {

//	public GroupsDTO[] getGroupsList() {
//		return GroupHbn.getArrayFromList(GroupHbn.listGroupsHbn());
//	}

	public GroupsDTO[] findGroups(GroupsParameterInput input) {
		return GroupHbn.getArrayFromList(GroupHbn.findGroupsByName(input.getGroupName(),
				input.getImpactedBusinessGroup(),
				input.getChangeTeam(),
				input.getCiOwner(),
				input.getEscalationList(),
				input.getImplementationTeam(),
				input.getOwningBusinessGroup(),
				input.getServiceCoordinator(),
				input.getSupportGroupIMResolver(),
				input.getManagerCWID(),
				input.getFullLikeSearch(),
				input.getStart(),
				input.getLimit()));
	}
}
