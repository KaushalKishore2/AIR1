package com.bayerbbs.applrepos.service;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import com.bayerbbs.applrepos.common.StringUtils;
import com.bayerbbs.applrepos.constants.AirKonstanten;
import com.bayerbbs.applrepos.domain.AppRepAuthData;
import com.bayerbbs.applrepos.domain.Application;
import com.bayerbbs.applrepos.domain.CiBase1;
import com.bayerbbs.applrepos.domain.CiBase2;
import com.bayerbbs.applrepos.dto.RolePersonDTO;
import com.bayerbbs.applrepos.hibernate.AnwendungHbn;
import com.bayerbbs.applrepos.hibernate.ApplReposHbn;

public class AccessRightChecker {

	/**
	 * checks if the object is editable
	 * 
	 * The check handles responsible, subresponsible, belonging groups, role admin and substitutes 
	 * 
	 * @param objectId
	 * @param tableId
	 * @param cwidInput
	 * @return
	 */
	public boolean isEditable(Long objectId, Long tableId, String cwidInput,String token) {
		boolean isEditable = false;

		String cwid = cwidInput.toUpperCase();

		// application
		if (2 == tableId.longValue()) {
			Application application = AnwendungHbn.findApplicationById(objectId);

			if (null == application || null != application.getDeleteTimestamp()) {
				// deleted items are not to be edited
				isEditable = false;
			} else if (StringUtils.isNotNullOrEmpty(application.getResponsible())
					&& cwid.equals(application.getResponsible().toUpperCase())) {
				// responsible has the right
				isEditable = true;
			} else if (StringUtils.isNotNullOrEmpty(application
					.getSubResponsible()) && cwid.equals(application.getSubResponsible().toUpperCase())) {
				// sub-responsible has the right
				isEditable = true;
			} else {
				// TODO neu GPSC-Group CI-Owner
				if (!isEditable && StringUtils.isNotNullOrEmpty(cwid)) {
					if (!AirKonstanten.STRING_0.equals(ApplReposHbn.getCountFromGPSCGroupCIOwnder(objectId, tableId, cwid))) {
						// allowed by GPSC-Group CI Owner
						isEditable = true;
					}
				}
				
				// check group rights responsible
				if (!isEditable
						&& StringUtils.isNotNullOrEmpty(cwid) && StringUtils.isNotNullOrEmpty(application.getResponsible())) {
					if (!AirKonstanten.STRING_0.equals(ApplReposHbn.getCountFromGroupNameAndCwid(
							application.getResponsible(), cwid))) {
						// allowed by group rights
						isEditable = true;
					}
				}

				if (!isEditable
						&& StringUtils.isNotNullOrEmpty(cwid) && StringUtils.isNotNullOrEmpty(application
								.getSubResponsible())) {
					if (!AirKonstanten.STRING_0.equals(ApplReposHbn.getCountFromGroupNameAndCwid(
							application.getSubResponsible(), cwid))) {
						// allowed by group rights
						isEditable = true;
					}
				}

				// admin rights check
				if (!isEditable && StringUtils.isNotNullOrEmpty(cwid)) {
					// Admin-Rolle nur f?r die Role AIR Administrator
					
					if (!AirKonstanten.STRING_0.equals(ApplReposHbn.getCountFromRoleNameAndCwid(
							AirKonstanten.ROLE_AIR_ADMINISTRATOR, cwid,token))) {
						// allowed by group rights for admin
						isEditable = true;
					}
				}

				// subsitute rights check
				if (!isEditable && StringUtils.isNotNullOrEmpty(cwid)) {
					// check subsitute group rights responsible
					if (StringUtils.isNotNullOrEmpty(application
							.getResponsible())) {
						if (!AirKonstanten.STRING_0.equals(ApplReposHbn
								.getCountFromGroupNameAndCwid(
										AirKonstanten.ROLE_SUBSTITUTE
												+ AirKonstanten.STRING_ONE_BLANK
												+ application.getResponsible(),
										cwid))) {
							// allowed by subsitute group rights
							isEditable = true;
						}
					}

					if (!isEditable && !StringUtils.isNotNullOrEmpty(cwid)
							&& StringUtils.isNotNullOrEmpty(application
									.getSubResponsible())) {
						if (!AirKonstanten.STRING_0.equals(ApplReposHbn
								.getCountFromGroupNameAndCwid(
										AirKonstanten.ROLE_SUBSTITUTE
												+ AirKonstanten.STRING_ONE_BLANK
												+ application.getSubResponsible(), cwid))) {
							// allowed by subsitute group rights
							isEditable = true;
						}
					}
				}
			}
		}

		return isEditable;
	}
	
