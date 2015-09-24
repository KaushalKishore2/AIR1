package com.bayerbbs.applrepos.hibernate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.bayerbbs.air.error.ErrorCodeManager;
import com.bayerbbs.applrepos.common.ApplReposTS;
import com.bayerbbs.applrepos.common.CiMetaData;
import com.bayerbbs.applrepos.constants.AirKonstanten;
import com.bayerbbs.applrepos.domain.Building;
import com.bayerbbs.applrepos.domain.BuildingArea;
import com.bayerbbs.applrepos.domain.CiBase;
import com.bayerbbs.applrepos.domain.CiLokationsKette;
import com.bayerbbs.applrepos.domain.Standort;
import com.bayerbbs.applrepos.domain.Terrain;
import com.bayerbbs.applrepos.dto.BuildingAreaDTO;
import com.bayerbbs.applrepos.dto.BuildingDTO;
import com.bayerbbs.applrepos.dto.CiBaseDTO;
import com.bayerbbs.applrepos.dto.KeyValueDTO;
import com.bayerbbs.applrepos.service.CiDetailParameterInput;
import com.bayerbbs.applrepos.service.CiEntityEditParameterOutput;
import com.bayerbbs.applrepos.service.CiItemsResultDTO;
import com.bayerbbs.applrepos.service.CiSearchParamsDTO;
import com.bayerbbs.applrepos.service.KeyValueOutput;

public class BuildingHbn extends LokationItemHbn {
	private static final Log log = LogFactory.getLog(BuildingHbn.class);
	
	public static Building findById(Long id) {
		return findById(Building.class, id);
	}
	public static BuildingArea findBuildingAreaById(Long id) {
		return findById(BuildingArea.class, id);
	}
	
	
	public static CiItemsResultDTO findBuildingsBy(CiSearchParamsDTO input) {
		CiMetaData metaData = new CiMetaData("gebaeude_id", "gebaeude_name", "alias", "land_kennzeichen,standort_code,terrain_name", "Building", "gebaeude", AirKonstanten.TABLE_ID_BUILDING,AirKonstanten.PROVIDER_NAME,AirKonstanten.PROVIDER_ADDRESS,null);
		return findLocationCisBy(input, metaData);
	}
	public static CiItemsResultDTO findBuildingAreasBy(CiSearchParamsDTO input) {
		CiMetaData metaData = new CiMetaData("area_id", "area_name", null, "land_kennzeichen,standort_code,terrain_name,gebaeude_name", "Building Area", "building_area", AirKonstanten.TABLE_ID_BUILDING_AREA,AirKonstanten.PROVIDER_NAME,AirKonstanten.PROVIDER_ADDRESS,null);
		return findLocationCisBy(input, metaData);
	}
	
	
	public static CiLokationsKette findLokationsKetteById(Long ciId) {
		return findLokationsKetteByCiTypeAndCiId(LokationItemHbn.GEBAEUDE_TYPE_LOCATION, ciId);
	}
	public static CiLokationsKette findLokationsKetteByAreaId(Long ciId) {
		return findLokationsKetteByCiTypeAndCiId(LokationItemHbn.BUILDING_AREA_TYPE_LOCATION, ciId);
	}
	
