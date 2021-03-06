package com.bayerbbs.applrepos.hibernate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.bayerbbs.applrepos.domain.OperationalStatus;
import com.bayerbbs.applrepos.dto.OperationalStatusDTO;

public class OperationalStatusHbn {
	
	
	public static OperationalStatus findById(Long id){
		Session session = HibernateUtil.getSession();
		return (OperationalStatus) session.get(OperationalStatus.class, id);
	}

	
	/**
	 * converts the list entry database table to dto
	 * 
	 * @param input
	 * @return
	 */
	private static List<OperationalStatusDTO> getDTOList(
			List<OperationalStatus> input) {
		ArrayList<OperationalStatusDTO> listDTO = new ArrayList<OperationalStatusDTO>();

		for (Iterator<OperationalStatus> iter = input.iterator(); iter.hasNext();) {
			OperationalStatus data = iter.next();
			OperationalStatusDTO dto = new OperationalStatusDTO();

			dto.setOperationalStatusId(data.getOperationalStatusId());
			dto.setOperationalStatus(data.getOperationalStatus());
			dto.setOperationalStatusEn(data.getOperationalStatusEn());
			dto.setOperationalStatusTxt(data.getOperationalStatusTxt());

			if (null != data.getSort()) {
				dto.setSort(data.getSort());
			}
			listDTO.add(dto);
		}
		return listDTO;
	}

	/**
	 * returns the array from list
	 * 
	 * @param input
	 * @return
	 */
	public static OperationalStatusDTO[] getArrayFromList(
			List<OperationalStatusDTO> input) {
		OperationalStatusDTO output[] = new OperationalStatusDTO[input.size()];
		int i = 0;
		for (final OperationalStatusDTO data : input) {
			output[i] = data;
			i++;
		}
		return output;
	}

	public static List<OperationalStatusDTO> listOperationalStatusHbn() {

		List<OperationalStatusDTO> listResult = new ArrayList<OperationalStatusDTO>();

		Transaction tx = null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			tx = session.beginTransaction();
			@SuppressWarnings("unchecked")
			List<OperationalStatus> values = session.createQuery("select h from OperationalStatus as h order by h.sort").list();

			listResult = getDTOList(values);

			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				try {
					tx.rollback();
				} catch (HibernateException e1) {
					System.out.println("Error rolling back transaction");
				}
				throw e;
			}
		}
		Collections.sort(listResult);
		return listResult;
	}

}
