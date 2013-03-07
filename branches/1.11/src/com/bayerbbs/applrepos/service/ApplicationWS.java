package com.bayerbbs.applrepos.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.bayerbbs.applrepos.common.StringUtils;
import com.bayerbbs.applrepos.constants.ApplreposConstants;
import com.bayerbbs.applrepos.domain.Application;
import com.bayerbbs.applrepos.domain.ApplicationRegion;
import com.bayerbbs.applrepos.dto.ApplicationAccessDTO;
import com.bayerbbs.applrepos.dto.ApplicationContact;
import com.bayerbbs.applrepos.dto.ApplicationContactEntryDTO;
import com.bayerbbs.applrepos.dto.ApplicationContactGroupDTO;
import com.bayerbbs.applrepos.dto.ApplicationContactsDTO;
import com.bayerbbs.applrepos.dto.ApplicationDTO;
import com.bayerbbs.applrepos.dto.CiSupportStuffDTO;
import com.bayerbbs.applrepos.dto.ComplianceControlStatusDTO;
import com.bayerbbs.applrepos.dto.HistoryViewDataDTO;
import com.bayerbbs.applrepos.dto.InterfacesDTO;
import com.bayerbbs.applrepos.dto.PersonsDTO;
import com.bayerbbs.applrepos.dto.ViewDataDTO;
import com.bayerbbs.applrepos.hibernate.AnwendungHbn;
import com.bayerbbs.applrepos.hibernate.ApplReposHbn;
import com.bayerbbs.applrepos.hibernate.ApplicationProcessHbn;
import com.bayerbbs.applrepos.hibernate.ApplicationRegionHbn;
import com.bayerbbs.applrepos.hibernate.CiEntitiesHbn;
import com.bayerbbs.applrepos.hibernate.CiGroupsHbn;
import com.bayerbbs.applrepos.hibernate.CiPersonsHbn;
import com.bayerbbs.applrepos.hibernate.CiSupportStuffHbn;
import com.bayerbbs.applrepos.hibernate.GroupHbn;
import com.bayerbbs.applrepos.hibernate.InterfacesHbn;
import com.bayerbbs.applrepos.hibernate.PersonsHbn;

public class ApplicationWS {

	// request functions
	private static final String MY_CIS_SUBSTITUTE = "myCisSubstitute";
	private static final String MY_CIS = "myCis";
	private static final String MY_CIS_FOR_DELETE = "myCisForDelete";

	/**
	 * checks if the name (or alias) is already in usage. Return the count of
	 * entries. This function does not check deleted items.
	 * 
	 * @param input
	 * @return
	 */
	public ApplicationParamOutput checkAllowedApplicationName(ApplicationParameterInput input) {
		String searchname = input.getQuery();
		List<ApplicationDTO> listAnwendungen = null;
		listAnwendungen = CiEntitiesHbn.findExistantCisByNameOrAlias(searchname, false);
		ApplicationParamOutput output = new ApplicationParamOutput();
		output.setCountResultSet(listAnwendungen.size());
		
		if (0 != listAnwendungen.size()) {
			output.setInformationText(listAnwendungen.get(0).getApplicationCat1Txt());
		}
		
		return output;
	}


	public ApplicationParamOutput findApplications(ApplicationParameterInput input) {
		String searchname = input.getQuery();
		Long startwert = input.getStart();
		Long limit = input.getLimit();

		String cwid = input.getCwid();
		String searchAction = input.getSearchAction();
		
		String ciType = input.getCiType();
		String ouUnit = input.getOuUnit(); // "BBS-ITO-SOL-BPS-SEeB"
		String ciOwnerType = input.getCiOwnerType(); // "APP", "CI" oder "ALL"
		String ouQueryMode = input.getOuQueryMode(); //  "EXAKT" oder "START"

		List<ApplicationDTO> listAnwendungen = null;

		if (LDAPAuthWS.isLoginValid(input.getCwid(), input.getToken())) {
			if (null == startwert) {
				startwert = 0L;
			}
			if (null == limit) {
				limit = 20L;
			}
			
			boolean onlyApplications = false;
			if (null != input.getOnlyapplications()
					&& ApplreposConstants.STRING_TRUE.equals(input.getOnlyapplications())) {
				onlyApplications = true;
			}
			
			if (null == input.getSort()) {
				// default sort
				input.setSort("applicationName");
			}

			if (null != ouUnit && !"".equals(ouUnit)) {
				// advanced search by ou
				ouUnit = GroupHbn.getCleanedGroupname(ouUnit);
				if ("BEGINS_WITH".equals(ouQueryMode)) {
					ouQueryMode = "START";
				}
				
				listAnwendungen = CiEntitiesHbn.findCisByOUunit(ciType, ouUnit, ciOwnerType, ouQueryMode);
			}
			else if (MY_CIS.equals(searchAction)) {
				if (StringUtils.isNotNullOrEmpty(cwid)) {
					listAnwendungen = CiEntitiesHbn.findMyCisOwner(cwid, input.getSort(), input.getDir(), onlyApplications);
				}
			} else if (MY_CIS_SUBSTITUTE.equals(searchAction)) {
				if (StringUtils.isNotNullOrEmpty(cwid)) {
					listAnwendungen = CiEntitiesHbn.findMyCisDelegate(cwid, input.getSort(), input.getDir(), onlyApplications);
				}
			} else if (MY_CIS_FOR_DELETE.equals(searchAction)) {
				if (StringUtils.isNotNullOrEmpty(cwid)) {
					listAnwendungen = CiEntitiesHbn.findMyCisForDelete(cwid, input.getSort(), input.getDir(), onlyApplications);
				}
			} else {
				if (ApplreposConstants.STRING_TRUE.equals(input.getAdvancedsearch())) {
					listAnwendungen = AnwendungHbn.findApplications(searchname, input.getQueryMode(),
						input.getAdvsearchappowner(), input.getAdvsearchappownerHidden(), input.getAdvsearchappdelegate(), input.getAdvsearchappdelegateHidden(),
						input.getAdvsearchciowner(), input.getAdvsearchciownerHidden(), input.getAdvsearchcidelegate(), input.getAdvsearchcidelegateHidden(), 
						onlyApplications, input.getAdvsearchObjectTypeId(), input.getSort(), input.getDir(),
						input.getAdvsearchcitypeid(), input.getAdvsearchdescription(),
						input.getAdvsearchoperationalstatusid(), input.getAdvsearchapplicationcat2id(),
						input.getAdvsearchlifecyclestatusid(), input.getAdvsearchprocessid(), input.getTemplate(), 
						input.getAdvsearchsteward(), input.getAdvsearchstewardHidden(), input.getBarRelevance(), input.getOrganisationalScope(),
						input.getItSetId(), input.getItSecGroupId(), input.getSource(), input.getBusinessEssentialId(),
						input.getCiTypeOptions(),input.getItSetOptions(), input.getDescriptionOptions(),
						input.getAppOwnerOptions(), input.getAppOwnerDelegateOptions(), input.getAppStewardOptions(),
						input.getCiOwnerOptions(), input.getCiOwnerDelegateOptions(),
						input.getGeneralUsageOptions(), input.getItCategoryOptions(), input.getLifecycleStatusOptions(),
						input.getOrganisationalScopeOptions(), input.getItSecGroupOptions(),
						input.getProcessOptions(), input.getSourceOptions(), input.getBusinessEssentialOptions()
					);
				} else {
					listAnwendungen = CiEntitiesHbn.findCisByNameOrAlias(searchname, input.getQueryMode(),
							onlyApplications, input.getSort(), input.getDir());
				}
			}
		}

		if (null == listAnwendungen) {
			listAnwendungen = new ArrayList<ApplicationDTO>();
		}

		ApplicationDTO anwendungen[] = null;

		if (listAnwendungen.size() > (startwert)) {
			List<ApplicationDTO> listAnwTemp = new ArrayList<ApplicationDTO>();
			long tempCounter = startwert;
			long anzCounter = 0;
			while (tempCounter < listAnwendungen.size() && anzCounter < limit) {
				listAnwTemp.add(listAnwendungen.get((int) tempCounter));
				tempCounter++;
				anzCounter++;
			}

			// weniger Anwendungen als erwartet
			anwendungen = new ApplicationDTO[listAnwTemp.size()];

			int i = 0;
			for (final ApplicationDTO anw : listAnwTemp) {
				anwendungen[i] = anw;
				i++;
			}

		} else {
			// weniger Anwendungen als erwartet
			anwendungen = new ApplicationDTO[listAnwendungen.size()];

			int i = 0;
			for (final ApplicationDTO anw : listAnwendungen) {
				anwendungen[i] = anw;
				i++;
			}
		}

		ApplicationParamOutput anwendungParamOut = new ApplicationParamOutput();
		anwendungParamOut.setCountResultSet(listAnwendungen.size());
		anwendungParamOut.setApplicationDTO(anwendungen);

		return anwendungParamOut;
	}