	//Doppelt weil: siehe @Column(name = "CWID_VERANTW_BETR") ciOwner Feld in Hibernateklasse ItSystem
	//anstatt @Column(name = "RESPONSIBLE") wie in allen anderen Transbase CI Tabellen
	public boolean isRelevanceOperational(String cwid, String token, CiBase2 ci) {//ItSystem ci
		
		boolean isEditable = false;
		if (null != cwid && null != ci && null == ci.getDeleteQuelle()) {

			if (null == ci.getCiOwner() && null == ci.getCiOwnerDelegate()) {
				//wenn kein owner oder delegate, d?rfen alle editieren
				isEditable = true;
			}

			if (!isEditable && (cwid.equals(ci.getCiOwner()) || 
					   cwid.equals(ci.getCiOwnerDelegate()))) {
				isEditable = true;
			}
			
			if (!isEditable && null != ci.getCiOwnerDelegate()) {
				if (!AirKonstanten.STRING_0.equals(ApplReposHbn.getCountFromGroupNameAndCwid(ci.getCiOwnerDelegate(), cwid))) {
					isEditable = true;
				}
			}
		}
		
		return isEditable;
	}

	//Location CIs mit dem Namen unknown d?rfen nicht editiert werden !!
	public boolean isRelevanceOperational(String cwid, String token, CiBase1 ci) {
		if(cwid == null || ci == null)// || (ci.getCiOwner() == null && ci.getCiOwnerDelegate() == null)
			return false;
		
//		boolean isUnknown = ci.getName().equalsIgnoreCase(AirKonstanten.UNKNOWN);
//		boolean isDeleted = ci.getDeleteQuelle() != null;
		boolean isLocked = ci.getName().equalsIgnoreCase(AirKonstanten.UNKNOWN) || ci.getDeleteQuelle() != null;
		
		/*
		if((!isLocked && cwid.equals(ci.getCiOwner()) ||//!isUnknown && !isDeleted && 
		   cwid.equals(ci.getCiOwnerDelegate()) || 
		   (ci.getCiOwner() == null && ci.getCiOwnerDelegate() == null)))//wenn kein owner oder delegate, d?rfen alle editieren
			return true;
		
		String groupCount = ci.getCiOwnerDelegate() != null ? ApplReposHbn.getCountFromGroupNameAndCwid(ci.getCiOwnerDelegate(), cwid) : AirKonstanten.STRING_0;
		if ((!isLocked && StringUtils.isNotNullOrEmpty(ci.getCiOwnerDelegate()) && !groupCount.equals(AirKonstanten.STRING_0)))
			return true;//!isUnknown && !isDeleted && 
		*/
		
		boolean isEditable = !isLocked && hasRole(cwid, token, AirKonstanten.ROLE_AIR_LOCATION_DATA_MAINTENANCE);//!isDeleted && !isUnknown && && false;// 
		
		return isEditable;//false
	}
	
	public boolean isRelevanceOperational(String cwid, String token, Long ciSubTypeId, Application application) {
		boolean isRelevanceOperational = false;
		
		
		if (null != cwid && null != application) {
			cwid = cwid.toUpperCase();
			
			if (cwid.equals(application.getResponsible())) {
				isRelevanceOperational = true;
			}
			
			if (!isRelevanceOperational && cwid.equals(application.getSubResponsible())) {
				isRelevanceOperational = true;
			}
			
			if (!isRelevanceOperational && cwid.equals(application.getApplicationSteward())) {
				isRelevanceOperational = true;
			}

			
			if (!isRelevanceOperational && StringUtils.isNotNullOrEmpty(application.getSubResponsible())) {
				if (!AirKonstanten.STRING_0.equals(ApplReposHbn.getCountFromGroupNameAndCwid(application.getSubResponsible(), cwid))) {
					// allowed by group rights
					isRelevanceOperational = true;
				}
			}
			
			if(!isRelevanceOperational) {
				if(ciSubTypeId.intValue() != AirKonstanten.APPLICATION_CAT1_APPLICATION.intValue() &&
				   hasRole(cwid, token, AirKonstanten.ROLE_AIR_APPLICATION_MANAGER))
					isRelevanceOperational = true;
				else if(hasRole(cwid, token, AirKonstanten.ROLE_AIR_INFRASTRUCTURE_MANAGER))
					isRelevanceOperational = true;
			}
		
			/*
			//wenn kein ciOwner, ciOwnerDelegate und kein Steward vorhanden, d?rfen alle editieren, wenn die
			//app nicht l?schmarkiert ist!
			if(!isRelevanceOperational &&
			   StringUtils.isNullOrEmpty(application.getResponsible()) &&
			   StringUtils.isNullOrEmpty(application.getSubResponsible()) &&
			   StringUtils.isNullOrEmpty(application.getApplicationSteward()) &&
			   application.getDeleteTimestamp() == null)
				isRelevanceOperational = true;*/
			
//			if(isRelevanceOperational && application.getDeleteTimestamp() != null)
//				isRelevanceOperational = false;
		}
		
		return isRelevanceOperational;
	}
	

