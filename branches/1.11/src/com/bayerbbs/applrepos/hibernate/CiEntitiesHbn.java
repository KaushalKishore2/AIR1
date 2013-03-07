package com.bayerbbs.applrepos.hibernate;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.bayerbbs.applrepos.common.StringUtils;
import com.bayerbbs.applrepos.constants.ApplreposConstants;
import com.bayerbbs.applrepos.dto.ApplicationDTO;
import com.bayerbbs.applrepos.dto.CiBaseDTO;
import com.bayerbbs.applrepos.dto.DwhEntityDTO;
import com.bayerbbs.applrepos.dto.ViewDataDTO;
import com.bayerbbs.applrepos.service.DwhEntityParameterOutput;

public class CiEntitiesHbn {

	private static final String PARAMETER_QUERYMODE_BEGINS_WITH = "BEGINS_WITH";
	private static final String PARAMETER_QUERYMODE_CONTAINS = "CONTAINS";
	private static final String PARAMETER_QUERYMODE_EMPTYSTRING = "";
	private static final String PARAMETER_QUERYMODE_EXACT = "EXACT";

	
	/**
	 * find all the ci's 
	 * @return
	 */
	public static List<ApplicationDTO> findExistantCisByNameOrAlias(String searchName, boolean withDeletedApplications) {

		ArrayList<ApplicationDTO> listResult = new ArrayList<ApplicationDTO>();

		boolean commit = false;
		Transaction tx = null;
		Statement selectStmt = null;
		Session session = HibernateUtil.getSession();

		Connection conn = null;

		searchName = searchName.toUpperCase();
		
		StringBuffer sql = new StringBuffer();

		sql.append("select * from DWH_ENTITY  where");
		// sql.append(" upper(deleted) = 'NO'");
		sql.append(" TABLE_ID in (");
			sql.append(ApplreposConstants.TABLE_ID_IT_SYSTEM);
			sql.append(",");
			sql.append(ApplreposConstants.TABLE_ID_APPLICATION);
			sql.append(",");
			sql.append(ApplreposConstants.TABLE_ID_WAYS);
		sql.append(")");
		
		sql.append(" and (upper(name) = '" + searchName + "'  or upper(ASSET_ID_OR_ALIAS) = '" +searchName + "')");
		
		if (withDeletedApplications) {
			sql.append(" and (TABLE_ID in ").append(ApplreposConstants.TABLE_ID_APPLICATION).append(" and upper(DELETED) = 'YES')");
		}
		else {
			sql.append(" and upper(DELETED) = 'NO'");
		}
		
		sql.append(" order by name");
		
		
		try {
			tx = session.beginTransaction();

			conn = session.connection();

			selectStmt = conn.createStatement();
			ResultSet rset = selectStmt.executeQuery(sql.toString());

			if (null != rset) {
				while (rset.next()) {
					ApplicationDTO anw = getApplicationDTOFromResultSet(rset);
					listResult.add(anw);
				}
				commit = true;
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
		} catch (Exception e) {
			//
			System.out.println(e.toString());
		}
		finally {
			HibernateUtil.close(tx, session, commit);
		}

		return listResult;
	}

	
	/**
	 * find all the ci's by exact name search
	 * @return
	 */
	public static List<ApplicationDTO> findCisByNameOrAlias(String searchName) {
		return findCisByNameOrAlias(searchName, PARAMETER_QUERYMODE_EXACT, false, null, null);
	}	 
 
	public static boolean isLikeStart(String queryMode) {
		boolean isLikeStart = false;
		
		if (null == queryMode || PARAMETER_QUERYMODE_EMPTYSTRING.equals(queryMode) || PARAMETER_QUERYMODE_CONTAINS.equals(queryMode)) {
			isLikeStart = true;
		}
		
		return isLikeStart;
	}
	
	public static boolean isLikeEnd(String queryMode) {
		boolean isLikeEnd = false;

		if (null == queryMode || PARAMETER_QUERYMODE_EMPTYSTRING.equals(queryMode) || PARAMETER_QUERYMODE_CONTAINS.equals(queryMode) || PARAMETER_QUERYMODE_BEGINS_WITH.equals(queryMode)) {
			isLikeEnd = true;
		}
		return isLikeEnd;
	}
	
	/**
	 * find all the ci's or only the applications
	 * @return
	 */
	public static List<ApplicationDTO> findCisByNameOrAlias(String searchName, String queryMode, boolean onlyapplications, String sort, String dir) {

		ArrayList<ApplicationDTO> listResult = new ArrayList<ApplicationDTO>();

		boolean commit = false;
		Transaction tx = null;
		Statement selectStmt = null;
		Session session = HibernateUtil.getSession();

		Connection conn = null;

		searchName = searchName.toUpperCase();
		
		StringBuffer sql = new StringBuffer();

		sql.append("select /*+ INDEX (DWH_ENTITY FIX_143_16) */ * from DWH_ENTITY  where");
		sql.append(" upper(deleted) = 'NO'");
		sql.append(" and TABLE_ID in (");
		if (!onlyapplications) {
			sql.append(ApplreposConstants.TABLE_ID_IT_SYSTEM);
			sql.append(",");
		}
		sql.append(ApplreposConstants.TABLE_ID_APPLICATION);
		if (!onlyapplications) {
			sql.append(",");
			sql.append(ApplreposConstants.TABLE_ID_ROOM);
			sql.append(",");
			sql.append(ApplreposConstants.TABLE_ID_BUILDING);//TABLE_ID_WAYS
		}
		sql.append(")");
		
		if (onlyapplications) {
			sql.append(" and UPPER(type) = UPPER('Application')");
		}
		
		sql.append(" and (upper(name) like '");

		// alles andere f�hrt zu einer exacten Suche
		
			if (isLikeStart(queryMode)) {
				sql.append("%");
			}
			
			sql.append(searchName);
			
			if (isLikeEnd(queryMode)) {
				sql.append("%");
			}

		sql.append("'  or upper(ASSET_ID_OR_ALIAS) like '");
		
			if (isLikeStart(queryMode)) {
				sql.append("%");
			}
			
			sql.append(searchName);
			
			if (isLikeEnd(queryMode)) {
				sql.append("%");
			}
		
		sql.append("')");
		
		
		
		
		if (StringUtils.isNotNullOrEmpty(sort)) {
			if ("applicationName".equals(sort)) {
				sql.append(" order by NAME");
			}
			else if ("applicationAlias".equals(sort)) {
				sql.append(" order by ASSET_ID_OR_ALIAS");
			}
			else if ("applicationCat1Txt".equals(sort)) {
				sql.append(" order by TYPE");
			}
			else if ("applicationCat2Txt".equals(sort)) {
				sql.append(" order by CATEGORY");
			}
			else if ("responsible".equals(sort)) {
				sql.append(" order by RESPONSIBLE");
			}
			else if ("subResponsible".equals(sort)) {
				sql.append(" order by SUB_RESPONSIBLE");
			}
			else if ("applicationOwner".equals(sort)) {
				sql.append(" order by APP_OWNER");
			}
			else if ("applicationOwnerDelegate".equals(sort)) {
				sql.append(" order by APP_OWNER_DELEGATE");
			}

			
		}
		else {
			sql.append(" order by name");
		}
		
		if (StringUtils.isNotNullOrEmpty(dir)) {
			sql.append(" ").append(dir);
		}
		
		try {
			tx = session.beginTransaction();

			conn = session.connection();

			selectStmt = conn.createStatement();
			//System.out.println(sql.toString());
			ResultSet rset = selectStmt.executeQuery(sql.toString());

			if (null != rset) {
				while (rset.next()) {
					ApplicationDTO anw = getApplicationDTOFromResultSet(rset);
					listResult.add(anw);
				}
				commit = true;
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
		} catch (Exception e) {
			//
			System.out.println(e.toString());
		}
		finally {
			HibernateUtil.close(tx, session, commit);
		}

		return listResult;
	}

	/**
	 * find all the ci's or only the applications
	 * @return
	 */
	public static List<CiBaseDTO> findCisByNameOrAlias(String searchName, Long ciTableId, boolean withDeleted) {

		ArrayList<CiBaseDTO> listResult = new ArrayList<CiBaseDTO>();

		boolean commit = false;
		Transaction tx = null;
		Statement selectStmt = null;
		Session session = HibernateUtil.getSession();

		Connection conn = null;

		searchName = searchName.toUpperCase();
		
		StringBuffer sql = new StringBuffer();

		sql.append("select /*+ INDEX (DWH_ENTITY FIX_143_16) */ * from DWH_ENTITY  where");

		if (!withDeleted) {
			sql.append(" upper(deleted) = 'NO' and ");
		}
		sql.append(" TABLE_ID in (");
		sql.append(ciTableId);
		sql.append(")");
		
		sql.append(" and (upper(name) like '");

			sql.append(searchName);

		sql.append("'  or upper(ASSET_ID_OR_ALIAS) like '");
		
			sql.append(searchName);
			
		sql.append("')");
		
		try {
			tx = session.beginTransaction();

			conn = session.connection();

			selectStmt = conn.createStatement();
			//System.out.println(sql.toString());
			ResultSet rset = selectStmt.executeQuery(sql.toString());

			if (null != rset) {
				while (rset.next()) {
					ApplicationDTO anw = getApplicationDTOFromResultSet(rset);
					CiBaseDTO baseDTO = new CiBaseDTO();
					baseDTO.setId(rset.getLong("CI_ID"));
					baseDTO.setName(rset.getString("NAME"));
					baseDTO.setAlias(rset.getString("ASSET_ID_OR_ALIAS"));
						
					listResult.add(baseDTO);
				}
				commit = true;
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
		} catch (Exception e) {
			//
			System.out.println(e.toString());
		}
		finally {
			HibernateUtil.close(tx, session, commit);
		}

		return listResult;
	}

	
	/**
	 * find all the ci's by type for the selectboxes
	 * @return
	 */
	public static List<ViewDataDTO> findCisByTypeAndNameOrAlias(String typename, String searchName) {

		ArrayList<ViewDataDTO> listResult = new ArrayList<ViewDataDTO>();

		boolean commit = false;
		Transaction tx = null;
		Statement selectStmt = null;
		Session session = HibernateUtil.getSession();

		Connection conn = null;

		searchName = searchName.toUpperCase();
		
		StringBuffer sql = new StringBuffer();

		sql.append("select * from DWH_ENTITY  where");
		sql.append(" upper(deleted) = 'NO'");
		sql.append(" and upper(type) = upper('").append(typename).append("')");
		
		sql.append(" and (upper(name) like '%" + searchName + "%'  or upper(ASSET_ID_OR_ALIAS) like '%" +searchName + "%')");
		
		sql.append(" order by NAME");
		
		try {
			tx = session.beginTransaction();

			conn = session.connection();

			selectStmt = conn.createStatement();
			ResultSet rsMessage = selectStmt.executeQuery(sql.toString());

			if (null != rsMessage) {
				while (rsMessage.next()) {
					ViewDataDTO dto = new ViewDataDTO();
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
			System.out.println(e.toString());
		}
		finally {
			HibernateUtil.close(tx, session, commit);
		}

		return listResult;
	}

	
	private static ApplicationDTO getApplicationDTOFromResultSet(ResultSet rset) {
		String type = null;
		String id = null;
		String name = null;
		String assetIdOrAlias = null;
		String responsible = null;
		String subResponsible = null;
		String applicationOwner = null;
		String applicationSteward = null;
		String applicationOwnerDelegate = null;
		Long ci_id = null;
		String category = null;
		Long tableId = null;
		String deleted = null;
		try {
			type = rset.getString("TYPE");
			id = rset.getString("ID");
			name = rset.getString("NAME");
			assetIdOrAlias = rset.getString("ASSET_ID_OR_ALIAS");
			responsible = rset.getString("RESPONSIBLE");
			subResponsible = rset.getString("SUB_RESPONSIBLE");
			category = rset.getString("CATEGORY");
			ci_id = rset.getLong("CI_ID");
			applicationOwner = rset.getString("APP_OWNER");
			applicationOwnerDelegate = rset.getString("APP_OWNER_DELEGATE");
			tableId = rset.getLong("TABLE_ID");
			deleted = rset.getString("DELETED");
			
			
			try {
				applicationSteward = rset.getString("APP_STEWARD");	
			} catch (Exception e) {
				//TODO DWH Entity - nothing da noch nicht in DWH Entity
			}
			
			
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
/*
		Long ciEntityId = new Long(0);
		if (null != id && id.startsWith("APP-")) {
			// only for apps (by now)
			String idTemp = id.substring("APP-".length());
			try {
				ciEntityId = Long.parseLong(idTemp);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
*/		
		
		// TODO rename fields application to ci entities
		ApplicationDTO anw = new ApplicationDTO();
		anw.setId(ci_id);
		anw.setName(name);
		anw.setAlias(assetIdOrAlias);
		anw.setCiOwner(responsible);
		anw.setApplicationSteward(applicationSteward);
		anw.setCiOwnerDelegate(subResponsible);
		anw.setApplicationCat1Txt(type);
		anw.setApplicationCat2Txt(category);
		anw.setApplicationOwner(applicationOwner);
		anw.setApplicationOwnerDelegate(applicationOwnerDelegate);
		anw.setTableId(tableId);
		anw.setDeleteQuelle(deleted);
		return anw;
	}

	public static List<ApplicationDTO> findCisByOUunit(String ciTypeaa, String ouUnit, String ciOwnerType, String ouQueryMode) {
		if (null == ouUnit || null == ciOwnerType) {
			return new ArrayList<ApplicationDTO>();
		}
		else {
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT * FROM TABLE (PCK_AIR.FT_OU_Owner('");
			sql.append(ouUnit);
			sql.append("', '");
			sql.append(ciOwnerType);
			sql.append("', '");
			sql.append(ouQueryMode);
			
			if (null != ciTypeaa && ciTypeaa.length() > 0) {
				sql.append("', '");
				sql.append(ciTypeaa);
			}
			
			sql.append("')) order by name");
			
			return findCis(sql.toString());
		}
	}
	
	public static List<ApplicationDTO> findMyCisOwner(String cwid, String sort, String dir, boolean onlyApplications) {
		return findMyCisOwnerOrDelegate("pck_air.FT_App_Owner", cwid, sort, dir, onlyApplications);
	}
	
	public static List<ApplicationDTO> findMyCisDelegate(String cwid, String sort, String dir, boolean onlyApplications) {
		return findMyCisOwnerOrDelegate("pck_air.FT_App_Owner_Delegate", cwid, sort, dir, onlyApplications);
	}
	
	public static List<ApplicationDTO> findMyCisForDelete(String cwid, String sort, String dir, boolean onlyApplications) {
		return findMyCisOwnerOrDelegate("pck_air.FT_My_CIs", cwid, sort, dir, onlyApplications);
	}
	
	private static List<ApplicationDTO> findMyCisOwnerOrDelegate(String ownerDelegate, String cwid, String sort, String dir, boolean onlyApplications) {

		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT * FROM TABLE (").append(ownerDelegate).append("('").append(cwid.toUpperCase()).append("'))");

		if (onlyApplications) {
			sql.append(" where TYPE= 'Application'"); 
		}
		
		// Order
		if (StringUtils.isNotNullOrEmpty(sort)) {
			if ("applicationName".equals(sort)) {
				sql.append(" order by NAME");
			}
			else if ("applicationAlias".equals(sort)) {
				sql.append(" order by ASSET_ID_OR_ALIAS");
			}
			else if ("applicationCat1Txt".equals(sort)) {
				sql.append(" order by TYPE");
			}
			else if ("applicationCat2Txt".equals(sort)) {
				sql.append(" order by CATEGORY");
			}
			else if ("responsible".equals(sort)) {
				sql.append(" order by RESPONSIBLE");
			}
			else if ("subResponsible".equals(sort)) {
				sql.append(" order by SUB_RESPONSIBLE");
			}
			else if ("applicationOwner".equals(sort)) {
				sql.append(" order by APP_OWNER");
			}
			else if ("applicationOwnerDelegate".equals(sort)) {
				sql.append(" order by APP_OWNER_DELEGATE");
			}
			
		}
		else {
			sql.append(" order by name");
		}

		// Order direction
		if (StringUtils.isNotNullOrEmpty(dir)) {
			sql.append(" ").append(dir);
		}
			
		return findCis(sql.toString());
	}

	
	private static List<ApplicationDTO> findCis(String sql) {

		ArrayList<ApplicationDTO> listeAnwendungen = new ArrayList<ApplicationDTO>();

		boolean commit = false;
		Transaction ta = null;
		Statement selectStmt = null;
		Session session = HibernateUtil.getSession();

		Connection conn = null;
		
		try {
			ta = session.beginTransaction();
			conn = session.connection();
			
			selectStmt = conn.createStatement();
			ResultSet rset = selectStmt.executeQuery(sql);
			while (rset.next()) {
				
				// TODO rename fields application to ci entities
				ApplicationDTO anw = getApplicationDTOFromResultSet(rset);
				listeAnwendungen.add(anw);
			}

			// disconnect
			ta.commit();
			rset.close();
			selectStmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			HibernateUtil.close(ta, session, commit);
		}
		return listeAnwendungen;
	}
	
	public static DwhEntityParameterOutput findByTypeAndName(String ciType, String ciName, int start, int limit) {
		String sql = "SELECT * FROM TABLE (pck_air.ft_findcis('" + ciName + "', '" + ciType + "'))";
		
		Transaction ta = null;
		Statement stmt = null;
		Connection conn = null;
		Session session = HibernateUtil.getSession();
		
		boolean commit = false;

		List<DwhEntityDTO> dwhEntities = new ArrayList<DwhEntityDTO>();
		DwhEntityParameterOutput output = new DwhEntityParameterOutput();
		int i = 0;
		
		try {
			ta = session.beginTransaction();
			conn = session.connection();
			stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery(sql);
			
			if (0 != start) { 
				rs.absolute(start + 1);//relative
			}
//			rs.setFetchSize(limit);
			
			DwhEntityDTO dwhEntity = null;
			
			while (rs.next()) {
				if(i < limit) {
					dwhEntity = new DwhEntityDTO();
					
					dwhEntity.setCiId(rs.getString("CI_ID"));
					dwhEntity.setCiType(rs.getString("TYPE"));
					dwhEntity.setCiName(rs.getString("NAME"));
					dwhEntity.setCiAlias(rs.getString("ASSET_ID_OR_ALIAS"));
					dwhEntity.setDwhEntityId(rs.getString("ID"));
					dwhEntity.setTableId(rs.getString("TABLE_ID"));
					dwhEntity.setCiOwner(rs.getString("RESPONSIBLE"));
					dwhEntity.setCiOwnerDelegate(rs.getString("SUB_RESPONSIBLE"));
					dwhEntity.setAppOwner(rs.getString("APP_OWNER"));
					dwhEntity.setAppOwnerDelegate(rs.getString("APP_OWNER_DELEGATE"));
					dwhEntity.setAppSteward(rs.getString("APP_STEWARD"));
					dwhEntity.setCategoryIt(rs.getString("CATEGORY"));
					dwhEntity.setLifecycleStatus(rs.getString("LIFECYCLE"));
					dwhEntity.setSource(rs.getString("SOURCE"));
					dwhEntity.setTemplate(rs.getString("TEMPLATE"));
	
	//				dwhEntity.setOperationalStatus(rs.getString("OPERATIONAL_STATUS"));
	//				dwhEntity.setGxpRelevance(rs.getString("GXP_RELEVANCE"));
	//				dwhEntity.setItSet(rs.getString("ITSET"));
	//				dwhEntity.setServiceContract(rs.getString("SERVICE_CONTRACT"));
	//				dwhEntity.setSeverityLevel(rs.getString("SEVERITY_LEVEL"));
	//				dwhEntity.setPriorityLevel(rs.getString("PRIORITY_LEVEL"));
	//				dwhEntity.setSla(rs.getString("SLA"));
	//				dwhEntity.setBusinessEssential(rs.getString("BUSINESS_ESSENTIAL"));
					//evtl. mehr
					
					dwhEntities.add(dwhEntity);
				}
				
				i++;
			}
			
			ta.commit();
			rs.close();
			stmt.close();
			conn.close();
			
			commit = true;
			
//			System.out.println("CiEntitesHbn::findByTypeAndName: ciTypes="+dwhEntities.size());
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			HibernateUtil.close(ta, session, commit);
		}
		
		output.setDwhEntityDTO(dwhEntities.toArray(new DwhEntityDTO[0]));
		output.setTotal(i + start);// + 1
		
		return output;
	}
	
	public static void saveCiRelations(Long tableId, Long ciId, String ciRelationsAddList, String ciRelationsDeleteList, String direction, String cwid) {
		String sql = "{call pck_air.p_save_relations(?,?,?,?,?,?)}";//"begin pck_air.p_save_relations(?,?,?,?,?,?);//"EXEC pck_air.p_save_relations ("+tableId+", "+ciId+", "+ciRelationsAddList+", "+ciRelationsDeleteList+", "+direction+", "+cwid+")";
		
		Transaction ta = null;
		Session session = HibernateUtil.getSession();
		
		boolean commit = false;
		
		try {
			ta = session.beginTransaction();
			Connection conn = session.connection();
			
			CallableStatement stmt = conn.prepareCall(sql);
			stmt.setLong(1, tableId);
			stmt.setLong(2, ciId);
			stmt.setString(3, ciRelationsAddList);
			stmt.setString(4, ciRelationsDeleteList);
			stmt.setString(5, direction);
			stmt.setString(6, cwid);
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


	public static DwhEntityParameterOutput getDwhEntityRelations(Long tableId, Long ciId, String direction) {
		String sql = "SELECT * FROM TABLE (pck_air.ft_relatedcis("+tableId+","+ciId+",'"+direction+"'))";//"begin pck_air.p_save_relations(?,?,?,?,?,?);//"EXEC pck_air.p_save_relations ("+tableId+", "+ciId+", "+ciRelationsAddList+", "+ciRelationsDeleteList+", "+direction+", "+cwid+")";
		
		Transaction ta = null;
		Statement stmt = null;
		Connection conn = null;
		Session session = HibernateUtil.getSession();
		
		boolean commit = false;

		List<DwhEntityDTO> dwhEntities = new ArrayList<DwhEntityDTO>();
		DwhEntityParameterOutput output = new DwhEntityParameterOutput();
		
		try {
			ta = session.beginTransaction();
			conn = session.connection();
			stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);

			
			DwhEntityDTO dwhEntity = null;
			
			while (rs.next()) {
				dwhEntity = new DwhEntityDTO();
				
				dwhEntity.setCiId(rs.getString("CI_ID"));
				dwhEntity.setCiType(rs.getString("TYPE"));
				dwhEntity.setCiName(rs.getString("NAME"));
//				dwhEntity.setCiAlias(rs.getString("ASSET_ID_OR_ALIAS"));
				dwhEntity.setDwhEntityId(rs.getString("ID"));
				dwhEntity.setSource(rs.getString("SOURCE"));
				
				dwhEntities.add(dwhEntity);
			}
			
			commit = true;
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			HibernateUtil.close(ta, session, commit);
		}
		
		output.setDwhEntityDTO(dwhEntities.toArray(new DwhEntityDTO[0]));
		
		return output;
	}
	
}