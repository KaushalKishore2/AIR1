package com.bayerbbs.applrepos.service;

import java.util.List;

import com.bayerbbs.applrepos.common.StringUtils;
import com.bayerbbs.applrepos.constants.AirKonstanten;
import com.bayerbbs.applrepos.domain.Building;
import com.bayerbbs.applrepos.domain.BuildingArea;
import com.bayerbbs.applrepos.domain.CiBase;
import com.bayerbbs.applrepos.domain.CiLokationsKette;
import com.bayerbbs.applrepos.domain.ItSystem;
import com.bayerbbs.applrepos.domain.Room;
import com.bayerbbs.applrepos.domain.Schrank;
import com.bayerbbs.applrepos.domain.Standort;
import com.bayerbbs.applrepos.domain.Terrain;
import com.bayerbbs.applrepos.dto.BuildingAreaDTO;
import com.bayerbbs.applrepos.dto.BuildingDTO;
import com.bayerbbs.applrepos.dto.CiBaseDTO;
import com.bayerbbs.applrepos.dto.ItSystemDTO;
import com.bayerbbs.applrepos.dto.PersonsDTO;
import com.bayerbbs.applrepos.dto.RoomDTO;
import com.bayerbbs.applrepos.dto.SchrankDTO;
import com.bayerbbs.applrepos.dto.StandortDTO;
import com.bayerbbs.applrepos.dto.TerrainDTO;
import com.bayerbbs.applrepos.dto.ViewDataDTO;
import com.bayerbbs.applrepos.hibernate.BuildingHbn;
import com.bayerbbs.applrepos.hibernate.CiEntitiesHbn;
import com.bayerbbs.applrepos.hibernate.PersonsHbn;
import com.bayerbbs.applrepos.hibernate.RoomHbn;
import com.bayerbbs.applrepos.hibernate.SchrankHbn;
import com.bayerbbs.applrepos.hibernate.StandortHbn;
import com.bayerbbs.applrepos.hibernate.TerrainHbn;

public class CiEntityWS {
	static final String YES = "Y";
	static final String NO = "N";

	public CiEntityParameterOutput findCiEntities(CiEntityParameterInput input) {		
		CiEntityParameterOutput output = new CiEntityParameterOutput();
		List<ViewDataDTO> listDTO = CiEntitiesHbn.findCisByTypeAndNameOrAlias(input.getType(), input.getQuery());
		
		if (listDTO.size() > 0) {
			output.setViewdataDTO(getViewDataArray(listDTO));
		}
		return output;
	}

	
	private static ViewDataDTO[] getViewDataArray(List<ViewDataDTO> listDTO) {
		ViewDataDTO aViewDataDTO[] = null;
		if (null != listDTO && !listDTO.isEmpty()) {
			aViewDataDTO = new ViewDataDTO[listDTO.size()];
			for (int i = 0; i < aViewDataDTO.length; i++) {
				aViewDataDTO[i] = listDTO.get(i);
			}
		}
		return aViewDataDTO;
	}
	
	public DwhEntityParameterOutput findByTypeAndName(CiEntityParameterInput input) {//String ciType, String ciName
		DwhEntityParameterOutput output = new DwhEntityParameterOutput();
		
		if(LDAPAuthWS.isLoginValid(input.getCwid(), input.getToken()))
			output = CiEntitiesHbn.findByTypeAndName(input.getType(), input.getQuery(), input.getStart(), input.getLimit());
		
		return output;
	}
	
	public DwhEntityParameterOutput getDwhEntityRelations(CiEntityParameterInput input) {
		DwhEntityParameterOutput output = new DwhEntityParameterOutput();
		
		if(LDAPAuthWS.isLoginValid(input.getCwid(), input.getToken()))
			output = CiEntitiesHbn.getDwhEntityRelations(input.getTableId(), input.getCiId(), input.getDirection());
		
		return output;
	}
	
