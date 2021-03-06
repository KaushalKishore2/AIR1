package com.bayerbbs.applrepos.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.bayerbbs.applrepos.domain.AttributeValue;
import com.bayerbbs.applrepos.dto.AttributeValueDTO;

public class AttributeValueHbn {

	@SuppressWarnings("unchecked")
	public static List<AttributeValueDTO> listAttributeValue(){
		List<AttributeValueDTO> resultDTO = new ArrayList<AttributeValueDTO>();

		Transaction tx = null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			tx = session.beginTransaction();
			//eugxs
			//IM0008339679 - Bug in AIR
			Criteria criteria = session.createCriteria(AttributeValue.class).add(Restrictions.isNull("deleteQuelle") );
			List<AttributeValue> listResult = criteria.list();

			resultDTO = convertToDTO(listResult);
			
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				try {
					// Second try catch as the rollback could fail as well
					tx.rollback();
				} catch (HibernateException e1) {
					System.out.println("Error rolling back transaction");
				}
				// throw again the first exception
				throw e;
			}

		}

		return resultDTO;
		
	}

	private static List<AttributeValueDTO> convertToDTO(
			List<AttributeValue> listResult) {
		List<AttributeValueDTO> resultDTO = new ArrayList<AttributeValueDTO>();
		
		for(AttributeValue aVal : listResult){
			AttributeValueDTO aDto = new AttributeValueDTO();
			aDto.setId(aVal.getId());
			aDto.setName(aVal.getName());
			aDto.setAttributeId(aVal.getAttribute().getId());
			aDto.setSelectable(aVal.getApplicationSelectable());
			resultDTO.add(aDto);
		}
		return resultDTO;
	}
}