	public ApplicationEditParameterOutput saveApplication(ApplicationEditParameterInput editInput) {
		ApplicationEditParameterOutput output = new ApplicationEditParameterOutput();

		if (null != editInput) {
			ApplicationDTO dto = getApplicationDTOFromEditInput(editInput);
			output = AnwendungHbn.saveAnwendung(editInput.getCwid(), dto);

			if (!ApplreposConstants.RESULT_ERROR.equals(output.getResult())) {
				try {
					// TODO DEBUG Test SupportStuff
					CiSupportStuffHbn.saveCiSupportStuffAll(editInput.getCwid(), dto.getId(),
							dto.getCiSupportStuffUserAuthorizationSupportedByDocumentation(),
							dto.getCiSupportStuffUserAuthorizationProcess(),
							dto.getCiSupportStuffChangeManagementSupportedByTool(),
							dto.getCiSupportStuffUserManagementProcess(),

							dto.getCiSupportStuffApplicationDocumentation(), dto.getCiSupportStuffRootDirectory(),
							dto.getCiSupportStuffDataDirectory(), dto.getCiSupportStuffProvidedServices(),
							dto.getCiSupportStuffProvidedMachineUsers());

					
					if (null != dto.getLicenseUsingRegions()) {
						ApplicationRegionHbn.saveApplicationRegionsAll(editInput.getCwid(), dto.getId(), dto.getLicenseUsingRegions());
					}

					ApplicationProcessHbn.saveApplicationProcessAll(editInput.getCwid(), dto.getId(), dto.getBusinessProcessHidden());

					for (String[] grouptype : ApplreposConstants.GPSCGROUP_MAPPING) {
						char d[] = grouptype[1].toCharArray();
						d[0] = String.valueOf(d[0]).toUpperCase().charAt(0);
						String method = "get" + new String(d);
						String gpscContact = (String) ApplicationDTO.class.getMethod(method).invoke(dto);
						String methodHidden = "get" + new String(d) + ApplreposConstants.GPSCGROUP_HIDDEN_DESCRIPTOR;
						String gpscContactHidden = (String) ApplicationDTO.class.getMethod(methodHidden).invoke(dto);
						if (!(ApplreposConstants.GPSCGROUP_DISABLED_MARKER.equals(gpscContact)) && !(ApplreposConstants.GPSCGROUP_DISABLED_MARKER.equals(gpscContactHidden))) {
							if ("Y".equals(grouptype[2])) { // Individual Contact(s)
								CiPersonsHbn.saveCiPerson(editInput.getCwid(),
										 dto.getId(), new Long(grouptype[0]), grouptype[3],
										 gpscContactHidden);
							} else { // Group(s)
								CiGroupsHbn.saveCiGroup(editInput.getCwid(),
										 dto.getId(), new Long(grouptype[0]), grouptype[3],
										 GroupHbn.getCleanedGroupname(gpscContact));
							}
						}
					}

					
					if(dto.getUpStreamAdd() != null && dto.getUpStreamAdd().length() > 0 || dto.getUpStreamDelete() != null && dto.getUpStreamDelete().length() > 0)
						CiEntitiesHbn.saveCiRelations(editInput.getTableId(), dto.getId(), dto.getUpStreamAdd(), dto.getUpStreamDelete(), "UPSTREAM", editInput.getCwid());
					
					if(dto.getDownStreamAdd() != null && dto.getDownStreamAdd().length() > 0 || dto.getDownStreamDelete() != null && dto.getDownStreamDelete().length() > 0)
						CiEntitiesHbn.saveCiRelations(editInput.getTableId(), dto.getId(), dto.getDownStreamAdd(), dto.getDownStreamDelete(), "DOWNSTREAM", editInput.getCwid());
					
					
					// Connection higher/lower
//					String cwid = editInput.getCwid();
//					Long applicationId = dto.getApplicationId();
//
//					ApplicationApplicationHbn.saveApplicationApplicationAll(cwid, null, applicationId, dto.getUpStreamAdd(), "ADD");
//					ApplicationApplicationHbn.saveApplicationApplicationAll(cwid, null, applicationId, dto.getUpStreamDelete(), "DELETE");
//					ApplicationApplicationHbn.saveApplicationApplicationAll(cwid, applicationId, null, dto.getDownStreamAdd(), "ADD");
//					ApplicationApplicationHbn.saveApplicationApplicationAll(cwid, applicationId, null, dto.getDownStreamDelete(), "DELETE");
					
					// TODO Connections ITSystem
					// Die hei�t pck_Sync_Tools.Support_Anwend_IT_System
					
					// ==========================================================================
					// FUNCTION Support_Anwend_IT_System (pin_APP_ID IN NUMBER,
					//	                                   pin_SPL_ID IN NUMBER,
					//	                                   piv_Quelle IN VARCHAR2,
					//	                                   piv_User IN VARCHAR2) RETURN BOOLEAN;
					//	==========================================================================




				} catch (Exception e) {
					// TODO: handle exception
					System.out.println(e.toString());
				}
			}
		}

		return output;
	}

