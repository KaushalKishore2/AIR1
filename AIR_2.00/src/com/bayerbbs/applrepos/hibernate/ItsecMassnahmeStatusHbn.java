package com.bayerbbs.applrepos.hibernate;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.CacheMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.bayerbbs.applrepos.common.ApplReposTS;
import com.bayerbbs.applrepos.constants.AirKonstanten;
import com.bayerbbs.applrepos.domain.ItsecCompliance;
import com.bayerbbs.applrepos.dto.ItsecMassnahmeDetailDTO;
import com.bayerbbs.applrepos.service.MassUpdateComplianceControlParameterInput;
import com.bayerbbs.applrepos.service.MassUpdateComplianceControlParameterOutput;

public class ItsecMassnahmeStatusHbn {

	
	public static ItsecCompliance findLinkedMassnahmeDetail(Long linkCiId, Long linkCiTableId, Long massnahmeGstoolId) {
		ItsecCompliance result = null;

		if (null != linkCiId && null != linkCiTableId && null != massnahmeGstoolId) {

			Transaction tx = null;
			Session session = HibernateUtil.getSession();
			try {
				tx = session.beginTransaction();
				@SuppressWarnings("unchecked")
				List<ItsecCompliance> values = session.createQuery(
						"select h from ItsecCompliance h where h.tabelleId = " + linkCiTableId + " h.and tabellePkId="+linkCiId + " and h.massnahmeGSTOOLID=" + massnahmeGstoolId).list();

				if (null != values && 0 < values.size()) {
					result = values.get(0);
				}

				HibernateUtil.close(tx, session, true);
			} catch (RuntimeException e) {
				HibernateUtil.close(tx, session, false);
			}
		}

		return result;
	}
	
	public static ItsecCompliance findById(Long itsecMassnStId) {
		ItsecCompliance result = null;

		if (null != itsecMassnStId) {

			Transaction tx = null;
			Session session = HibernateUtil.getSession();
			try {
				tx = session.beginTransaction();
				@SuppressWarnings("unchecked")
				List<ItsecCompliance> values = session.createQuery(
						"select h from ItsecCompliance as h where h.itsecMassnStId = " + itsecMassnStId).list();

				if (null != values && 0 < values.size()) {
					result = values.get(0);
				}

				HibernateUtil.close(tx, session, true);
			} catch (RuntimeException e) {
				HibernateUtil.close(tx, session, false);
			}
		}

		return result;
	}

	public static ItsecMassnahmeDetailDTO findDTOById(Long itsecMassnStId) {
		ItsecMassnahmeDetailDTO dto = null;
		
		ItsecCompliance status = findById(itsecMassnStId);
		
		if (null != status) {
			dto = new ItsecMassnahmeDetailDTO();
			dto.setItsecMassnahmenStatusId(status.getItsecMassnStId());
			dto.setZobId(status.getZobId());
			dto.setTabelleId(status.getTabelleId());
			dto.setTabellePkId(status.getTabellePkId());
			dto.setStatusId(status.getStatusId());
			dto.setStatusKommentar(status.getStatusKommentar());
			dto.setMassnahmeGstoolId(status.getMassnahmeGSTOOLID());
			dto.setNoUpdateYN(status.getNoUpdateYN());
			dto.setGap(status.getGap());
			dto.setGapResponsible(status.getGapResponsible());
			dto.setGapResponsibleHidden(status.getGapResponsible());
			dto.setGapMeasure(status.getGapMeasure());
			dto.setGapEndDate(status.getGapEndDate().getTime());//.toString()
			dto.setGapPriority(status.getGapPriority());
			dto.setRefTableID(status.getRefTableID());
			dto.setRefPKID(status.getRefPKID());
			// --
			dto.setExpense(status.getExpense());
			dto.setProbOccurence(status.getProbOccurence());
			dto.setDamage(status.getDamage());
			dto.setMitigationPotential(status.getMitigationPotential());
			dto.setSignee(status.getSignee());
			dto.setGapClassApproved(status.getGapClassApproved().getTime());//.toString()
			// --
			dto.setExpenseText(status.getExpenseT());
			dto.setProbOccurenceText(status.getProbOccucrenceT());
			dto.setDamageText(status.getDamageT());
			dto.setMitigationPotentialText(status.getMitigationPotentialT());
			dto.setRiskAnalysisAsFreetext(status.getRiskAnalysisAsFreetext());
			dto.setGapEndDateIncreased(status.getGapEndDateIncreased());
			dto.setCurrency(status.getCurrency());
			dto.setTemplateException(status.getTemplateException());
		}
		
		return dto;
	}
	