	public StandortDTO getStandort(CiDetailParameterInput input) {
		StandortDTO standortDTO = new StandortDTO();

		if(LDAPAuthWS.isLoginValid(input.getCwid(), input.getToken())) {
			Standort standort = StandortHbn.findById(Standort.class, input.getCiId());
			CiLokationsKette lokationsKette = TerrainHbn.findLokationsKetteById(input.getCiId());
			
//			Set<Terrain> terrains = standort.getTerrains();
			standortDTO.setTableId(AirKonstanten.TABLE_ID_SITE);
			
			setCiBaseData(standortDTO, standort);
			standortDTO.setCiLokationsKette(lokationsKette);
		}
		
		return standortDTO;
	}
	
	public TerrainDTO getTerrain(CiDetailParameterInput input) {
		TerrainDTO terrainDTO = new TerrainDTO();

		if(LDAPAuthWS.isLoginValid(input.getCwid(), input.getToken())) {
			Terrain terrain = TerrainHbn.findById(Terrain.class, input.getCiId());
			CiLokationsKette lokationsKette = TerrainHbn.findLokationsKetteById(input.getCiId());
			
//			Set<Building> building = terrain.getBuildings();
			terrainDTO.setTableId(AirKonstanten.TABLE_ID_TERRAIN);
			
			setCiBaseData(terrainDTO, terrain);
			terrainDTO.setCiLokationsKette(lokationsKette);
		}
		
		return terrainDTO;
	}
	
	public SchrankDTO getSchrank(CiDetailParameterInput input) {
		SchrankDTO schrankDTO = new SchrankDTO();

		if(LDAPAuthWS.isLoginValid(input.getCwid(), input.getToken())) {
			Schrank schrank = SchrankHbn.findById(input.getCiId());
			CiLokationsKette lokationsKette = SchrankHbn.findLokationsKetteById(input.getCiId());
			
			//wenn noch alle R�ume irgendwie auf die GUI sollen
//			Set<Room> rooms = buildingArea.getRooms();
			schrankDTO.setTableId(AirKonstanten.TABLE_ID_POSITION);
			
			setCiBaseData(schrankDTO, schrank);
			schrankDTO.setCiLokationsKette(lokationsKette);
		}
		
		return schrankDTO;
	}
	
	public BuildingDTO getBuilding(CiDetailParameterInput input) {
		BuildingDTO buildingDTO = new BuildingDTO();

		if(LDAPAuthWS.isLoginValid(input.getCwid(), input.getToken())) {
			Building building = BuildingHbn.findById(input.getCiId());
			CiLokationsKette lokationsKette = BuildingHbn.findLokationsKetteById(input.getCiId());
			
			//wenn noch alle R�ume aller BuildingAreas irgendwie auf die GUI sollen
//			Set<BuildingArea> buildingAreas = building.getBuildingAreas();
			
			buildingDTO.setAlias(building.getAlias());
			buildingDTO.setStreet(building.getStreet());
			buildingDTO.setStreetNumber(building.getStreetNumber());
			buildingDTO.setPostalCode(building.getPostalCode());
			buildingDTO.setLocation(building.getLocation());
			buildingDTO.setTableId(AirKonstanten.TABLE_ID_BUILDING);
			
			setCiBaseData(buildingDTO, building);
			buildingDTO.setCiLokationsKette(lokationsKette);
		}

		return buildingDTO;
	}
	