	public ApplicationEditParameterOutput createApplication(ApplicationEditParameterInput editInput) {
		ApplicationEditParameterOutput output = new ApplicationEditParameterOutput();

		if (null != editInput) {
			ApplicationDTO dto = getApplicationDTOFromEditInput(editInput);
			
			// create Application - fill attributes
			if (null == dto.getCiOwner() || dto.getCiOwner().length() == 0) {//getResponsible
				dto.setCiOwner(editInput.getCwid().toUpperCase());//oder dto.getApplicationOwner() ?
				dto.setCiOwnerHidden(editInput.getCwid().toUpperCase());//oder dto.getApplicationOwnerHidden() ?
			}
			if (null == dto.getBusinessEssentialId()) {
				dto.setBusinessEssentialId(ApplreposConstants.BUSINESS_ESSENTIAL_DEFAULT);
			}

			// save / create application
			output = AnwendungHbn.createAnwendung(editInput.getCwid(), dto, editInput.getForceOverride());

			if (ApplreposConstants.RESULT_OK.equals(output.getResult())) {
				// get detail
				List<Application> listAnwendung = AnwendungHbn.findApplicationByName(editInput.getName());
				if (null != listAnwendung && 1 == listAnwendung.size()) {
					output.setApplicationId(listAnwendung.get(0).getApplicationId());
					
					Long ciId = listAnwendung.get(0).getApplicationId();
					
					//--- neu seit Wizard RFC 8271 - required Attributes
					
					if (null != editInput.getGpsccontactSupportGroupHidden()) {
						CiGroupsHbn.saveCiGroup(editInput.getCwid(),
								ciId, new Long(1), "SUPPORT GROUP - IM RESOLVER",
										dto.getGpsccontactSupportGroup());//getGpsccontactSupportGroupHidden
					}
					if (null != editInput.getGpsccontactOwningBusinessGroupHidden()) {
						CiGroupsHbn.saveCiGroup(editInput.getCwid(),
								ciId, new Long(6), "OWNING BUSINESS GROUP",
								dto.getGpsccontactOwningBusinessGroup());//getGpsccontactOwningBusinessGroupHidden
					}
					
					if (null != dto.getLicenseUsingRegions()) {
						ApplicationRegionHbn.saveApplicationRegionsAll(editInput.getCwid(), ciId, dto.getLicenseUsingRegions());
					}

					ApplicationProcessHbn.saveApplicationProcessAll(editInput.getCwid(), ciId, dto.getBusinessProcessHidden());

				} else {
					// unknown?
					output.setApplicationId(new Long(-1));
				}
			} else {
				// TODO errorcodes / Texte
				if (null != output.getMessages() && output.getMessages().length > 0) {
					output.setDisplayMessage(output.getMessages()[0]);
				}
			}
		}

		return output;
	}

	public ApplicationEditParameterOutput deleteApplication(ApplicationEditParameterInput editInput) {

		ApplicationEditParameterOutput output = new ApplicationEditParameterOutput();

		if (null != editInput) {

			if (LDAPAuthWS.isLoginValid(editInput.getCwid(), editInput.getToken())) {
				ApplicationDTO dto = getApplicationDTOFromEditInput(editInput);

				AnwendungHbn.deleteApplicationApplication(editInput.getCwid(), dto.getId());
				AnwendungHbn.deleteApplicationItSystem(editInput.getCwid(), dto.getId());
				
				output = AnwendungHbn.deleteAnwendung(editInput.getCwid(), dto);
			} else {
				// TODO MESSAGE LOGGED OUT
			}
		}

		return output;

	}

