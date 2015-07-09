package com.bayerbbs.applrepos.hibernate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.bayerbbs.applrepos.common.ApplReposTS;
import com.bayerbbs.applrepos.constants.AirKonstanten;
import com.bayerbbs.applrepos.domain.Konto;
import com.bayerbbs.applrepos.domain.SoftwareComponent;
import com.bayerbbs.applrepos.dto.AssetViewDataDTO;
import com.bayerbbs.applrepos.dto.PersonsDTO;
import com.bayerbbs.applrepos.service.AssetManagementParameterInput;
import com.bayerbbs.applrepos.service.AssetManagementParameterOutput;

public class SoftwareComponentHbn {

	@SuppressWarnings("unchecked")
	public static AssetManagementParameterOutput searchAsset(
			AssetManagementParameterInput input) {
		AssetManagementParameterOutput out = new AssetManagementParameterOutput();

		List<AssetViewDataDTO> list = new ArrayList<AssetViewDataDTO>();

		Transaction tx = null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {

			tx = session.beginTransaction();

			Criteria criteria = session.createCriteria(SoftwareComponent.class);

			Criterion sapDescription = Restrictions.like("prouctDescription",
					"%" + input.getQuery() + "%").ignoreCase();
			Criterion pspElement = Restrictions.like("innenauftrag",
					"%" + input.getQuery() + "%").ignoreCase();
			criteria.createAlias("konto", "konto");
			Criterion kontoName = Restrictions.like("konto.name",
					"%" + input.getQuery() + "%").ignoreCase();
			Criterion serialNumber = Restrictions.like("serialNumber",
					"%" + input.getQuery() + "%").ignoreCase();
			Criterion technicalMaster = Restrictions.like("technicalMaster",
					"%" + input.getQuery() + "%").ignoreCase();
			Criterion technicalNumber = Restrictions.like("technicalNumber",
					"%" + input.getQuery() + "%").ignoreCase();
			Criterion inventoryNumber = Restrictions.like("inventoryNumber",
					"%" + input.getQuery() + "%").ignoreCase();
			Criterion orgUnit = Restrictions.like("subResponsible",
					"%" + input.getQuery() + "%").ignoreCase();

			Criterion completeCondition = Restrictions.disjunction()
					.add(sapDescription).add(pspElement).add(kontoName)
					.add(serialNumber).add(technicalMaster)
					.add(technicalNumber).add(inventoryNumber).add(orgUnit);
			criteria.add(completeCondition);

			if (input.getSort() != null) {
				addSortingCriteria(criteria, input.getSort(), input.getDir());
			}

			criteria.setFirstResult(input.getStart());
			criteria.setMaxResults(input.getLimit());
			List<SoftwareComponent> values = (List<SoftwareComponent>) criteria
					.list();
			criteria.setFirstResult(0);
			Integer total = (Integer) criteria.setProjection(
					Projections.rowCount()).uniqueResult();
			out.setCountResultSet(total.longValue());
			list = getDTOList(values);
			out.setAssetViewDataDTO(list.toArray(new AssetViewDataDTO[list
					.size()]));
			tx.commit();
		
//			tx = session.beginTransaction();
//			BigDecimal total = (BigDecimal) session.createSQLQuery(
//					"select count(*) from SOFTWAREKOMPONENTE where lower(PRODUKTBEZ) like '%"
//							+ input.getQuery().toLowerCase() + "%'")
//					.uniqueResult();
//			out.setCountResultSet(total.longValue());
//
//			Criteria criteria = session.createCriteria(SoftwareComponent.class);
//			criteria.add(Restrictions.like("prouctDescription",
//					"%" + input.getQuery() + "%").ignoreCase());
//
//			if (input.getSort() != null) {
//				addSortingCriteria(criteria, input.getSort(), input.getDir());
//			}
//			criteria.setFirstResult(input.getStart());
//			criteria.setMaxResults(input.getLimit());
//			List<SoftwareComponent> values = (List<SoftwareComponent>) criteria
//					.list();
//			list = getDTOList(values);
//			out.setAssetViewDataDTO(list.toArray(new AssetViewDataDTO[list
//					.size()]));
//			tx.commit();
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
		return out;
	}

	private static void addSortingCriteria(Criteria criteria, String sort,
			String desc) {
		if (sort.equals("sapDescription")) {
			if ("DESC".equalsIgnoreCase(desc)) {
				criteria.addOrder(Order.desc("prouctDescription"));
			} else {
				criteria.addOrder(Order.asc("prouctDescription"));
			}
		} else if (sort.equals("pspElement")) {
			if ("DESC".equalsIgnoreCase(desc)) {
				criteria.addOrder(Order.desc("innenauftrag"));
			} else {
				criteria.addOrder(Order.asc("innenauftrag"));
			}
		} else if (sort.equals("costCenter")) {
			criteria.createAlias("konto", "konto");
			if ("DESC".equalsIgnoreCase(desc)) {
				criteria.addOrder(Order.desc("konto.name"));
			} else {
				criteria.addOrder(Order.asc("konto.name"));
			}
		} else if (sort.equals("serialNumber")) {
			if ("DESC".equalsIgnoreCase(desc)) {
				criteria.addOrder(Order.desc("serialNumber"));
			} else {
				criteria.addOrder(Order.asc("serialNumber"));
			}
		} else if (sort.equals("inventoryNumber")) {
			if ("DESC".equalsIgnoreCase(desc)) {
				criteria.addOrder(Order.desc("inventoryNumber"));
			} else {
				criteria.addOrder(Order.asc("inventoryNumber"));
			}
		} else if (sort.equals("organizationalunit")) {
			if ("DESC".equalsIgnoreCase(desc)) {
				criteria.addOrder(Order.desc("subResponsible"));
			} else {
				criteria.addOrder(Order.asc("subResponsible"));
			}
		}
	}

	private static List<AssetViewDataDTO> getDTOList(
			List<SoftwareComponent> values) {

		List<AssetViewDataDTO> list = new ArrayList<AssetViewDataDTO>();
		for (SoftwareComponent hwComp : values) {
			AssetViewDataDTO dto = getDTO(hwComp);
			dto.setIsSoftwareComponent(true);
			list.add(dto);
		}
		return list;
	}

	private static AssetViewDataDTO getDTO(SoftwareComponent swComp) {

		AssetViewDataDTO dto = new AssetViewDataDTO();

		dto.setId(swComp.getId());

		// Asset Information
		dto.setIdentNumber(swComp.getName());
		dto.setInventoryNumber(swComp.getInventoryNumber());
		dto.setSapDescription(swComp.getProuctDescription());

		// Product
		if (swComp.getHersteller() != null) {
			dto.setManufacturer(swComp.getHersteller().getName());
			dto.setManufacturerId(swComp.getHersteller().getId());
		}
		// Product Name remaining

		// Business Administration
		dto.setOrderNumber(swComp.getBestellNumber());
		Konto pspelement = PspElementHbn.getPspElementByName(swComp
				.getInnenauftrag());
		dto.setPspElement(swComp.getInnenauftrag());
		if (pspelement != null) {
			dto.setPspElementId(pspelement.getId());
			dto.setPspText(pspelement.getBeschreibung());
		}
		if (swComp.getKonto() != null) {
			dto.setCostCenter(swComp.getKonto().getName());
			dto.setCostCenterId(swComp.getKonto().getId());
			if (swComp.getKonto().getCwidVerantw() != null) {
				List<PersonsDTO> persons = PersonsHbn.findPersonByCWID(swComp
						.getKonto().getCwidVerantw());
				dto.setCostCenterManager(persons.get(0).getDisplayNameFull());
				dto.setOwner(persons.get(0).getDisplayNameFull());
				dto.setCostCenterManagerId(swComp.getKonto().getCwidVerantw());
				dto.setOrganizationalunit(persons.get(0).getOrgUnit());
			}
		}
		dto.setRequesterId(swComp.getRequester());
		if(swComp.getRequester() != null){
			List<PersonsDTO> persons = PersonsHbn.findPersonByCWID(swComp
					.getRequester());
			dto.setRequester(persons.get(0).getDisplayNameFull());
		}
		if (swComp.getSoftwareCategory1() != null) {
			dto.setSapAssetClass(swComp.getSoftwareCategory1().getSwKategory1());
			dto.setSapAssetClassId(swComp.getSoftwareCategory1().getId());
		}

		dto.setSerialNumber(swComp.getSerialNumber());
		dto.setEditorsGroup(swComp.getSubResponsible());
		return dto;
	}

	@SuppressWarnings("unchecked")
	public static AssetManagementParameterOutput findAssetById(Long assetId) {
		AssetManagementParameterOutput out = new AssetManagementParameterOutput();
		List<AssetViewDataDTO> list = new ArrayList<AssetViewDataDTO>();
		Transaction tx = null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			tx = session.beginTransaction();
			Criteria criteria = session.createCriteria(SoftwareComponent.class);
			criteria.add(Restrictions.eq("id", assetId));
			List<SoftwareComponent> values = (List<SoftwareComponent>) criteria
					.list();
			list = getDTOList(values);
			out.setAssetViewDataDTO(list.toArray(new AssetViewDataDTO[list
					.size()]));
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
		return out;
	}

	public static Boolean saveSoftwareAsset(
			AssetViewDataDTO dto) {
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		tx = session.beginTransaction();

		System.out.println(dto);
		SoftwareComponent softwareComponent = getSoftwareComponent(dto);
		System.out.println(softwareComponent);
		try {
			session.save(softwareComponent);
			session.flush();
		} catch (Exception e) {
			System.out.println("error---" + e.getMessage());
			if (tx.isActive()) {
				tx.rollback();
			}
			return false;
		} finally {
			if (tx.isActive()) {
				tx.commit();
			}
			session.close();
		}
		return true;
	}

	private static SoftwareComponent getSoftwareComponent(AssetViewDataDTO dto) {
		SoftwareComponent softwareComponent = new SoftwareComponent();

		if (dto.getId() == null) {
			softwareComponent
					.setInsertQuelle(AirKonstanten.APPLICATION_GUI_NAME);
			softwareComponent.setInsertTimestamp(ApplReposTS
					.getCurrentTimestamp());
			softwareComponent.setInsertUser(dto.getCwid());
		} else {
			softwareComponent.setId(dto.getId());
		}
		softwareComponent.setUpdateQuelle(AirKonstanten.APPLICATION_GUI_NAME);
		softwareComponent.setUpdateTimestamp(ApplReposTS.getCurrentTimestamp());
		softwareComponent.setUpdateUser(dto.getCwid());

		if (dto.getIdentNumber() != null && dto.getIdentNumber().length() > 0) {
			softwareComponent.setName(dto.getIdentNumber());
		} else {
			softwareComponent.setName(getIdentNumber());
		}

		softwareComponent.setInventoryNumber(dto.getInventoryNumber());
		softwareComponent.setTechnicalMaster(dto.getTechnicalMaster());
		softwareComponent.setTechnicalNumber(dto.getTechnicalNumber());
		softwareComponent.setSerialNumber(dto.getSerialNumber());

		softwareComponent.setSoftwareCategory1Id(dto.getSapAssetClassId());
		softwareComponent.setSoftwareCategory2Id(dto.getSubcategoryId());
		softwareComponent.setHerstellerId(dto.getManufacturerId());

		softwareComponent.setKontoId(dto.getCostCenterId());
		softwareComponent.setBestellNumber(dto.getOrderNumber());
		softwareComponent.setInnenauftrag(dto.getPspElement());
		softwareComponent.setInventoryNumber(dto.getInventoryNumber());
		softwareComponent.setSubResponsible(dto.getEditorsGroup());

		softwareComponent.setRequester(dto.getRequesterId());
		softwareComponent.setProuctDescription(dto.getSapDescription());
		softwareComponent.setResponsible(dto.getCostCenterManagerId());

		Long itSet = null;
		String strItSet = ApplReposHbn.getItSetFromCwid(dto.getRequesterId());
		if (null != strItSet) {
			itSet = Long.parseLong(strItSet);
		}
		if (null == itSet) {
			itSet = new Long(AirKonstanten.IT_SET_DEFAULT);
		}
		softwareComponent.setItset(itSet);

		return softwareComponent;
	}

	private static String getIdentNumber() {

		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		return df.format(new Date(System.currentTimeMillis()));
	}

}
