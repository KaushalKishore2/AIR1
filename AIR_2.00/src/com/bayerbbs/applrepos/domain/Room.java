package com.bayerbbs.applrepos.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "RAUM")
@org.hibernate.annotations.Entity(dynamicInsert = true)
@SequenceGenerator(name = "MySeqRoom", sequenceName = "SEQ_RAUM")
@NamedQueries({
//	@NamedQuery(name="findByNameOrAliasAndBuildingAreaId", query="FROM Room r WHERE (r.roomName=:name OR r.alias=:alias) AND buildingAreaId=:buildingAreaId"),
	@NamedQuery(name="findByNameOrAliasAndBuildingAreaId", query="FROM Room r WHERE (upper(r.roomName)=upper(:name) OR upper(r.alias)=upper(:alias) OR upper(r.roomName)=upper(:alias) OR upper(r.alias)=upper(:name)) AND buildingAreaId=:buildingAreaId"),
	@NamedQuery(name="findByNameAndBuildingAreaId", query="FROM Room r WHERE upper(r.roomName)=upper(:name) AND buildingAreaId=:buildingAreaId")
})
public class Room extends CiBase1 implements Serializable {
	private static final long serialVersionUID = -1270064907617489118L;

	private String alias;
	private String roomType;
	private String floor;
	
	private Long severityLevelId;
	private Long businessEssentialId;
	
	private Set<Schrank> schranks;
//	ITSEC_SB_VERTR_ID,ITSEC_SB_VERTR_TXT: confidentiality
//	ITSEC_SB_INTEG_ID,ITSEC_SB_INTEG_TXT: integrity
	
	private Long buildingAreaId;
	private BuildingArea buildingArea;
	 //vandana
    private String Provider_Name;
    private String Provider_Address; 
    private String It_Head;
    
    @Column(name = "PROVIDER_NAME")
	public String getProvider_Name() {
		return Provider_Name;
	}
	public void setProvider_Name(String Provider_Name) {
		this.Provider_Name = Provider_Name;
	}
	
	@Column(name = "PROVIDER_ADDRESS")
	public String getProvider_Address() {
		return Provider_Address;
	}
	public void setProvider_Address(String Provider_Address) {
		this.Provider_Address = Provider_Address;
	}
	
    //ended vandana

	@Column(name = "IT_HEAD")
	public String getIt_Head() {
		return It_Head;
	}
	public void setIt_Head(String It_Head) {
		this.It_Head = It_Head;
	}
	

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "MySeqRoom")
	@Column(name = "RAUM_ID")
	public Long getRoomId() {
		return getId();
	}
	public void setRoomId(Long roomId) {
		setId(roomId);
	}

	@Column(name = "RAUM_NAME")
	public String getRoomName() {
		return getName();
	}
	public void setRoomName(String roomName) {
		setName(roomName);
	}

	@Column(name = "RAUMALIAS")
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Column(name = "RAUM_TYP")
	public String getRoomType() {
		return roomType;
	}
	public void setRoomType(String roomType) {
		this.roomType = roomType;
	}

	@Column(name = "ETAGE")
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	
	@Column(name = "SEVERITY_LEVEL_ID")
	public Long getSeverityLevelId() {
		return severityLevelId;
	}
	public void setSeverityLevelId(Long severityLevelId) {
		this.severityLevelId = severityLevelId;
	}
	
	@Column(name = "BUSINESS_ESSENTIAL_ID")
	public Long getBusinessEssentialId() {
		return businessEssentialId;
	}
	public void setBusinessEssentialId(Long businessEssentialId) {
		this.businessEssentialId = businessEssentialId;
	}
	
	
	@ManyToOne
	@JoinColumn(name="AREA_ID")
	public BuildingArea getBuildingArea() {
		return buildingArea;
	}
	public void setBuildingArea(BuildingArea buildingArea) {
		this.buildingArea = buildingArea;
	}
	@Column(name = "AREA_ID", insertable=false, updatable=false)
	public Long getBuildingAreaId() {
		return buildingAreaId;
	}

	public void setBuildingAreaId(Long areaId) {
		this.buildingAreaId = areaId;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "room")//EAGER
	public Set<Schrank> getSchranks() {
		return schranks;
	}
	public void setSchranks(Set<Schrank> schranks) {
		this.schranks = schranks;
	}
	
}