	/**
	 * converts the editInput to the dto
	 * 
	 * @param editInput
	 * @return
	 */
	private ApplicationDTO getApplicationDTOFromEditInput(ApplicationEditParameterInput editInput) {
		ApplicationDTO dto = new ApplicationDTO();
		dto.setId(editInput.getId());

		// Basics
		dto.setName(editInput.getName());
		dto.setAlias(editInput.getAlias());
		dto.setVersion(editInput.getVersion());
		dto.setApplicationCat2Id(editInput.getApplicationCat2Id());
		// view - primary function
		dto.setLifecycleStatusId(editInput.getLifecycleStatusId());
		dto.setOperationalStatusId(editInput.getOperationalStatusId());
		dto.setComments(editInput.getComments());
		dto.setBarRelevance(editInput.getBarRelevance());
		// TODO business category

		// Agreements
		dto.setSlaId(editInput.getSlaName());
		dto.setPriorityLevelId(editInput.getPriorityLevel());
		dto.setServiceContractId(editInput.getServiceContract());
		dto.setSeverityLevelId(editInput.getSeverityLevel());
		dto.setBusinessEssentialId(editInput.getBusinessEssentialId());

//		dto.setClusterCode(editInput.getClusterCode());
//		dto.setClusterType(editInput.getClusterType());

		// contacts
		dto.setCiOwner(editInput.getCiOwner());//setResponsible
		dto.setCiOwnerDelegate(editInput.getCiOwnerDelegate());//setSubResponsible

		dto.setCiOwnerHidden(editInput.getCiOwnerHidden());
		dto.setCiOwnerDelegateHidden(editInput.getCiOwnerDelegateHidden());

		dto.setApplicationOwner(editInput.getApplicationOwner());
		dto.setApplicationSteward(editInput.getApplicationSteward());
		dto.setApplicationOwnerDelegate(editInput.getApplicationOwnerDelegate());

		dto.setApplicationOwnerHidden(editInput.getApplicationOwnerHidden());
		dto.setApplicationStewardHidden(editInput.getApplicationStewardHidden());
		dto.setApplicationOwnerDelegateHidden(editInput.getApplicationOwnerDelegateHidden());
		
		dto.setGpsccontactSupportGroupHidden(editInput.getGpsccontactSupportGroupHidden());
		dto.setGpsccontactChangeTeamHidden(editInput.getGpsccontactChangeTeamHidden());
		dto.setGpsccontactServiceCoordinatorHidden(editInput.getGpsccontactServiceCoordinatorHidden());
		dto.setGpsccontactEscalationHidden(editInput.getGpsccontactEscalationHidden());
		dto.setGpsccontactCiOwnerHidden(editInput.getGpsccontactCiOwnerHidden());
		dto.setGpsccontactOwningBusinessGroupHidden(editInput.getGpsccontactOwningBusinessGroupHidden());
		dto.setGpsccontactImplementationTeamHidden(editInput.getGpsccontactImplementationTeamHidden());
		dto.setGpsccontactServiceCoordinatorIndivHidden(editInput.getGpsccontactServiceCoordinatorIndivHidden());
		dto.setGpsccontactEscalationIndivHidden(editInput.getGpsccontactEscalationIndivHidden());
		dto.setGpsccontactResponsibleAtCustomerSideHidden(editInput.getGpsccontactResponsibleAtCustomerSideHidden());
		dto.setGpsccontactSystemResponsibleHidden(editInput.getGpsccontactSystemResponsibleHidden());
		dto.setGpsccontactImpactedBusinessHidden(editInput.getGpsccontactImpactedBusinessHidden()); 
		dto.setGpsccontactBusinessOwnerRepresentativeHidden(editInput.getGpsccontactBusinessOwnerRepresentativeHidden());

		dto.setGpsccontactSupportGroup(editInput.getGpsccontactSupportGroup());
		dto.setGpsccontactChangeTeam(editInput.getGpsccontactChangeTeam());
		dto.setGpsccontactServiceCoordinator(editInput.getGpsccontactServiceCoordinator());
		dto.setGpsccontactEscalation(editInput.getGpsccontactEscalation());
		dto.setGpsccontactCiOwner(editInput.getGpsccontactCiOwner());
		dto.setGpsccontactOwningBusinessGroup(editInput.getGpsccontactOwningBusinessGroup());
		dto.setGpsccontactImplementationTeam(editInput.getGpsccontactImplementationTeam());
		dto.setGpsccontactServiceCoordinatorIndiv(editInput.getGpsccontactServiceCoordinatorIndiv());
		dto.setGpsccontactEscalationIndiv(editInput.getGpsccontactEscalationIndiv());
		dto.setGpsccontactResponsibleAtCustomerSide(editInput.getGpsccontactResponsibleAtCustomerSide());
		dto.setGpsccontactSystemResponsible(editInput.getGpsccontactSystemResponsible());
		dto.setGpsccontactImpactedBusiness(editInput.getGpsccontactImpactedBusiness()); 
		dto.setGpsccontactBusinessOwnerRepresentative(editInput.getGpsccontactBusinessOwnerRepresentative());
		
		// compliance
		dto.setItset(editInput.getItset());
		dto.setTemplate(editInput.getTemplate());
		dto.setItsecGroupId(editInput.getItSecGroupId());
		dto.setRefId(editInput.getRefId());
		dto.setRelevanceICS(editInput.getRelevanceICS());
		dto.setRelevanzItsec(editInput.getRelevanzItsec());
		dto.setGxpFlagId(editInput.getGxpFlag());
		dto.setGxpFlagTxt(editInput.getGxpFlag());
//		dto.setRiskAnalysisYN(editInput.getRiskAnalysisYN());
		// TODO ITSEC Group
		// ---

		dto.setLicenseTypeId(editInput.getLicenseTypeId());
		dto.setDedicated(editInput.getDedicated());
		dto.setAccessingUserCount(editInput.getAccessingUserCount());
		dto.setAccessingUserCountMeasured(editInput.getAccessingUserCountMeasured());
		dto.setLoadClass(editInput.getLoadClass());
		dto.setServiceModel(editInput.getServiceModel());
		dto.setOrganisationalScope(editInput.getOrganisationalScope());

		dto.setCostRunPa(editInput.getCostRunPa());
		dto.setCostChangePa(editInput.getCostChangePa());
		dto.setCurrencyId(editInput.getCurrencyId());
		dto.setCostRunAccountId(editInput.getCostRunAccountId());
		dto.setCostChangeAccountId(editInput.getCostChangeAccountId());
		dto.setLicenseUsingRegions(editInput.getLicenseUsingRegions());

		dto.setCiSupportStuffUserAuthorizationSupportedByDocumentation(editInput.getCiSupportStuffUserAuthorizationSupportedByDocumentation());
		dto.setCiSupportStuffUserAuthorizationProcess(editInput.getCiSupportStuffUserAuthorizationProcess());
		dto.setCiSupportStuffChangeManagementSupportedByTool(editInput.getCiSupportStuffChangeManagementSupportedByTool());
		dto.setCiSupportStuffUserManagementProcess(editInput.getCiSupportStuffUserManagementProcess());
		dto.setCiSupportStuffApplicationDocumentation(editInput.getCiSupportStuffApplicationDocumentation());
		dto.setCiSupportStuffRootDirectory(editInput.getCiSupportStuffRootDirectory());
		dto.setCiSupportStuffDataDirectory(editInput.getCiSupportStuffDataDirectory());
		dto.setCiSupportStuffProvidedServices(editInput.getCiSupportStuffProvidedServices());
		dto.setCiSupportStuffProvidedMachineUsers(editInput.getCiSupportStuffProvidedMachineUsers());

		dto.setItSecSbAvailabilityId(editInput.getItSecSbAvailability());
		dto.setItSecSbAvailabilityDescription(editInput.getItSecSbAvailabilityDescription());

		dto.setCategoryBusinessId(editInput.getCategoryBusinessId());
		
		dto.setClassDataId(editInput.getClassDataId());
		dto.setClassInformationId(editInput.getClassInformationId());
		dto.setClassInformationExplanation(editInput.getClassInformationExplanation());

		// business Process
		dto.setBusinessProcess(editInput.getBusinessProcess());
		dto.setBusinessProcessHidden(editInput.getBusinessProcessHidden());
		
		// compliance request
		dto.setRelevanceGR1435(editInput.getRelevanceGR1435());
		dto.setRelevanceGR2059(editInput.getRelevanceGR2059());
		dto.setRelevanceGR1920(editInput.getRelevanceGR1920());
		dto.setRelevanceGR2008(editInput.getRelevanceGR2008());
		
		// connections
		dto.setUpStreamAdd(editInput.getUpStreamAdd());
		dto.setUpStreamDelete(editInput.getUpStreamDelete());
		dto.setDownStreamAdd(editInput.getDownStreamAdd());
		dto.setDownStreamDelete(editInput.getDownStreamDelete());
		
		return dto;
	}

	/**
	 * delivers the application upstream information
	 * 
	 * @param detailInput
	 * @return
	 */
	public ApplicationViewdataOutput getApplicationUpstream(ApplicationDetailParameterInput detailInput) {

		ApplicationViewdataOutput output = new ApplicationViewdataOutput();

		if (LDAPAuthWS.isLoginValid(detailInput.getCwid(), detailInput.getToken())) {
			if (null != detailInput.getId()) {
				List<ViewDataDTO> listDTO = AnwendungHbn.findApplicationUpStream(detailInput.getId());

				output.setViewdataDTO(getViewDataArray(listDTO));
			}
		}

		return output;
	}

	/**
	 * delivers the application downstream information
	 * 
	 * @param detailInput
	 * @return
	 */
	public ApplicationViewdataOutput getApplicationDownstream(ApplicationDetailParameterInput detailInput) {

		ApplicationViewdataOutput output = new ApplicationViewdataOutput();

		if (LDAPAuthWS.isLoginValid(detailInput.getCwid(), detailInput.getToken())) {
			if (null != detailInput.getId()) {
				List<ViewDataDTO> listDTO = AnwendungHbn.findApplicationDownStream(detailInput.getId());

				output.setViewdataDTO(getViewDataArray(listDTO));
			}
		}

		return output;
	}

	/**
	 * delivers the application downstream information
	 * 
	 * @param detailInput
	 * @return
	 */
	public ApplicationViewdataOutput getApplicationProcess(ApplicationDetailParameterInput detailInput) {

		ApplicationViewdataOutput output = new ApplicationViewdataOutput();

		if (LDAPAuthWS.isLoginValid(detailInput.getCwid(), detailInput.getToken())) {
			if (null != detailInput.getId()) {
				 List<ViewDataDTO> listDTO = AnwendungHbn.findApplicationProcess(detailInput.getId());

				//List<ViewDataDTO> listDTO = AnwendungHbn.findApplicationConnections(detailInput.getId());

				output.setViewdataDTO(getViewDataArray(listDTO));
			}
		}

		return output;
	}

	public ApplicationViewdataOutput getAllConnections(ApplicationDetailParameterInput detailInput) {

		ApplicationViewdataOutput output = new ApplicationViewdataOutput();

		if (LDAPAuthWS.isLoginValid(detailInput.getCwid(), detailInput.getToken())) {
			if (null != detailInput.getId()) {
				List<ViewDataDTO> listDTO = AnwendungHbn.findApplicationConnections(detailInput.getId());

				output.setViewdataDTO(getViewDataArray(listDTO));
			}
		}

		return output;
	}