	public static CiEntityEditParameterOutput createBuilding(String cwid, BuildingDTO dto, Boolean forceOverride) {
		CiEntityEditParameterOutput output = new CiEntityEditParameterOutput();

		if (null != cwid) {
			cwid = cwid.toUpperCase();
			
			if (null != dto.getId() && 0 == dto.getId()) {

				// check der InputWerte
//				List<CiBaseDTO> listCi = CiEntitiesHbn.findCisByNameOrAlias(dto.getName(), dto.getTableId(), true);
//				List<CiItemDTO> listCi = CiEntitiesHbn.findCisByNameOrAlias(dto.getName());
				List<String> messages = validateBuilding(dto, false);//validateCi  , listCi

				
				if (messages.isEmpty()) {
					Building building = new Building();
					boolean isNameAndAliasNameAllowed = true;
					
					/*List<CiItemDTO> listCi = CiEntitiesHbn.findCisByNameOrAlias(dto.getName());
					
					if (isNameAndAliasNameAllowed) {
						if (null != listCi && 0 < listCi.size()) {
							// name is not allowed
							isNameAndAliasNameAllowed = false;
							output.setResult(AirKonstanten.RESULT_ERROR);
							if (null != listCi.get(0).getDeleteQuelle()) {
								boolean override = forceOverride != null && forceOverride.booleanValue();
								
								if(override) {
									// ENTWICKLUNG RFC8279
									Session session = HibernateUtil.getSession();
									Building buildingDeleted = (Building)session.get(Building.class, listCi.get(0).getId());
									
									// reactivate
									reactivateBuilding(cwid, dto, buildingDeleted);
									// save the data
									dto.setId(buildingDeleted.getId());
									return saveBuilding(cwid, dto);

								} else {
									output.setMessages(new String[] {"Building Name '" + listCi.get(0).getName() + "' already exists but marked as deleted<br>Please ask ITILcenter@bayer.com for reactivation."});
								}
							}
							else {
								output.setMessages(new String[] {"Building Name '" + listCi.get(0).getName() + "' already exists."});
							}
						}
					}
					
					if (isNameAndAliasNameAllowed) {
						List<CiBaseDTO> listCI = CiEntitiesHbn.findCisByNameOrAlias(dto.getAlias(), AirKonstanten.TABLE_ID_ROOM, true);
						if (null != listCI && 0 < listCI.size()) {
							// alias is not allowed
							isNameAndAliasNameAllowed = false;
							output.setResult(AirKonstanten.RESULT_ERROR);
							if (null != listCI.get(0).getDeleteQuelle()) {
								output.setMessages(new String[] {"Building Alias '" + listCI.get(0).getAlias() + "' already exists but marked as deleted<br>Please ask ITILcenter@bayer.com for reactivation."});
							}
							else {
								output.setMessages(new String[] {"Building Alias '" + listCI.get(0).getAlias() + "' already exists."});
							}
						}						
					}*/
					
					
					if (isNameAndAliasNameAllowed) {
						// create the ci

						Session session = HibernateUtil.getSession();
						Transaction tx = null;
						tx = session.beginTransaction();
						
						/*
						// calculates the ItSet
						Long itSet = null;
						String strItSet = ApplReposHbn.getItSetFromCwid(dto.getCiOwner());
						if (null != strItSet) {
							itSet = Long.parseLong(strItSet);
						}
						if (null == itSet) {
							// set default itSet
							itSet = new Long(AirKonstanten.IT_SET_DEFAULT);
						}

						// ci - insert values
						building.setInsertUser(cwid);
						building.setInsertQuelle(AirKonstanten.APPLICATION_GUI_NAME);
						building.setInsertTimestamp(ApplReposTS.getCurrentTimestamp());

						// ci - update values
						building.setUpdateUser(building.getInsertUser());
						building.setUpdateQuelle(building.getInsertQuelle());
						building.setUpdateTimestamp(building.getInsertTimestamp());

						// ci - attributes
						building.setBuildingName(dto.getName());
						
						
						if (null != dto.getCiOwnerHidden()) {
							building.setCiOwner(dto.getCiOwnerHidden());
						}
						if (null != dto.getCiOwnerDelegateHidden()) {
							building.setCiOwnerDelegate(dto.getCiOwnerDelegateHidden());
						}
						
						building.setTemplate(dto.getTemplate());
//						building.setBusinessEssentialId(dto.getBusinessEssentialId());
						
						building.setRelevanceITSEC(dto.getRelevanzItsec());
						building.setRelevanceICS(dto.getRelevanceICS());*/
						
						
						building.setAlias(dto.getAlias());
						
						setUpCi(building, dto, cwid, true);
						
						building.setTerrainId(dto.getTerrainId());
						Terrain terrain = TerrainHbn.findById(dto.getTerrainId());
						building.setTerrain(terrain);
						
						building.setStreet(dto.getStreet());
						building.setStreetNumber(dto.getStreetNumber());
						building.setPostalCode(dto.getPostalCode());
						building.setLocation(dto.getLocation());
						//vandana
						building.setProvider_Name(dto.getProviderName());
						building.setProvider_Address(dto.getProviderAddress());
						//vandana

						
						boolean toCommit = false;
						try {
							session.save(building);
							session.flush();
							toCommit = true;
						} catch (Exception e) {
							// handle exception
							output.setResult(AirKonstanten.RESULT_ERROR);
							output.setMessages(new String[] { e.getMessage() });
						} finally {
							String hbnMessage = HibernateUtil.close(tx, session, toCommit);
							if (toCommit) {
								if (null == hbnMessage) {
									output.setResult(AirKonstanten.RESULT_OK);
									output.setMessages(new String[] { EMPTY });
									output.setTableId(AirKonstanten.TABLE_ID_BUILDING);
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
				// ci id not 0
				output.setResult(AirKonstanten.RESULT_ERROR);
				output.setMessages(new String[] { "the ci id should not be 0" });
			}
		} else {
			// cwid missing
			output.setResult(AirKonstanten.RESULT_ERROR);
			output.setMessages(new String[] { "cwid missing" });
		}

		return output;
	}
	
	public static CiEntityEditParameterOutput createBuildingArea(String cwid, BuildingAreaDTO dto, Boolean forceOverride) {
		CiEntityEditParameterOutput output = new CiEntityEditParameterOutput();

		if (null != cwid) {
			cwid = cwid.toUpperCase();
			
			if (null != dto.getId() && 0 == dto.getId()) {

				// check der InputWerte
//				List<CiBaseDTO> listCi = CiEntitiesHbn.findCisByNameOrAlias(dto.getName(), AirKonstanten.TABLE_ID_BUILDING_AREA, true);
				List<String> messages = validateBuildingArea(dto, false);//validateCi, listCi

				if (messages.isEmpty()) {
					BuildingArea buildingArea = new BuildingArea();
					boolean isNameAndAliasNameAllowed = true;
					
					/*
					if (isNameAndAliasNameAllowed) {
						List<CiBaseDTO> listCi = CiEntitiesHbn.findCisByNameOrAlias(dto.getName(), AirKonstanten.TABLE_ID_BUILDING_AREA, true);
						
						if (null != listCi && 0 < listCi.size()) {
							// name is not allowed
							isNameAndAliasNameAllowed = false;
							output.setResult(AirKonstanten.RESULT_ERROR);
							if (null != listCi.get(0).getDeleteQuelle()) {
								boolean override = forceOverride != null && forceOverride.booleanValue();
								
								if(override) {
									// ENTWICKLUNG RFC8279
									Session session = HibernateUtil.getSession();
									BuildingArea buildingAreaDeleted = (BuildingArea)session.get(BuildingArea.class, listCi.get(0).getId());
									
									// reactivate
									reactivateBuildingArea(cwid, dto, buildingAreaDeleted);
									// save the data
									dto.setId(buildingAreaDeleted.getId());
									return saveBuildingArea(cwid, dto);

								} else {
									output.setMessages(new String[] {"Building Name '" + listCi.get(0).getName() + "' already exists but marked as deleted<br>Please ask ITILcenter@bayer.com for reactivation."});
								}
							}
							else {
								output.setMessages(new String[] {"Building Name '" + listCi.get(0).getName() + "' already exists."});
							}
						}
					}
					
					if (isNameAndAliasNameAllowed) {
						List<CiBaseDTO> listCI = CiEntitiesHbn.findCisByNameOrAlias(dto.getAlias(), AirKonstanten.TABLE_ID_ROOM, true);
						if (null != listCI && 0 < listCI.size()) {
							// alias is not allowed
							isNameAndAliasNameAllowed = false;
							output.setResult(AirKonstanten.RESULT_ERROR);
							if (null != listCI.get(0).getDeleteQuelle()) {
								output.setMessages(new String[] {"Building Alias '" + listCI.get(0).getAlias() + "' already exists but marked as deleted<br>Please ask ITILcenter@bayer.com for reactivation."});
							}
							else {
								output.setMessages(new String[] {"Building Alias '" + listCI.get(0).getAlias() + "' already exists."});
							}
						}						
					}*/
					
					
					if (isNameAndAliasNameAllowed) {
						// create the ci

						Session session = HibernateUtil.getSession();
						Transaction tx = session.beginTransaction();


						
						/*buildingArea.setName(dto.getName());
						
						// ci - insert values
						buildingArea.setInsertUser(cwid);
						buildingArea.setInsertQuelle(AirKonstanten.APPLICATION_GUI_NAME);
						buildingArea.setInsertTimestamp(ApplReposTS.getCurrentTimestamp());

						// ci - update values
						buildingArea.setUpdateUser(buildingArea.getInsertUser());
						buildingArea.setUpdateQuelle(buildingArea.getInsertQuelle());
						buildingArea.setUpdateTimestamp(buildingArea.getInsertTimestamp());

						

						
						
						// calculates the ItSet
						Long itSet = null;
						String strItSet = ApplReposHbn.getItSetFromCwid(dto.getCiOwner());
						if (null != strItSet) {
							itSet = Long.parseLong(strItSet);
						}
						if (null == itSet) {
							// set default itSet
							itSet = new Long(AirKonstanten.IT_SET_DEFAULT);
						}

						
						if (null != dto.getCiOwnerHidden()) {
							buildingArea.setCiOwner(dto.getCiOwnerHidden());
						}
						if (null != dto.getCiOwnerDelegateHidden()) {
							buildingArea.setCiOwnerDelegate(dto.getCiOwnerDelegateHidden());
						}
						
						buildingArea.setTemplate(dto.getTemplate());
						
						buildingArea.setRelevanceITSEC(dto.getRelevanzItsec());
						buildingArea.setRelevanceICS(dto.getRelevanceICS());*/
						
						setUpCi(buildingArea, dto, cwid, true);
						
						// ci - attributes
						buildingArea.setBuildingId(dto.getBuildingId());
						Building building = BuildingHbn.findById(dto.getBuildingId());
						buildingArea.setBuilding(building);
						//vandana
						
						buildingArea.setProvider_Name(dto.getProviderName());
						buildingArea.setProvider_Address(dto.getProviderAddress());
						//vandana
						
						
						boolean toCommit = false;
						try {
							session.save(buildingArea);
							session.flush();
							toCommit = true;
						} catch (Exception e) {
							// handle exception
							output.setResult(AirKonstanten.RESULT_ERROR);
							output.setMessages(new String[] { e.getMessage() });
						} finally {
							String hbnMessage = HibernateUtil.close(tx, session, toCommit);
							if (toCommit) {
								if (null == hbnMessage) {
									output.setResult(AirKonstanten.RESULT_OK);
									output.setMessages(new String[] { EMPTY });
									output.setTableId(AirKonstanten.TABLE_ID_BUILDING_AREA);
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
				// ci id not 0
				output.setResult(AirKonstanten.RESULT_ERROR);
				output.setMessages(new String[] { "the ci id should not be 0" });
			}
		} else {
			// cwid missing
			output.setResult(AirKonstanten.RESULT_ERROR);
			output.setMessages(new String[] { "cwid missing" });
		}

		return output;
	}
	
	
	
	public static CiEntityEditParameterOutput saveBuilding(String cwid,	BuildingDTO dto) {
		CiEntityEditParameterOutput output = new CiEntityEditParameterOutput();
		String validationMessage = null;
		
		if (null != cwid) {
			cwid = cwid.toUpperCase();
			
			if (null != dto.getId() || 0 < dto.getId().longValue()) {
				Long id = new Long(dto.getId());

				// check der InputWerte
//				List<CiBaseDTO> listCi = CiEntitiesHbn.findCisByNameOrAlias(dto.getName(), dto.getTableId(), true);
//				List<String> messages = validateCi(dto);//, listCi
				List<String> messages = validateBuilding(dto, true);

				if (messages.isEmpty()) {
					Session session = HibernateUtil.getSession();
					Transaction tx = session.beginTransaction();
					Building building = (Building) session.get(Building.class, id);

					if (null == building) {
						// building was not found in database
						output.setErrorMessage("1000", EMPTY+id);
					} else if (null != building.getDeleteTimestamp()) {
						// building is deleted
						output.setErrorMessage("1001", EMPTY+id);
					} else {
						// building found - change values
						
						// validate template
//						if (null != building.getTemplate() && -1 == building.getTemplate().longValue()) {
//							if (null != dto.getTemplate()) {
//								if (0 == dto.getTemplate().longValue()) {
//									// user wants to change to non template
//									// check if there are referencing values
//									if (!"0".equals(ApplReposHbn.getCountReferencingTemplates(id))) {
//										output.setErrorMessage("1002");
//									}
//								}
//							}
//						}
						
						setUpCi(building, dto, cwid, false);
						
						if (null != dto.getAlias())
							building.setAlias(dto.getAlias());
						
						building.setStreet(dto.getStreet());
						building.setStreetNumber(dto.getStreetNumber());
						building.setPostalCode(dto.getPostalCode());
						building.setLocation(dto.getLocation());
                        //vandana
						building.setProvider_Name(dto.getProviderName());
						building.setProvider_Address(dto.getProviderAddress());
						//vandana
						
						
						/*
						building.setUpdateUser(cwid);
						building.setUpdateQuelle(AirKonstanten.APPLICATION_GUI_NAME);
						building.setUpdateTimestamp(ApplReposTS.getCurrentTimestamp());
						
						// RFC8344 change Insert-Quelle? // RFC 8532
//						if (ApplreposConstants.INSERT_QUELLE_ANT.equals(application.getInsertQuelle()) ||
//							ApplreposConstants.INSERT_QUELLE_RFC.equals(application.getInsertQuelle())  ||
//							ApplreposConstants.INSERT_QUELLE_SISEC.equals(application.getInsertQuelle())) {
//							application.setInsertQuelle(ApplreposConstants.APPLICATION_GUI_NAME);
//						}

						// ======
						// Basics
						// ======

//						if (null != dto.getName()) {
//							building.setBuildingName(dto.getName());
//						}
						if (null != dto.getAlias()) {
							building.setAlias(dto.getAlias());
						}

						

						
						// ================
						// Owner / Delegate
						// ================
						if (null != dto.getCiOwnerHidden()) {
							if(StringUtils.isNullOrEmpty(dto.getCiOwnerHidden())) {
								building.setCiOwner(null);
							}
							else {
								building.setCiOwner(dto.getCiOwnerHidden());
							}
						}
						if (null != dto.getCiOwnerDelegateHidden()) {
							if(StringUtils.isNullOrEmpty(dto.getCiOwnerDelegateHidden())) {
								building.setCiOwnerDelegate(null);
							}
							else {
								building.setCiOwnerDelegate(dto.getCiOwnerDelegateHidden());
							}
						}
						
//						building.setSlaId(dto.getSlaId());
//						building.setServiceContractId(dto.getServiceContractId());
						if (null != dto.getSlaId()) {
							if (-1 == dto.getSlaId()) {
								building.setSlaId(null);
							}
							else {
								building.setSlaId(dto.getSlaId());
							}
						}
						if (null != dto.getServiceContractId() || null != dto.getSlaId()) {
							// wenn SLA gesetzt ist, und ServiceContract nicht, dann muss der Service Contract gel�scht werden
							building.setServiceContractId(dto.getServiceContractId());
						}


						
//						Long businessEssentialIdOld = building.getBusinessEssentialId();
//						if (hasBusinessEssentialChanged) {
//							sendBusinessEssentialChangedMail(building, dto, businessEssentialIdOld);
//						}
						
						if (null != dto.getItSecSbAvailabilityId()) {
							if (-1 == dto.getItSecSbAvailabilityId()) {
								building.setItSecSbAvailability(null);
							}
							else if (0 != dto.getItSecSbAvailabilityId().longValue()) {
								building.setItSecSbAvailability(dto.getItSecSbAvailabilityId());
							}
						}
						if (null != dto.getItSecSbAvailabilityDescription()) {
							building.setItSecSbAvailabilityText(dto.getItSecSbAvailabilityDescription());
						}
						
						// ==========
						// compliance
						// ==========
						// Template
						if (null != dto.getTemplate()) {
//							if (-1 == dto.getTemplate()) {
//								application.setTemplate(null);
//							}
//							else {
							building.setTemplate(dto.getTemplate());
//							}
						}
						
						if (null != dto.getItsecGroupId() && 0 != dto.getItsecGroupId()) {
							if (-1 == dto.getItsecGroupId()) {
								building.setItsecGroupId(null);
							}
							else {
								building.setItsecGroupId(dto.getItsecGroupId());
							}
						}
						
						if (null != dto.getRefId()) {
							if (-1 == dto.getRefId()) {
								building.setRefId(null);
							}
							else {
								building.setRefId(dto.getRefId());
							}
						}
						
//						if (null != dto.getRelevanceICS()) {
//							building.setRelevanceICS(dto.getRelevanceICS());
//						}
//						if (null != dto.getRelevanzItsec()) {//getRelevanceITSEC
//							building.setRelevanceITSEC(dto.getRelevanzItsec());//getRelevanceITSEC
//						}
						
//						if (null != dto.getRelevanceICS()) {
//							building.setRelevanceICS(dto.getRelevanceGR1920());
//						}
//						if (null != dto.getRelevanzItsec()) {
//							building.setRelevanceITSEC(dto.getRelevanceGR1435());
//						}
						
						if (null == dto.getRelevanzItsec()) {
							if ("Y".equals(dto.getRelevanceGR1435())) {
								dto.setRelevanzItsec(new Long(-1));
							}
							else if ("N".equals(dto.getRelevanceGR1435())) {
								dto.setRelevanzItsec(new Long(0));
							}
						}
						if (null == dto.getRelevanceICS()) {
							if ("Y".equals(dto.getRelevanceGR1920())) {
								dto.setRelevanceICS(new Long(-1));
							}
							else if ("N".equals(dto.getRelevanceGR1920())) {
								dto.setRelevanceICS(new Long(0));
							}
						}
						
						building.setRelevanceITSEC(dto.getRelevanzItsec());
						building.setRelevanceICS(dto.getRelevanceICS());
						

						if (null == dto.getGxpFlag()) {
							//	we don't know, let the current value 
						}
						else {
							if (EMPTY.equals(dto.getGxpFlag())) {
								building.setGxpFlag(null);
							}
							else {
								building.setGxpFlag(dto.getGxpFlag());
							}
						}

						if (null != dto.getItSecSbAvailabilityId()) {
							if (-1 == dto.getItSecSbAvailabilityId()) {
								building.setItSecSbAvailability(null);
							}
							else if (0 != dto.getItSecSbAvailabilityId().longValue()) {
								building.setItSecSbAvailability(dto.getItSecSbAvailabilityId());
							}
						}
						if (null != dto.getItSecSbAvailabilityDescription()) {
							building.setItSecSbAvailabilityText(dto.getItSecSbAvailabilityDescription());
						}
						
						
//						if (null != dto.getClassInformationId()) {
//							if (-1 == dto.getClassInformationId()) {
//								building.setClassInformationId(null);
//							} else {
//								building.setClassInformationId(dto.getClassInformationId());
//							}
//						}
//						if (null != dto.getClassInformationExplanation()) {
//							building.setClassInformationExplanation(dto.getClassInformationExplanation());
//						}*/
					}
					
					boolean toCommit = false;
					
					try {
						if (null == validationMessage) {
							if (null != building && null == building.getDeleteTimestamp()) {
								session.saveOrUpdate(building);
								session.flush();
								
								toCommit = true;
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
						if (toCommit && null != building) {
							if (null == hbnMessage) {
								output.setResult(AirKonstanten.RESULT_OK);
								output.setMessages(new String[] { EMPTY });
							} else {
								output.setResult(AirKonstanten.RESULT_ERROR);
								output.setMessages(new String[] { hbnMessage });
							}
						}
						
						if (building.getRefId() == null && building.getItsecGroupId() != null) {
							// Anlegen der ITSec Massnahmen
							ItsecMassnahmeStatusHbn.saveSaveguardAssignment(dto.getTableId(), building.getId(), building.getItsecGroupId());
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
			// errorcodes / Texte
			if (null != output.getMessages() && output.getMessages().length > 0) {
				output.setDisplayMessage(output.getMessages()[0]);
			}
		}
		
		return output;
	}
	

	public static CiEntityEditParameterOutput saveBuildingArea(String cwid,	BuildingAreaDTO dto) {
		CiEntityEditParameterOutput output = new CiEntityEditParameterOutput();
		String validationMessage = null;
		
		if (null != cwid) {
			cwid = cwid.toUpperCase();
			
			if (null != dto.getId() || 0 < dto.getId().longValue()) {
				Long id = new Long(dto.getId());

				// check der InputWerte
//				List<CiBaseDTO> listCi = CiEntitiesHbn.findCisByNameOrAlias(dto.getName(), dto.getTableId(), true);
//				List<CiItemDTO> listCi = CiEntitiesHbn.findCisByNameOrAlias(dto.getName());
//				List<String> messages = validateCi(dto);//, listCi
				List<String> messages = validateBuildingArea(dto, true);

				if (messages.isEmpty()) {
					Session session = HibernateUtil.getSession();
					Transaction tx = session.beginTransaction();
					BuildingArea buildingArea = (BuildingArea) session.get(BuildingArea.class, id);

					if (null == buildingArea) {
						// building was not found in database
						output.setErrorMessage("1000", EMPTY+id);
					} else if (null != buildingArea.getDeleteTimestamp()) {
						// building is deleted
						output.setErrorMessage("1001", EMPTY+id);
					} else {
						// building found - change values
						
						// validate template
//						if (null != building.getTemplate() && -1 == building.getTemplate().longValue()) {
//							if (null != dto.getTemplate()) {
//								if (0 == dto.getTemplate().longValue()) {
//									// user wants to change to non template
//									// check if there are referencing values
//									if (!"0".equals(ApplReposHbn.getCountReferencingTemplates(id))) {
//										output.setErrorMessage("1002");
//									}
//								}
//							}
//						}
						
						setUpCi(buildingArea, dto, cwid, false);
						//vandana
						
						buildingArea.setProvider_Name(dto.getProviderName());
						buildingArea.setProvider_Address(dto.getProviderAddress());
						
						/*
						buildingArea.setUpdateUser(cwid);
						buildingArea.setUpdateQuelle(AirKonstanten.APPLICATION_GUI_NAME);
						buildingArea.setUpdateTimestamp(ApplReposTS.getCurrentTimestamp());
						
						// RFC8344 change Insert-Quelle? // RFC 8532
//						if (ApplreposConstants.INSERT_QUELLE_ANT.equals(application.getInsertQuelle()) ||
//							ApplreposConstants.INSERT_QUELLE_RFC.equals(application.getInsertQuelle())  ||
//							ApplreposConstants.INSERT_QUELLE_SISEC.equals(application.getInsertQuelle())) {
//							application.setInsertQuelle(ApplreposConstants.APPLICATION_GUI_NAME);
//						}

						// ======
						// Basics
						// ======

//						if (null != dto.getName()) {
//							building.setBuildingName(dto.getName());
//						}
						
						if (null != dto.getBuildingId()) {
							//ORA-20000: Building area 1157 cannot be moved to another building. Set parameter CHECK_LOCATION_INTEGRITY to N to disable this check.
							//ORA-06512: at "TBADM.TRG_088_BIU", line 210
							//ORA-04088: error during execution of trigger 'TBADM.TRG_088_BIU'
							Building building = findById(dto.getBuildingId());
							buildingArea.setBuilding(building);
							buildingArea.setBuildingId(dto.getBuildingId());
						}
						
						// ================
						// Owner / Delegate
						// ================
						if (null != dto.getCiOwnerHidden()) {
							if(StringUtils.isNullOrEmpty(dto.getCiOwnerHidden())) {
								buildingArea.setCiOwner(null);
							}
							else {
								buildingArea.setCiOwner(dto.getCiOwnerHidden());
							}
						}
						if (null != dto.getCiOwnerDelegateHidden()) {
							if(StringUtils.isNullOrEmpty(dto.getCiOwnerDelegateHidden())) {
								buildingArea.setCiOwnerDelegate(null);
							}
							else {
								buildingArea.setCiOwnerDelegate(dto.getCiOwnerDelegateHidden());
							}
						}
						
//						buildingArea.setSlaId(dto.getSlaId());
//						buildingArea.setServiceContractId(dto.getServiceContractId());
						
						if (null != dto.getSlaId()) {
							if (-1 == dto.getSlaId()) {
								buildingArea.setSlaId(null);
							}
							else {
								buildingArea.setSlaId(dto.getSlaId());
							}
						}
						if (null != dto.getServiceContractId() || null != dto.getSlaId()) {
							// wenn SLA gesetzt ist, und ServiceContract nicht, dann muss der Service Contract gel�scht werden
							buildingArea.setServiceContractId(dto.getServiceContractId());
						}
						


						
//						Long businessEssentialIdOld = building.getBusinessEssentialId();
//						if (hasBusinessEssentialChanged) {
//							sendBusinessEssentialChangedMail(building, dto, businessEssentialIdOld);
//						}
						
						if (null != dto.getItSecSbAvailabilityId()) {
							if (-1 == dto.getItSecSbAvailabilityId()) {
								buildingArea.setItSecSbAvailability(null);
							}
							else if (0 != dto.getItSecSbAvailabilityId().longValue()) {
								buildingArea.setItSecSbAvailability(dto.getItSecSbAvailabilityId());
							}
						}
						if (null != dto.getItSecSbAvailabilityDescription()) {
							buildingArea.setItSecSbAvailabilityText(dto.getItSecSbAvailabilityDescription());
						}
						
						// ==========
						// compliance
						// ==========
						// Template
						if (null != dto.getTemplate()) {
//							if (-1 == dto.getTemplate()) {
//								application.setTemplate(null);
//							}
//							else {
							buildingArea.setTemplate(dto.getTemplate());
//							}
						}
						
						if (null != dto.getItsecGroupId() && 0 != dto.getItsecGroupId()) {
							if (-1 == dto.getItsecGroupId()) {
								buildingArea.setItsecGroupId(null);
							}
							else {
								buildingArea.setItsecGroupId(dto.getItsecGroupId());
							}
						}
						
						if (null != dto.getRefId()) {
							if (-1 == dto.getRefId()) {
								buildingArea.setRefId(null);
							}
							else {
								buildingArea.setRefId(dto.getRefId());
							}
						}
						
//						if (null != dto.getRelevanceICS()) {
//							building.setRelevanceICS(dto.getRelevanceICS());
//						}
//						if (null != dto.getRelevanzItsec()) {//getRelevanceITSEC
//							building.setRelevanceITSEC(dto.getRelevanzItsec());//getRelevanceITSEC
//						}
						
//						if (null != dto.getRelevanceICS()) {
//							building.setRelevanceICS(dto.getRelevanceGR1920());
//						}
//						if (null != dto.getRelevanzItsec()) {
//							building.setRelevanceITSEC(dto.getRelevanceGR1435());
//						}
						
						if (null == dto.getRelevanzItsec()) {
							if ("Y".equals(dto.getRelevanceGR1435())) {
								dto.setRelevanzItsec(new Long(-1));
							}
							else if ("N".equals(dto.getRelevanceGR1435())) {
								dto.setRelevanzItsec(new Long(0));
							}
						}
						if (null == dto.getRelevanceICS()) {
							if ("Y".equals(dto.getRelevanceGR1920())) {
								dto.setRelevanceICS(new Long(-1));
							}
							else if ("N".equals(dto.getRelevanceGR1920())) {
								dto.setRelevanceICS(new Long(0));
							}
						}
						
						buildingArea.setRelevanceITSEC(dto.getRelevanzItsec());
						buildingArea.setRelevanceICS(dto.getRelevanceICS());
						

						if (null == dto.getGxpFlag()) {
							//	we don't know, let the current value 
						}
						else {
							if (EMPTY.equals(dto.getGxpFlag())) {
								buildingArea.setGxpFlag(null);
							}
							else {
								buildingArea.setGxpFlag(dto.getGxpFlag());
							}
						}

						if (null != dto.getItSecSbAvailabilityId()) {
							if (-1 == dto.getItSecSbAvailabilityId()) {
								buildingArea.setItSecSbAvailability(null);
							}
							else if (0 != dto.getItSecSbAvailabilityId().longValue()) {
								buildingArea.setItSecSbAvailability(dto.getItSecSbAvailabilityId());
							}
						}
						if (null != dto.getItSecSbAvailabilityDescription()) {
							buildingArea.setItSecSbAvailabilityText(dto.getItSecSbAvailabilityDescription());
						}
						
						
//						if (null != dto.getClassInformationId()) {
//							if (-1 == dto.getClassInformationId()) {
//								building.setClassInformationId(null);
//							} else {
//								building.setClassInformationId(dto.getClassInformationId());
//							}
//						}
//						if (null != dto.getClassInformationExplanation()) {
//							building.setClassInformationExplanation(dto.getClassInformationExplanation());
//						}*/
					}
					
					boolean toCommit = false;
					
					try {
						if (null == validationMessage) {
							if (null != buildingArea && null == buildingArea.getDeleteTimestamp()) {
								session.saveOrUpdate(buildingArea);
								session.flush();
								
								toCommit = true;
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
						if (toCommit && null != buildingArea) {
							if (null == hbnMessage) {
								output.setResult(AirKonstanten.RESULT_OK);
								output.setMessages(new String[] { EMPTY });
							} else {
								output.setResult(AirKonstanten.RESULT_ERROR);
								output.setMessages(new String[] { hbnMessage });
							}
						}
						
						if (buildingArea.getRefId() == null && buildingArea.getItsecGroupId() != null) {
							// Anlegen der ITSec Massnahmen
							ItsecMassnahmeStatusHbn.saveSaveguardAssignment(dto.getTableId(), buildingArea.getId(), buildingArea.getItsecGroupId());
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
			// errorcodes / Texte
			if (null != output.getMessages() && output.getMessages().length > 0) {
				output.setDisplayMessage(output.getMessages()[0]);
			}
		}
		
		return output;
	}
	
	/**
	 * reactivates an marked as deleted building. Clears all data attributes !!!
	 * @param cwid
	 * @param dto
	 * @param application
	 * @return
	 */
	public static CiEntityEditParameterOutput reactivateBuilding(String cwid, BuildingDTO dto, Building building) {
		CiEntityEditParameterOutput output = new CiEntityEditParameterOutput();
		
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		tx = session.beginTransaction();

		if (null == building) {
			// application was not found in database
			output.setResult(AirKonstanten.RESULT_ERROR);
			output.setMessages(new String[] { "the building was not found in database" });
		} else {
			Timestamp tsNow = ApplReposTS.getCurrentTimestamp();
			
			// application found - change values
			building.setUpdateUser(cwid);
			building.setUpdateQuelle(AirKonstanten.APPLICATION_GUI_NAME);
			building.setUpdateTimestamp(tsNow);
			// override INSERT-attributes
			building.setInsertUser(cwid);
			building.setInsertQuelle(AirKonstanten.APPLICATION_GUI_NAME);
			building.setInsertTimestamp(tsNow);
			
			// reactivate DELETE-attributes
			building.setDeleteTimestamp(null);
			building.setDeleteQuelle(null);
			building.setDeleteUser(null);


			building.setBuildingName(null);
			building.setAlias(null);


			building.setCiOwner(null);
			building.setCiOwnerDelegate(null);
			
		/*	//Added vandana

			building.setProvider_Name(null);
			building.setProvider_Address(null);
			//Ended vandana
			*/
			
			building.setItset(null);
			building.setTemplate(null);
			building.setItsecGroupId(null);
			building.setRefId(null);
			
			building.setRelevanceICS(null);
			building.setRelevanceITSEC(null);
			building.setGxpFlag(null);
		}

		boolean toCommit = false;
		try {
			if (null != building) {
				session.saveOrUpdate(building);
				session.flush();
			}
			toCommit = true;
		} catch (Exception e) {
			log.error(e.getMessage());
			// handle exception
			output.setResult(AirKonstanten.RESULT_ERROR);
			output.setMessages(new String[] { e.getMessage() });
		} finally {
			String hbnMessage = HibernateUtil.close(tx, session, toCommit);
			if (toCommit && null != building) {
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
	 * reactivates an marked as deleted building. Clears all data attributes !!!
	 * @param cwid
	 * @param dto
	 * @param application
	 * @return
	 */
	public static CiEntityEditParameterOutput reactivateBuildingArea(String cwid, BuildingAreaDTO dto, BuildingArea buildingArea) {
		CiEntityEditParameterOutput output = new CiEntityEditParameterOutput();
		
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		tx = session.beginTransaction();

		if (null == buildingArea) {
			// application was not found in database
			output.setResult(AirKonstanten.RESULT_ERROR);
			output.setMessages(new String[] { "the building was not found in database" });
		} else {
			Timestamp tsNow = ApplReposTS.getCurrentTimestamp();
			
			// application found - change values
			buildingArea.setUpdateUser(cwid);
			buildingArea.setUpdateQuelle(AirKonstanten.APPLICATION_GUI_NAME);
			buildingArea.setUpdateTimestamp(tsNow);
			// override INSERT-attributes
			buildingArea.setInsertUser(cwid);
			buildingArea.setInsertQuelle(AirKonstanten.APPLICATION_GUI_NAME);
			buildingArea.setInsertTimestamp(tsNow);
			
			// reactivate DELETE-attributes
			buildingArea.setDeleteTimestamp(null);
			buildingArea.setDeleteQuelle(null);
			buildingArea.setDeleteUser(null);


			buildingArea.setName(null);


			buildingArea.setCiOwner(null);
			buildingArea.setCiOwnerDelegate(null);
			
			
			buildingArea.setItset(null);
			buildingArea.setTemplate(null);
			buildingArea.setItsecGroupId(null);
			buildingArea.setRefId(null);
			
			buildingArea.setRelevanceICS(null);
			buildingArea.setRelevanceITSEC(null);
			buildingArea.setGxpFlag(null);
			//Added vandana

			//buildingArea.setProvider_Name(null);
			//buildingArea.setProvider_Address(null);
			//Ended vandana
		}

		boolean toCommit = false;
		try {
			if (null != buildingArea) {
				session.saveOrUpdate(buildingArea);
				session.flush();
			}
			toCommit = true;
		} catch (Exception e) {
			log.error(e.getMessage());
			// handle exception
			output.setResult(AirKonstanten.RESULT_ERROR);
			output.setMessages(new String[] { e.getMessage() });
		} finally {
			String hbnMessage = HibernateUtil.close(tx, session, toCommit);
			if (toCommit && null != buildingArea) {
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
	
	public static CiEntityEditParameterOutput deleteBuilding(String cwid, BuildingDTO dto) {
		return deleteCi(cwid, dto.getId(), Building.class);
		
		/*CiEntityEditParameterOutput output = new CiEntityEditParameterOutput();

		if (null != cwid) {
			cwid = cwid.toUpperCase();
			
			if (null != dto.getId()	&& 0 < dto.getId().longValue()) {
				Long id = new Long(dto.getId());

				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				Building building = (Building) session.get(Building.class, id);
				
				if (null == building) {
					// application was not found in database
					output.setResult(AirKonstanten.RESULT_ERROR);
					output.setMessages(new String[] { "the building id " + id + " was not found in database" });
				}

				// if it is not already marked as deleted, we can do it
				else if (null == building.getDeleteTimestamp()) {
					building.setDeleteUser(cwid);
					building.setDeleteQuelle(AirKonstanten.APPLICATION_GUI_NAME);
					building.setDeleteTimestamp(ApplReposTS.getDeletionTimestamp());

					boolean toCommit = false;
					try {
						session.saveOrUpdate(building);
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
					output.setMessages(new String[] { "the building is already deleted" });
				}
			} else {
				// application id is missing
				output.setResult(AirKonstanten.RESULT_ERROR);
				output.setMessages(new String[] { "the building id is missing or invalid" });
			}
		} else {
			// cwid missing
			output.setResult(AirKonstanten.RESULT_ERROR);
			output.setMessages(new String[] { "cwid missing" });
		}

		return output;*/
	}
	
	public static CiEntityEditParameterOutput deleteBuildingArea(String cwid, BuildingAreaDTO dto) {
		return deleteCi(cwid, dto.getId(), BuildingArea.class);
		
		/*CiEntityEditParameterOutput output = new CiEntityEditParameterOutput();

		if (null != cwid) {
			cwid = cwid.toUpperCase();
			
			if (null != dto.getId()	&& 0 < dto.getId().longValue()) {
				Long id = new Long(dto.getId());

				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				BuildingArea buildingArea = (BuildingArea) session.get(BuildingArea.class, id);
				
				if (null == buildingArea) {
					// application was not found in database
					output.setResult(AirKonstanten.RESULT_ERROR);
					output.setMessages(new String[] { "the building id " + id + " was not found in database" });
				}

				// if it is not already marked as deleted, we can do it
				else if (null == buildingArea.getDeleteTimestamp()) {
					buildingArea.setDeleteUser(cwid);
					buildingArea.setDeleteQuelle(AirKonstanten.APPLICATION_GUI_NAME);
					buildingArea.setDeleteTimestamp(ApplReposTS.getDeletionTimestamp());

					boolean toCommit = false;
					try {
						session.saveOrUpdate(buildingArea);
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
					output.setMessages(new String[] { "the building is already deleted" });
				}
			} else {
				// application id is missing
				output.setResult(AirKonstanten.RESULT_ERROR);
				output.setMessages(new String[] { "the building id is missing or invalid" });
			}
		} else {
			// cwid missing
			output.setResult(AirKonstanten.RESULT_ERROR);
			output.setMessages(new String[] { "cwid missing" });
		}

		return output;*/
	}
	
	public static KeyValueOutput getBuildingsByBuildingArea(CiDetailParameterInput detailInput) {
		KeyValueOutput output = new KeyValueOutput();
		List<KeyValueDTO> buildingDataList = new ArrayList<KeyValueDTO>();
		
		BuildingArea buildingArea = BuildingHbn.findById(BuildingArea.class, detailInput.getCiId());//findBuildingAreaById(detailInput.getCiId());
		Terrain terrain = buildingArea.getBuilding().getTerrain();
		Set<Building> buildings = terrain.getBuildings();
		
		if(buildings != null && buildings.size() > 0)
			for(Building building : buildings)
				buildingDataList.add(new KeyValueDTO(building.getId(), building.getName()));
		
		Collections.sort(buildingDataList);
		output.setKeyValueDTO(buildingDataList.toArray(new KeyValueDTO[0]));
		
		return output;
	}
	
	public static KeyValueDTO[] findBuildingsByTerrainId(Long id) {
		Terrain terrain = TerrainHbn.findById(id);
		Set<Building> buildings = terrain.getBuildings();
		
		List<KeyValueDTO> data = new ArrayList<KeyValueDTO>();
		for(Building building : buildings) {
			if (null == building.getDeleteTimestamp()) {
				data.add(new KeyValueDTO(building.getId(), building.getName()));	
			}
		}
			
		
		Collections.sort(data);
		
		return data.toArray(new KeyValueDTO[0]);
	}
	
	public static KeyValueDTO[] findBuildingAreasByBuildingId(Long id) {
		Building building = BuildingHbn.findById(id);
		Set<BuildingArea> buildingAreas = building.getBuildingAreas();
		
		List<KeyValueDTO> data = new ArrayList<KeyValueDTO>();
		for(BuildingArea buildingArea : buildingAreas) {
			if (null == buildingArea.getDeleteTimestamp()) {
				data.add(new KeyValueDTO(buildingArea.getId(), buildingArea.getName()));	
			}
		}
			
		
		Collections.sort(data);
		
		return data.toArray(new KeyValueDTO[0]);
	}
	
	
	public static List<Building> findByNameOrAliasAndTerrainId(String name, String alias, Long terrainId) {
		Session session = HibernateUtil.getSession();
		
		Query q = session.getNamedQuery("findByNameOrAliasAndTerrainId");
		q.setParameter("name", name);
		q.setParameter("alias", alias);
		q.setParameter("terrainId", terrainId);

		@SuppressWarnings("unchecked")
		List<Building> areas = q.list();
		
		return areas;
	}
	public static Building findByNameAndTerrainId(String name, Long terrainId) {
		Session session = HibernateUtil.getSession();
		
		Query q = session.getNamedQuery("findByNameAndTerrainId");
		q.setParameter("name", name);
		q.setParameter("terrainId", terrainId);

		Building building = (Building)q.uniqueResult();
		
		return building;
	}
	
	
	public static BuildingArea findByNameAndBuildingId(String name,	Long buildingId) {
		Session session = HibernateUtil.getSession();
		
		Query q = session.getNamedQuery("findByNameAndBuildingId");
		q.setParameter("name", name);
		q.setParameter("buildingId", buildingId);

		BuildingArea room = (BuildingArea)q.uniqueResult();
		
		return room;
	}
	
	
	private static List<String> validateBuilding(BuildingDTO dto, boolean isUpdate) {
		List<Building> buildings = findByNameOrAliasAndTerrainId(dto.getName(), dto.getAlias(), dto.getTerrainId());
		
		boolean alreadyExists = false;
		
		if(isUpdate) {
			for(Building building : buildings) {
				//wenn es nicht das selbe CI ist, gibt es schon ein CI mit diesem Namen und dieser terrain id
				if(building.getId().longValue() != dto.getId().longValue()) {
					alreadyExists = true;
					break;
				}
			}
		} else {
			alreadyExists = buildings.size() > 0;
		}
		
		List<String> messages = validateCi(dto);//new ArrayList<String>();
		if(alreadyExists) {
			ErrorCodeManager errorCodeManager = new ErrorCodeManager();
			
//			Building building = buildings.get(0);
//			if(building.getDeleteTimestamp() == null)
				messages.add(errorCodeManager.getErrorMessage("4000", null));
//			else
//				messages.add(errorCodeManager.getErrorMessage("4001", null));
		}
		
		return messages;
	}
	
	private static List<String> validateBuildingArea(BuildingAreaDTO dto, boolean isUpdate) {
		BuildingArea buildingArea = findByNameAndBuildingId(dto.getName(), dto.getBuildingId());
		
		boolean alreadyExists = isUpdate ? buildingArea != null && buildingArea.getId().longValue() != dto.getId().longValue() : buildingArea != null;
		
		List<String> messages = validateCi(dto);//new ArrayList<String>();
		if(alreadyExists) {
			ErrorCodeManager errorCodeManager = new ErrorCodeManager();
			
//			Building building = buildings.get(0);
//			if(building.getDeleteTimestamp() == null)
				messages.add(errorCodeManager.getErrorMessage("5000", null));
//			else
//				messages.add(errorCodeManager.getErrorMessage("5001", null));
		}
		
		return messages;
	}
	
	
	public static void getBuilding(BuildingDTO dto, Building building) {
		dto.setTableId(AirKonstanten.TABLE_ID_BUILDING);
		BaseHbn.getCi((CiBaseDTO) dto, (CiBase) building);

		dto.setTerrainId(building.getTerrainId());		
		dto.setBuildingCode(building.getPostalCode());
		dto.setLocation(building.getLocation());
		dto.setStreet(building.getStreet());
		dto.setStreetNumber(building.getStreetNumber());
		//vandana
		dto.setProviderName(building.getProvider_Name());
		dto.setProviderAddress(building.getProvider_Address());
		//vandana
	}

	
	public static CiEntityEditParameterOutput copyBuilding(String cwid, Long buildingIdSource, Long buildingIdTarget, String ciNameTarget, String ciAliasTarget) {
		CiEntityEditParameterOutput output = new CiEntityEditParameterOutput();

		String validationMessage = null;
		
		if (null != cwid) {
			cwid = cwid.toUpperCase();
			
				// check der InputWerte
				List<String> messages = new ArrayList<String>();

				if (messages.isEmpty()) {

					Session session = HibernateUtil.getSession();
					Transaction tx = null;
					tx = session.beginTransaction();
					
					Building buildingSource = (Building) session.get(Building.class, buildingIdSource);
					Building buildingTarget = null;
					if (null == buildingIdTarget) {
						// Komplette Neuanlage des Datensatzes mit Insert/Update-Feldern
						
						buildingTarget = new Building();
						// building - insert values
						buildingTarget.setInsertUser(cwid);
						buildingTarget.setInsertQuelle(AirKonstanten.APPLICATION_GUI_NAME);
						buildingTarget.setInsertTimestamp(ApplReposTS.getCurrentTimestamp());

						// building - update values
						buildingTarget.setUpdateUser(buildingTarget.getInsertUser());
						buildingTarget.setUpdateQuelle(buildingTarget.getInsertQuelle());
						buildingTarget.setUpdateTimestamp(buildingTarget.getInsertTimestamp());
						
						buildingTarget.setBuildingName(ciNameTarget);
						buildingTarget.setAlias(ciAliasTarget);
						// 
						buildingTarget.setCiOwner(cwid.toUpperCase());
						buildingTarget.setCiOwnerDelegate(buildingSource.getCiOwnerDelegate());
						buildingTarget.setTemplate(buildingSource.getTemplate());
						
						buildingTarget.setRelevanceITSEC(buildingSource.getRelevanceITSEC());
						buildingTarget.setRelevanceICS(buildingSource.getRelevanceICS());

					}
					else {
						// Reaktivierung / �bernahme des bestehenden Datensatzes
						buildingTarget = (Building) session.get(Building.class, buildingIdTarget);
						// building found - change values
						output.setCiId(buildingIdTarget);
						
						buildingTarget.setUpdateUser(cwid);
						buildingTarget.setUpdateQuelle(AirKonstanten.APPLICATION_GUI_NAME);
						buildingTarget.setUpdateTimestamp(ApplReposTS.getCurrentTimestamp());
					}

					if (null == buildingSource) {
						// application was not found in database
						output.setResult(AirKonstanten.RESULT_ERROR);
						output.setMessages(new String[] { "the building id " + buildingIdSource + " was not found in database" });
					} else if (null != buildingTarget.getDeleteTimestamp()) {
						// application is deleted
						output.setResult(AirKonstanten.RESULT_ERROR);
						output.setMessages(new String[] { "the building id " + buildingIdTarget + " is deleted" });
					} else {

						buildingTarget.setTerrainId(buildingSource.getTerrainId());
						
						// ==========
						buildingTarget.setTerrainId(buildingSource.getTerrainId());
						buildingTarget.setBuildingCode(buildingSource.getBuildingCode());
						buildingTarget.setPostalCode(buildingSource.getPostalCode());
						buildingTarget.setLocation(buildingSource.getLocation());
						buildingTarget.setStreet(buildingSource.getStreet());
						buildingTarget.setStreetNumber(buildingSource.getStreetNumber());
						
						// ==============================
						buildingTarget.setItSecSbAvailability(buildingSource.getItSecSbAvailability());
						buildingTarget.setItSecSbAvailabilityTxt(buildingSource.getItSecSbAvailabilityTxt());
						
						// der kopierende User wird Responsible
						buildingTarget.setCiOwner(cwid);
						buildingTarget.setCiOwnerDelegate(buildingSource.getCiOwnerDelegate());
						
						// ==========
						// compliance
						// ==========
						
						// IT SET only view!
						buildingTarget.setItset(buildingSource.getItset());
						buildingTarget.setTemplate(buildingSource.getTemplate());
						buildingTarget.setItsecGroupId(null);
						buildingTarget.setRefId(null);
						
					}

					boolean toCommit = false;
					try {
						if (null == validationMessage) {
							if (null != buildingTarget && null == buildingTarget.getDeleteTimestamp()) {
								session.saveOrUpdate(buildingTarget);
								session.flush();
								
								output.setCiId(buildingTarget.getId());
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
						if (toCommit && null != buildingTarget) {
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
	
	public static void getBuildingArea(BuildingAreaDTO dto, BuildingArea buildingArea) {
		dto.setTableId(AirKonstanten.TABLE_ID_BUILDING_AREA);
		BaseHbn.getCi((CiBaseDTO) dto, (CiBase) buildingArea);

		dto.setAreaName(buildingArea.getName());
		dto.setBuildingId(buildingArea.getBuildingId());
		//vandana
		dto.setProviderName(buildingArea.getProvider_Name());
		dto.setProviderAddress(buildingArea.getProvider_Address());
		
		
		// dto.setSeverityLevelId(buildingArea.getSeverityLevelId());
		// dto.setBusinessEssentialId(buildingArea.getBusinessEssentialId());
	}


	public static CiEntityEditParameterOutput copyBuildingArea(String cwid, Long buildingAreaIdSource, Long buildingAreaIdTarget, String ciNameTarget) {
		CiEntityEditParameterOutput output = new CiEntityEditParameterOutput();

		String validationMessage = null;
		
		if (null != cwid) {
			cwid = cwid.toUpperCase();
			
				// check der InputWerte
				List<String> messages = new ArrayList<String>();

				if (messages.isEmpty()) {

					Session session = HibernateUtil.getSession();
					Transaction tx = null;
					tx = session.beginTransaction();
					
					BuildingArea buildingAreaSource = (BuildingArea) session.get(BuildingArea.class, buildingAreaIdSource);
					BuildingArea buildingAreaTarget = null;
					if (null == buildingAreaIdTarget) {
						// Komplette Neuanlage des Datensatzes mit Insert/Update-Feldern
						
						buildingAreaTarget = new BuildingArea();
						// buildingArea - insert values
						buildingAreaTarget.setInsertUser(cwid);
						buildingAreaTarget.setInsertQuelle(AirKonstanten.APPLICATION_GUI_NAME);
						buildingAreaTarget.setInsertTimestamp(ApplReposTS.getCurrentTimestamp());

						// buildingArea - update values
						buildingAreaTarget.setUpdateUser(buildingAreaTarget.getInsertUser());
						buildingAreaTarget.setUpdateQuelle(buildingAreaTarget.getInsertQuelle());
						buildingAreaTarget.setUpdateTimestamp(buildingAreaTarget.getInsertTimestamp());
						
						buildingAreaTarget.setBuildingAreaName(ciNameTarget);
						// buildingAreaTarget.setAlias(ciAliasTarget);
						// 
						buildingAreaTarget.setCiOwner(cwid.toUpperCase());
						buildingAreaTarget.setCiOwnerDelegate(buildingAreaSource.getCiOwnerDelegate());
						buildingAreaTarget.setTemplate(buildingAreaSource.getTemplate());
						
						buildingAreaTarget.setRelevanceITSEC(buildingAreaSource.getRelevanceITSEC());
						buildingAreaTarget.setRelevanceICS(buildingAreaSource.getRelevanceICS());

					}
					else {
						// Reaktivierung / �bernahme des bestehenden Datensatzes
						buildingAreaTarget = (BuildingArea) session.get(BuildingArea.class, buildingAreaIdTarget);
						// buildingArea found - change values
						output.setCiId(buildingAreaIdTarget);
						
						buildingAreaTarget.setUpdateUser(cwid);
						buildingAreaTarget.setUpdateQuelle(AirKonstanten.APPLICATION_GUI_NAME);
						buildingAreaTarget.setUpdateTimestamp(ApplReposTS.getCurrentTimestamp());
					}

					if (null == buildingAreaSource) {
						// room was not found in database
						output.setResult(AirKonstanten.RESULT_ERROR);
						output.setMessages(new String[] { "the buildingArea id "	+ buildingAreaIdSource + " was not found in database" });
					} else if (null != buildingAreaTarget.getDeleteTimestamp()) {
						// room is deleted
						output.setResult(AirKonstanten.RESULT_ERROR);
						output.setMessages(new String[] { "the buildingArea id "	+ buildingAreaIdTarget + " is deleted" });
					} else {

						buildingAreaTarget.setBuildingId(buildingAreaSource.getBuildingId());
						
						// ==========
						// buildingAreaTarget.setSeverityLevelId(buildingAreaSource.getSeverityLevelId());
						// buildingAreaTarget.setBusinessEssentialId(buildingAreaSource.getBusinessEssentialId());

						// ==============================
						buildingAreaTarget.setItSecSbAvailability(buildingAreaSource.getItSecSbAvailability());
						buildingAreaTarget.setItSecSbAvailabilityTxt(buildingAreaSource.getItSecSbAvailabilityTxt());
						
						// der kopierende User wird Responsible
						buildingAreaTarget.setCiOwner(cwid);
						buildingAreaTarget.setCiOwnerDelegate(buildingAreaSource.getCiOwnerDelegate());
						
						// ==========
						// compliance
						// ==========
						
						// IT SET only view!
						buildingAreaTarget.setItset(buildingAreaSource.getItset());
						buildingAreaTarget.setTemplate(buildingAreaSource.getTemplate());
						buildingAreaTarget.setItsecGroupId(null);
						buildingAreaTarget.setRefId(null);
						
					}

					boolean toCommit = false;
					try {
						if (null == validationMessage) {
							if (null != buildingAreaTarget && null == buildingAreaTarget.getDeleteTimestamp()) {
								session.saveOrUpdate(buildingAreaTarget);
								session.flush();
								
								output.setCiId(buildingAreaTarget.getId());
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
						if (toCommit && null != buildingAreaTarget) {
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
			if (null != output.getMessages() && output.getMessages().length > 0) {
				output.setDisplayMessage(output.getMessages()[0]);
			}
		}
		
		return output;
	}
	public static KeyValueDTO[] findBuildingsBySiteId(Long id) {

		Standort site = StandortHbn.findById(id);
		List<KeyValueDTO> data = new ArrayList<KeyValueDTO>();
		
		for(Terrain terrain : site.getTerrains()){
			Set<Building> buildings = terrain.getBuildings();
			
			for(Building building : buildings) {
				if (null == building.getDeleteTimestamp()) {
					data.add(new KeyValueDTO(building.getId(), building.getName()));	
				}
			}	
		}
		
		Collections.sort(data);
		return data.toArray(new KeyValueDTO[0]);
	}

}