	private static boolean isNotNullOrEmpty(String input) {
		boolean isNotNullOrEmpty = false;
		if (null != input && !"".equals(input)) {
			isNotNullOrEmpty = true;
		}
		return isNotNullOrEmpty;
	}
	
	public static boolean saveItsecMassnahmeFromDTO(String cwid, Long itsecMassnStId, ItsecMassnahmeDetailDTO dto) {
		boolean result = false;
		
		if (null != itsecMassnStId) {
			ItsecCompliance status = findById(itsecMassnStId);
			
			if (null != status) {
				// fill data
				if (null != dto.getZobId()) {
					status.setZobId(dto.getZobId());
				}
				if (null != dto.getTabelleId()) {
					status.setTabelleId(dto.getTabelleId());
				}
				if (null != dto.getTabellePkId()) {
					status.setTabellePkId(dto.getTabellePkId());
				}
				status.setStatusId(dto.getStatusId());
				status.setStatusKommentar(dto.getStatusKommentar());
				status.setMassnahmeGSTOOLID(dto.getMassnahmeGstoolId());
				if (isNotNullOrEmpty(dto.getNoUpdateYN())) {
					status.setNoUpdateYN(dto.getNoUpdateYN());
				}
				status.setGap(dto.getGap());
				status.setGapResponsible(dto.getGapResponsibleHidden());
				status.setGapMeasure(dto.getGapMeasure());

//				if (null != dto.getGapEndDate() && !"".equals(dto.getGapEndDate())) {
//					Date gapEndDate = Date.valueOf(dto.getGapEndDate());
//					status.setGapEndDate(gapEndDate);
//				}
				if(dto.getGapEndDate() != null && dto.getGapEndDate() > 0) {
					Date gapEndDate = new Date(dto.getGapEndDate());
					status.setGapEndDate(gapEndDate);
				}
				else {
					status.setGapEndDate(null);
				}
				
				status.setGapPriority(dto.getGapPriority());
				if (null != dto.getRefTableID()) {
					if (-1 == dto.getRefTableID()) {
						status.setRefTableID(null);
					}
					else {
						status.setRefTableID(dto.getRefTableID());
					}
				}
				if (null != dto.getRefPKID()) {
					if (-1 == dto.getRefPKID()) {
						status.setRefPKID(null);
					}
					else {
						status.setRefPKID(dto.getRefPKID());
					}
				}
				// --
				status.setExpense(dto.getExpense());
				status.setProbOccurence(dto.getProbOccurence());
				status.setDamage(dto.getDamage());
				status.setMitigationPotential(dto.getMitigationPotential());// / 100
				status.setSignee(dto.getSignee());
				
//				if (null != dto.getGapClassApproved() && !"".equals(dto.getGapClassApproved())) {
//					Date gapClassApproved = Date.valueOf(dto.getGapClassApproved());
//					status.setGapClassApproved(gapClassApproved);
//				}
				if(dto.getGapClassApproved() != null && dto.getGapClassApproved() > 0) {
					Date gapClassApproved = new Date(dto.getGapClassApproved());
					status.setGapClassApproved(gapClassApproved);
				}
				else {
					status.setGapClassApproved(null);
				}
				// --
				status.setExpenseT(dto.getExpenseText());
				status.setProbOccucrenceT(dto.getProbOccurenceText());
				status.setDamageT(dto.getDamageText());
				status.setMitigationPotentialT(dto.getMitigationPotentialText());
				status.setRiskAnalysisAsFreetext(dto.getRiskAnalysisAsFreetext());
				status.setGapEndDateIncreased(dto.getGapEndDateIncreased());
				status.setCurrency(dto.getCurrency());
				status.setTemplateException(dto.getTemplateException());
				
				status.setUpdateUser(cwid);
				status.setUpdateQuelle(AirKonstanten.APPLICATION_GUI_NAME);
				status.setUpdateTimestamp(ApplReposTS.getCurrentTimestamp());
				
				// RFC 8344 change Insert-Quelle? // RFC 8532
				if (AirKonstanten.INSERT_QUELLE_ANT.equals(status.getInsertQuelle()) ||
					AirKonstanten.INSERT_QUELLE_RFC.equals(status.getInsertQuelle())  ||
					AirKonstanten.INSERT_QUELLE_SISEC.equals(status.getInsertQuelle())) {
					status.setInsertQuelle(AirKonstanten.APPLICATION_GUI_NAME);
				}
			}
			

			if (null != status) {
				// save data
				Session session = HibernateUtil.getSession();
				Transaction tx = null;
				tx = session.beginTransaction();
				boolean toCommit = false;
				try {
					session.saveOrUpdate(status);
					session.flush();
					toCommit = true;
					result = true;
				} catch (Exception e) {
					// handle exception
					System.out.println(e.toString());
					// output.setResult(ApplreposConstants.RESULT_ERROR);
					// output.setMessages(new String[] { e.getMessage() });
				} finally {
					String hbnMessage = HibernateUtil.close(tx, session, toCommit);
					if (toCommit) {
						if (null == hbnMessage) {
							// output
							// .setResult(ApplreposConstants.RESULT_OK);
							// output.setMessages(new String[] { "" });
						} else {
							// output
							// .setResult(ApplreposConstants.RESULT_ERROR);
							// output
							// .setMessages(new String[] { hbnMessage });
						}
					}
				}
			}
		}		
		return result;
	}
	
	
	public static void saveSaveguardAssignment(Integer tableId, Long ciId, Long itsecGroupId) {
		String sql = "{call pck_Logical_Integrity.P_CI_Safeguard_Assignment (?,?,?)}";
		
		Transaction ta = null;
		Session session = HibernateUtil.getSession();
		
		boolean commit = false;
		
		/*try {
			ta = session.beginTransaction();
			Connection conn = session.connection();
			
			CallableStatement stmt = conn.prepareCall(sql);
			stmt.setLong(1, tableId);
			stmt.setLong(2, ciId);
			stmt.setLong(3, 10136);//muss bei location items ver?ndert werden, damit PL/SQL Funktion reagiert
			stmt.executeUpdate();
			
			ta.commit();
			
			stmt.close();
			conn.close();
			
			commit = true;
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
//			HibernateUtil.close(ta, session, commit);
		}*/
		
		try {
			ta = session.beginTransaction();
			@SuppressWarnings("deprecation")
			Connection conn = session.connection();
			
			CallableStatement stmt = conn.prepareCall(sql);
			stmt.setLong(1, tableId);
			stmt.setLong(2, ciId);
			stmt.setLong(3, itsecGroupId);
			stmt.executeUpdate();
			
			ta.commit();
			
			stmt.close();
			conn.close();
			
			commit = true;
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			HibernateUtil.close(ta, session, commit);
		}
	}
	public static MassUpdateComplianceControlParameterOutput saveMassUpdateComplianceControl(MassUpdateComplianceControlParameterInput  input){
		MassUpdateComplianceControlParameterOutput output = new MassUpdateComplianceControlParameterOutput();
		if(StringUtils.isNotEmpty(input.getSelectedItsecMassnStIds())){
			String[] selectedMassnahStatusIDsArray = input.getSelectedItsecMassnStIds().split(",");
			int size = selectedMassnahStatusIDsArray.length;
			for(int i = 0 ;i< size ; i++){
				String itsecMassnStId = selectedMassnahStatusIDsArray[i];
				ItsecCompliance status = findById(Long.valueOf(itsecMassnStId));
				Session session = HibernateUtil.getSession();;
				Transaction tx = session.beginTransaction();
				boolean toCommit = false;
				try {
					ScrollableResults compliances = session.createQuery("select h from ItsecCompliance as h where h.zobId = "+status.getZobId()+" and h.tabelleId = "+status.getTabelleId()+" and h.massnahmeGSTOOLID = "+status.getMassnahmeGSTOOLID() +" and h.tabellePkId in("+input.getSelectedCIs()+")")
				    .setCacheMode(CacheMode.IGNORE)
				    .scroll(ScrollMode.FORWARD_ONLY);
					int count=0;
					while(compliances.next()){
						ItsecCompliance itsecCompliance = (ItsecCompliance) compliances.get(0);
						
						itsecCompliance.setStatusId(status.getStatusId());
						itsecCompliance.setStatusKommentar(status.getStatusKommentar());
						itsecCompliance.setNoUpdateYN(status.getNoUpdateYN());
						itsecCompliance.setGap(status.getGap());
						itsecCompliance.setGapResponsible(status.getGapResponsible());
						itsecCompliance.setGapMeasure(status.getGapMeasure());
						itsecCompliance.setGapEndDate(status.getGapEndDate());
						itsecCompliance.setRefTableID(status.getRefTableID());
						itsecCompliance.setRefPKID(status.getRefPKID());
						itsecCompliance.setExpense(status.getExpense());
						itsecCompliance.setProbOccurence(status.getProbOccurence());
						itsecCompliance.setDamage(status.getDamage());
						itsecCompliance.setMitigationPotential(status.getMitigationPotential());// / 100
						itsecCompliance.setSignee(status.getSignee());
						itsecCompliance.setGapClassApproved(status.getGapClassApproved());
						
						itsecCompliance.setExpenseT(status.getExpenseT());
						itsecCompliance.setProbOccucrenceT(status.getProbOccucrenceT());
						itsecCompliance.setDamageT(status.getDamageT());
						itsecCompliance.setMitigationPotentialT(status.getMitigationPotentialT());
						itsecCompliance.setRiskAnalysisAsFreetext(status.getRiskAnalysisAsFreetext());
						itsecCompliance.setGapEndDateIncreased(status.getGapEndDateIncreased());
						itsecCompliance.setCurrency(status.getCurrency());
						itsecCompliance.setTemplateException(status.getTemplateException());
						
						itsecCompliance.setUpdateUser(input.getCwid());
						itsecCompliance.setUpdateQuelle(AirKonstanten.APPLICATION_GUI_NAME);
						itsecCompliance.setUpdateTimestamp(ApplReposTS.getCurrentTimestamp());
					    if ( ++count % 20 == 0 ) {
					        session.flush();
					        session.clear();
					    }
					}
					session.flush();
					session.clear();				   
					toCommit = true;
				}catch (Exception e) {
					output.setResult(AirKonstanten.RESULT_ERROR);
					output.setMessages(new String[] { e.getMessage() });
				}finally{
					if (toCommit) {
						String hbnMessage = HibernateUtil.close(tx, session, toCommit);
						if (null == hbnMessage) {
							output.setResult(AirKonstanten.RESULT_OK);
							output.setMessages(new String[] { "" });
						} else {
							output.setResult(AirKonstanten.RESULT_ERROR);
							output.setMessages(new String[] { hbnMessage });
						}
					}
				}
			}
			
			
		}
		return output;
	}

}