	public ApplicationViewdataOutput getApplicationItSystems(ApplicationDetailParameterInput detailInput) {

		ApplicationViewdataOutput output = new ApplicationViewdataOutput();

		if (LDAPAuthWS.isLoginValid(detailInput.getCwid(), detailInput.getToken())) {
			if (null != detailInput.getId()) {
				List<ViewDataDTO> listDTO = AnwendungHbn.findApplicationItSystems(detailInput.getId());
				output.setViewdataDTO(getViewDataArray(listDTO));
			}
		}

		return output;
	}

	private static ViewDataDTO[] getViewDataArray(List<ViewDataDTO> listDTO) {
		ViewDataDTO aViewDataDTO[] = null;
		if (null != listDTO && !listDTO.isEmpty()) {
			aViewDataDTO = new ViewDataDTO[listDTO.size()];
			for (int i = 0; i < aViewDataDTO.length; i++) {
				aViewDataDTO[i] = listDTO.get(i);
			}
		}
		return aViewDataDTO;
	}

	
	public ApplicationEditParameterOutput createApplicationByCopy(ApplicationCopyParameterInput copyInput) {
		ApplicationEditParameterOutput output = new ApplicationEditParameterOutput();
		ApplicationDTO dto = new ApplicationDTO();
		
		output.setResult(ApplreposConstants.RESULT_ERROR);
		
		if (LDAPAuthWS.isLoginValid(copyInput.getCwid(), copyInput.getToken())) {
			// TODO check tableId !!! (app only)
			
			Application applicationSource = AnwendungHbn.findApplicationById(copyInput.getCiIdSource());

			if (null != applicationSource) {
				dto.setId(new Long(0));
				dto.setName(copyInput.getCiNameTarget());
				dto.setAlias(copyInput.getCiAliasTarget());
				
				if (null == dto.getBusinessEssentialId()) {
					dto.setBusinessEssentialId(ApplreposConstants.BUSINESS_ESSENTIAL_DEFAULT);
				}

				// set the actual cwid as responsible
				dto.setCiOwner(copyInput.getCwid().toUpperCase());
				dto.setCiOwnerHidden(copyInput.getCwid().toUpperCase());
				dto.setCiOwnerDelegate(applicationSource.getSubResponsible());
				dto.setCiOwnerDelegateHidden(applicationSource.getSubResponsible());
				dto.setTemplate(applicationSource.getTemplate());
				
				dto.setRelevanzItsec(applicationSource.getRelevanzITSEC());
				dto.setRelevanceICS(applicationSource.getRelevanceICS());

				dto.setRelevance2059(applicationSource.getRelevance2059());
				dto.setRelevance2008(applicationSource.getRelevance2008());
				
				
				// save / create application
				ApplicationEditParameterOutput createOutput = AnwendungHbn.createAnwendung(copyInput.getCwid(), dto, null);

				if (ApplreposConstants.RESULT_OK.equals(createOutput.getResult())) {
					List<Application> listAnwendung = AnwendungHbn.findApplicationByName(copyInput.getCiNameTarget());
					if (null != listAnwendung && 1 == listAnwendung.size()) {
						dto.setId(listAnwendung.get(0).getApplicationId());
						
						Long ciId = listAnwendung.get(0).getApplicationId();
						Application applicationTarget = AnwendungHbn.findApplicationById(ciId);
						
						if (null != applicationTarget) {
							ApplicationEditParameterOutput temp = AnwendungHbn.copyApplication(copyInput.getCwid(), applicationSource.getId(), applicationTarget.getId());
							
							output.setApplicationId(temp.getApplicationId());
							output.setResult(temp.getResult());
							output.setMessages(temp.getMessages());
							output.setDisplayMessage(temp.getDisplayMessage());
						}
					}
				}
				else {
					output.setApplicationId(createOutput.getApplicationId());
					output.setResult(createOutput.getResult());
					output.setMessages(createOutput.getMessages());
					output.setDisplayMessage(createOutput.getDisplayMessage());
				}
			}
		}

		if (null == output.getDisplayMessage() && null != output.getMessages()) {
			output.setDisplayMessage(output.getMessages()[0]);
		}
		
		return output;
	}
	
	public ApplicationDetailParameterOutput getApplication(ApplicationDetailParameterInput detailInput) {
		ApplicationDTO applicationDTO = new ApplicationDTO();
		ApplicationDetailParameterOutput output = new ApplicationDetailParameterOutput();

		if(LDAPAuthWS.isLoginValid(detailInput.getCwid(), detailInput.getToken())) {
			Application application = AnwendungHbn.findApplicationById(detailInput.getId());

			applicationDTO.setId(application.getApplicationId());
			applicationDTO.setName(application.getApplicationName());
			applicationDTO.setAlias(application.getApplicationAlias());
			applicationDTO.setApplicationCat2Id(application.getApplicationCat2Id());
			applicationDTO.setItsecGroupId(application.getItsecGroupId());
			//evtl. weitere falls diese Methode noch woanders ben�tigt wird

		}
		
		output.setApplicationDTO(applicationDTO);
		return output;
	}
	
