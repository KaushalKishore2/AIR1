package com.bayerbbs.applrepos.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import com.bayerbbs.air.error.ErrorCodeManager;
import com.bayerbbs.applrepos.common.ApplReposTS;
import com.bayerbbs.applrepos.common.StringUtils;
import com.bayerbbs.applrepos.constants.AirKonstanten;
import com.bayerbbs.applrepos.domain.CiBase;
import com.bayerbbs.applrepos.dto.CiBaseDTO;
import com.bayerbbs.applrepos.service.CiItemDTO;

public class BaseHbn {
	protected static final String NOT_EQUAL = "<>";
	protected static final String EQUAL = "=";
	protected static final String EMPTY = "";
	protected static final String KOMMA = ",";
	
	protected static final String NOT_LIKE = "not like";
	protected static final String LIKE = "like";
	
	protected static final String Y = "Y";
	protected static final String N = "N";
	protected static final String DELETE = "-1";
	
	
	public static <T> T findById(Class<T> ci, Long id) {
		Session session = HibernateUtil.getSession();

		T t = (T)session.get(ci, id);

		return t;
	}
	
	protected static List<String> validateCi(CiBaseDTO dto) {//, List<CiBaseDTO> listCi
		List<String> messages = new ArrayList<String>();
		
		ErrorCodeManager errorCodeManager = new ErrorCodeManager();


		if (StringUtils.isNullOrEmpty(dto.getName())) {
			messages.add("name must not be is empty");
		}
		else {
			List<CiItemDTO> listCi = CiEntitiesHbn.findCisByNameOrAlias(dto.getName());
//			List<CiBaseDTO> listCi = CiEntitiesHbn.findCisByNameOrAlias(dto.getName(), dto.getTableId(), false);//true
			
			if (!listCi.isEmpty()) {
				// check if the name is unique
				if (dto.getId().longValue() != listCi.get(0).getId().longValue()) {
					messages.add(errorCodeManager.getErrorMessage("1100", dto.getName()));
				}
			}
		}

		//evtl. ber�cksichtigen, dass nicht alle CI-Typen einen alias haben. Z.B wenn CI-Typ ohne Alias "-1" zur�ckgibt
		//nicht den Namen f�r den Alias setzen.
		if (StringUtils.isNullOrEmpty(dto.getAlias())) {
			// messages.add("application alias is empty");
			dto.setAlias(dto.getName());
		}
		else {
			List<CiItemDTO> listCi = CiEntitiesHbn.findCisByNameOrAlias(dto.getAlias());
//			List<CiBaseDTO> listCi = CiEntitiesHbn.findCisByNameOrAlias(dto.getAlias(), dto.getTableId(), false);//true

			if (!listCi.isEmpty()) {
				// check if the alias is unique
				if (dto.getId().longValue() != listCi.get(0).getId().longValue()) {
					messages.add(errorCodeManager.getErrorMessage("1101", dto.getAlias()));
				}
			}
		}
		
		if (null == dto.getTemplate()) {
			// TODO 1 TESTCODE Template
			dto.setTemplate(new Long(0)); // no template
		}


		return messages;
	}

	
	protected static boolean isNot(String options) {
		if(options == null)
			return false;
		
		boolean isNot = options.indexOf(',') > 0 ? options.split(KOMMA)[0].equals(Y) : options.equals(Y);//options != null && 
		
		return isNot;
	}
	
	protected static String getLikeNotLikeOperator(boolean isNot) {
		return isNot ? NOT_LIKE : LIKE;
	}
	
	protected static String getEqualNotEqualOperator(boolean isNot) {
		return isNot ? NOT_EQUAL : EQUAL;
	}
	
