package com.bayerbbs.applrepos.dto;



public class BuildingAreaDTO extends LocationDTO {
	private static final long serialVersionUID = -8683668255902232558L;
	
//	private Set<BuildingDTO> buildings;
	private String buildingData;
	
	private Long buildingId;
	
//	public Set<BuildingDTO> getBuildings() {
//		return buildings;
//	}
//	public void setBuildings(Set<BuildingDTO> buildings) {
//		this.buildings = buildings;
//	}
	
	public String getBuildingData() {
		return buildingData;
	}
	public void setBuildingData(String buildingData) {
		this.buildingData = buildingData;
	}
	
	public Long getBuildingId() {
		return buildingId;
	}
	public void setBuildingId(Long buildingId) {
		this.buildingId = buildingId;
	}
}