	/**
	 * delivers the application detail information with the acl
	 * 
	 * @param detailInput
	 * @return
	 */
	public ApplicationDetailParameterOutput getApplicationDetail(ApplicationDetailParameterInput detailInput) {
		ApplicationDetailParameterOutput output = new ApplicationDetailParameterOutput();

		ApplicationDTO dto = new ApplicationDTO();
		ApplicationAccessDTO accessDTO = new ApplicationAccessDTO();

		if (LDAPAuthWS.isLoginValid(detailInput.getCwid(), detailInput.getToken())) {
			Application application = AnwendungHbn.findApplicationById(detailInput.getId());

			if (null != application) {
				dto = AnwendungHbn.getApplicationDetail(detailInput.getId());
				dto.setTemplateReferencedByItem(ApplreposConstants.NO_SHORT);
				
				if (null != dto.getTemplate() && !"0".equals(dto.getTemplate())) {
					// it is a template, so see if it is referenced by some ci's
					if (!"0".equals(ApplReposHbn.getCountReferencingTemplates(detailInput.getId()))) {
						dto.setTemplateReferencedByItem(ApplreposConstants.YES_SHORT);
					}
				}

				if (StringUtils.isNotNullOrEmpty(dto.getCiOwner())) {
					List<PersonsDTO> listPers = PersonsHbn.findPersonByCWID(dto.getCiOwner());
					if (null != listPers && 1 == listPers.size()) {
						PersonsDTO tempPers = listPers.get(0);
						dto.setCiOwner(tempPers.getDisplayNameFull());
					}
				}

				if (StringUtils.isNotNullOrEmpty(dto.getCiOwnerDelegate())) {
					List<PersonsDTO> listPers = PersonsHbn.findPersonByCWID(dto.getCiOwnerDelegate());
					if (null != listPers && 1 == listPers.size()) {
						PersonsDTO tempPers = listPers.get(0);
						dto.setCiOwnerDelegate(tempPers.getDisplayNameFull());
					}
				}

				if (StringUtils.isNotNullOrEmpty(dto.getApplicationOwner())) {
					List<PersonsDTO> listPers = PersonsHbn.findPersonByCWID(dto.getApplicationOwner());
					if (null != listPers && 1 == listPers.size()) {
						PersonsDTO tempPers = listPers.get(0);
						dto.setApplicationOwner(tempPers.getDisplayNameFull());
					}
				}
				
				if (StringUtils.isNotNullOrEmpty(dto.getApplicationSteward())) {
					List<PersonsDTO> listPers = PersonsHbn.findPersonByCWID(dto.getApplicationSteward());
					if (null != listPers && 1 == listPers.size()) {
						PersonsDTO tempPers = listPers.get(0);
						dto.setApplicationSteward(tempPers.getDisplayNameFull());
					}
				}

				if (StringUtils.isNotNullOrEmpty(dto.getApplicationOwnerDelegate())) {
					List<PersonsDTO> listPers = PersonsHbn.findPersonByCWID(dto.getApplicationOwnerDelegate());
					if (null != listPers && 1 == listPers.size()) {
						PersonsDTO tempPers = listPers.get(0);
						dto.setApplicationOwnerDelegate(tempPers.getDisplayNameFull());
					}
				}

				AccessRightChecker checker = new AccessRightChecker();

				// hier wird gepr�ft, ob der aktive Anwender �ber Edit-Zugriffsrechte (�ber Person, seine Gruppenzugeh�rigkeit oder globale Adminrechte) verf�gt  
				if (checker.isEditable(application.getApplicationId(), new Long(2), detailInput.getCwid())) {
					dto.setIsEditable(ApplreposConstants.YES_SHORT);
				}

				// RFC 7465
				if (checker.isRelevanceOperational(detailInput.getCwid(), application)) {
					accessDTO.setRelevanceOperational(ApplreposConstants.YES_SHORT);
				} else {
					accessDTO.setRelevanceOperational(ApplreposConstants.NO_SHORT);
				}

				// RFC 9101 Erweiterung "AIR Infrastructure Manager"
				long applCat1Id = dto.getApplicationCat1Id().longValue();
				if (ApplreposConstants.APPLICATION_CAT1_APPLICATION.longValue() == applCat1Id) {
					// nur f�r CI's Typ = Anwendung
					if (checker.isRelevanceStrategic(detailInput.getCwid(), application)) {
						accessDTO.setRelevanceStrategic(ApplreposConstants.YES_SHORT);
					} else {
						accessDTO.setRelevanceStrategic(ApplreposConstants.NO_SHORT);
					}
				}
				else if (ApplreposConstants.APPLICATION_CAT1_COMMON_SERVICE.longValue() == applCat1Id ||
						 ApplreposConstants.APPLICATION_CAT1_COMMON_MIDDLEWARE.longValue() == applCat1Id ||
						 ApplreposConstants.APPLICATION_CAT1_COMMON_APPLICATIONPLATFORM.longValue() == applCat1Id) {

					if (ApplreposConstants.NO_SHORT.equals(accessDTO.getRelevanceOperational())) {
						// Abfrage Infrastructure Manager
						if (checker.isEditableRoleInfrastructureManager(detailInput.getCwid())) {
							accessDTO.setRelevanceOperational(ApplreposConstants.YES_SHORT);
						}
					}
					
					// diese CI's haben keine Trennung zwischen Operational und Strategic
					// deshalb setzen wir hier beide Berechtigungen gleich
					accessDTO.setRelevanceStrategic(accessDTO.getRelevanceOperational());
				}
				else {

					// alle anderen CI's haben keine Trennung, z.B. Raum
					// deshalb setzen wir hier beide Berechtigungen gleich
					accessDTO.setRelevanceStrategic(accessDTO.getRelevanceOperational());
				}
				

				if (StringUtils.isNotNullOrEmpty(application.getInsertQuelle())) {
					if (ApplreposConstants.YES_SHORT.equals(dto.getIsEditable())) {

						if (ApplreposConstants.INSERT_QUELLE_SISEC.equals(application.getInsertQuelle())
								|| ApplreposConstants.APPLICATION_GUI_NAME.equals(application.getInsertQuelle())) {
							// for the application itself, all are editable
							accessDTO.setAllEditable();
						} else {
							InterfacesDTO interfaceDto = InterfacesHbn.findInterfacesByInterfaceToken(application.getInsertQuelle());
							
							if (StringUtils.isNotNullOrEmpty(interfaceDto.getSisecEditable()) && null != interfaceDto) {
								String allRights = interfaceDto.getSisecEditable();
								if (-1 != allRights.indexOf("License_Scanning")) {
									accessDTO.setLicense_Scanning(ApplreposConstants.YES_SHORT);
								}
								if (-1 != allRights.indexOf("Responsible")) {
									accessDTO.setResponsible(ApplreposConstants.YES_SHORT);
								}
								if (-1 != allRights.indexOf("Sub_Responsible")) {
									accessDTO.setSub_Responsible(ApplreposConstants.YES_SHORT);
								}
								if (-1 != allRights.indexOf("Relevance_Ics")) {
									accessDTO.setRelevance_Ics(ApplreposConstants.YES_SHORT);
								}
								if (-1 != allRights.indexOf("Relevanz_Itsec")) {
									accessDTO.setRelevanz_Itsec(ApplreposConstants.YES_SHORT);
								}
								if (-1 != allRights.indexOf("Gxp_Flag")) {
									accessDTO.setGxp_Flag(ApplreposConstants.YES_SHORT);
								}
								if (-1 != allRights.indexOf("Itsec_Gruppe_Id")) {
									accessDTO.setItsec_Gruppe_Id(ApplreposConstants.YES_SHORT);
								}
								if (-1 != allRights.indexOf("Sample_Test_Date")) {
									accessDTO.setSample_Test_Date(ApplreposConstants.YES_SHORT);
								}
								if (-1 != allRights.indexOf("Sample_Test_Result")) {
									accessDTO.setSample_Test_Result(ApplreposConstants.YES_SHORT);
								}
								if (-1 != allRights.indexOf("Sla_Id")) {
									accessDTO.setSla_Id(ApplreposConstants.YES_SHORT);
								}
								if (-1 != allRights.indexOf("Service_Contract_Id")) {
									accessDTO.setService_Contract_Id(ApplreposConstants.YES_SHORT);
								}
								if (-1 != allRights.indexOf("Priority_Level_Id")) {
									accessDTO.setPriority_Level_Id(ApplreposConstants.YES_SHORT);
								}
								if (-1 != allRights.indexOf("Severity_Level_Id")) {
									accessDTO.setSeverity_Level_Id(ApplreposConstants.YES_SHORT);
								}

								// business essential only by group right

								if (-1 != allRights.indexOf("Itsec_SB_Integ_ID")) {
									accessDTO.setItsec_SB_Integ_ID(ApplreposConstants.YES_SHORT);
								}
								if (-1 != allRights.indexOf("Itsec_SB_Integ_Txt")) {
									accessDTO.setItsec_SB_Integ_Txt(ApplreposConstants.YES_SHORT);
								}
								if (-1 != allRights.indexOf("Itsec_SB_Verfg_ID")) {
									accessDTO.setItsec_SB_Verfg_ID(ApplreposConstants.YES_SHORT);
								}
								if (-1 != allRights.indexOf("Itsec_SB_Verfg_Txt")) {
									accessDTO.setItsec_SB_Verfg_Txt(ApplreposConstants.YES_SHORT);
								}
								if (-1 != allRights.indexOf("Itsec_SB_Vertr_ID")) {
									accessDTO.setItsec_SB_Vertr_ID(ApplreposConstants.YES_SHORT);
								}
								if (-1 != allRights.indexOf("Itsec_SB_Vertr_Txt")) {
									accessDTO.setItsec_SB_Vertr_Txt(ApplreposConstants.YES_SHORT);
								}
							}
						}
					}
				}

				// ci support stuff
				// ================
				readAndFillCiStuff(dto, application);
				
				// compliance
				Long releItsec = dto.getRelevanzItsec();
				Long releICS = dto.getRelevanceICS();
				Long rele2059 = dto.getRelevance2059();
				Long rele2008 = dto.getRelevance2008();
				
				if (-1 == releItsec) {
					dto.setRelevanceGR1435(ApplreposConstants.YES_SHORT);
				}
				else if (0 == releItsec) {
					dto.setRelevanceGR1435(ApplreposConstants.NO_SHORT);
				}
				if (-1 == releICS) {
					dto.setRelevanceGR1920(ApplreposConstants.YES_SHORT);
				}
				else if (0 == releICS) {
					dto.setRelevanceGR1920(ApplreposConstants.NO_SHORT);
				}
				if (-1 == rele2059) {
					dto.setRelevanceGR2059(ApplreposConstants.YES_SHORT);
				}
				else if (0 == rele2059) {
					dto.setRelevanceGR2059(ApplreposConstants.NO_SHORT);
				}
				if (-1 == rele2008) {
					dto.setRelevanceGR2008(ApplreposConstants.YES_SHORT);
				}
				else if (0 == rele2008) {
					dto.setRelevanceGR2008(ApplreposConstants.NO_SHORT);
				}

				
				
				// licenseUsingRegions
				// ===================
				StringBuffer licenseUsingRegions = new StringBuffer();
				List<ApplicationRegion> listApplicationRegion = ApplicationRegionHbn.findCurrentApplicationRegion(detailInput.getId());

				if (null != listApplicationRegion && !listApplicationRegion.isEmpty()) {
					for (ApplicationRegion applRegion : listApplicationRegion) {
						if (0 < licenseUsingRegions.length()) {
							licenseUsingRegions.append(",");
						}
						licenseUsingRegions.append(applRegion.getRegionId());
					}
				}

				dto.setLicenseUsingRegions(licenseUsingRegions.toString());
			}

			// the attribute business essential is only editable for users
			// with the role "BusinessEssential-Editor"
			// so we have to check it here
			String count = ApplReposHbn.getCountFromRoleNameAndCwid(ApplreposConstants.ROLE_BUSINESS_ESSENTIAL_EDITOR, detailInput.getCwid());
			if (null != count && !"0".equals(count)) {
				accessDTO.setBusiness_Essential_Id(ApplreposConstants.YES_SHORT);
				// if we can edit the business essential, we can edit the ci
				dto.setIsEditable(ApplreposConstants.YES_SHORT);
			} else {
				accessDTO.setBusiness_Essential_Id(ApplreposConstants.NO_SHORT);
			}
		} // end of if valid session

		output.setApplicationDTO(dto);
		output.setApplicationAccessDTO(accessDTO);

		return output;
	}


