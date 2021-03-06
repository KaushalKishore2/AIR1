package com.bayerbbs.applrepos.hibernate;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.bayerbbs.air.error.ErrorCodeManager;
import com.bayerbbs.applrepos.common.ApplReposTS;
import com.bayerbbs.applrepos.common.StringUtils;
import com.bayerbbs.applrepos.constants.AirKonstanten;
import com.bayerbbs.applrepos.domain.Application;
import com.bayerbbs.applrepos.domain.ItSystem;
import com.bayerbbs.applrepos.dto.ApplicationContact;
import com.bayerbbs.applrepos.dto.ApplicationDTO;
import com.bayerbbs.applrepos.dto.ConnectionsViewDataDTO;
import com.bayerbbs.applrepos.dto.GroupsDTO;
import com.bayerbbs.applrepos.dto.HistoryViewDataDTO;
import com.bayerbbs.applrepos.dto.PersonsDTO;
import com.bayerbbs.applrepos.dto.ReferenzDTO;
import com.bayerbbs.applrepos.dto.ViewDataDTO;
import com.bayerbbs.applrepos.service.ApplicationEditParameterOutput;
import com.bayerbbs.applrepos.service.CiEntityEditParameterOutput;
import com.bayerbbs.applrepos.service.CiItemDTO;


public class AnwendungHbn extends BaseHbn {

	
	private static final String SQL_GET_ITSET = "SELECT pck_sync_tools.fn_ITSet(:responsible, :subResponsible, :tableID, :itemID, :source) AS ITSet FROM DUAL";

	private static final Log log = LogFactory.getLog(AnwendungHbn.class);
	