	public BuildingAreaDTO getBuildingArea(CiDetailParameterInput detailInput) {
		BuildingAreaDTO buildingAreaDTO = new BuildingAreaDTO();

		if(LDAPAuthWS.isLoginValid(detailInput.getCwid(), detailInput.getToken())) {
			BuildingArea buildingArea = BuildingHbn.findBuildingAreaById(detailInput.getCiId());
			CiLokationsKette lokationsKette = BuildingHbn.findLokationsKetteByAreaId(detailInput.getCiId());
			
			//wenn noch alle R�ume irgendwie auf die GUI sollen
//			Set<Room> rooms = buildingArea.getRooms();
			buildingAreaDTO.setTableId(AirKonstanten.TABLE_ID_BUILDING_AREA);
			
			setCiBaseData(buildingAreaDTO, buildingArea);
			buildingAreaDTO.setCiLokationsKette(lokationsKette);
		}

		return buildingAreaDTO;
	}
	
	
	public RoomDTO getRoom(CiDetailParameterInput detailInput) {
		RoomDTO roomDTO = new RoomDTO();
//		CiDetailParameterOutput<RoomDTO> output = new CiDetailParameterOutput<RoomDTO>();

		if(LDAPAuthWS.isLoginValid(detailInput.getCwid(), detailInput.getToken())) {
			Room room = RoomHbn.findById(detailInput.getCiId());
			CiLokationsKette lokationsKette = RoomHbn.findLokationsKetteById(detailInput.getCiId());
			Building building = room.getBuildingArea().getBuilding();
			

			setCiBaseData(roomDTO, room);
			roomDTO.setAlias(room.getAlias());
			roomDTO.setSeverityLevelId(room.getSeverityLevelId());
			roomDTO.setBusinessEssentialId(room.getBusinessEssentialId());
			roomDTO.setRoomType(room.getRoomType());
			roomDTO.setFloor(room.getFloor());
			roomDTO.setAreaId(room.getBuildingAreaId());
			
			roomDTO.setCiLokationsKette(lokationsKette);
			
			roomDTO.setStreet(building.getStreet());
			roomDTO.setStreetNumber(building.getStreetNumber());
			roomDTO.setPostalCode(building.getPostalCode());
			roomDTO.setLocation(building.getLocation());
			roomDTO.setTableId(AirKonstanten.TABLE_ID_ROOM);
			
			
			//Zugriffsrechte setzen
			AccessRightChecker checker = new AccessRightChecker();
			if (checker.isRelevanceOperational(detailInput.getCwid().toUpperCase(), room)) {
				roomDTO.setRelevanceOperational(AirKonstanten.YES_SHORT);
			} else {
				roomDTO.setRelevanceOperational(AirKonstanten.NO_SHORT);
			}
			
			String source = room.getInsertQuelle();
			if(!source.equals(AirKonstanten.INSERT_QUELLE_SISEC) && !source.equals(AirKonstanten.APPLICATION_GUI_NAME)) {
				roomDTO.setSeverityLevelIdAcl(AirKonstanten.NO_SHORT);
			}
		}
		
//		output.setCiDetailDTO(roomDTO);//setRoomDTO setCiDetailDTO
		return roomDTO;
	}
	
	public ItSystemDTO getSystemPlatformsById(CiEntityParameterInput detailInput) {
		List<ItSystem> itSystems = RoomHbn.getSystemPlatformsById(detailInput.getCiId());
		
		
		 
		return null;
	}
	
