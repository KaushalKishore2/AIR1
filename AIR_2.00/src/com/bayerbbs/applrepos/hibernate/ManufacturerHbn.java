package com.bayerbbs.applrepos.hibernate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Session;

import com.bayerbbs.applrepos.domain.Partner;
import com.bayerbbs.applrepos.dto.KeyValueDTO;
import com.bayerbbs.applrepos.dto.ProductDTO;

public class ManufacturerHbn extends BaseHbn {


	private static final String SQL_Hardware_Product = "select * from partner where partner_id in (select partner_id from hw_kategorie3) ORDER BY NLSSORT(partner_name, 'NLS_SORT=GENERIC_M') ";

	private static final String SQL_Software_Product = "select * from partner where partner_id in (select hersteller_partnid from SW_KATEGORIE2) ORDER BY NLSSORT(partner_name, 'NLS_SORT=GENERIC_M')";

	public static Partner findById(Long id) {
		return findById(Partner.class, id);
	}

	private static List<KeyValueDTO> getLegalDTOList(List<Partner> input) {
		List<KeyValueDTO> listDTO = new ArrayList<KeyValueDTO>();

		for (Partner partner : input) {
			listDTO.add(new KeyValueDTO(partner.getId(), partner.getOwner()));
		}
		return listDTO;
	}

	@SuppressWarnings("deprecation")
	public static ProductDTO[] findManufacturerList() {

		List<ProductDTO> data = new ArrayList<ProductDTO>();
		Session session = HibernateUtil.getSessionFactory().openSession();

		try {
			Connection conn = session.connection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL_Hardware_Product);
			while (rs.next()) {
				ProductDTO productDTO = new ProductDTO();
				productDTO.setManufacturerId(rs.getLong(1));
				productDTO.setManufacturer(rs.getString(2));
				data.add(productDTO);
			}
			rs.close();
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return data.toArray(new ProductDTO[data.size()]);
	}

	@SuppressWarnings("deprecation")
	public static KeyValueDTO[] findSoftwareManufacturerList() {

		List<KeyValueDTO> data = new ArrayList<KeyValueDTO>();
		Session session = HibernateUtil.getSessionFactory().openSession();

		try {
			Connection conn = session.connection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL_Software_Product);
			while (rs.next()) {
				KeyValueDTO keyValueDTO = new KeyValueDTO();
				keyValueDTO.setId(rs.getLong(1));
				keyValueDTO.setName(rs.getString(2));
				data.add(keyValueDTO);
			}
			rs.close();
			stmt.close();

		} catch (SQLException e) {
			session.close();
			e.printStackTrace();
		}
		//Collections.sort(data);
		return data.toArray(new KeyValueDTO[0]);
	}

	public static KeyValueDTO[] findLegalEntityList() {
		List<KeyValueDTO> data = new ArrayList<KeyValueDTO>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			@SuppressWarnings("unchecked")
			List<Partner> values = session.createQuery("from Partner p where p.number is not null and p.deleteTimestamp is null ORDER BY NLSSORT(name, 'NLS_SORT=GENERIC_M') ").list();

			data = getLegalDTOList(values);
			session.close();
		} catch (RuntimeException e) {
			session.close();
		}
		//Collections.sort(data);
		return data.toArray(new KeyValueDTO[0]);

	}
	
	public static Long findPartnerByPartnerNameAndNumber(String partnerName, long partnerNumber) {
		Long partnerId = null;
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();
			Connection conn = session.connection();
			PreparedStatement pstmt = conn.prepareStatement("select * FROM PARTNER where PARTNER_NAME = ? and PARTNER_NR = ?");
			pstmt.setString(1, partnerName);
			pstmt.setLong(2, partnerNumber);
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) {
				partnerId = rs.getLong(1);
				break;
			}
			rs.close();
			pstmt.close();
			conn.close();
			session.close();
			System.out.println("returnFlag >>>>>>>>>>>>> "+partnerId);
		} catch(Exception ex)
		{
			System.out.println("Error ------> "+ex.getMessage());
			ex.printStackTrace();
			partnerId = null;
		}
		return partnerId;
	}
	
	public static Partner findByPartnerNameAndNumber(String partnerName, long partnerNumber) {
        
        try {
               Session session = HibernateUtil.getSessionFactory().openSession();
               
               List<Partner> parners = session.createQuery("from Partner p where p.name= '"      + partnerName + "'" +" and p.number="+partnerNumber).list();
               
               if(parners != null && !parners.isEmpty())
               {
                     return parners.get(0);
               }
               
               session.close();
        } catch(Exception ex)
        {
               System.out.println("Error ------> "+ex.getMessage());
               ex.printStackTrace();
               return null;
        }
        return null;
 }


}