	public static Application findApplicationById(Long applicationId) {
		Application application = null;
		Transaction tx = null;
		Session session = HibernateUtil.getSession();
		
		try {
			tx = session.beginTransaction();
			@SuppressWarnings("unchecked")
			List<Application> list = session.createQuery("select h from Application as h where h.applicationId= " + applicationId).list();

			if (null != list && 0 < list.size()) {
				application = list.get(0);
			}

			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				try {
					// Second try catch as the rollback could fail as well
					tx.rollback();
				} catch (HibernateException e1) {
					log.error(e1.getMessage());
				}
				// throw again the first exception
				throw e;
			}

		}
		return application;
	}


	@SuppressWarnings("unchecked")
	public static List<Application> findDeletedApplicationByName(String applicationName) {
		List<Application> listApplications = null;
		Transaction tx = null;
		Session session = HibernateUtil.getSession();
		
		try {
			tx = session.beginTransaction();
			listApplications = session.createQuery("select h from Application as h where h.deleteTimestamp is not null and upper(h.applicationName)= '"	+ applicationName.toUpperCase() + "'").list();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				try {
					// Second try catch as the rollback could fail as well
					tx.rollback();
				} catch (HibernateException e1) {
					log.error(e1.getMessage());
				}
				// throw again the first exception
				throw e;
			}
		}
		
		return listApplications;
	}


	@SuppressWarnings("unchecked")
	public static List<Application> findApplicationByName(String applicationName) {
		List<Application> listApplications = null;
		Transaction tx = null;
		Session session = HibernateUtil.getSession();
		
		try {
			tx = session.beginTransaction();
			listApplications = session.createQuery("select h from Application as h where h.deleteTimestamp is null and upper(h.applicationName)= '"	+ applicationName.toUpperCase() + "'").list();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				try {
					// Second try catch as the rollback could fail as well
					tx.rollback();
				} catch (HibernateException e1) {
					log.error(e1.getMessage());
				}
				// throw again the first exception
				throw e;
			}

		}
		return listApplications;
	}


	public static ApplicationEditParameterOutput saveAnwendung(String cwid,	ApplicationDTO dto) {
		ApplicationEditParameterOutput output = new ApplicationEditParameterOutput();
		String validationMessage = null;
		
		if (null != cwid) {
			cwid = cwid.toUpperCase();
			
			boolean hasBusinessEssentialChanged = false;
			Long businessEssentialIdOld = null;
			
			if (null != dto.getId()	|| 0 < dto.getId().longValue()) {
				Long id = new Long(dto.getId());

				//ORIG
				// check der InputWerte
//				List<String> messages = AnwendungHbn.validateApplication(dto);
				//ORIG
				
				//NEU
				List<String> messages = validateAnwendung(dto, true);//ItSystemHbn.validateItSystem
				if (null == dto.getTemplate()) {
					// TODO 1 TESTCODE Template
					dto.setTemplate(new Long (0)); // no template
				}

				if (null == dto.getBusinessEssentialId()) {
					// messages.add("business essential is empty");
					// TODO 1 TESTCODE getBusinessEssentialId
					dto.setBusinessEssentialId(null);
				}
				//NEU
				

				if (messages.isEmpty()) {
					Session session = HibernateUtil.getSession();
					Transaction tx = null;
					tx = session.beginTransaction();
					Application application = (Application) session.get(Application.class, id);

					if (null == application) {
						//  was not found in database
						output.setErrorMessage("1000", EMPTY+id);
					} else if (null != application.getDeleteTimestamp()) {
						// application is deleted
						output.setErrorMessage("1001", EMPTY+id);
						
					} else {
						// application found - change values
						
						// validate template
						if (null != application.getTemplate() && -1 == application.getTemplate().longValue()) {
							if (null != dto.getTemplate()) {
								if (0 == dto.getTemplate().longValue()) {
									// user wants to change to non template
									// check if there are referencing values
									if (!"0".equals(ApplReposHbn.getCountReferencingTemplates(id))) {
										output.setErrorMessage("1002");
									}
								}
							}
						}
						

						// TODO check if allowed
						application.setUpdateUser(cwid);
						application.setUpdateQuelle(AirKonstanten.APPLICATION_GUI_NAME);
						application.setUpdateTimestamp(ApplReposTS.getCurrentTimestamp());
						
						// RFC 8344 change Insert-Quelle? // RFC 8532
						if (AirKonstanten.INSERT_QUELLE_ANT.equals(application.getInsertQuelle()) ||
							AirKonstanten.INSERT_QUELLE_RFC.equals(application.getInsertQuelle())  ||
							AirKonstanten.INSERT_QUELLE_SISEC.equals(application.getInsertQuelle())) {
							application.setInsertQuelle(AirKonstanten.APPLICATION_GUI_NAME);
						}
						else if (AirKonstanten.INSERT_QUELLE_GSDB.equals(application.getInsertQuelle())) {
							if (!AirKonstanten.SERVICE_ENVIRONMENT_OWNER_SE_BCBS.equals(application.getServiceEnvironmentOwner())) {
								// RFC 9176 - we must change the insert source
								application.setInsertQuelle(AirKonstanten.APPLICATION_GUI_NAME);
							}
						}

						// ======
						// Basics
						// ======

						if (null != dto.getName()) {
							application.setApplicationName(dto.getName());
						}
						if (null != dto.getAlias()) {
							application.setApplicationAlias(dto.getAlias());
						}
						if (null != dto.getVersion()) {
							application.setVersion(dto.getVersion());
						}
						if (null != dto.getApplicationCat2Id()) {
							if (-1 == dto.getApplicationCat2Id()) {
								application.setApplicationCat2Id(null);
							}
							else {
								application.setApplicationCat2Id(dto.getApplicationCat2Id());
							}
						}


						if (null != dto.getLifecycleStatusId()) {
							if (-1 == dto.getLifecycleStatusId()) {
								application.setLifecycleStatusId(null);
							}
							else {
								application.setLifecycleStatusId(dto.getLifecycleStatusId());
							}
						}
						
						
						if (null != dto.getOperationalStatusId()) {
							if (-1 == dto.getOperationalStatusId()) {
								application.setOperationalStatusId(null);
							}
							else {
								application.setOperationalStatusId(dto.getOperationalStatusId());
							}
						}
						if (null != dto.getComments()) {
							application.setComments(dto.getComments());
						}
						// TODO business category
						// -------
						
						
						// ==========
						// Agreements
						// ==========
						if (null != dto.getSlaId()) {
							if (-1 == dto.getSlaId()) {
								application.setSlaId(null);
							}
							else {
								application.setSlaId(dto.getSlaId());
							}
						}
						if (null != dto.getServiceContractId() || null != dto.getSlaId()) {
							// wenn SLA gesetzt ist, und ServiceContract nicht, dann muss der Service Contract gel?scht werden
							if (-1 == dto.getServiceContractId()) {
								application.setServiceContractId(null);
							}
							else {
								application.setServiceContractId(dto.getServiceContractId());
							}
						}

						if (null != dto.getPriorityLevelId()) {
							if (-1 == dto.getPriorityLevelId()) {
								application.setPriorityLevelId(null);
							}
							else {
								application.setPriorityLevelId(dto.getPriorityLevelId());
							}
						}
						if (null != dto.getSeverityLevelId()) {
							if (-1 == dto.getSeverityLevelId()) {
								application.setSeverityLevelId(null);
							}
							else {
								application.setSeverityLevelId(dto.getSeverityLevelId());
							}
						}

						hasBusinessEssentialChanged = false;
						businessEssentialIdOld = application.getBusinessEssentialId();
						if (null == dto.getBusinessEssentialId()) {
							if (null == application.getBusinessEssentialId()) {
								// set the default value
								application.setBusinessEssentialId(AirKonstanten.BUSINESS_ESSENTIAL_DEFAULT);
								hasBusinessEssentialChanged = true;
							}
						}
						else {
							if (null == application.getBusinessEssentialId() || application.getBusinessEssentialId().longValue() != dto.getBusinessEssentialId().longValue()) {
								hasBusinessEssentialChanged = true;
							}
							application.setBusinessEssentialId(dto.getBusinessEssentialId());
						}
						
						if(null != dto.getItSecSbIntegrityId()){
							if(-1 == dto.getItSecSbIntegrityId()){
								application.setItSecSbIntegrityId(null);
							}
							else if(0 != dto.getItSecSbIntegrityId().longValue()){
								application.setItSecSbIntegrityId(dto.getItSecSbIntegrityId());
							}
						}
						if(StringUtils.isNotNullOrEmpty(dto.getItSecSbIntegrityTxt())){
							application.setItSecSbIntegrityTxt(dto.getItSecSbIntegrityTxt());
						}
						
						if (null != dto.getItSecSbAvailabilityId()) {
							if (-1 == dto.getItSecSbAvailabilityId()) {
								application.setItSecSbAvailability(null);
							}
							else if (0 != dto.getItSecSbAvailabilityId().longValue()) {
								application.setItSecSbAvailability(dto.getItSecSbAvailabilityId());
							}
						}
						if (null != dto.getItSecSbAvailabilityTxt()) {//getItSecSbAvailabilityDescription
							application.setItSecSbAvailabilityTxt(dto.getItSecSbAvailabilityTxt());//getItSecSbAvailabilityDescription
						}						
						if (null != dto.getClassInformationId()) {
							if (-1 == dto.getClassInformationId()) {
								application.setClassInformationId(null);
							}
							else {
								application.setClassInformationId(dto.getClassInformationId());
							}
						}
						if (null != dto.getClassInformationExplanation()) {
							application.setClassInformationExplanation(dto.getClassInformationExplanation());
						}
						
						if (null != dto.getCiOwnerHidden()) {
							if(StringUtils.isNullOrEmpty(dto.getCiOwnerHidden())) {
								application.setResponsible(null);
							}
							else {
								application.setResponsible(dto.getCiOwnerHidden());
							}
						}
						if (null != dto.getCiOwnerDelegateHidden()) {
							application.setSubResponsible(dto.getCiOwnerDelegateHidden());
						}

						if (null != dto.getApplicationOwnerHidden()) {
							application.setApplicationOwner(dto.getApplicationOwnerHidden());
						}
						
						if (null != dto.getApplicationStewardHidden()) {
							application.setApplicationSteward(dto.getApplicationStewardHidden());
						}
						
						if (null != dto.getApplicationOwnerDelegateHidden()) {
							application.setApplicationOwnerDelegate(dto.getApplicationOwnerDelegateHidden());
						}
						
						
						// ==========
						// compliance
						// ==========
						
						// IT SET only view!
						application.setItset(getItset(dto.getCiOwnerHidden(), dto.getCiOwnerDelegateHidden(), AirKonstanten.TABLE_ID_APPLICATION, application.getApplicationId(), AirKonstanten.APPLICATION_GUI_NAME));
						
						
						// Template
						if (null != dto.getTemplate()) {
//							if (-1 == dto.getTemplate()) {
//								application.setTemplate(null);
//							}
//							else {
								application.setTemplate(dto.getTemplate());
//							}
						}
						
						if (null != dto.getItsecGroupId() && 0 != dto.getItsecGroupId()) {
							if (-1 == dto.getItsecGroupId()) {
								application.setItsecGroupId(null);
							}
							else {
								application.setItsecGroupId(dto.getItsecGroupId());
							}
						}
						
						if (null != dto.getRefId()) {
							if (-1 == dto.getRefId()) {
								application.setRefId(null);
								// Anlegen der ITSec Massnahmen
								ItsecMassnahmeStatusHbn.saveSaveguardAssignment(AirKonstanten.TABLE_ID_APPLICATION, application.getApplicationId(), application.getItsecGroupId());
							}
							else {
								application.setRefId(dto.getRefId());
							}
						}
						
						if (null != dto.getRelevanceICS()) {
							application.setRelevanceICS(dto.getRelevanceICS());
						}

						if (null != dto.getRelevanzItsec()) {
							application.setRelevanzITSEC(dto.getRelevanzItsec());
						}

						if (null != dto.getRelevance2059()) {
							application.setRelevance2059(dto.getRelevance2059());
						}
						if (null != dto.getRelevance2008()) {
							application.setRelevance2008(dto.getRelevance2008());
						}
						
						if ("null".equals( dto.getGxpFlagId())) {
							application.setGxpFlag(null); 
						}
						else {
							if (EMPTY.equals(dto.getGxpFlagId())) {
								application.setGxpFlag(null);
							}
							else {
								application.setGxpFlag(dto.getGxpFlagId());
							}
						}
						
//						if (null != dto.getRiskAnalysisYN()) {
//							application
//							.setRiskAnalysisYN(dto.getRiskAnalysisYN());
//						}
						// ----------------
						
						// ===============
						// License & Costs
						// ===============
						if (null != dto.getLicenseTypeId()) {
							if (-1 == dto.getLicenseTypeId()) {
								application.setLicenseTypeId(null);
							} else {
								application.setLicenseTypeId(dto.getLicenseTypeId());
							}
						}

						if (null != dto.getAccessingUserCount()) {
							if (-1 == dto.getAccessingUserCount()) {
								application.setAccessingUserCount(null);
							} else {
								application.setAccessingUserCount(dto.getAccessingUserCount());
							}
						}

						if (null != dto.getAccessingUserCountMeasured()) {
							if (-1 == dto.getAccessingUserCountMeasured()) {
								application.setAccessingUserCountMeasured(null);
							} else {
								application.setAccessingUserCountMeasured(dto.getAccessingUserCountMeasured());
							}
						}

						if (null != dto.getDedicated()) {
							if ("-1".equals(dto.getDedicated())) {
								application.setDedicated(null);
							} else {
								application.setDedicated(dto.getDedicated());
							}
						}
						if (null != dto.getLoadClass()) {
							if ("-1".equals(dto.getLoadClass())) {
								application.setLoadClass(null);
							} else {
								application.setLoadClass(dto.getLoadClass());
							}
						}
						
						if (null != dto.getCostRunAccountId()) {
							if (-1 == dto.getCostRunAccountId()) {
								application.setCostRunAccountId(null);
							} else {
								application.setCostRunAccountId(dto.getCostRunAccountId());
							}
							
						}
						if (null != dto.getCostChangeAccountId()) {
							if (-1 == dto.getCostChangeAccountId()) {
								application.setCostChangeAccountId(null);
							} else {
								application.setCostChangeAccountId(dto.getCostChangeAccountId());
							}
						}

						
						// ----------------
						
						if (null != dto.getCostRunPa()) {
							if (-1 == dto.getCostRunPa()) {
								application.setCostRunPa(null);
							} else {
								application.setCostRunPa(dto.getCostRunPa());
							}
							
						}
						if (null != dto.getCostChangePa()) {
							if (-1 == dto.getCostChangePa()) {
								application.setCostChangePa(null);
							}
							else {
								application.setCostChangePa(dto.getCostChangePa());
							}
						}
						if (null != dto.getCurrencyId()) {
							if (-1 == dto.getCurrencyId()) {
								application.setCurrencyId(null);
							}
							else {
								application.setCurrencyId(dto.getCurrencyId());
							}
						}
						
						if (null != dto.getCategoryBusinessId()) {
							if (-1 == dto.getCategoryBusinessId()) {
								application.setCategoryBusiness(null);
								// we have to delete the data class 
								dto.setClassDataId(new Long(-1));
							} else {
								application.setCategoryBusiness(dto.getCategoryBusinessId());
							}
						}
						
						if (null != dto.getClassDataId()) {
							if (0 != dto.getClassDataId().longValue()) {
								if (-1 == dto.getClassDataId()) {
									application.setClassDataId(null);
								} else {
									application.setClassDataId(dto.getClassDataId());
								}
							}
						}
						
						
						if (null != dto.getServiceModel()) {
							if (AirKonstanten.STRING_ONE_BLANK.equals(dto.getServiceModel())) {
								application.setServiceModel(null);
							}
							else {
								application.setServiceModel(dto.getServiceModel());
							}
						}
						
						if (null != dto.getOrganisationalScope()) {
							if ("-1".equals(dto.getOrganisationalScope())) {
								application.setOrganisationalScope(null);
							}
							else {
								application.setOrganisationalScope(dto.getOrganisationalScope());
							}
						}
						//ENFZM: C0000145157
						/*if (null != dto.getBarRelevance()) {
							dto.setBarRelevance(dto.getBarRelevance().toUpperCase());
								if (AirKonstanten.YES_SHORT.equals(dto.getBarRelevance()) || AirKonstanten.NO_SHORT.equals(dto.getBarRelevance())) {
									application.setBarRelevance(dto.getBarRelevance());
								}
								
								if (AirKonstanten.NO_SHORT.equals(dto.getBarRelevance())) {
									application.setBarApplicationId(null);
								}
						}
						if(null!= dto.getBarApplicationId()){
							application.setBarApplicationId(dto.getBarApplicationId());
						}*/
						
						//ENFZM: C0000145157
						
						// for BOV
						if (null == application.getBovApplicationNeeded()) {
							application.setBovApplicationNeeded(AirKonstanten.YES_SHORT);
						}
					}

					boolean toCommit = false;
					try {
						if (null == validationMessage) {
							if (null != application && null == application.getDeleteTimestamp()) {
								
								
								//emria test for Air saving iussue
								//String message = "File uploaded successfully.";
								//Session session=null;
								Connection conn=null;;
								PreparedStatement pstmt=null;;
								
									 //session = HibernateUtil.getSession();
									 conn = session.connection();
									 int i=',';
										System.out.println("I value is "+i);
										i='_';
										System.out.println("I value is "+i);
System.out.println("Air Saving issue>>>>>>>>>>");
										 pstmt = conn.prepareStatement("alter session set nls_comp=binary");
										System.out.println("pstmt.executeUpdate()  "+pstmt.executeUpdate()); 
										
										 i=',';
										System.out.println("I value is "+i);
										i='_';
										System.out.println("I value is "+i);
									
									//
									
								session.saveOrUpdate(application);
								session.flush();
								
								toCommit = true;
								
								if (hasBusinessEssentialChanged) {
									sendBusinessEssentialChangedMail(application, dto, businessEssentialIdOld);
								}

							}
						}
					} catch (Exception e) {
						String message = e.getMessage();
						log.error(message);
						// handle exception
						output.setResult(AirKonstanten.RESULT_ERROR);
						message = ApplReposHbn.getOracleTransbaseErrorMessage(message);
						output.setMessages(new String[] { message });
					} finally {
						String hbnMessage = HibernateUtil.close(tx, session, toCommit);
						if (toCommit && null != application) {
							if (null == hbnMessage) {
								output.setResult(AirKonstanten.RESULT_OK);
								output.setMessages(new String[] { EMPTY });
							} else {
								output.setResult(AirKonstanten.RESULT_ERROR);
								output.setMessages(new String[] { hbnMessage });
							}
						}
					}
				} else {
					// messages
					output.setResult(AirKonstanten.RESULT_ERROR);
					String astrMessages[] = new String[messages.size()];
					for (int i = 0; i < messages.size(); i++) {
						astrMessages[i] = messages.get(i);
					}
					output.setMessages(astrMessages);
				}

			} else {
				// application id is missing
				output.setErrorMessage("1003");
			}

		} else {
			// cwid missing
			output.setErrorMessage("100");
		}

		if (AirKonstanten.RESULT_ERROR.equals(output.getResult())) {
			// TODO errorcodes / Texte
			if (null != output.getMessages() && output.getMessages().length > 0) {
				output.setDisplayMessage(output.getMessages()[0]);
			}
		}
		
		return output;
	}

	private static Long getItset(String responsible, String subResponsible, Integer tableId, Long itemId, String source) {
		Long result = null;
		Session session = HibernateUtil.getSession();
		Query selectQuery = session.createSQLQuery(SQL_GET_ITSET)
		.setString("responsible", responsible)
		.setString("subResponsible", subResponsible)
		.setLong("tableID", tableId)
		.setLong("itemID", itemId)
		.setString("source", source);
		
		try {
			result = (Long) ((BigDecimal) selectQuery.uniqueResult()).longValue();
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			session.flush();		
		}
		
		return result;
	}
	
	public static void updateDWHTypeAndCategory(String type, String category, Long ApplicationId){
		StringBuilder builder = new StringBuilder();
		builder.append("APP-").append(ApplicationId.toString());
		String SQL = "UPDATE DWH_ENTITY set TYPE = ? ,CATEGORy = ? WHERE ID = ?";
		Session session = HibernateUtil.getSession();		
		Query selectQueryForCategory = session.createSQLQuery(SQL);
		selectQueryForCategory.setString(0, type);
		selectQueryForCategory.setString(1, category);
		selectQueryForCategory.setString(2, builder.toString());		
		selectQueryForCategory.executeUpdate();
	
		session.flush();
		session.close();

		
	}

	public static ApplicationEditParameterOutput createAnwendung(String cwid, ApplicationDTO dto, Boolean forceOverride, boolean neuanlage) {
		ApplicationEditParameterOutput output = new ApplicationEditParameterOutput();
		Long id = null;

		// TODO check validate token

		if (null != cwid) {
			cwid = cwid.toUpperCase();
			
			
			if (null != dto.getId() && 0 == dto.getId()) {
				
				    List<String> messages = new ArrayList<String>();
					ErrorCodeManager errorCodeManager = new ErrorCodeManager();

				
					List<Application> applications = ItSystemHbn.findApplicationsByNameOrAlias(dto.getName(), dto.getAlias());
					List<ItSystem> itSystems = ItSystemHbn.findItSystemsByNameOrAlias(dto.getName(), dto.getAlias());
					
					if(itSystems.size() > 0) {
							messages.add(errorCodeManager.getErrorMessage("8000", null));
					}
					if(applications.size() > 0) {
							messages.add(errorCodeManager.getErrorMessage("9000", null));
					}
					if(!messages.isEmpty()){
						// messages
						output.setResult(AirKonstanten.RESULT_ERROR);
						String astrMessages[] = new String[messages.size()];
						for (int i = 0; i < messages.size(); i++) {
							astrMessages[i] = messages.get(i);
						}
						output.setMessages(astrMessages);
						return output;
					}
					messages = validateAnwendung(dto, false);//ItSystemHbn.validateItSystem

				if (messages.isEmpty()) {
					Application application = new Application();
					boolean isApplicationNameAndAliasNameAllowed = true;					
					if (isApplicationNameAndAliasNameAllowed) {
						// calculates the ItSet
						Long itSet = null;
						String strItSet = ApplReposHbn.getItSetFromCwid(dto.getCiOwnerHidden());
						
						if (null != strItSet) {
							itSet = Long.parseLong(strItSet);//getItset(responsible, subResponsible, tableId, itemId, source);
						}
						if (null == itSet) {
							// set default itSet
							itSet = new Long(AirKonstanten.IT_SET_GERMANY);//nicht IT_SET_DEFAULT ?
						}
						application.setItset(itSet);

						if (0 == dto.getBusinessEssentialId().longValue()) {
							dto.setBusinessEssentialId(new Long(AirKonstanten.BUSINESS_ESSENTIAL_DEFAULT));
						}

						if (null == application.getBovApplicationNeeded()) {
							// for BOV
							application.setBovApplicationNeeded(AirKonstanten.YES_SHORT);
						}
						
						Session session = HibernateUtil.getSession();
						Transaction tx = null;
						tx = session.beginTransaction();

						// application - insert values
						application.setInsertUser(cwid);
						application.setInsertQuelle(AirKonstanten.APPLICATION_GUI_NAME);
						application.setInsertTimestamp(ApplReposTS.getCurrentTimestamp());

						// application - update values
						application.setUpdateUser(application.getInsertUser());
						application.setUpdateQuelle(application.getInsertQuelle());
						application.setUpdateTimestamp(application.getInsertTimestamp());

						// application - attributes
						application.setApplicationName(dto.getName());
						application.setApplicationAlias(dto.getAlias());
						application.setComments(dto.getComments());

						application.setApplicationCat2Id(dto.getApplicationCat2Id());
						application.setLifecycleStatusId(dto.getLifecycleStatusId());
						application.setOperationalStatusId(dto.getOperationalStatusId());

						if (null != dto.getCiOwnerHidden()) {//getResponsibleHidden
							application.setResponsible(dto.getCiOwnerHidden());
						}
						if (null != dto.getCiOwnerDelegateHidden()) {//getSubResponsibleHidden
							application.setSubResponsible(dto.getCiOwnerDelegateHidden());
						}

						if (null != dto.getApplicationOwnerHidden()) {
							application.setApplicationOwner(dto.getApplicationOwnerHidden());
						}
						
						if (null != dto.getApplicationStewardHidden()) {
							application.setApplicationSteward(dto.getApplicationStewardHidden());
						}
						
						if (null != dto.getApplicationOwnerDelegateHidden()) {
							application.setApplicationOwnerDelegate(dto.getApplicationOwnerDelegateHidden());
						}
						
						application.setBusinessEssentialId(dto.getBusinessEssentialId());

						// TODO !!!
						if (null == dto.getRelevanzItsec()) {
							if (AirKonstanten.YES_SHORT.equals(dto.getRelevanceGR1435())) {
								dto.setRelevanzItsec(new Long(-1));
							}
							else if (AirKonstanten.NO_SHORT.equals(dto.getRelevanceGR1435())) {
								dto.setRelevanzItsec(new Long(0));
							}
						}
						
						if (null == dto.getRelevanceICS()) {
							if (AirKonstanten.YES_SHORT.equals(dto.getRelevanceGR1920())) {
								dto.setRelevanceICS(new Long(-1));
							}
							else if (AirKonstanten.NO.equals(dto.getRelevanceGR1920())) {
								dto.setRelevanceICS(new Long(0));
							}
						}
						
						if (null == dto.getRelevance2059()) {
							if (AirKonstanten.YES_SHORT.equals(dto.getRelevanceGR2059())) {
								dto.setRelevance2059(new Long(-1));
							}
							else if (AirKonstanten.NO_SHORT.equals(dto.getRelevanceGR2059())) {
								dto.setRelevance2059(new Long(0));
							}
						}

						if (null == dto.getRelevance2008()) {
							if (AirKonstanten.YES_SHORT.equals(dto.getRelevanceGR2008())) {
								dto.setRelevance2008(new Long(-1));
							}
							else if (AirKonstanten.NO_SHORT.equals(dto.getRelevanceGR2008())) {
								dto.setRelevance2008(new Long(0));
							}
						}
						
						// TODO
						application.setTemplate(dto.getTemplate());
						application.setRelevanzITSEC(dto.getRelevanzItsec());
						application.setRelevanceICS(dto.getRelevanceICS());
						application.setRelevance2059(dto.getRelevance2059());
						application.setRelevance2008(dto.getRelevance2008());
						application.setGxpFlag(dto.getGxpFlagId());
						application.setSlaId(dto.getSlaId());
						application.setServiceContractId(dto.getServiceContractId());
						application.setSeverityLevelId(dto.getSeverityLevelId());
						
						//--- neu seit Wizard RFC 8271 - required Attributes (Attribute aus Sub-Tabellen werden im Anschluss gespeichert.
						if (null != dto.getClassInformationId()) {
							application.setClassInformationId(dto.getClassInformationId());
						}
						if (null != dto.getItSecSbAvailabilityId()) {
							application.setItSecSbAvailability(dto.getItSecSbAvailabilityId());
						}
						if (StringUtils.isNotNullOrEmpty(dto.getItSecSbAvailabilityTxt())) {//getItSecSbAvailabilityDescription
							application.setItSecSbAvailabilityTxt(dto.getItSecSbAvailabilityTxt());//getItSecSbAvailabilityDescription
						}
						if (StringUtils.isNotNullOrEmpty(dto.getOrganisationalScope())) {
							application.setOrganisationalScope(dto.getOrganisationalScope());
						}

						//ENFZM: C0000145157
						/*if (null != dto.getBarRelevance()) {
							dto.setBarRelevance(dto.getBarRelevance().toUpperCase());
							if (!AirKonstanten.YES_SHORT.equals(application.getBarRelevance())) {
								if (AirKonstanten.YES_SHORT.equals(dto.getBarRelevance()) || AirKonstanten.NO_SHORT.equals(dto.getBarRelevance())) {
									application.setBarRelevance(dto.getBarRelevance());
								}
							}
						}*/
						//ENFZM: C0000145157
						boolean toCommit = false;
						try {
							id =(Long) session.save(application);
							session.flush();
							toCommit = true;
						} catch (Exception e) {
							// handle exception
							output.setResult(AirKonstanten.RESULT_ERROR);
							output.setMessages(new String[] { e.getMessage() });
						} finally {
							String hbnMessage = HibernateUtil.close(tx,	session, toCommit);
							if (toCommit) {
								if (null == hbnMessage) {
									output.setResult(AirKonstanten.RESULT_OK);
									output.setMessages(new String[] { EMPTY });
									output.setApplicationId(id);
								} else {
									output.setResult(AirKonstanten.RESULT_ERROR);
									output.setMessages(new String[] { hbnMessage });
								}
							}
						}
					}
				} else {
					// messages
					output.setResult(AirKonstanten.RESULT_ERROR);
					String astrMessages[] = new String[messages.size()];
					for (int i = 0; i < messages.size(); i++) {
						astrMessages[i] = messages.get(i);
					}
					output.setMessages(astrMessages);
				}
			} else {
				// application id not 0
				output.setResult(AirKonstanten.RESULT_ERROR);
				output.setMessages(new String[] { "the application id should be 0" });
			}
		} else {
			// cwid missing
			output.setResult(AirKonstanten.RESULT_ERROR);
			output.setMessages(new String[] { "cwid missing" });
		}

		return output;
	}

	/**
	 * reactivates an marked as deleted application. Clears all data attributes !!!
	 * @param cwid
	 * @param dto
	 * @param application
	 * @return
	 */
	public static ApplicationEditParameterOutput reactivateApplication(String cwid, ApplicationDTO dto, Application application) {
		
		ApplicationEditParameterOutput output = new ApplicationEditParameterOutput();
		
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		tx = session.beginTransaction();

		if (null == application) {
			// application was not found in database
			output.setResult(AirKonstanten.RESULT_ERROR);
			output.setMessages(new String[] { "the application was not found in database" });
		} else {
			Timestamp tsNow = ApplReposTS.getCurrentTimestamp();
			
			// application found - change values
			application.setUpdateUser(cwid);
			application.setUpdateQuelle(AirKonstanten.APPLICATION_GUI_NAME);
			application.setUpdateTimestamp(tsNow);
			// override INSERT-attributes
			application.setInsertUser(cwid);
			application.setInsertQuelle(AirKonstanten.APPLICATION_GUI_NAME);
			application.setInsertTimestamp(tsNow);
			
			// reactivate DELETE-attributes
			application.setDeleteTimestamp(null);
			application.setDeleteQuelle(null);
			application.setDeleteUser(null);

			

			// Daten setzen
			// ============
			
			// applicationId bleibt nat?rlich
			
			//ENFZM: C0000145157
			//application.setBarApplicationId(null);	// Bayer application register id
			//ENFZM: C0000145157
			
			application.setApplicationAlias(null);
			application.setVersion(null);
			application.setApplicationCat2Id(null);
			
//			ORA-20000: The table ID of lifecycle substatus (null) does not match the destination table ID 2.
//			ORA-06512: at "TBADM.TRG_002_BIUD", line 307
//			ORA-04088: error during execution of trigger 'TBADM.TRG_002_BIUD'
//			application.setLifecycleStatusId(null);
			application.setOperationalStatusId(null);
			application.setComments(null);

			// contacts
			application.setResponsible(null);
			application.setSubResponsible(null);
			
			// agreements
			application.setSlaId(null);
			application.setPriorityLevelId(null);
			application.setServiceContractId(null);
			application.setSeverityLevelId(null);
			
//			ORA-01407: cannot update ("TBADM"."ANWENDUNG"."BUSINESS_ESSENTIAL_ID") to NULL
//			application.setBusinessEssentialId(null);

			// protection
			application.setItSecSbAvailability(null);
			application.setItSecSbAvailabilityTxt(null);
			// compliance
			
			// ORA-20000: In case of securing the multiple client capabilities the column itset must have a value.
			// ORA-06512: at "TBADM.TRG_002_BIUD", line 307
			// ORA-04088: error during execution of trigger 'TBADM.TRG_002_BIUD'
			// application.setItset(null);
			
//			ORA-01407: cannot update ("TBADM"."ANWENDUNG"."TEMPLATE") to NULL
//			application.setTemplate(null);
			application.setItsecGroupId(null);
			application.setRefId(null);
			
//			ORA-02290: check constraint (TBADM.CHK_002_RELEVANCE_ICS) violated
//			application.setRelevanceICS(null);

//			ORA-02290: check constraint (TBADM.CHK_002_RELEVANZ_ITSEC) violated
//			application.setRelevanzITSEC(null);

			application.setGxpFlag(null);
			
			// license & costs
			application.setLicenseTypeId(null);
			
			application.setDedicated(null);
			application.setAccessingUserCount(null);
			application.setAccessingUserCountMeasured(null);
			application.setLoadClass(null);
			application.setCostRunPa(null);
			application.setCostChangePa(null);
			application.setCurrencyId(null);
			application.setCostRunAccountId(null);
			application.setCostChangeAccountId(null);
			
			
			// unsorted
			application.setClusterCode(null);
			application.setClusterType(null);
			
			application.setApplicationOwner(cwid);
			application.setApplicationOwnerDelegate(null);
			
			application.setCategoryBusiness(null);

			
			application.setClassDataId(null);
			application.setClassInformationId(null);
			application.setClassInformationExplanation(null);
			
			application.setServiceModel(null);
			application.setOrganisationalScope(null);

			// for BOV
			application.setBovApplicationNeeded(AirKonstanten.YES_SHORT);
			// ==============================
		}

		boolean toCommit = false;
		try {
			if (null != application) {
				session.saveOrUpdate(application);
				session.flush();
			}
			toCommit = true;
		} catch (Exception e) {
			log.error(e.getMessage());
			// handle exception
			output.setResult(AirKonstanten.RESULT_ERROR);
			output.setMessages(new String[] { e.getMessage() });
		} finally {
			String hbnMessage = HibernateUtil.close(tx, session,
					toCommit);
			if (toCommit && null != application) {
				if (null == hbnMessage) {
					output.setResult(AirKonstanten.RESULT_OK);
					output.setMessages(new String[] { EMPTY });
				} else {
					output.setResult(AirKonstanten.RESULT_ERROR);
					output.setMessages(new String[] { hbnMessage });
				}
			}
		}
		return output;
	}


	/**
	 * mark an application as deleted
	 * 
	 * @param cwid
	 * @param dto
	 * @return
	 */
	public static CiEntityEditParameterOutput deleteAnwendung(String cwid, Long id) {//ApplicationEditParameterOutput ApplicationDTO dto
		CiEntityEditParameterOutput output = new CiEntityEditParameterOutput();

		// TODO check validate token

		if (null != cwid) {
			cwid = cwid.toUpperCase();
//			if (null != dto.getId()	&& 0 < dto.getId().longValue()) {
//				Long id = new Long(dto.getId());
			if(id != null) {

				// TODO check der InputWerte
				Session session = HibernateUtil.getSession();
				Transaction tx = null;
				tx = session.beginTransaction();
				Application application = (Application) session.get(Application.class, id);
				if (null == application) {
					// application was not found in database
					output.setResult(AirKonstanten.RESULT_ERROR);
					output.setMessages(new String[] { "the application id "	+ id + " was not found in database" });
				}

				// if it is not already marked as deleted, we can do it
				else if (null == application.getDeleteTimestamp()) {
					application.setDeleteUser(cwid);
					application.setDeleteQuelle(AirKonstanten.APPLICATION_GUI_NAME);
					application.setDeleteTimestamp(ApplReposTS.getDeletionTimestamp());

					boolean toCommit = false;
					try {
						session.saveOrUpdate(application);
						session.flush();
						toCommit = true;
					} catch (Exception e) {
						log.error(e.getMessage());
						// handle exception
						output.setResult(AirKonstanten.RESULT_ERROR);
						output.setMessages(new String[] { e.getMessage() });
					} finally {
						String hbnMessage = HibernateUtil.close(tx, session, toCommit);
						if (toCommit) {
							if (null == hbnMessage) {
								output.setResult(AirKonstanten.RESULT_OK);
								output.setMessages(new String[] { EMPTY });
							} else {
								output.setResult(AirKonstanten.RESULT_ERROR);
								output.setMessages(new String[] { hbnMessage });
							}
						}
					}

				} else {
					// application is already deleted
					output.setResult(AirKonstanten.RESULT_ERROR);
					output.setMessages(new String[] { "the application is already deleted" });
				}

			} else {
				// application id is missing
				output.setResult(AirKonstanten.RESULT_ERROR);
				output.setMessages(new String[] { "the application id is missing or invalid" });
			}

		} else {
			// cwid missing
			output.setResult(AirKonstanten.RESULT_ERROR);
			output.setMessages(new String[] { "cwid missing" });
		}

		return output;
	}

	/**
	 * mark an application as deleted
	 * 
	 * @param cwid
	 * @param dto
	 * @return
	 */
	/*public static ApplicationEditParameterOutput cleanBARApplicationID(String cwid, ApplicationDTO dto) {
		ApplicationEditParameterOutput output = new ApplicationEditParameterOutput();

		// TODO check validate token

		if (null != cwid) {
			cwid = cwid.toUpperCase();
			if (null != dto.getId()	&& 0 < dto.getId().longValue()) {
				Long id = new Long(dto.getId());

				// TODO check der InputWerte
				Session session = HibernateUtil.getSession();
				Transaction tx = null;
				tx = session.beginTransaction();
				Application application = (Application) session.get(Application.class, id);
				if (null == application) {
					// application was not found in database
					output.setResult(AirKonstanten.RESULT_ERROR);
					output.setMessages(new String[] { "the application id "	+ id + " was not found in database" });
				}

				// if it is not already marked as deleted, we can do it
				else if (null != application.getBarApplicationId()) {
					application.setBarApplicationId(null);
					application.setUpdateUser(cwid);
					application.setUpdateQuelle(AirKonstanten.APPLICATION_GUI_NAME);
					application.setUpdateTimestamp(ApplReposTS.getCurrentTimestamp());

					boolean toCommit = false;
					try {
						session.saveOrUpdate(application);
						session.flush();
						toCommit = true;
					} catch (Exception e) {
						log.error(e.getMessage());
						// handle exception
						output.setResult(AirKonstanten.RESULT_ERROR);
						output.setMessages(new String[] { e.getMessage() });
					} finally {
						String hbnMessage = HibernateUtil.close(tx, session, toCommit);
						if (toCommit) {
							if (null == hbnMessage) {
								output.setResult(AirKonstanten.RESULT_OK);
								output.setMessages(new String[] { EMPTY });
							} else {
								output.setResult(AirKonstanten.RESULT_ERROR);
								output.setMessages(new String[] { hbnMessage });
							}
						}
					}

				} else {
					// application is already deleted
					output.setResult(AirKonstanten.RESULT_ERROR);
					output.setMessages(new String[] { "the BAR application ID is already deleted" });
				}

			} else {
				// application id is missing
				output.setResult(AirKonstanten.RESULT_ERROR);
				output.setMessages(new String[] { "the application id is missing or invalid" });
			}

		} else {
			// cwid missing
			output.setResult(AirKonstanten.RESULT_ERROR);
			output.setMessages(new String[] { "cwid missing" });
		}

		return output;
	}

*/	
	public static ApplicationDTO getApplicationDetail(Long applicationId) {
		ApplicationDTO applicationDTO = null;

		Transaction tx = null;
		Statement selectStmt = null;
		Session session = HibernateUtil.getSession();

//		Connection conn = null;

		StringBuffer sql = new StringBuffer();

		sql.append("select ");
		sql.append("  anw.anwendung_id");
		//sql.append(", anw.BAR_APPLICATION_ID");
		//sql.append(", anw.BAR_RELEVANCE_Y_N");
		sql.append(", anw.anwendung_name");
		sql.append(", anw.anwendung_kat2_id");
		sql.append(", kat2.anwendung_kat2_txt");
		sql.append(", kat2.anwendung_kat1_id");
		sql.append(", kat1.anwendung_kat1_en");
		sql.append(", anw.RELEVANZ_ITSEC");
		sql.append(", anw.TEMPLATE");
		sql.append(", anw.itsec_gruppe_id");
		sql.append(", anw.ref_id");
		sql.append(", anwref.anwendung_name as REF_TXT");
		
		sql.append(", itsgrp.itsec_gruppe");
		
		sql.append(", anw.ITSEC_SB_INTEG_ID, anw.ITSEC_SB_INTEG_TXT");
		sql.append(", anw.ITSEC_SB_VERFG_ID, anw.ITSEC_SB_VERFG_TXT");
		sql.append(", anw.ITSEC_SB_VERTR_ID, anw.ITSEC_SB_VERTR_TXT");
		
		sql.append(", anw.einsatz_status_id");
		sql.append(", einsstat.einsatz_status_en");
		sql.append("		  , anw.lc_status_id");
		sql.append("		  , lcstat.lc_status_en || ' :: ' || lcsubstat.lc_sub_stat_staten as LIFECYCLE_STATUS");
		sql.append("		  , anw.user_create");
		sql.append("		  , anw.cluster_code");
		sql.append("		  , anw.cluster_type");
		sql.append("		  , anw.del_timestamp");
		sql.append("		  , anw.del_user");
		sql.append("		  , anw.del_quelle");
		sql.append("		  , anw.insert_timestamp");
		sql.append("		  , anw.insert_user");
		sql.append("		  , anw.insert_quelle");
		sql.append("		  , anw.update_timestamp");
		sql.append("		  , anw.update_user");
		sql.append("		  , anw.update_quelle");
		sql.append("		  , anw.cwid_verantw_betr");
		sql.append("		  , anw.sub_responsible");
		sql.append("		  , anw.application_owner");
		sql.append("		  , anw.application_steward");
		sql.append("		  , anw.application_owner_delegate");
		sql.append("		  , anw.itset");
		sql.append("		  , itsverb.it_verbund_name");
		sql.append("		  , anw.relevance_ics");
		sql.append("		  , anw.relevance_2059");
		sql.append("		  , anw.relevance_2008");
		sql.append("		  , anw.gxp_flag");
		sql.append("		  , anw.sla_id");
		sql.append("		  , sla.sla_name");
		sql.append("		  , anw.service_contract_id");
		sql.append("		  , servcontr.service_contract");
		sql.append("		  , anw.root_dir");
		sql.append("		  , anw.data_dir");
		sql.append("		  , anw.services");
		sql.append("		  , anw.machine_users");
		sql.append("		  , anw.comments");
		sql.append("		  , anw.alias");
		sql.append("		  , anw.priority_level_id");
		sql.append("		  , priolev.priority_level");
		sql.append("		  , anw.severity_level_id");
		sql.append("		  , sevlev.severity_level");
		sql.append("		  , anw.location_path");
		sql.append("		  , anw.business_essential_id");
		sql.append("		  , busess.severity_level as BUSINESS_ESSENTIAL");
		sql.append("		  , anw.risk_analysis_yn");
		sql.append("		  , anw.license_type_id");
		sql.append("          , lic.LICENSE_TYPE_NAME as LICENSE_TYPE_TXT");
		sql.append("		  , anw.dedicated_Y_N");
		sql.append("		  , anw.accessing_user_count");
		sql.append("		  , anw.accessing_user_count_measured");
		sql.append("		  , anw.load_class");
		sql.append("		  , anw.service_model");
		sql.append("		  , anw.ORG_SCOPE");
		sql.append("		  , anw.version");
		sql.append("		  , anw.cost_run_pa");
		sql.append("		  , anw.cost_change_pa");
		sql.append("		  , anw.currency_id");
		sql.append("          , cicurrency.CURRENCY_NAME as CURRENCY_TXT");
		sql.append("		  , anw.cost_run_account_id");
		sql.append("		  , costrun.KONTO_NAME as COST_RUN_ACCOUNT_TXT");
		sql.append("		  , anw.cost_change_account_id");
		sql.append("		  , costchange.KONTO_NAME as COST_CHANGE_ACCOUNT_TXT");
		sql.append("          , itsecsbinteg.SB_TEXT_EN as ITSECSBINTEG");
		sql.append("          , itsecsbverfg.SB_TEXT_EN as ITSECSBVERFG");
		sql.append("          , itsecsbvertr.SB_TEXT_EN as ITSECSBVERTR");
		sql.append("          , anw.CATEGORY_BUSINESS_ID");
		sql.append("          , katbus.CATEGORY_BUSINESS_NAME");
		sql.append("          , anw.CLASS_DATA_ID");
		sql.append("          , classdata.CLASS_DATA_NAME");
		sql.append("          , anw.CLASS_INFORMATION_ID");
		sql.append("          , classinfo.CLASS_INFORMATION_NAME");
		sql.append("          , anw.CLASS_INFORMATION_EXPLANATION");
		sql.append("          , classinfo.CLASS_PROTECTION_NAME");
		sql.append("		  , anw.SE_OWNER");	
		sql.append("		from anwendung anw");
		sql.append("		left join category_business katbus on anw.category_business_id = katbus.category_business_id");
		sql.append("		left join anwendung_kat2 kat2 on anw.anwendung_kat2_id = kat2.anwendung_kat2_id");
		sql.append("		left join anwendung_kat1 kat1 on kat2.anwendung_kat1_id = kat1.anwendung_kat1_id");
		sql.append("		left join itsec_gruppe itsgrp on anw.itsec_gruppe_id = itsgrp.ITSEC_GRP_GSTOOLID");
		sql.append(" 		left join anwendung anwref on anw.ref_id = anwref.anwendung_id");
		sql.append("		left join einsatz_status einsstat on anw.einsatz_status_id = einsstat.einsatz_status_id");
		sql.append("		left join lifecycle_sub_stat lcsubstat on anw.lc_status_id = lcsubstat.lc_sub_stat_id and lcsubstat.tabelle_id = 2");
		sql.append("		left join lifecycle_status lcstat on lcsubstat.lc_status_id = lcstat.lc_status_id and lcstat.tabelle_id = 2");
		sql.append("		left join itsec_it_verbund itsverb on anw.itset = itsverb.gstool_zob_id");
		sql.append("		left join sla sla on anw.sla_id = sla.sla_id");
		sql.append("		left join service_contract servcontr on anw.service_contract_id = servcontr.service_contract_id");
		sql.append("		left join priority_level priolev on anw.priority_level_id = priolev.priority_level_id");
		sql.append("		left join severity_level sevlev on anw.severity_level_id = sevlev.severity_level_id");
		sql.append("		left join severity_level busess on anw.business_essential_id = busess.severity_level_id");
		sql.append(" 		left join LICENSE_TYPE lic on anw.license_type_id = lic.license_type_id");
		sql.append(" 		left join CURRENCY cicurrency on anw.currency_id = cicurrency.currency_id");
		sql.append(" 		left join KONTO costrun on anw.cost_run_account_id = costrun.konto_id");
		sql.append(" 		left join KONTO costchange on anw.cost_change_account_id = costchange.konto_id");
		sql.append(" 		left join ITSEC_SB_WERTE itsecsbinteg on anw.ITSEC_SB_INTEG_ID = itsecsbinteg.ITSEC_SB_ID");
		sql.append(" 		left join ITSEC_SB_WERTE itsecsbverfg on anw.ITSEC_SB_VERFG_ID = itsecsbverfg.ITSEC_SB_ID");
		sql.append(" 		left join ITSEC_SB_WERTE itsecsbvertr on anw.ITSEC_SB_VERTR_ID = itsecsbvertr.ITSEC_SB_ID");
		sql.append(" 		left join CLASS_DATA classdata on anw.CLASS_DATA_ID = classdata.CLASS_DATA_ID and classdata.DEL_QUELLE is null");
		sql.append(" 		left join CLASS_INFORMATION classinfo on anw.CLASS_INFORMATION_ID = classinfo.CLASS_INFORMATION_ID and classinfo.DEL_QUELLE is null");
		sql.append("		where anw.anwendung_id=").append(applicationId);

		try {
			tx = session.beginTransaction();
			@SuppressWarnings("deprecation")
			Connection conn = session.connection();
			System.out.println("Application SQL"+sql.toString());
			selectStmt = conn.createStatement();
			ResultSet rsMessage = selectStmt.executeQuery(sql.toString());

			if (null != rsMessage) {
				rsMessage.next();
				applicationDTO = new ApplicationDTO();
				applicationDTO.setId(rsMessage.getLong("ANWENDUNG_ID"));
				//applicationDTO.setBarApplicationId(rsMessage.getString("BAR_APPLICATION_ID"));
				//applicationDTO.setBarRelevance(rsMessage.getString("BAR_RELEVANCE_Y_N"));
				applicationDTO.setName(rsMessage.getString("ANWENDUNG_NAME"));
				applicationDTO.setApplicationCat2Id(rsMessage.getLong("ANWENDUNG_KAT2_ID"));
				applicationDTO.setApplicationCat2Txt(rsMessage.getString("ANWENDUNG_KAT2_TXT"));
				applicationDTO.setApplicationCat1Id(rsMessage.getLong("ANWENDUNG_KAT1_ID"));
				applicationDTO.setApplicationCat1Txt(rsMessage.getString("ANWENDUNG_KAT1_EN"));
				applicationDTO.setRelevanzItsec(rsMessage.getLong("RELEVANZ_ITSEC"));
				applicationDTO.setItsecGroupId(rsMessage.getLong("ITSEC_GRUPPE_ID"));
				applicationDTO.setItsecGroup(rsMessage.getString("ITSEC_GRUPPE"));
				applicationDTO.setRefId(rsMessage.getLong("REF_ID"));
				applicationDTO.setRefTxt(rsMessage.getString("REF_TXT"));
				applicationDTO.setOperationalStatusId(rsMessage.getLong("EINSATZ_STATUS_ID"));
				applicationDTO.setOperationalStatusTxt(rsMessage.getString("EINSATZ_STATUS_EN"));
				applicationDTO.setLifecycleStatusId(rsMessage.getLong("LC_STATUS_ID"));
				applicationDTO.setLifecycleStatusTxt(rsMessage.getString("LIFECYCLE_STATUS"));
				
				if (" :: ".equals(applicationDTO.getLifecycleStatusTxt())) {
					// attributes are combined, must be handled as single
					// attributes, because of
					// null-attributes results => " :: "
					// so change to empty string for display
					applicationDTO.setLifecycleStatusTxt(AirKonstanten.STRING_EMPTY);
				}

				applicationDTO.setUserCreate(rsMessage.getString("USER_CREATE"));
				applicationDTO.setDeleteTimestamp(ApplReposTS.getTimestampDisp(rsMessage.getTimestamp("DEL_TIMESTAMP")));
				applicationDTO.setDeleteUser(rsMessage.getString("DEL_USER"));
				applicationDTO.setDeleteQuelle(rsMessage.getString("DEL_QUELLE"));
				applicationDTO.setInsertTimestamp(ApplReposTS.getTimestampDisp(rsMessage.getTimestamp("INSERT_TIMESTAMP")));
				applicationDTO.setInsertUser(rsMessage.getString("INSERT_USER"));
				applicationDTO.setInsertQuelle(rsMessage.getString("INSERT_QUELLE"));
				applicationDTO.setUpdateTimestamp(ApplReposTS.getTimestampDisp(rsMessage.getTimestamp("UPDATE_TIMESTAMP")));
				applicationDTO.setUpdateUser(rsMessage.getString("UPDATE_USER"));
				applicationDTO.setUpdateQuelle(rsMessage.getString("UPDATE_QUELLE"));
				applicationDTO.setCiOwner(rsMessage.getString("CWID_VERANTW_BETR"));//setResponsible
				applicationDTO.setCiOwnerHidden(rsMessage.getString("CWID_VERANTW_BETR"));//setResponsibleHidden
				applicationDTO.setCiOwnerDelegate(rsMessage.getString("SUB_RESPONSIBLE"));//setSubResponsible
				applicationDTO.setCiOwnerDelegateHidden(rsMessage.getString("SUB_RESPONSIBLE"));//setSubResponsibleHidden

				applicationDTO.setApplicationOwner(rsMessage.getString("APPLICATION_OWNER"));
				applicationDTO.setApplicationSteward(rsMessage.getString("APPLICATION_STEWARD"));
				applicationDTO.setApplicationOwnerDelegate(rsMessage.getString("APPLICATION_OWNER_DELEGATE"));
				
				applicationDTO.setApplicationOwnerHidden(rsMessage.getString("APPLICATION_OWNER"));
				applicationDTO.setApplicationStewardHidden(rsMessage.getString("APPLICATION_STEWARD"));
				applicationDTO.setApplicationOwnerDelegateHidden(rsMessage.getString("APPLICATION_OWNER_DELEGATE"));
				
				applicationDTO.setItset(rsMessage.getLong("ITSET"));
				applicationDTO.setItsetName(rsMessage.getString("IT_VERBUND_NAME"));

				Long relevanceICS = rsMessage.getLong("RELEVANCE_ICS");
				applicationDTO.setRelevanceICS(relevanceICS);
				
				Long relevance2059 = rsMessage.getLong("RELEVANCE_2059");
				applicationDTO.setRelevance2059(relevance2059);

				Long relevance2008 = rsMessage.getLong("RELEVANCE_2008");
				applicationDTO.setRelevance2008(relevance2008);

				Long template = rsMessage.getLong("TEMPLATE");
				if (-1 == template.longValue()) {
					// TODO -1 != 1 - Achtung beim Speichern
					template = new Long(1);
				}

				applicationDTO.setTemplate(template);
				applicationDTO.setSlaId(rsMessage.getLong("SLA_ID"));
				applicationDTO.setSlaName(rsMessage.getString("SLA_NAME"));
				applicationDTO.setServiceContractId(rsMessage.getLong("SERVICE_CONTRACT_ID"));
				applicationDTO.setServiceContract(rsMessage.getString("SERVICE_CONTRACT"));
				applicationDTO.setComments(rsMessage.getString("COMMENTS"));
				applicationDTO.setAlias(rsMessage.getString("ALIAS"));
				applicationDTO.setPriorityLevelId(rsMessage.getLong("PRIORITY_LEVEL_ID"));
				applicationDTO.setPriorityLevel(rsMessage.getString("PRIORITY_LEVEL"));
				applicationDTO.setSeverityLevelId(rsMessage.getLong("SEVERITY_LEVEL_ID"));
				applicationDTO.setSeverityLevel(rsMessage.getString("SEVERITY_LEVEL"));
				applicationDTO.setLocationPath(rsMessage.getString("LOCATION_PATH"));
				applicationDTO.setBusinessEssentialId(rsMessage.getLong("BUSINESS_ESSENTIAL_ID"));
				applicationDTO.setBusinessEssential(rsMessage.getString("BUSINESS_ESSENTIAL"));
				// set both values!
				applicationDTO.setGxpFlagId(rsMessage.getString("GXP_FLAG"));
				applicationDTO.setGxpFlagTxt(rsMessage.getString("GXP_FLAG"));

				// TODO neue Attribute
				applicationDTO.setLicenseTypeId(rsMessage.getLong("LICENSE_TYPE_ID"));
				applicationDTO.setLicenseTypeTxt(rsMessage.getString("LICENSE_TYPE_TXT"));
				
				applicationDTO.setDedicated(rsMessage.getString("DEDICATED_Y_N"));
				
				String testAccessingUserCount = rsMessage.getString("ACCESSING_USER_COUNT");
				if (null == testAccessingUserCount) {
					applicationDTO.setAccessingUserCount(null);
				}
				else {
					applicationDTO.setAccessingUserCount(rsMessage.getLong("ACCESSING_USER_COUNT"));
				}
				
				String testAccessingUserCountMeasured = rsMessage.getString("ACCESSING_USER_COUNT_MEASURED");
				if (null == testAccessingUserCountMeasured) {
					applicationDTO.setAccessingUserCountMeasured(null);
				}
				else {
					applicationDTO.setAccessingUserCountMeasured(rsMessage.getLong("ACCESSING_USER_COUNT_MEASURED"));
				}

				applicationDTO.setLoadClass(rsMessage.getString("LOAD_CLASS"));
				applicationDTO.setServiceModel(rsMessage.getString("SERVICE_MODEL"));
				applicationDTO.setOrganisationalScope(rsMessage.getString("ORG_SCOPE"));
				applicationDTO.setVersion(rsMessage.getString("VERSION"));
				String testCostRunPa = rsMessage.getString("COST_RUN_PA");
				
				if (null == testCostRunPa) {
					applicationDTO.setCostRunPa(null);
				}
				else {
					applicationDTO.setCostRunPa(rsMessage.getLong("COST_RUN_PA"));
				}

				String testCostChangePa = rsMessage.getString("COST_CHANGE_PA");
				if (null == testCostChangePa) {
					applicationDTO.setCostChangePa(null);
				}
				else {
					applicationDTO.setCostChangePa(rsMessage.getLong("COST_CHANGE_PA"));
				}
				
				applicationDTO.setCurrencyId(rsMessage.getLong("CURRENCY_ID"));
				applicationDTO.setCurrencyTxt(rsMessage.getString("CURRENCY_TXT"));
				applicationDTO.setCostRunAccountId(rsMessage.getLong("COST_RUN_ACCOUNT_ID"));
				applicationDTO.setCostRunAccountTxt(rsMessage.getString("COST_RUN_ACCOUNT_TXT"));
				applicationDTO.setCostChangeAccountId(rsMessage.getLong("COST_CHANGE_ACCOUNT_ID"));
				applicationDTO.setCostChangeAccountTxt(rsMessage.getString("COST_CHANGE_ACCOUNT_TXT"));
				
				// itSec
				applicationDTO.setItSecSbIntegrityId(rsMessage.getLong("ITSEC_SB_INTEG_ID"));
				applicationDTO.setItSecSbIntegrityTxt(rsMessage.getString("ITSEC_SB_INTEG_TXT"));//ITSECSBINTEG
				
				applicationDTO.setItSecSbAvailabilityId(rsMessage.getLong("ITSEC_SB_VERFG_ID"));
				applicationDTO.setItSecSbAvailabilityTxt(rsMessage.getString("ITSEC_SB_VERFG_TXT"));//ITSECSBVERFG
				
				applicationDTO.setClassInformationId(rsMessage.getLong("ITSEC_SB_VERTR_ID"));
				applicationDTO.setClassInformationTxt(rsMessage.getString("ITSEC_SB_VERTR_TXT"));	
				
				
				if (null == applicationDTO.getItSecSbIntegrityId()) {
					applicationDTO.setItSecSbIntegrityId(new Long(0));
				}
				if (null == applicationDTO.getItSecSbAvailabilityId()) {
					applicationDTO.setItSecSbAvailabilityId(new Long(0));
				}
				if(null == applicationDTO.getClassInformationId()){
					applicationDTO.setClassInformationId(new Long(0));
				}
				
				applicationDTO.setCategoryBusinessId(rsMessage.getLong("CATEGORY_BUSINESS_ID"));
				applicationDTO.setCategoryBusiness(rsMessage.getString("CATEGORY_BUSINESS_NAME"));
				
				applicationDTO.setClassDataId(rsMessage.getLong("CLASS_DATA_ID"));
				applicationDTO.setClassData(rsMessage.getString("CLASS_DATA_NAME"));				
				applicationDTO.setApplicationProtection(rsMessage.getString("CLASS_PROTECTION_NAME"));
				
				applicationDTO.setServiceEnvironmentOwner(rsMessage.getString("SE_OWNER"));
			}

			if (null != rsMessage) {
				rsMessage.close();
			}
			if (null != selectStmt) {
				selectStmt.close();
			}
			if (null != conn) {
				conn.close();
			}
			tx.commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			if (tx != null && tx.isActive()) {
				try {
					// Second try catch as the rollback could fail as well
					tx.rollback();
				} catch (HibernateException e1) {
					log.error(e1.getMessage());
				}
				// throw again the first exception
				// throw e;
			}
		}
		return applicationDTO;
	}


	/**
	 * find the application contacts
	 * @param applicationId
	 * @return
	 */
	public static List<ApplicationContact> findApplicationContacts(Long ciId, Integer tableId) {
		ArrayList<ApplicationContact> listResult = new ArrayList<ApplicationContact>();

		Transaction tx = null;
		Statement selectStmt = null;
		Session session = HibernateUtil.getSession();

//		Connection conn = null;

		StringBuffer sql = new StringBuffer();

		sql.append("select ");
		sql.append(" gt.group_type_id");
		sql.append(", gt.group_type_name");
		sql.append(", gt.individual_contact_y_n");
		sql.append(", gt.min_contacts");
		sql.append(", gt.max_contacts");
		sql.append(", cipers.cwid");
		sql.append(", pers.vorname");
		sql.append(", pers.nachname");
		sql.append(", grp.group_id");
		sql.append(", grp.group_name");

		sql.append(" from group_types gt");

		sql.append(" left join ci_groups cigr on gt.GROUP_TYPE_ID = cigr.GROUP_TYPE_ID and cigr.table_id="+tableId+" and cigr.ci_id=").append(ciId);//cigr.table_id=2
		sql.append(" and cigr.del_quelle is null");
		sql.append(" left join ci_persons cipers on gt.INDIVIDUAL_CONTACT_Y_N='Y' and gt.group_type_id = cipers.group_type_id and cipers.table_id="+tableId+" and cipers.del_quelle is null and cipers.ci_id=").append(ciId);
		sql.append(" left join persons pers on cipers.cwid = pers.cwid");
		sql.append(" left join groups grp on gt.INDIVIDUAL_CONTACT_Y_N='N' and cigr.group_id= grp.group_id");
		sql.append(" where gt.visible_application = 1 and gt.del_quelle is null");
		sql.append(" order by gt.group_type_id, gt.INDIVIDUAL_CONTACT_Y_N, gt.GROUP_TYPE_NAME");
		
		try {
			tx = session.beginTransaction();
			@SuppressWarnings("deprecation")
			Connection conn = session.connection();

			selectStmt = conn.createStatement();
			//System.out.println(sql.toString());
			ResultSet rsMessage = selectStmt.executeQuery(sql.toString());

			if (null != rsMessage) {
				while (rsMessage.next()) {
					ApplicationContact contact = new ApplicationContact();
					contact.setGroupTypeId(rsMessage.getLong("GROUP_TYPE_ID"));
					contact.setGroupTypeName(rsMessage.getString("GROUP_TYPE_NAME"));
					contact.setIndividualContactYN(rsMessage.getString("INDIVIDUAL_CONTACT_Y_N"));
					contact.setMinContacts(rsMessage.getLong("MIN_CONTACTS"));
					contact.setMaxContacts(rsMessage.getLong("MAX_CONTACTS"));
					contact.setCwid(rsMessage.getString("CWID"));

					String vorname = rsMessage.getString("VORNAME");
					String nachname = rsMessage.getString("NACHNAME");

					if (null == vorname && null == nachname) {
						contact.setPersonName(EMPTY);
					} else {
						contact.setPersonName(nachname+ ", " + vorname);
					}
					contact.setGroupId(rsMessage.getLong("GROUP_ID"));
					contact.setGroupName(rsMessage.getString("GROUP_NAME"));

					listResult.add(contact);
				}
			}

			if (null != rsMessage) {
				rsMessage.close();
			}
			if (null != selectStmt) {
				selectStmt.close();
			}
			if (null != conn) {
				conn.close();
			}
			tx.commit();
		} catch (Exception e) {
			if (tx != null && tx.isActive()) {
				try {
					// Second try catch as the rollback could fail as well
					tx.rollback();
				} catch (HibernateException e1) {
					log.error(e1.getMessage());
				}
				// throw again the first exception
				// throw e;
			}
		}
		return listResult;
	}


	public static List<ReferenzDTO> findApplicationReferenz() {
		ArrayList<ReferenzDTO> listResult = new ArrayList<ReferenzDTO>();

		Transaction tx = null;
		Statement selectStmt = null;
		Session session = HibernateUtil.getSession();

//		Connection conn = null;

		StringBuffer sql = new StringBuffer();

//		sql.append("select anw.anwendung_id, anw.anwendung_name, anw.itsec_gruppe_id, i.it_verbund_zob_id1 from anwendung anw ");//orig: ohne anw.itsec_gruppe_id
//		sql.append(" join anwendung_kat2 anwkat2 on anw.anwendung_kat2_id = anwkat2.anwendung_kat2_id");
//		sql.append(" join anwendung_kat1 anwkat1 on anwkat2.anwendung_kat1_id= anwkat1.anwendung_kat1_id");
//		sql.append(" join itverbund_itsecgrp i on i.itsec_gruppe_zobid = anw.itsec_gruppe_id");
//		sql.append(" where ");
//		sql.append(" anw.del_timestamp is null");
//		sql.append(" and anw.template = -1");
//		sql.append(" and anwkat1.anwendung_kat1_id = 5");
//		sql.append(" order by anw.anwendung_name");
		
		
		sql.append("SELECT ANW.Anwendung_Id, ANW.Anwendung_Name, ANW.itset, ANW.itsec_gruppe_id, AK2.Anwendung_Kat1_Id ");
		sql.append("FROM ANWENDUNG ANW ");
		sql.append("INNER JOIN Anwendung_KAT2 AK2 ON ANW.Anwendung_Kat2_Id = AK2.Anwendung_Kat2_Id ");
//		sql.append(" join itverbund_itsecgrp i on i.itsec_gruppe_zobid = anw.itsec_gruppe_id");
//		sql.append("WHERE AK2.Anwendung_Kat1_Id = 5 ");
//		sql.append("AND ANW.Itset = 10002 ");
		sql.append("WHERE ANW.Del_Quelle is NULL ");//AND
		sql.append("AND ANW.Template = -1 ");
//		sql.append("AND ANW.Anwendung_Id <> 8675--11568 10346 um nicht template auf sich selbst ausw?hlen zu k?nnen");
//		sql.append("AND pck_SISec.ReferencedBy('ANWENDUNG', ANW.Anwendung_Id, 2) = 0");
		sql.append("ORDER BY ANW.Anwendung_Name");
		
		
		try {
			tx = session.beginTransaction();
			@SuppressWarnings("deprecation")
			Connection conn = session.connection();

			selectStmt = conn.createStatement();
			ResultSet rsMessage = selectStmt.executeQuery(sql.toString());

			if (null != rsMessage) {
				while (rsMessage.next()) {
					ReferenzDTO ref = new ReferenzDTO();
					ref.setId(rsMessage.getLong("ANWENDUNG_ID"));
					ref.setName(rsMessage.getString("ANWENDUNG_NAME"));
					ref.setItsetId(rsMessage.getLong("ITSET"));//it_verbund_zob_id1
					ref.setItsecGroupId(rsMessage.getLong("ITSEC_GRUPPE_ID"));//itsec_gruppe_id
					ref.setCiKat1(rsMessage.getLong("ANWENDUNG_KAT1_ID"));
					listResult.add(ref);
				}
			}

			if (null != rsMessage) {
				rsMessage.close();
			}
			if (null != selectStmt) {
				selectStmt.close();
			}
			if (null != conn) {
				conn.close();
			}
			tx.commit();
		} catch (Exception e) {
			if (tx != null && tx.isActive()) {
				try {
					// Second try catch as the rollback could fail as well
					tx.rollback();
				} catch (HibernateException e1) {
					log.error(e1.getMessage());
				}
				// throw again the first exception
				// throw e;
			}
		}
		return listResult;
	}

	/**
	 * find the application upstream
	 * @return
	 */
	public static List<ViewDataDTO> findApplicationUpStream(Long applicationId) {
		ArrayList<ViewDataDTO> listResult = new ArrayList<ViewDataDTO>();

		boolean commit = false;
		Transaction tx = null;
		Statement selectStmt = null;
		Session session = HibernateUtil.getSession();

//		Connection conn = null;

		StringBuffer sql = new StringBuffer();

		sql.append("select");
		sql.append(" anwanw.app_higher_id");
		sql.append(" , anw.anwendung_name");
		sql.append(" , anwkat2.anwendung_kat2_txt");
		sql.append(" , anwkat1.anwendung_kat1_en");
		sql.append(" from anw_anw anwanw");
		sql.append(" join anwendung anw on anwanw.app_higher_id = anw.anwendung_id");
		sql.append(" join anwendung_kat2 anwkat2 on anw.anwendung_kat2_id = anwkat2.anwendung_kat2_id");
		sql.append(" join anwendung_kat1 anwkat1 on anwkat2.anwendung_kat1_id= anwkat1.anwendung_kat1_id");
		sql.append(" where anwanw.app_lower_id = ");
		sql.append(applicationId);
		sql.append("  and anwanw.del_quelle is null");
		
		try {
			tx = session.beginTransaction();
			@SuppressWarnings("deprecation")
			Connection conn = HibernateUtil.getSession().connection();

			selectStmt = conn.createStatement();
			ResultSet rsMessage = selectStmt.executeQuery(sql.toString());

			if (null != rsMessage) {
				while (rsMessage.next()) {
					ViewDataDTO dto = new ViewDataDTO();
					dto.setId(EMPTY +rsMessage.getLong("APP_HIGHER_ID"));

					dto.setText(rsMessage
							.getString("ANWENDUNG_NAME"));
					dto.setType(rsMessage
							.getString("ANWENDUNG_KAT1_EN"));

					listResult.add(dto);
				}
				commit = true;
			}

			if (null != rsMessage) {
				rsMessage.close();
			}
			if (null != selectStmt) {
				selectStmt.close();
			}
			if (null != conn) {
				conn.close();
			}
		} catch (Exception e) {
			//
			System.out.println(e.toString());
		}
		finally {
			HibernateUtil.close(tx, session, commit);
		}
		
		return listResult;
	}


	public static List<ViewDataDTO> findApplicationDownStream(Long applicationId) {
		ArrayList<ViewDataDTO> listResult = new ArrayList<ViewDataDTO>();

		boolean commit = false;
		Transaction tx = null;
		Statement selectStmt = null;
		Session session = HibernateUtil.getSession();
//		Connection conn = null;

		StringBuffer sql = new StringBuffer();

		sql.append("select");
		sql.append(" anwanw.app_lower_id");
		sql.append(" , anw.anwendung_name");
		sql.append(" , anwkat2.anwendung_kat2_txt");
		sql.append(" , anwkat1.anwendung_kat1_en");
		sql.append(" from anw_anw anwanw");
		sql.append(" join anwendung anw on anwanw.app_lower_id = anw.anwendung_id");
		sql.append(" join anwendung_kat2 anwkat2 on anw.anwendung_kat2_id = anwkat2.anwendung_kat2_id");
		sql.append(" join anwendung_kat1 anwkat1 on anwkat2.anwendung_kat1_id= anwkat1.anwendung_kat1_id");
		sql.append(" where anwanw.app_higher_id = ");
		sql.append(applicationId);
		sql.append("  and anwanw.del_quelle is null");
		
		try {
			tx = session.beginTransaction();
			@SuppressWarnings("deprecation")
			Connection conn = HibernateUtil.getSession().connection();

			selectStmt = conn.createStatement();
			ResultSet rsMessage = selectStmt.executeQuery(sql.toString());

			if (null != rsMessage) {
				while (rsMessage.next()) {
					ViewDataDTO dto = new ViewDataDTO();
					dto.setId(EMPTY +rsMessage.getLong("APP_LOWER_ID"));
					
					dto.setText(rsMessage.getString("ANWENDUNG_NAME"));
					dto.setType(rsMessage.getString("ANWENDUNG_KAT1_EN"));

					listResult.add(dto);
				}
				commit = true;
			}

			if (null != rsMessage) {
				rsMessage.close();
			}
			if (null != selectStmt) {
				selectStmt.close();
			}
			if (null != conn) {
				conn.close();
			}
		} catch (Exception e) {
			//
		}
		finally {
			HibernateUtil.close(tx, session, commit);
		}

		return listResult;
	}
 

	public static List<ViewDataDTO> findApplicationProcess(Long applicationId) {

		ArrayList<ViewDataDTO> listResult = new ArrayList<ViewDataDTO>();

		boolean commit = false;
		Transaction tx = null;
		Statement selectStmt = null;
		Session session = HibernateUtil.getSession();

//		Connection conn = null;

		StringBuffer sql = new StringBuffer();

		sql.append("select");
		sql.append("  anwprocess.process_id");
		sql.append(" , anwprocess.application_id");
		sql.append(" , proc.process_name");
		sql.append(" , proc.process_owner");
		sql.append(" , proc.process_manager");
		sql.append(" , proc.del_timestamp");
		sql.append(" from application_process anwprocess");
		sql.append(" join process proc on anwprocess.process_id = proc.process_id");
		sql.append(" where anwprocess.application_id  = ");
		sql.append(applicationId);
		sql.append("  and anwprocess.del_quelle is null");
	
		try {
			tx = session.beginTransaction();
			@SuppressWarnings("deprecation")
			Connection conn = HibernateUtil.getSession().connection();

			selectStmt = conn.createStatement();
			ResultSet rsMessage = selectStmt.executeQuery(sql.toString());

			if (null != rsMessage) {
				while (rsMessage.next()) {
					ViewDataDTO dto = new ViewDataDTO();
					dto.setId(EMPTY +rsMessage.getLong("PROCESS_ID"));
					
					StringBuffer sb = new StringBuffer();
					sb.append(rsMessage.getString("PROCESS_NAME"));
					
//					if (null != rsMessage.getTimestamp("DEL_TIMESTAMP")) {
//						sb.append(" (DELETED)");
//					}					
					dto.setText(sb.toString());

					listResult.add(dto);
				}
				commit = true;
			}

			if (null != rsMessage) {
				rsMessage.close();
			}
			if (null != selectStmt) {
				selectStmt.close();
			}
			if (null != conn) {
				conn.close();
			}
		} catch (Exception e) {
			//
		}
		finally {
			HibernateUtil.close(tx, session, commit);
		}

		return listResult;
	}

	
	public static List<ViewDataDTO> findApplicationConnections(Long applicationId) {
		ArrayList<ViewDataDTO> listResult = new ArrayList<ViewDataDTO>();

		boolean commit = false;
		Transaction tx = null;
		Statement selectStmt = null;
		Session session = HibernateUtil.getSession();

//		Connection conn = null;

		StringBuffer sql = new StringBuffer();

		sql.append("select 'H' as DIRECTION, dwh_entity.* from dwh_entity where upper(TYPE) <> 'PROCESS' and (TABLE_ID, CI_ID) IN");
		sql.append(" (select HIGHER_TABLE_ID, HIGHER_ci_ID from DWH_RELATION where LOWER_TABLE_ID=2 and LOWER_CI_ID=").append(applicationId).append(") and UPPER(deleted) = 'NO'");
		sql.append(" UNION");
		sql.append(" select 'L' as DIRECTION, dwh_entity.* from dwh_entity where (TABLE_ID, CI_ID) IN");
		sql.append(" (select LOWER_TABLE_ID, LOWER_ci_ID from DWH_RELATION where upper(TYPE) <> 'PROCESS' and HIGHER_TABLE_ID=2 and HIGHER_CI_ID=").append(applicationId).append(") and UPPER(deleted) = 'NO'");
		sql.append(" order by 1,2,4");
	
		try {
			tx = session.beginTransaction();
			@SuppressWarnings("deprecation")
			Connection conn = HibernateUtil.getSession().connection();

			selectStmt = conn.createStatement();
			ResultSet rsMessage = selectStmt.executeQuery(sql.toString());

			if (null != rsMessage) {
				while (rsMessage.next()) {
					ViewDataDTO dto = new ViewDataDTO();
					dto.setDirection(rsMessage.getString("DIRECTION"));
					dto.setType(rsMessage.getString("TYPE"));
					dto.setId(rsMessage.getString("ID"));
					dto.setName(rsMessage.getString("NAME"));
					dto.setAlias(rsMessage.getString("ASSET_ID_OR_ALIAS"));
					dto.setResponsible(rsMessage.getString("RESPONSIBLE"));
					dto.setSubResponsible(rsMessage.getString("SUB_RESPONSIBLE"));
					dto.setCategory(rsMessage.getString("CATEGORY"));
					dto.setTableId(rsMessage.getLong("TABLE_ID"));
					dto.setCiId(rsMessage.getLong("CI_ID"));
					
					dto.setGroupsort(rsMessage.getString("DIRECTION") +"::" + rsMessage.getString("TYPE"));

					listResult.add(dto);
				}
				commit = true;
			}

			if (null != rsMessage) {
				rsMessage.close();
			}
			if (null != selectStmt) {
				selectStmt.close();
			}
			if (null != conn) {
				conn.close();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		finally {
			HibernateUtil.close(tx, session, commit);
		}

		return listResult;
	}

	/**
	 * find the connection entries for the picker
	 * @return
	 */
	public static List<ViewDataDTO> findConnectionEntries(String type, String searchparam) {
		ArrayList<ViewDataDTO> listResult = new ArrayList<ViewDataDTO>();

		boolean commit = false;
		Transaction tx = null;
		Statement selectStmt = null;
		Session session = HibernateUtil.getSession();

//		Connection conn = null;
		searchparam = searchparam.toUpperCase();
		StringBuffer sql = new StringBuffer();

		sql.append("select * from dwh_entity");
		sql.append(" where upper(type) = upper('").append(type).append("') and UPPER(deleted) = 'NO'"); 
		sql.append(" and (upper(name) like ('%").append(searchparam).append("%') or upper(asset_id_or_alias) like ('%").append(searchparam).append("%')) order by name");
		
		try {
			tx = session.beginTransaction();
			@SuppressWarnings("deprecation")
			Connection conn = HibernateUtil.getSession().connection();

			selectStmt = conn.createStatement();
			ResultSet rsMessage = selectStmt.executeQuery(sql.toString());

			if (null != rsMessage) {
				while (rsMessage.next()) {
					ViewDataDTO dto = new ViewDataDTO();
					dto.setDirection(rsMessage.getString("DIRECTION"));
					dto.setType(rsMessage.getString("TYPE"));
					dto.setId(rsMessage.getString("ID"));
					dto.setName(rsMessage.getString("NAME"));
					dto.setAlias(rsMessage.getString("ASSET_ID_OR_ALIAS"));
					dto.setResponsible(rsMessage.getString("RESPONSIBLE"));
					dto.setSubResponsible(rsMessage.getString("SUB_RESPONSIBLE"));
					dto.setCategory(rsMessage.getString("CATEGORY"));
					dto.setTableId(rsMessage.getLong("TABLE_ID"));
					dto.setCiId(rsMessage.getLong("CI_ID"));

					listResult.add(dto);
				}
				commit = true;
			}

			if (null != rsMessage) {
				rsMessage.close();
			}
			if (null != selectStmt) {
				selectStmt.close();
			}
			if (null != conn) {
				conn.close();
			}
		} catch (Exception e) {
			//
		}
		finally {
			HibernateUtil.close(tx, session, commit);
		}

		return listResult;
	}

	
	public static List<ViewDataDTO> findApplicationItSystems(Long applicationId) {
		ArrayList<ViewDataDTO> listResult = new ArrayList<ViewDataDTO>();

		boolean commit = false;
		Transaction tx = null;
		Statement selectStmt = null;
		Session session = HibernateUtil.getSession();

//		Connection conn = null;

		StringBuffer sql = new StringBuffer();

		sql.append("select");
		sql.append(" anwits.it_system_id");
		sql.append(" , its.it_system_name");
		sql.append(" from  anwend_it_system anwits");
		sql.append(" join it_system its on its.it_system_id = anwits.it_system_id");
		sql.append(" where   anwits.anwendung_id = ");
		sql.append(applicationId);
		sql.append(" and anwits.del_quelle is null");
		
		
		try {
			tx = session.beginTransaction();
			@SuppressWarnings("deprecation")
			Connection conn = HibernateUtil.getSession().connection();

			selectStmt = conn.createStatement();
			ResultSet rsMessage = selectStmt.executeQuery(sql.toString());

			if (null != rsMessage) {
				while (rsMessage.next()) {
					ViewDataDTO dto = new ViewDataDTO();
					dto.setId(EMPTY +rsMessage.getLong("IT_SYSTEM_ID"));
					dto.setText(rsMessage.getString("IT_SYSTEM_NAME"));
					
					listResult.add(dto);
				}
				commit = true;
			}

			if (null != rsMessage) {
				rsMessage.close();
			}
			if (null != selectStmt) {
				selectStmt.close();
			}
			if (null != conn) {
				conn.close();
			}
		} catch (Exception e) {
			//
			System.out.println(e.toString());
		}
		finally {
			HibernateUtil.close(tx, session, commit);
		}

		return listResult;
	}

	//ApplicationDTO
	public static List<CiItemDTO> findApplications(
			String query, boolean showDeleted, String queryMode, String advsearchappowner, String advsearchappownerHidden, String advsearchappdelegate, 
			String advsearchappdelegateHidden, String advsearchciowner, String advsearchciownerHidden, String advsearchcidelegate, 
			String advsearchcidelegateHidden, boolean onlyapplications, String sort, String dir,
			Integer tableId, Integer ciSubType, String advsearchdescription, Long advsearchoperationalstatusid,
			Long advsearchapplicationcat2id,
			Long advsearchlifecyclestatusid,
			Long advsearchprocessid,
			String template,
			String advsearchsteward,
			String advsearchstewardHidden,
			String barRelevance,
			String organisationalScope,
			String itSetId,
			String itSecGroupId,
			String source,
			String businessEssentialId,
			String ciTypeOptions, String itSetOptions, String descriptionOptions,
			String appOwnerOptions, String appOwnerDelegateOptions, String appStewardOptions, String ciOwnerOptions, String ciOwnerDelegateOptions,
			String generalUsageOptions, String itCategoryOptions, String lifecycleStatusOptions, String organisationalScopeOptions,
			String itSecGroupOptions, String processOptions, String sourceOptions, String businessEssentialOptions,String complainceGR1435,String complainceICS) {
		
		// Start Adding for C0000241362 
		long complainceGR1435Long=0;
		long complainceICSLong=0;
		System.out.println("complainceGR1435"+complainceGR1435);
		System.out.println("complainceICS"+complainceICS);
		if(complainceGR1435!=null&&complainceGR1435.equalsIgnoreCase("Yes"))
			
			complainceGR1435Long = -1;
		if(complainceGR1435!=null&&complainceGR1435.equalsIgnoreCase("No"))
			complainceGR1435Long=0;
		if(complainceICS!=null&&complainceICS.equalsIgnoreCase("Yes"))
			complainceICSLong = -1;
		if(complainceICS!=null&&complainceICS.equalsIgnoreCase("No"))
			complainceICSLong=0;
		// End Adding for C0000241362
		if (null != advsearchappownerHidden) {//advsearchappowner
			advsearchappownerHidden = advsearchappownerHidden.replace("*", "%");//advsearchappowner
		}
		if (null != advsearchappdelegate) {
			advsearchappdelegate = advsearchappdelegate.replace("*", "%");
		}
		if (null != advsearchciownerHidden) {//advsearchciowner
			advsearchciownerHidden = advsearchciownerHidden.replace("*", "%");//advsearchciowner
		}
		if (null != advsearchcidelegate) {
			advsearchcidelegate = advsearchcidelegate.replace("*", "%");
		}
		if (null != advsearchstewardHidden) {//advsearchsteward
			advsearchstewardHidden = advsearchstewardHidden.replace("*", "%");//advsearchsteward
		}
		
		//ApplicationDTO
		ArrayList<CiItemDTO> listResult = new ArrayList<CiItemDTO>();

		Transaction tx = null;
		Statement selectStmt = null;
		Session session = HibernateUtil.getSession();

//		Connection conn = null;

		StringBuffer sql = new StringBuffer();

		sql.append("SELECT anw.* , kat2.anwendung_kat2_txt , kat1.anwendung_kat1_en from ANWENDUNG anw");
		sql.append(" left join anwendung_kat2 kat2 on anw.anwendung_kat2_id = kat2.anwendung_kat2_id");
		sql.append(" left join anwendung_kat1 kat1 on kat2.anwendung_kat1_id = kat1.anwendung_kat1_id");
		
		if (null != advsearchprocessid) {
			sql.append(" left join application_process appproc on anw.ANWENDUNG_ID = appproc.application_id");
		}
		sql.append(" where");
		
		// ANWENDUNG_NAME
		sql.append(" (UPPER (anw.ANWENDUNG_NAME) like '");
		
		// alles andere f?hrt zu einer exacten Suche
		
		if (CiEntitiesHbn.isLikeStart(queryMode)) {
			sql.append("%");
		}
		
		sql.append(query.toUpperCase());
		
		if (CiEntitiesHbn.isLikeEnd(queryMode)) {
			sql.append("%");
		}
		sql.append("'");
		
		
		// ALIAS
		sql.append(" or UPPER (anw.ALIAS) like '");
		

		
		if (CiEntitiesHbn.isLikeStart(queryMode)) {
			sql.append("%");
		}
		
		sql.append(query.toUpperCase());
		
		if (CiEntitiesHbn.isLikeEnd(queryMode)) {
			sql.append("%");
		}
		sql.append("' )");
		
		// start Adding for C0000241362 
		
		// RELEVANCE_ICS
if(complainceICS!=null&&complainceICS.length()>0)
				sql.append(" and UPPER (anw.RELEVANCE_ICS) = '"+complainceICSLong+"'");
				System.out.println("complainceGR1435Long appened"+complainceICSLong);
				// RELEVANZ_ITSEC
				if(complainceGR1435!=null&&complainceGR1435.length()>0)
				sql.append(" and UPPER (anw.RELEVANZ_ITSEC) = '"+complainceGR1435Long+"'");
				
//System.out.println("complainceGR1435Long appened"+complainceGR1435Long);
				// End Adding for C0000241362 
		boolean isNot = false;

		
		if (StringUtils.isNotNullOrEmpty(advsearchappownerHidden)) {//advsearchappowner
			isNot = isNot(appOwnerOptions);
			
			sql.append(" and (");
			if(isNot)
				sql.append("UPPER(anw.APPLICATION_OWNER) is null or ");
			
			sql.append("UPPER(anw.APPLICATION_OWNER) "+ getLikeNotLikeOperator(isNot) +" '").append(advsearchappownerHidden.toUpperCase()).append("')");//advsearchappowner
		}
		
		if (StringUtils.isNotNullOrEmpty(advsearchappdelegate)) {
			boolean isCwid = advsearchappdelegate.indexOf(')') > -1;
			String delegate = isCwid ? advsearchappdelegateHidden : advsearchappdelegate;//gruppe oder cwid?
			
			isNot = isNot(appOwnerDelegateOptions);
			
			sql.append(" and (");
			if(isNot)
				sql.append("UPPER(anw.APPLICATION_OWNER_DELEGATE) is null or ");
			
			sql.append("UPPER(anw.APPLICATION_OWNER_DELEGATE) "+ getLikeNotLikeOperator(isNot) +" '").append(delegate.toUpperCase()).append("')");
			
			if(!isCwid)
				sql.insert(sql.length() - 2, '%');
			
			
			
//			isNot = isNot(appOwnerDelegateOptions);
//			sql.append(" and UPPER(anw.APPLICATION_OWNER_DELEGATE) "+ getLikeNotLikeOperator(isNot) +" '").append(delegate.toUpperCase()).append("'");
//			if(advsearchappdelegate.indexOf('_') == -1)
//				sql.insert(sql.length() - 1, '%');
		}

		if (StringUtils.isNotNullOrEmpty(advsearchciownerHidden)) {//advsearchciowner
			isNot = isNot(ciOwnerOptions);
			
			sql.append(" and (");
			if(isNot)
				sql.append("UPPER(anw.CWID_VERANTW_BETR) is null or ");
			
			sql.append("UPPER(anw.CWID_VERANTW_BETR) "+ getLikeNotLikeOperator(isNot) +" '").append(advsearchciownerHidden.toUpperCase()).append("')");//advsearchciowner
		}
		
		if (StringUtils.isNotNullOrEmpty(advsearchcidelegate)) {//advsearchcidelegateHidden
			boolean isCwid = advsearchcidelegate.indexOf(')') > -1;
			String delegate = isCwid ? advsearchcidelegateHidden : advsearchcidelegate;//gruppe oder cwid?
			
			isNot = isNot(ciOwnerDelegateOptions);
			
			sql.append(" and (");
			if(isNot)
				sql.append("UPPER(anw.SUB_RESPONSIBLE) is null or ");
			
			sql.append("UPPER(anw.SUB_RESPONSIBLE) "+ getLikeNotLikeOperator(isNot) +" '").append(delegate.toUpperCase()).append("')");
						
			if(!isCwid)
				sql.insert(sql.length() - 2, '%');
		}

		if (StringUtils.isNotNullOrEmpty(advsearchstewardHidden)) {//advsearchsteward
			isNot = isNot(appStewardOptions);
			
			sql.append(" and (");
			if(isNot)
				sql.append("UPPER(anw.APPLICATION_STEWARD) is null or ");
			
			sql.append("UPPER(anw.APPLICATION_STEWARD) "+ getLikeNotLikeOperator(isNot) +" '").append(advsearchstewardHidden.toUpperCase()).append("')");//advsearchsteward
		}

		if (StringUtils.isNotNullOrEmpty(advsearchdescription)) {
			isNot = isNot(descriptionOptions);
			sql.append(" and UPPER(anw.COMMENTS) "+ getLikeNotLikeOperator(isNot) +" '%").append(advsearchdescription.toUpperCase()).append("%'");
		}
		
		
		
		if (null != advsearchapplicationcat2id) {
			isNot = isNot(itCategoryOptions);
			sql.append(" and NVL(anw.ANWENDUNG_KAT2_ID, 0) "+ getEqualNotEqualOperator(isNot) +" ").append(advsearchapplicationcat2id.longValue());
		}
		if (null != advsearchoperationalstatusid) {
			isNot = isNot(generalUsageOptions);
			sql.append(" and NVL(anw.EINSATZ_STATUS_ID, 0) "+ getEqualNotEqualOperator(isNot) +" ").append(advsearchoperationalstatusid.longValue());
		}
		if (null != advsearchlifecyclestatusid) {
			isNot = isNot(lifecycleStatusOptions);
			sql.append(" and NVL(anw.LC_STATUS_ID, 0) "+ getEqualNotEqualOperator(isNot) +" ").append(advsearchlifecyclestatusid.longValue());
		}
		if(StringUtils.isNotNullOrEmpty(itSetId)) {
			isNot = isNot(itSetOptions);
			sql.append(" and NVL(anw.ITSET, 0) "+ getEqualNotEqualOperator(isNot) +" ").append(Long.parseLong(itSetId));
		}
		if(StringUtils.isNotNullOrEmpty(itSecGroupId)) {
			Long itsec = Long.parseLong(itSecGroupId);
			isNot = isNot(itSecGroupOptions);
			if(1234567<=itsec && itsec<=1234579){
				sql.append(" and NVL(anw.ITSEC_GRUPPE_ID, -1) "+ getEqualNotEqualOperator(isNot) +" ").append(10136);
			}else{
				sql.append(" and NVL(anw.ITSEC_GRUPPE_ID, -1) "+ getEqualNotEqualOperator(isNot) +" ").append(Long.parseLong(itSecGroupId));
			}
		}
		if(StringUtils.isNotNullOrEmpty(source)) {
			isNot = isNot(sourceOptions);
			sql.append(" and anw.INSERT_QUELLE "+ getEqualNotEqualOperator(isNot) +" '").append(source).append("'");
		}
		if(StringUtils.isNotNullOrEmpty(businessEssentialId)) {
			isNot = isNot(businessEssentialOptions);
			sql.append(" and anw.BUSINESS_ESSENTIAL_ID "+ getEqualNotEqualOperator(isNot) +" ").append(Long.parseLong(businessEssentialId));
		}

		if (null != template) {
			String searchTemplate = null;
			if ("Y".equals(template)) {
				searchTemplate = "-1";
			}
			else if ("N".equals(template)) {
				searchTemplate = "0";
			}
			
			if (null != searchTemplate) {
				sql.append(" and NVL(anw.template, 0) = ").append(searchTemplate);
			}
		}
		
		if (null != advsearchprocessid) {
			isNot = isNot(processOptions);
			sql.append(" and appproc.del_quelle is null");
			sql.append(" and appproc.process_id "+ getEqualNotEqualOperator(isNot) +" ").append(advsearchprocessid.longValue());
		}
		
		// TODO LOCATION
//		if (StringUtils.isNotNullOrEmpty(advsearchcidelegate)) {
//			sql.append(" and UPPER(anw.SUB_RESPONSIBLE) = '").append(advsearchcidelegate.toUpperCase()).append("'");
//		}
		
		if (onlyapplications) {
			sql.append(" and kat1.anwendung_kat1_id="+AirKonstanten.APPLICATION_CAT1_APPLICATION);
		}
		
		if (null != ciSubType) {
			isNot = isNot(ciTypeOptions);
			sql.append(" and kat1.anwendung_kat1_id "+ getEqualNotEqualOperator(isNot) +" ").append(ciSubType);
		}

		if (null != barRelevance && !EMPTY.equals(barRelevance)) {
			if ("U".equals(barRelevance)) {
				sql.append(" and anw.BAR_RELEVANCE_Y_N is null");
			}
			else {
				sql.append(" and anw.BAR_RELEVANCE_Y_N='").append(barRelevance).append("'");
			}
		}
		
		if (null != organisationalScope && !EMPTY.equals(organisationalScope)) {
			isNot = isNot(organisationalScopeOptions);
			
			sql.append(" and (");
			
			int count = 0;
			StringTokenizer tk = new StringTokenizer(organisationalScope, ",");
			while (tk.hasMoreTokens()) {
				String temp = tk.nextToken();
				if (count != 0) {
					sql.append(" or ");
				}
				sql.append("anw.ORG_SCOPE "+ getEqualNotEqualOperator(isNot) +" '").append(temp).append("'");
				count++;
			}
			
			sql.append(")");
		}

		if (!showDeleted) {
			sql.append("  and anw.DEL_QUELLE is null");
		}
		sql.append(" order by nlssort(");
		if (StringUtils.isNotNullOrEmpty(sort)) {
			if ("applicationName".equals(sort)) {
				sql.append("anw.ANWENDUNG_NAME");
			}
			else if ("location".equals(sort)) {
				sql.append("anw.ANWENDUNG_NAME");
			}			
			else if ("name".equals(sort)) {
				sql.append("anw.ANWENDUNG_NAME");
			}			
			else if ("applicationAlias".equals(sort)) {
				sql.append("anw.ALIAS");
			}
			else if ("applicationCat1Txt".equals(sort)) {
				sql.append("kat1.anwendung_kat1_en");
			}
			else if ("applicationCat2Txt".equals(sort)) {
				sql.append("kat2.anwendung_kat2_txt");
			}
			else if ("ciOwner".equals(sort)) {
				sql.append("anw.CWID_VERANTW_BETR");
			}
			else if ("ciOwnerDelegate".equals(sort)) {
				sql.append("anw.SUB_RESPONSIBLE");
			}
			else if ("applicationOwner".equals(sort)) {
				sql.append("anw.APPLICATION_OWNER");
			}
			else if ("applicationOwnerDelegate".equals(sort)) {
				sql.append("anw.APPLICATION_OWNER_DELEGATE");
			}
			else if ("applicationSteward".equals(sort)) {
				sql.append("anw.APPLICATION_STEWARD");
			}			
			else {
				sql.append(sort);
			}
			sql.append(", 'NLS_SORT = GENERIC_M')");
			
			if (StringUtils.isNotNullOrEmpty(dir)) {
				sql.append(" ").append(dir);
			}
		}
		else {
			sql.append("anw.ANWENDUNG_NAME");			
			sql.append(", 'NLS_SORT = GENERIC_M')");
		}
		
		try {
			tx = session.beginTransaction();
			@SuppressWarnings("deprecation")
			Connection conn = session.connection();

			selectStmt = conn.createStatement();
			System.out.println(sql.toString());
			ResultSet rset = selectStmt.executeQuery(sql.toString());

			if (null != rset) {
				while (rset.next()) {
					long anwendungId = rset.getLong("ANWENDUNG_ID");
//					String barApplicationId = rset.getString("BAR_APPLICATION_ID");
					String anwendungName = rset.getString("ANWENDUNG_NAME");
					String anwendungAlias = rset.getString("ALIAS");
					//--
					String responsible = rset.getString("CWID_VERANTW_BETR");
					String subResponsible = rset.getString("SUB_RESPONSIBLE");
					String applicationCat2Txt = rset.getString("anwendung_kat2_txt");
					String applicationCat1Txt = rset.getString("anwendung_kat1_en");
					String applicationOwner = rset.getString("APPLICATION_OWNER");
					String applicationOwnerDelegate = rset.getString("APPLICATION_OWNER_DELEGATE");
					String applicationSteward = rset.getString("APPLICATION_STEWARD");
					String deleteQuelle = rset.getString("DEL_QUELLE");
					CiItemDTO anw = new CiItemDTO();//ApplicationDTO

					if(AirKonstanten.IS_TEMPLATE==rset.getInt("TEMPLATE")){
						anw.setIsTemplate(AirKonstanten.YES);
					}else{
						anw.setIsTemplate(AirKonstanten.NO);
					}
					anw.setId(anwendungId);
//					anw.setBarApplicationId(barApplicationId);
					anw.setName(anwendungName);
					anw.setAlias(anwendungAlias);
					anw.setCiOwner(responsible);//setResponsible
					anw.setCiOwnerDelegate(subResponsible);//setSubResponsible
					anw.setApplicationCat1Txt(applicationCat1Txt);
					anw.setApplicationCat2Txt(applicationCat2Txt);
					anw.setApplicationOwner(applicationOwner);
					anw.setApplicationOwnerDelegate(applicationOwnerDelegate);
					anw.setApplicationSteward(applicationSteward);
					anw.setTableId(AirKonstanten.TABLE_ID_APPLICATION);
					anw.setDeleteQuelle(deleteQuelle);
					
//					if (null != anw.getDeleteQuelle()) {
//						anw.setName(anw.getName() + " (DELETED)");
//					}
					
					listResult.add(anw);
				}
			}

			if (null != rset) {
				rset.close();
			}
			if (null != selectStmt) {
				selectStmt.close();
			}
			if (null != conn) {
				conn.close();
			}
			tx.commit();
		} catch (Exception e) {
			log.error(e.toString());
			if (tx != null && tx.isActive()) {
				try {
					// Second try catch as the rollback could fail as well
					tx.rollback();
				} catch (HibernateException e1) {
					log.error(e1.getMessage());
				}
			}
		}
		return listResult;
	}

	
	public static List<HistoryViewDataDTO> findApplicationHistory(Integer tableId, Long ciId) {
		ArrayList<HistoryViewDataDTO> listResult = new ArrayList<HistoryViewDataDTO>();

		boolean commit = false;
		Transaction tx = null;
		Statement selectStmt = null;
		Session session = HibernateUtil.getSession();

//		Connection conn = null;

//		StringBuffer sql = new StringBuffer();
//
//		sql.append("select");
//		sql.append(" * from history ");
//		sql.append(" where table_id = ").append(ApplreposConstants.TABLE_ID_APPLICATION).append(" and primarykey = ");
//		sql.append(applicationId);
//		sql.append(" order by datetime desc");
		
		String lang = "'E'";
		
		
//		String sql = "SELECT * FROM TABLE(PCK_AIR.ft_history("+applicationId+"))";
		
		String sql = "SELECT * FROM TABLE (pck_History.Read (" + tableId +", " + ciId + ", " + lang + ", 'N', 'Y')) ORDER BY 1 DESC";
		
		try {
			tx = session.beginTransaction();

			@SuppressWarnings("deprecation")
			Connection conn = HibernateUtil.getSession().connection();

			selectStmt = conn.createStatement();
			ResultSet rsMessage = selectStmt.executeQuery(sql.toString());

			Long id = new Long(0);
			
			if (null != rsMessage) {
				while (rsMessage.next()) {
					HistoryViewDataDTO dto = new HistoryViewDataDTO();
					dto.setId(id++);
					/*dto.setTableId(rsMessage.getLong("TABLE_ID"));
					dto.setCiId(rsMessage.getLong("PRIMARYKEY"));
					dto.setDatetime(rsMessage.getTimestamp("DATETIME").toString());	//toLocaleString TODO History Datumsformat
					dto.setChangeSource(rsMessage.getString("CHANGESOURCE"));
					dto.setChangeDBUser(rsMessage.getString("DBUSER"));
					dto.setChangeUserCWID(rsMessage.getString("CHANGEUSER"));
					dto.setChangeUserName(EMPTY);
					
					String change = rsMessage.getString("CHANGES");
					
					if (null != change && change.contains("|")) {
						 StringTokenizer strTk  = new StringTokenizer(change, "|");

						 while (strTk.hasMoreTokens()) {
							 String temp = strTk.nextToken().toString();
							 
							 if (2 < temp.length()) {
								 String changeAttributeName = temp.substring(1, temp.indexOf(": "));
								 String changeAttributeOldValue = temp.substring(temp.indexOf(": ")+2, temp.indexOf(" => "));
								 String changeAttributeNewValue = temp.substring(temp.indexOf(" => ")+4);
								 
								 dto.setChangeAttributeName(changeAttributeName);
								 dto.setChangeAttributeOldValue(changeAttributeOldValue);
								 dto.setChangeAttributeNewValue(changeAttributeNewValue);
							 }
							 
							 listResult.add(dto);
							 dto = new HistoryViewDataDTO();
							 
								dto.setDatetime(EMPTY);
								dto.setChangeSource(EMPTY);
								dto.setChangeDBUser(EMPTY);
								dto.setChangeUserCWID(EMPTY);
								dto.setChangeUserName(EMPTY);
							 
							 dto.setId(id++);
						 }
						
					}*/
					
					dto.setCiId(rsMessage.getLong("CI_ID"));
//					dto.setDatetime(rsMessage.getTimestamp("DATETIME").toString());	//toLocaleString TODO History Datumsformat
//					dto.setChangeSource(rsMessage.getString("SOURCE"));
//					dto.setChangeUserCWID(rsMessage.getString("USERNAME"));
//					dto.setChangeDBUser(rsMessage.getString("DBUSER"));
//					dto.setChangeAttributeName(rsMessage.getString("COLUMN_NAME"));
//					dto.setChangeAttributeOldValue(rsMessage.getString("OLD_VALUE"));
//					dto.setChangeAttributeNewValue(rsMessage.getString("NEW_VALUE"));
//					dto.setInfoType(rsMessage.getString("TABLE_NAME"));
					
//					dto.setCiId(ciId);
					dto.setDatetime(rsMessage.getString("DATEVALUE"));	//toLocaleString TODO History Datumsformat
					dto.setChangeSource(rsMessage.getString("CHANGESOURCE"));
					dto.setChangeUserCWID(rsMessage.getString("CWID"));
					dto.setChangeDBUser(rsMessage.getString("CHANGEUSER"));
					dto.setChangeObjectName(rsMessage.getString("OBJECT_NAME"));
					dto.setChangeAttributeName(rsMessage.getString("ATTRIBUTE_GLOSSARY"));
					dto.setChangeAttributeOldValue(rsMessage.getString("OLD_VALUE"));
					dto.setChangeAttributeNewValue(rsMessage.getString("NEW_VALUE"));
					dto.setInfoType(rsMessage.getString("TABLE_READABLE"));
					
					listResult.add(dto);
				}
				commit = true;
			}

			if (null != rsMessage) {
				rsMessage.close();
			}
			if (null != selectStmt) {
				selectStmt.close();
			}
			if (null != conn) {
				conn.close();
			}
			//System.out.println("In History 2");
			//Thread.sleep(30000);//emria
			//System.out.println("In History 3");
		} catch (Exception e) {
			System.out.println(e.toString());
			//
		}
		finally {
			HibernateUtil.close(tx, session, commit);
		}

		 
		return listResult;
	}

	
	
	/**
	 * updates an existing entry
	 * 
	 * @param cwid
	 * @param dto
	 * @return
	 */
	public static ApplicationEditParameterOutput copyApplication(String cwid, Long applicationIdSource, Long applicationIdTarget, String ciNameTarget, String ciAliasTarget) {
		ApplicationEditParameterOutput output = new ApplicationEditParameterOutput();

		String validationMessage = null;
		
		if (null != cwid) {
			cwid = cwid.toUpperCase();
			
				// check der InputWerte
				List<String> messages = new ArrayList<String>();

				if (messages.isEmpty()) {

					Session session = HibernateUtil.getSession();
					Transaction tx = null;
					tx = session.beginTransaction();
					
					Application applicationSource = (Application) session.get(Application.class, applicationIdSource);
					Application applicationTarget = null;
					if (null == applicationIdTarget) {
						// Komplette Neuanlage des Datensatzes mit Insert/Update-Feldern
						
						applicationTarget = new Application();
						// application - insert values
						applicationTarget.setInsertUser(cwid);
						applicationTarget.setInsertQuelle(AirKonstanten.APPLICATION_GUI_NAME);
						applicationTarget.setInsertTimestamp(ApplReposTS.getCurrentTimestamp());

						// application - update values
						applicationTarget.setUpdateUser(applicationTarget.getInsertUser());
						applicationTarget.setUpdateQuelle(applicationTarget.getInsertQuelle());
						applicationTarget.setUpdateTimestamp(applicationTarget.getInsertTimestamp());
						
						applicationTarget.setApplicationName(ciNameTarget);
						applicationTarget.setApplicationAlias(ciAliasTarget);
						// 
						applicationTarget.setResponsible(cwid.toUpperCase());
						applicationTarget.setSubResponsible(applicationSource.getSubResponsible());
						applicationTarget.setTemplate(applicationSource.getTemplate());
						
						applicationTarget.setRelevanzITSEC(applicationSource.getRelevanzITSEC());
						applicationTarget.setRelevanceICS(applicationSource.getRelevanceICS());

						applicationTarget.setRelevance2059(applicationSource.getRelevance2059());
						applicationTarget.setRelevance2008(applicationSource.getRelevance2008());


					}
					else {
						// Rekativierung / ?bernahme des bestehenden Datensatzes
						applicationTarget = (Application) session.get(Application.class, applicationIdTarget);
						// application found - change values
						output.setApplicationId(applicationIdTarget);
						
						applicationTarget.setUpdateUser(cwid);
						applicationTarget.setUpdateQuelle(AirKonstanten.APPLICATION_GUI_NAME);
						applicationTarget.setUpdateTimestamp(ApplReposTS.getCurrentTimestamp());
					}

					if (null == applicationSource) {
						// application was not found in database
						output.setResult(AirKonstanten.RESULT_ERROR);
						output.setMessages(new String[] { "the application id "	+ applicationIdSource + " was not found in database" });
					} else if (null != applicationTarget.getDeleteTimestamp()) {
						// application is deleted
						output.setResult(AirKonstanten.RESULT_ERROR);
						output.setMessages(new String[] { "the application id "	+ applicationIdTarget + " is deleted" });
					} else {

						// ======
						// Basics
						// ======
						applicationTarget.setVersion(applicationSource.getVersion());
						applicationTarget.setApplicationCat2Id(applicationSource.getApplicationCat2Id());
						// primary function only view

						applicationTarget.setLifecycleStatusId(applicationSource.getLifecycleStatusId());
						applicationTarget.setOperationalStatusId(applicationSource.getOperationalStatusId());
						applicationTarget.setComments(applicationSource.getComments());
						// TODO business category
						// -------
						
						
						// ==========
						// Agreements
						// ==========
						applicationTarget.setSlaId(applicationSource.getSlaId());
						applicationTarget.setServiceContractId(applicationSource.getServiceContractId());
						applicationTarget.setPriorityLevelId(applicationSource.getPriorityLevelId());
						applicationTarget.setSeverityLevelId(applicationSource.getSeverityLevelId());
						applicationTarget.setBusinessEssentialId(applicationSource.getBusinessEssentialId());
						// ----------
						
						// TODO edit more Attributes

						// TODO welche?
						// TODO check ob alle Variablen gesetzt worden sind!
						// ==============================

						applicationTarget.setItSecSbAvailability(applicationSource.getItSecSbAvailability());
						applicationTarget.setItSecSbAvailabilityTxt(applicationSource.getItSecSbAvailabilityTxt());//getItSecSbAvailabilityText

						applicationTarget.setClusterCode(applicationSource.getClusterCode());
						applicationTarget.setClusterType(applicationSource.getClusterType());
						
						// der kopierende User wird Responsible
						applicationTarget.setResponsible(cwid);
						// applicationTarget.setResponsible(applicationSource.getResponsible());
						applicationTarget.setSubResponsible(applicationSource.getSubResponsible());
						applicationTarget.setApplicationOwner(applicationSource.getApplicationOwner());
						
						// RFC 8539 
						// applicationTarget.setApplicationSteward(applicationSource.getApplicationSteward());
						applicationTarget.setApplicationSteward(cwid);
						applicationTarget.setApplicationOwnerDelegate(applicationSource.getApplicationOwnerDelegate());
						
						
						// ==========
						// compliance
						// ==========
						
						// IT SET only view!
						applicationTarget.setItset(applicationSource.getItset());
						applicationTarget.setTemplate(applicationSource.getTemplate());
						applicationTarget.setItsecGroupId(null);
						applicationTarget.setRefId(null);
						
// TODO anderes Feld?
//						applicationTarget.setRelevanceICS(applicationSource.getRelevanceICS());
//						applicationTarget.setRelevanzITSEC(applicationSource.getRelevanzITSEC());
						applicationTarget.setGxpFlag(applicationSource.getGxpFlag());
						
						
						// ===============
						// License & Costs
						// ===============
						applicationTarget.setLicenseTypeId(applicationSource.getLicenseTypeId());
						applicationTarget.setDedicated(applicationSource.getDedicated());
						applicationTarget.setAccessingUserCount(applicationSource.getAccessingUserCount());
						applicationTarget.setAccessingUserCountMeasured(applicationSource.getAccessingUserCountMeasured());
						applicationTarget.setLoadClass(applicationSource.getLoadClass());
						
						applicationTarget.setCostRunAccountId(applicationSource.getCostRunAccountId());
						applicationTarget.setCostChangeAccountId(applicationSource.getCostChangeAccountId());

						
						// ----------------
						applicationTarget.setCostRunPa(applicationSource.getCostRunPa());
						applicationTarget.setCostChangePa(applicationSource.getCostChangePa());
						applicationTarget.setCurrencyId(applicationSource.getCurrencyId());
						applicationTarget.setCategoryBusiness(applicationSource.getCategoryBusiness());
						applicationTarget.setClassDataId(applicationSource.getClassDataId());
						applicationTarget.setClassInformationId(applicationSource.getClassInformationId());
						applicationTarget.setClassInformationExplanation(applicationSource.getClassInformationExplanation());
						applicationTarget.setServiceModel(applicationSource.getServiceModel());
						applicationTarget.setOrganisationalScope(applicationSource.getOrganisationalScope());
						
						// for BOV
						applicationTarget.setBovApplicationNeeded(AirKonstanten.YES_SHORT);
					}

					boolean toCommit = false;
					try {
						if (null == validationMessage) {
							if (null != applicationTarget && null == applicationTarget.getDeleteTimestamp()) {
								session.saveOrUpdate(applicationTarget);
								session.flush();
								
								output.setApplicationId(applicationTarget.getApplicationId());
							}
							toCommit = true;
						}
					} catch (Exception e) {
						String message = e.getMessage();
						log.error(message);
						// handle exception
						output.setResult(AirKonstanten.RESULT_ERROR);
						
						if (null != message && message.startsWith("ORA-20000: ")) {
							message = message.substring("ORA-20000: ".length());
						}
						
						output.setMessages(new String[] { message });
					} finally {
						String hbnMessage = HibernateUtil.close(tx, session, toCommit);
						if (toCommit && null != applicationTarget) {
							if (null == hbnMessage) {
								output.setResult(AirKonstanten.RESULT_OK);
								output.setMessages(new String[] { EMPTY });
							} else {
								output.setResult(AirKonstanten.RESULT_ERROR);
								output.setMessages(new String[] { hbnMessage });
							}
						}
					}
				} else {
					// messages
					output.setResult(AirKonstanten.RESULT_ERROR);
					String astrMessages[] = new String[messages.size()];
					for (int i = 0; i < messages.size(); i++) {
						astrMessages[i] = messages.get(i);
					}
					output.setMessages(astrMessages);
				}

		} else {
			// cwid missing
			output.setResult(AirKonstanten.RESULT_ERROR);
			output.setMessages(new String[] { "cwid missing" });
		}

		if (AirKonstanten.RESULT_ERROR.equals(output.getResult())) {
			// TODO errorcodes / Texte
			if (null != output.getMessages() && output.getMessages().length > 0) {
				output.setDisplayMessage(output.getMessages()[0]);
			}
		}
		
		return output;
	}

	/**
	 * find the connection entries for the tree view
	 * @return
	 */
	public static List<ConnectionsViewDataDTO> findConnectionTreeEntries(String id) {
		ArrayList<ConnectionsViewDataDTO> listResult = new ArrayList<ConnectionsViewDataDTO>();

		boolean commit = false;
		Transaction tx = null;
		Statement selectStmt = null;
		Session session = HibernateUtil.getSession();

//		Connection conn = null;

		String searchparam = id.toUpperCase();
		
		if (null != searchparam && !searchparam.startsWith("APP-")) {
			searchparam = "APP-" + searchparam;
		}

		
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT level, id, name,  NVL(pid,-1) as pid");
		sql.append(" FROM (");
		sql.append("SELECT lower_id as id, lower_name as name, higher_id as pid");
		sql.append(" FROM TABLE ( PCK_DWH.TREE('").append(searchparam).append("')))"); 
		sql.append(" CONNECT BY NOCYCLE PRIOR id = pid START WITH pid IS NULL");
		
		try {
			tx = session.beginTransaction();
			@SuppressWarnings("deprecation")
			Connection conn = HibernateUtil.getSession().connection();

			selectStmt = conn.createStatement();
			ResultSet rsMessage = selectStmt.executeQuery(sql.toString());

			if (null != rsMessage) {
				while (rsMessage.next()) {
					ConnectionsViewDataDTO dto = new ConnectionsViewDataDTO();
					dto.setLevel(rsMessage.getString("LEVEL"));
					dto.setId(rsMessage.getString("ID"));
					dto.setName(rsMessage.getString("NAME"));
					dto.setPid(rsMessage.getString("PID"));
					listResult.add(dto);
				}
				commit = true;
			}

			if (null != rsMessage) {
				rsMessage.close();
			}
			if (null != selectStmt) {
				selectStmt.close();
			}
			if (null != conn) {
				conn.close();
			}
		} catch (Exception e) {
			//
		}
		finally {
			HibernateUtil.close(tx, session, commit);
		}

		return listResult;
	}


	/**
	 * marks all actual references as deleted
	 * @param cwid
	 * @param ciId
	 * @return
	 */
	public static boolean deleteApplicationApplication(String cwid, Long ciId) {
		boolean result = false;
		cwid = cwid.toUpperCase();

		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		tx = session.beginTransaction();

		String stampSQL = "update ANW_ANW set DEL_QUELLE = '" + AirKonstanten.APPLICATION_GUI_NAME +"', DEL_TIMESTAMP = ADD_MONTHS(current_timestamp, 120), DEL_USER = ? WHERE APP_HIGHER_ID = ? OR APP_LOWER_ID = ? AND del_quelle IS NULL";
		try {
			@SuppressWarnings("deprecation")
			PreparedStatement stmt = session.connection().prepareStatement(stampSQL);
			stmt.setString(1, cwid);
			stmt.setLong(2, ciId);
			stmt.setLong(3, ciId);
			stmt.executeUpdate();
			result = true;
		} catch (Exception e) {
			// handle exception
			System.out.println(e.toString());
		}

		HibernateUtil.close(tx, session, true);

		return result;
	}

	/**
	 * marks all actual references as deleted
	 * @param cwid
	 * @param ciId
	 * @return
	 */
	public static boolean deleteApplicationItSystem(String cwid, Long ciId) {
		boolean result = false;
		cwid = cwid.toUpperCase();

		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		tx = session.beginTransaction();

		String stampSQL = "update ANWEND_IT_SYSTEM set DEL_QUELLE = '" + AirKonstanten.APPLICATION_GUI_NAME +"', DEL_TIMESTAMP = ADD_MONTHS(current_timestamp, 120), DEL_USER = ? WHERE ANWENDUNG_ID = ? AND del_quelle IS NULL";
		try {
			@SuppressWarnings("deprecation")
			PreparedStatement stmt = session.connection().prepareStatement(stampSQL);
			stmt.setString(1, cwid);
			stmt.setLong(2, ciId);
			stmt.executeUpdate();
			result = true;
		} catch (Exception e) {
			// handle exception
			System.out.println(e.toString());
		}

		HibernateUtil.close(tx, session, true);

		return result;
	}


	public static void sendBusinessEssentialChangedMail(Application application, ApplicationDTO dto, Long businessEssentialIdOld) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT application_cat1_en FROM v_md_application_cat WHERE application_cat2_id=").append(dto.getApplicationCat2Id().toString());
		boolean commit = false;
		Transaction tx = null;
		Statement selectStmt = null;
		Session session = HibernateUtil.getSession();
		String ciType = "unknown";
		try {
			tx = session.beginTransaction();
			@SuppressWarnings("deprecation")
			Connection conn = HibernateUtil.getSession().connection();

			selectStmt = conn.createStatement();
			ResultSet rsMessage = selectStmt.executeQuery(sql.toString());

			if (null != rsMessage) {
				while (rsMessage.next()) {
					ciType = rsMessage.getString("application_cat1_en");
				}
				commit = true;
			}

			if (null != rsMessage) {
				rsMessage.close();
			}
			if (null != selectStmt) {
				selectStmt.close();
			}
			if (null != conn) {
				conn.close();
			}
		} catch (Exception e) {
			//
		}
		finally {
			HibernateUtil.close(tx, session, commit);
		}

		ApplReposHbn.sendBusinessEssentialChangedMail(application.getApplicationOwner(), ciType, application.getApplicationName(), application.getApplicationAlias(), dto.getBusinessEssentialId(), businessEssentialIdOld, dto.getTableId(), dto.getId());
	
	}
	
	
	
	static List<String> validateAnwendung(ApplicationDTO dto, boolean isUpdate) {
		
		List<String> messages = new ArrayList<String>();//new ArrayList<String>();
		
		ErrorCodeManager errorCodeManager = new ErrorCodeManager();

		
		if(isUpdate) {
			if (StringUtils.isNotNullOrEmpty(dto.getName()) && StringUtils.isNotNullOrEmpty(dto.getAlias())) {
				List<Application> applications = ItSystemHbn.findApplicationsByNameOrAlias(dto.getName(), dto.getAlias());
				List<ItSystem> itSystems = ItSystemHbn.findItSystemsByNameOrAlias(dto.getName(), dto.getAlias());

				// check allowed itsystem name
				for(ItSystem itSystem : itSystems) {
					if(itSystem.getId().longValue() != dto.getId().longValue()) {
						messages.add(errorCodeManager.getErrorMessage("8000", null));
					}
				}
				
				// check allowed application name
				for(Application application : applications) {
					if(application.getId().longValue() != dto.getId().longValue()) {
						messages.add(errorCodeManager.getErrorMessage("9000", null));
					}
				}
			}
		} 
		
		if (null == dto.getTemplate()) {
			// TODO 1 TESTCODE Template
			dto.setTemplate(new Long (0)); // no template
		}

		if (null == dto.getBusinessEssentialId()) {
			// messages.add("business essential is empty");
			// TODO 1 TESTCODE getBusinessEssentialId
			dto.setBusinessEssentialId(null);
		}


		if (StringUtils.isNullOrEmpty(dto.getCiOwnerHidden())) {//getResponsibleHidden
			// RFC 9102 - darf jetzt nicht mehr leer sein und wird automatisch vorbelegt.
			dto.setCiOwnerHidden(dto.getApplicationOwnerHidden());
		}
		else {
			List<PersonsDTO> listPersons = PersonsHbn.findPersonByCWID(dto.getCiOwnerHidden());//getResponsibleHidden
			if (null == listPersons || listPersons.isEmpty()) {
				messages.add(errorCodeManager.getErrorMessage("1103"));
			}
			else if (1 != listPersons.size()) {
				messages.add(errorCodeManager.getErrorMessage("1104"));
			}
		}

		// application owner delegate
		if (!StringUtils.isNullOrEmpty(dto.getApplicationOwnerDelegateHidden())) {
			List<PersonsDTO> listPersons = PersonsHbn.findPersonByCWID(dto.getApplicationOwnerDelegateHidden());
			if (null == listPersons || listPersons.isEmpty()) {
				// not a valid person, maybe a group?
				GroupsDTO group = GroupHbn.findGroupByName(dto.getApplicationOwnerDelegate());
				if (null == group) {
					messages.add(errorCodeManager.getErrorMessage("1105"));
				}
				else {
					// sub responsible is a valid group
					dto.setApplicationOwnerDelegateHidden(dto.getApplicationOwnerDelegate());
				}
			}
			else if (1 != listPersons.size()) {
				messages.add(errorCodeManager.getErrorMessage("1106"));
			}
		}
		// subresponsible
		if (!StringUtils.isNullOrEmpty(dto.getCiOwnerDelegateHidden())) {//getSubResponsibleHidden
			List<PersonsDTO> listPersons = PersonsHbn.findPersonByCWID(dto.getCiOwnerDelegateHidden());
			if (null == listPersons || listPersons.isEmpty()) {
				// not a valid person, maybe a group?
				GroupsDTO group = GroupHbn.findGroupByName(dto.getCiOwnerDelegate());//getSubResponsible
				if (null == group) {
					messages.add(errorCodeManager.getErrorMessage("1107")); // "subresponsible is not valid");
				}
				else {
					dto.setCiOwnerDelegateHidden(dto.getCiOwnerDelegate());//
				}
			}
			else if (1 != listPersons.size()) {
				messages.add(errorCodeManager.getErrorMessage("1108")); 
			}
		}

		


		return messages;
	}
}