	private void setCiBaseData(CiBaseDTO ciBaseDTO, CiBase ciBase) {
		ciBaseDTO.setId(ciBase.getId());
		ciBaseDTO.setName(ciBase.getName());
		
		
		//			applicationDTO.setItsecGroupId(application.getItsecGroupId());
		ciBaseDTO.setInsertQuelle(ciBase.getInsertQuelle());
		ciBaseDTO.setInsertUser(ciBase.getInsertUser());
		
		if (null != ciBase.getInsertTimestamp())
			ciBaseDTO.setInsertTimestamp(ciBase.getInsertTimestamp().toString());
		
		ciBaseDTO.setUpdateQuelle(ciBase.getUpdateQuelle());
		ciBaseDTO.setUpdateUser(ciBase.getUpdateUser());
		
		if (null != ciBase.getUpdateTimestamp())
			ciBaseDTO.setUpdateTimestamp(ciBase.getUpdateTimestamp().toString());
		
		ciBaseDTO.setDeleteQuelle(ciBase.getDeleteQuelle());
		ciBaseDTO.setDeleteUser(ciBase.getDeleteUser());
		
		if (null != ciBase.getDeleteTimestamp())
			ciBaseDTO.setDeleteTimestamp(ciBase.getDeleteTimestamp().toString());

		ciBaseDTO.setCiOwnerHidden(ciBase.getCiOwner());
		ciBaseDTO.setCiOwnerDelegateHidden(ciBase.getCiOwnerDelegate());
		
		ciBaseDTO.setSlaId(ciBase.getSlaId());
		ciBaseDTO.setServiceContractId(ciBase.getServiceContractId());
		
		ciBaseDTO.setItset(ciBase.getItset());
		ciBaseDTO.setTemplate(ciBase.getTemplate());
		ciBaseDTO.setItsecGroupId(ciBase.getItsecGroupId());
		
		Long template = ciBase.getTemplate();
		if (-1 == template.longValue()) {
			// TODO -1 != 1 - Achtung beim Speichern
			template = new Long(1);
			//FEHLT NOCH siehe ApplicationWS!!
		}

		ciBaseDTO.setRefId(ciBase.getRefId());
		
		
		if (StringUtils.isNotNullOrEmpty(ciBaseDTO.getCiOwnerHidden())) {
			List<PersonsDTO> persons = PersonsHbn.findPersonByCWID(ciBaseDTO.getCiOwnerHidden());
			if (null != persons && 1 == persons.size()) {
				PersonsDTO person = persons.get(0);
				ciBaseDTO.setCiOwner(person.getDisplayNameFull());
			}
		}

		if (StringUtils.isNotNullOrEmpty(ciBaseDTO.getCiOwnerDelegateHidden())) {
			List<PersonsDTO> persons = PersonsHbn.findPersonByCWID(ciBaseDTO.getCiOwnerDelegateHidden());
			if (null != persons && 1 == persons.size()) {
				PersonsDTO person = persons.get(0);
				ciBaseDTO.setCiOwnerDelegate(person.getDisplayNameFull());
			}
		}
		
		
		Long relevanceItsec = ciBase.getRelevanceITSEC();
		Long relevanceICS = ciBase.getRelevanceICS();
		
		if (-1 == relevanceItsec) {
			ciBaseDTO.setRelevanceGR1435(YES);
		}
		else {// if (0 == relevanceItsec) {
			ciBaseDTO.setRelevanceGR1435(NO);
		}
		if (-1 == relevanceICS) {
			ciBaseDTO.setRelevanceGR1920(YES);
		}
		else {//(0 == relevanceICS) {
			ciBaseDTO.setRelevanceGR1920(NO);
		}

		
		String source = ciBaseDTO.getInsertQuelle();
		if(!source.equals(AirKonstanten.INSERT_QUELLE_SISEC) &&
		   !source.equals(AirKonstanten.APPLICATION_GUI_NAME)) {
			
			ciBaseDTO.setCiOwnerAcl(AirKonstanten.NO_SHORT);
			ciBaseDTO.setCiOwnerDelegateAcl(AirKonstanten.NO_SHORT);
			ciBaseDTO.setRelevanceGR1435Acl(AirKonstanten.NO_SHORT);
			ciBaseDTO.setRelevanceGR1920Acl(AirKonstanten.NO_SHORT);
			ciBaseDTO.setGxpFlagIdAcl(AirKonstanten.NO_SHORT);
			ciBaseDTO.setRefIdAcl(AirKonstanten.NO_SHORT);
			ciBaseDTO.setItsecGroupIdAcl(AirKonstanten.NO_SHORT);
			ciBaseDTO.setSlaIdAcl(AirKonstanten.NO_SHORT);
			ciBaseDTO.setServiceContractIdAcl(AirKonstanten.NO_SHORT);
			
			//fehlt, n�tig?
//			License_Scanning,Sample_Test_Date,Sample_Test_Result,Itsec_SB_Integ_ID,Itsec_SB_Integ_Txt,
//			Itsec_SB_Verfg_ID,Itsec_SB_Verfg_Txt,Itsec_SB_Vertr_ID,Itsec_SB_Vertr_Txt
		}
	}