	public boolean isRelevanceStrategic(String cwid, String token, Long ciSubTypeId, Application application) {
		boolean isRelevanceStrategic = false;
		
//		if(!ciSubTypeId.equals(Long.(AirKonstanten.APPLICATION_CAT1_APPLICATION)))
		if(ciSubTypeId.intValue() != AirKonstanten.APPLICATION_CAT1_APPLICATION.intValue())
			return false;
		
		if (null != cwid && null != application) {
			cwid = cwid.toUpperCase();
			
			if(application.getDeleteQuelle() != null)
				return false;
			
			if (cwid.equals(application.getApplicationOwner())) {
				isRelevanceStrategic = true;
			}
			
			if (!isRelevanceStrategic && cwid.equals(application.getApplicationOwnerDelegate())) {
				isRelevanceStrategic = true;
			}

			if (!isRelevanceStrategic && cwid.equals(application.getApplicationSteward())) {
				isRelevanceStrategic = true;
			}
			
			if (!isRelevanceStrategic && StringUtils.isNotNullOrEmpty(application.getApplicationOwnerDelegate())) {
				if (!AirKonstanten.STRING_0.equals(ApplReposHbn.getCountFromGroupNameAndCwid(application.getApplicationOwnerDelegate(), cwid))) {
					// allowed by group rights
					isRelevanceStrategic = true;
				}
			}
			
			//wenn kein ciOwner, ciOwnerDelegate und kein Steward vorhanden, d?rfen alle editieren, wenn die
			//app nicht l?schmarkiert ist!
			// RFC 9472
			if(!isRelevanceStrategic &&
			   StringUtils.isNullOrEmpty(application.getApplicationOwner()) &&
			   StringUtils.isNullOrEmpty(application.getApplicationOwnerDelegate()) &&
			   StringUtils.isNullOrEmpty(application.getApplicationSteward()) &&
			   StringUtils.isNullOrEmpty(application.getResponsible()) &&
			   StringUtils.isNullOrEmpty(application.getSubResponsible()))
				isRelevanceStrategic = true;
			
			if (!isRelevanceStrategic) {
//				if (isEditableRoleApplicationManager(cwid)) {
				if(hasRole(cwid, token, AirKonstanten.ROLE_AIR_APPLICATION_MANAGER)) {
					isRelevanceStrategic = true;
				}
			}
			
//			if(isRelevanceStrategic && application.getDeleteTimestamp() != null)
//				isRelevanceStrategic = false;
		}
		
		return isRelevanceStrategic;
	}

//	private boolean isEditableByRoleAdminType(String adminTypeRoleName, String cwidInput) {
//		boolean isEditableByRoleAdminType = false;
//		if (!AirKonstanten.STRING_0.equals(ApplReposHbn.getCountFromRoleNameAndCwid(adminTypeRoleName, cwidInput))) {
//			// allowed by role rights for admin type
//			isEditableByRoleAdminType = true;
//		}
//		
//		return isEditableByRoleAdminType;
//	}

//	public boolean isEditableRoleApplicationManager(String cwidInput) {
//		return isEditableByRoleAdminType(AirKonstanten.ROLE_AIR_APPLICATION_MANAGER, cwidInput);
//	}
	
//	public boolean isEditableRoleInfrastructureManager(String cwidInput) {
//		return isEditableByRoleAdminType(AirKonstanten.ROLE_AIR_INFRASTRUCTURE_MANAGER, cwidInput);
//	}
	
	private static String getCacheKey(String cwid, String token) {
		return cwid.toUpperCase() + ":" + token;
	}
	
	public static boolean hasRole(String cwid, String token, String... roleNames) {//String roleName
		Cache cache = CacheManager.getInstance().getCache(AirKonstanten.CACHENAME);
		Element element = cache.get(getCacheKey(cwid, token));
		if (element != null) {
			AppRepAuthData authData = (AppRepAuthData)element.getObjectValue();
			
			for(RolePersonDTO role : authData.getRoles())
				for(String roleName : roleNames)
					if(role.getRoleName().equals(roleName))
						return true;
		}
		return false;
	}
}