	protected static void setUpCi(CiBase ci, CiBaseDTO ciDTO, String cwid) {
		ci.setName(ciDTO.getName());
		
		// calculates the ItSet
		Long itSet = null;
		String strItSet = ApplReposHbn.getItSetFromCwid(ciDTO.getCiOwner());
		if (null != strItSet) {
			itSet = Long.parseLong(strItSet);
		}
		if (null == itSet) {
			// set default itSet
			itSet = new Long(AirKonstanten.IT_SET_DEFAULT);
		}
		
		
		// ci - insert values
		ci.setInsertUser(cwid);
		ci.setInsertQuelle(AirKonstanten.APPLICATION_GUI_NAME);
		ci.setInsertTimestamp(ApplReposTS.getCurrentTimestamp());

		// ci - update values
		ci.setUpdateUser(ci.getInsertUser());
		ci.setUpdateQuelle(ci.getInsertQuelle());
		ci.setUpdateTimestamp(ci.getInsertTimestamp());
		
		
//		if (null != ciDTO.getCiOwnerHidden()) {
//			ci.setCiOwner(ciDTO.getCiOwnerHidden());
//		}
//		if (null != ciDTO.getCiOwnerDelegateHidden()) {
//			ci.setCiOwnerDelegate(ciDTO.getCiOwnerDelegateHidden());
//		}
		if (null != ciDTO.getCiOwnerHidden()) {
			if(StringUtils.isNullOrEmpty(ciDTO.getCiOwnerHidden())) {
				ci.setCiOwner(null);
			}
			else {
				ci.setCiOwner(ciDTO.getCiOwnerHidden());
			}
		}
		if (null != ciDTO.getCiOwnerDelegateHidden()) {
			if(StringUtils.isNullOrEmpty(ciDTO.getCiOwnerDelegateHidden())) {
				ci.setCiOwnerDelegate(null);
			}
			else {
				ci.setCiOwnerDelegate(ciDTO.getCiOwnerDelegateHidden());
			}
		}
		
		
		if (null != ciDTO.getSlaId()) {
			if (-1 == ciDTO.getSlaId()) {
				ci.setSlaId(null);
			}
			else {
				ci.setSlaId(ciDTO.getSlaId());
			}
		}
		if (null != ciDTO.getServiceContractId() || null != ciDTO.getSlaId()) {
			// wenn SLA gesetzt ist, und ServiceContract nicht, dann muss der Service Contract gel�scht werden
			ci.setServiceContractId(ciDTO.getServiceContractId());
		}
		
		
		
		if (null != ciDTO.getItSecSbAvailabilityId()) {
			if (-1 == ciDTO.getItSecSbAvailabilityId()) {
				ci.setItSecSbAvailability(null);
			}
			else if (0 != ciDTO.getItSecSbAvailabilityId().longValue()) {
				ci.setItSecSbAvailability(ciDTO.getItSecSbAvailabilityId());
			}
		}
		if (null != ciDTO.getItSecSbAvailabilityDescription()) {
			ci.setItSecSbAvailabilityText(ciDTO.getItSecSbAvailabilityDescription());
		}
		
		
//		ci.setTemplate(ciDTO.getTemplate());
		
		if (null != ciDTO.getTemplate()) {
			if (-1 == ciDTO.getTemplate()) {
				ci.setTemplate(null);
			}
			else {
				ci.setTemplate(ciDTO.getTemplate());
			}
		}
		
		if (null != ciDTO.getItsecGroupId() && 0 != ciDTO.getItsecGroupId()) {
			if (-1 == ciDTO.getItsecGroupId()) {
				ci.setItsecGroupId(null);
			}
			else {
				ci.setItsecGroupId(ciDTO.getItsecGroupId());
			}
		}
		
		if (null != ciDTO.getRefId()) {
			if (-1 == ciDTO.getRefId()) {
				ci.setRefId(null);
			}
			else {
				ci.setRefId(ciDTO.getRefId());
			}
		}
		
		if (null == ciDTO.getRelevanzItsec()) {
			if ("Y".equals(ciDTO.getRelevanceGR1435())) {
				ciDTO.setRelevanzItsec(new Long(-1));
			}
			else if ("N".equals(ciDTO.getRelevanceGR1435())) {
				ciDTO.setRelevanzItsec(new Long(0));
			}
		}
		if (null == ciDTO.getRelevanceICS()) {
			if ("Y".equals(ciDTO.getRelevanceGR1920())) {
				ciDTO.setRelevanceICS(new Long(-1));
			}
			else if ("N".equals(ciDTO.getRelevanceGR1920())) {
				ciDTO.setRelevanceICS(new Long(0));
			}
		}
		
//		ci.setRelevanceITSEC(ciDTO.getRelevanzItsec());
//		ci.setRelevanceICS(ciDTO.getRelevanceICS());
		
		
		if (null == ciDTO.getGxpFlag()) {
			//	we don't know, let the current value 
		}
		else {
			if (EMPTY.equals(ciDTO.getGxpFlag())) {
				ci.setGxpFlag(null);
			}
			else {
				ci.setGxpFlag(ciDTO.getGxpFlag());
			}
		}
	}
}