	/**
	 * reads and fills the ci stuff related data. The empty values will be set
	 * as empty string.
	 * 
	 * @param dto
	 * @param application
	 */
	private void readAndFillCiStuff(ApplicationDTO dto, Application application) {
		Long ciId = application.getApplicationId();
		CiSupportStuffDTO supportStuffDTO = CiSupportStuffHbn.findCiSupportStuffByTableAndCiAndTypeId(
				ApplreposConstants.TABLE_ID_APPLICATION, ciId,
				ApplreposConstants.CI_SUPPORT_STUFF_TYPE_UserAuthorizationSupportedByDocumentation);
		if (null != supportStuffDTO) {
			dto.setCiSupportStuffUserAuthorizationSupportedByDocumentation(supportStuffDTO.getCiSupportStuffValue());
		}

		supportStuffDTO = CiSupportStuffHbn.findCiSupportStuffByTableAndCiAndTypeId(
				ApplreposConstants.TABLE_ID_APPLICATION, ciId,
				ApplreposConstants.CI_SUPPORT_STUFF_TYPE_UserAuthorizationProcess);
		if (null != supportStuffDTO) {
			dto.setCiSupportStuffUserAuthorizationProcess(supportStuffDTO.getCiSupportStuffValue());
		}

		supportStuffDTO = CiSupportStuffHbn.findCiSupportStuffByTableAndCiAndTypeId(
				ApplreposConstants.TABLE_ID_APPLICATION, ciId,
				ApplreposConstants.CI_SUPPORT_STUFF_TYPE_ChangeManagementSupportedByTool);
		if (null != supportStuffDTO) {
			dto.setCiSupportStuffChangeManagementSupportedByTool(supportStuffDTO.getCiSupportStuffValue());
		}

		supportStuffDTO = CiSupportStuffHbn.findCiSupportStuffByTableAndCiAndTypeId(
				ApplreposConstants.TABLE_ID_APPLICATION, ciId,
				ApplreposConstants.CI_SUPPORT_STUFF_TYPE_UserManagementProcess);
		if (null != supportStuffDTO) {
			dto.setCiSupportStuffUserManagementProcess(supportStuffDTO.getCiSupportStuffValue());
		}

		supportStuffDTO = CiSupportStuffHbn.findCiSupportStuffByTableAndCiAndTypeId(
				ApplreposConstants.TABLE_ID_APPLICATION, ciId,
				ApplreposConstants.CI_SUPPORT_STUFF_TYPE_ApplicationDocumentation);
		if (null != supportStuffDTO) {
			dto.setCiSupportStuffApplicationDocumentation(supportStuffDTO.getCiSupportStuffValue());
		}

		supportStuffDTO = CiSupportStuffHbn.findCiSupportStuffByTableAndCiAndTypeId(
				ApplreposConstants.TABLE_ID_APPLICATION, ciId,
				ApplreposConstants.CI_SUPPORT_STUFF_TYPE_RootDirectory);
		if (null != supportStuffDTO) {
			dto.setCiSupportStuffRootDirectory(supportStuffDTO.getCiSupportStuffValue());
		}

		supportStuffDTO = CiSupportStuffHbn.findCiSupportStuffByTableAndCiAndTypeId(
				ApplreposConstants.TABLE_ID_APPLICATION, ciId,
				ApplreposConstants.CI_SUPPORT_STUFF_TYPE_DataDirectory);
		if (null != supportStuffDTO) {
			dto.setCiSupportStuffDataDirectory(supportStuffDTO.getCiSupportStuffValue());
		}

		supportStuffDTO = CiSupportStuffHbn.findCiSupportStuffByTableAndCiAndTypeId(
				ApplreposConstants.TABLE_ID_APPLICATION, ciId,
				ApplreposConstants.CI_SUPPORT_STUFF_TYPE_ProvidedServices);
		if (null != supportStuffDTO) {
			dto.setCiSupportStuffProvidedServices(supportStuffDTO.getCiSupportStuffValue());
		}

		supportStuffDTO = CiSupportStuffHbn.findCiSupportStuffByTableAndCiAndTypeId(
				ApplreposConstants.TABLE_ID_APPLICATION, ciId,
				ApplreposConstants.CI_SUPPORT_STUFF_TYPE_ProvidedMachineUsers);
		if (null != supportStuffDTO) {
			dto.setCiSupportStuffProvidedMachineUsers(supportStuffDTO.getCiSupportStuffValue());
		}

		// quickhack
		if (null == dto.getCiSupportStuffUserAuthorizationSupportedByDocumentation()) {
			dto.setCiSupportStuffUserAuthorizationSupportedByDocumentation(ApplreposConstants.STRING_EMPTY);
		}
		if (null == dto.getCiSupportStuffUserAuthorizationProcess()) {
			dto.setCiSupportStuffUserAuthorizationProcess(ApplreposConstants.STRING_EMPTY);
		}
		if (null == dto.getCiSupportStuffChangeManagementSupportedByTool()) {
			dto.setCiSupportStuffChangeManagementSupportedByTool(ApplreposConstants.STRING_EMPTY);
		}
		if (null == dto.getCiSupportStuffUserManagementProcess()) {
			dto.setCiSupportStuffUserManagementProcess(ApplreposConstants.STRING_EMPTY);
		}
		if (null == dto.getCiSupportStuffApplicationDocumentation()) {
			dto.setCiSupportStuffApplicationDocumentation(ApplreposConstants.STRING_EMPTY);
		}
		if (null == dto.getCiSupportStuffRootDirectory()) {
			dto.setCiSupportStuffRootDirectory(ApplreposConstants.STRING_EMPTY);
		}
		if (null == dto.getCiSupportStuffDataDirectory()) {
			dto.setCiSupportStuffDataDirectory(ApplreposConstants.STRING_EMPTY);
		}
		if (null == dto.getCiSupportStuffProvidedServices()) {
			dto.setCiSupportStuffProvidedServices(ApplreposConstants.STRING_EMPTY);
		}
		if (null == dto.getCiSupportStuffProvidedMachineUsers()) {
			dto.setCiSupportStuffProvidedMachineUsers(ApplreposConstants.STRING_EMPTY);
		}
	}