	protected RoomDTO getRoomDTOFromEditInput(RoomEditParameterInput editInput) {
		RoomDTO roomDTO = new RoomDTO();
		roomDTO.setId(editInput.getId());
		roomDTO.setName(editInput.getName());
		roomDTO.setAlias(editInput.getAlias());
		roomDTO.setFloor(editInput.getFloor());
		roomDTO.setAreaId(editInput.getAreaId());
		
		roomDTO.setCiOwner(editInput.getCiOwner());
		roomDTO.setCiOwnerHidden(editInput.getCiOwnerHidden());
		roomDTO.setCiOwnerDelegate(editInput.getCiOwnerDelegate());
		roomDTO.setCiOwnerDelegateHidden(editInput.getCiOwnerDelegateHidden());

		roomDTO.setBusinessEssentialId(editInput.getBusinessEssentialId());
		
		roomDTO.setItset(editInput.getItset());
		roomDTO.setTemplate(editInput.getTemplate());
		roomDTO.setItsecGroupId(editInput.getItsecGroupId());
		roomDTO.setRefId(editInput.getRefId());
		
		roomDTO.setRelevanceICS(editInput.getRelevanceICS());
		roomDTO.setRelevanzItsec(editInput.getRelevanzITSEC());
		roomDTO.setGxpFlag(editInput.getGxpFlag());
		roomDTO.setGxpFlagId(editInput.getGxpFlagId());

		return roomDTO;
	}
	
	public CiEntityEditParameterOutput saveRoom(RoomEditParameterInput editInput) {
		CiEntityEditParameterOutput output = new CiEntityEditParameterOutput();

		if (null != editInput) {
			RoomDTO dto = getRoomDTOFromEditInput(editInput);
			output = RoomHbn.saveRoom(editInput.getCwid(), dto);
		}
		
		return output;
	}
	
	public CiEntityEditParameterOutput deleteRoom(RoomEditParameterInput editInput) {
		CiEntityEditParameterOutput output = new CiEntityEditParameterOutput();

		if (null != editInput) {
			if (LDAPAuthWS.isLoginValid(editInput.getCwid(), editInput.getToken())) {
				RoomDTO dto = getRoomDTOFromEditInput(editInput);

				output = RoomHbn.deleteRoom(editInput.getCwid(), dto);
			} else {
				// TODO MESSAGE LOGGED OUT
			}
		}

		return output;
	}

	
	public CiEntityEditParameterOutput createRoom(RoomEditParameterInput editInput) {

		CiEntityEditParameterOutput output = new CiEntityEditParameterOutput();

		if (null != editInput && (LDAPAuthWS.isLoginValid(editInput.getCwid(), editInput.getToken())) ) {
			RoomDTO dto = getRoomDTOFromEditInput(editInput);

			// create Application - fill attributes
			if (null == dto.getCiOwner()) {
				dto.setCiOwner(editInput.getCwid().toUpperCase());
				dto.setCiOwnerHidden(editInput.getCwid().toUpperCase());
			}
			if (null == dto.getBusinessEssentialId()) {
				dto.setBusinessEssentialId(AirKonstanten.BUSINESS_ESSENTIAL_DEFAULT);
			}

			// save / create application
			output = RoomHbn.createRoom(editInput.getCwid(), dto, true);

			if (AirKonstanten.RESULT_OK.equals(output.getResult())) {
				// get detail
				List<CiBaseDTO> listCi = CiEntitiesHbn.findCisByNameOrAlias(dto.getName(), AirKonstanten.TABLE_ID_ROOM, false);
				if (null != listCi && 1 == listCi.size()) {
					Long ciId = listCi.get(0).getId();
					output.setCiId(ciId);
				} else {
					// unknown?
					output.setCiId(new Long(-1));
				}
			} else {
				// TODO errorcodes / Texte
				if (null != output.getMessages() && output.getMessages().length > 0) {
					output.setDisplayMessage(output.getMessages()[0]);
				}
			}
		}

		return output;
	}

	
}