	/**
	 * delivers the grouped contacts for the ci application
	 * 
	 * @param contactsInput
	 * @return
	 */
	public ApplicationContactsParameterOutput getApplicationContacts(ApplicationContactsParameterInput contactsInput) {
		ApplicationContactsParameterOutput output = new ApplicationContactsParameterOutput();

		ApplicationContactsDTO applicationContactsDTO = new ApplicationContactsDTO();

		List<ApplicationContact> listContacts = AnwendungHbn.findApplicationContacts(contactsInput.getApplicationId());

		String lastGroupTypeName = ApplreposConstants.STRING_EMPTY;

		ArrayList<ApplicationContactGroupDTO> listGroups = new ArrayList<ApplicationContactGroupDTO>();
		ArrayList<ApplicationContactEntryDTO> listEntries = new ArrayList<ApplicationContactEntryDTO>();
		ApplicationContactGroupDTO group = new ApplicationContactGroupDTO();

		Iterator<ApplicationContact> itContacts = listContacts.iterator();

		ApplicationContact contact = null;
		ApplicationContactEntryDTO entry = new ApplicationContactEntryDTO();

		while (itContacts.hasNext()) {
			contact = itContacts.next();

			if (!lastGroupTypeName.equals(contact.getGroupTypeName()) || !itContacts.hasNext()) {
				if ("".equals(lastGroupTypeName)) {
					// handle the first group - nothing more to do
					group.setGroupTypeId(contact.getGroupTypeId());
					group.setGroupTypeName(contact.getGroupTypeName());
					group.setIndividualContactYN(contact.getIndividualContactYN());
					group.setMinContacts(contact.getMinContacts());
					group.setMaxContacts(contact.getMaxContacts());
					
					lastGroupTypeName = contact.getGroupTypeName();
					
				} else {
					if (null != entry) {
						listEntries.add(entry);
					}
					// the group is finished - set entries
					ApplicationContactEntryDTO temp[] = new ApplicationContactEntryDTO[listEntries.size()];
					for (int i = 0; i < temp.length; i++) {
						temp[i] = listEntries.get(i);
					}
					group.setApplicationContactEntryDTO(temp);
					listGroups.add(group);
					lastGroupTypeName = contact.getGroupTypeName();
					// new group
					group = new ApplicationContactGroupDTO();
					group.setGroupTypeId(contact.getGroupTypeId());
					group.setGroupTypeName(contact.getGroupTypeName());
					group.setIndividualContactYN(contact.getIndividualContactYN());
					group.setMinContacts(contact.getMinContacts());
					group.setMaxContacts(contact.getMaxContacts());

					listEntries = new ArrayList<ApplicationContactEntryDTO>();
					entry = new ApplicationContactEntryDTO();

				}
			}
			if (null != contact.getCwid()) { 
				// contact has valid entry
				//entry = new ApplicationContactEntryDTO();
				entry.setCwid(contact.getCwid());
				StringBuffer sb = new StringBuffer();
				sb.append(contact.getPersonName());
				sb.append(" (").append(contact.getCwid()).append(")");
				entry.setPersonName(sb.toString());
			}
				// entry.setPersonName(contact.getPersonName());
			if ( null != contact.getGroupName()) {
				entry.setGroupId(contact.getGroupId().toString());
				entry.setGroupName(contact.getGroupName());
			}

		}


		// create the result (array)
		ApplicationContactGroupDTO temp[] = new ApplicationContactGroupDTO[listGroups.size()];
		for (int i = 0; i < temp.length; i++) {
			temp[i] = listGroups.get(i);
		}

		applicationContactsDTO.setApplicationContactGroupDTO(temp);

		output.setApplicationContactsDTO(applicationContactsDTO);

		return output;
	}

	/**
	 * delivers the ApplicationComplianceControlStatus for the ci application
	 * 
	 * @param contactsInput
	 * @return
	 */
	public ComplianceControlStatusDTO[] getApplicationComplianceControlStatus(
			ApplicationContactsParameterInput contactsInput) {

		Long tableId = ApplreposConstants.TABLE_ID_APPLICATION;

		ComplianceControlStatusDTO aControls[] = null;

		if (null != contactsInput.getApplicationId()) {
		
			List<ComplianceControlStatusDTO> listControls = ApplReposHbn.getComplianceControlStatus(tableId,
					contactsInput.getApplicationId());
	
	
			if (null != listControls && !listControls.isEmpty()) {
				aControls = new ComplianceControlStatusDTO[listControls.size()];
				for (int i = 0; i < listControls.size(); i++) {
					aControls[i] = listControls.get(i);
				}
			}
			
		}
		return aControls;
	}

	
	public HistoryViewDataDTO[] getApplicationHistory(ApplicationDetailParameterInput detailInput) {
		
		HistoryViewDataDTO[] arrayHist = null;

		if (null != detailInput.getId() && 0 < detailInput.getId().longValue()) {
		
			List<HistoryViewDataDTO> listHistory = AnwendungHbn.findApplicationHistory(detailInput.getId());
			
			if (!listHistory.isEmpty()) {
				arrayHist = new HistoryViewDataDTO[listHistory.size()];
				for (int i = 0; i < arrayHist.length; i++) {
					arrayHist[i] = listHistory.get(i);
				}
			}
		}
		return arrayHist;
	}

}