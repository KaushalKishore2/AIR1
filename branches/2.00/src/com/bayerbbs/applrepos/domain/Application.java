package com.bayerbbs.applrepos.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "ANWENDUNG")
@SequenceGenerator(name = "MySeqAnwendung", sequenceName = "TBADM.SEQ_ANWENDUNG")
public class Application extends DeletableRevisionInfo {
	// basics
	private Long applicationId;
	private String barApplicationId;	// Bayer application register id
	private String applicationName;
	private String applicationAlias;
	private String version;
	private Long applicationCat2Id;
	// primary function - only display
	private Long lifecycleStatusId; // Attribut LC_STATUS_ID
	private Long operationalStatusId; // Attribut EINSATZ_STATUS_ID
	private String comments;
	

	// contacts
	private String responsible; // Attribut CWID_VERANTW_BETR
	private String subResponsible;
	
	
	// agreements
	private Long slaId;
	private Long priorityLevelId;
	private Long serviceContractId;
	private Long severityLevelId;
	private Long businessEssentialId;


	// protection
	private Long itSecSbAvailability;
	private String itSecSbAvailabilityText;
	private Long classInformationId;
	private String classInformationExplanation;

	// compliance
	private Long itset;
	private Long template;
	private Long itsecGroupId;
	private Long refId;
	
	private Long relevanceICS;
	private Long relevanzITSEC;
	private Long relevance2059;
	private Long relevance2008;
	private String gxpFlag;
	// private String riskAnalysisYN;
	
	
	// license & costs
	private Long licenseTypeId;
	
	private String dedicated;
	private Long accessingUserCount;
	private Long accessingUserCountMeasured;
	private String loadClass;
	private Long costRunPa;
	private Long costChangePa;
	private Long currencyId;
	private Long costRunAccountId;
	private Long costChangeAccountId;
	
	
	// unsorted
	private String clusterCode;
	private String clusterType;
	
	private String applicationOwner;
	private String applicationOwnerDelegate;
	private String applicationSteward;
	
	private Long categoryBusiness;
	private Long classDataId;

	
	private String serviceModel;
	private String organisationalScope;
	private String barRelevance;
	

	public Application() {
	}

	
	@Transient
	public Long getId() {
		return getApplicationId();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "MySeqAnwendung")
	@Column(name = "ANWENDUNG_ID")
	public Long getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Long anwendungId) {
		this.applicationId = anwendungId;
	}

	@Column(name = "BAR_APPLICATION_ID")
	public String getBarApplicationId() {
		return barApplicationId;
	}

	public void setBarApplicationId(String barApplicationId) {
		this.barApplicationId = barApplicationId;
	}

	@Column(name = "ANWENDUNG_NAME")
	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	@Column(name = "ALIAS")
	public String getApplicationAlias() {
		return applicationAlias;
	}

	public void setApplicationAlias(String applicationAlias) {
		this.applicationAlias = applicationAlias;
	}

	@Column(name = "ANWENDUNG_KAT2_ID")
	public Long getApplicationCat2Id() {
		return applicationCat2Id;
	}

	public void setApplicationCat2Id(Long applicationCat2Id) {
		this.applicationCat2Id = applicationCat2Id;
	}

	@Column(name = "CLUSTER_CODE")
	public String getClusterCode() {
		return clusterCode;
	}

	public void setClusterCode(String clusterCode) {
		this.clusterCode = clusterCode;
	}

	@Column(name = "CLUSTER_TYPE")
	public String getClusterType() {
		return clusterType;
	}

	public void setClusterType(String clusterType) {
		this.clusterType = clusterType;
	}

	@Column(name = "COMMENTS")
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@Column(name = "LC_STATUS_ID")
	public Long getLifecycleStatusId() {
		return lifecycleStatusId;
	}

	public void setLifecycleStatusId(Long lifecycleStatusId) {
		this.lifecycleStatusId = lifecycleStatusId;
	}

	//in Basisklasse?
	@Column(name = "CWID_VERANTW_BETR")//Feldname=CWID_VERANTW_BETR in IT_SYSTEM und ANWENDUNG, sonst Feldname=RESPONSIBLE f�r DAS SELBE Feld
	public String getResponsible() {
		return responsible;
	}

	public void setResponsible(String responsible) {
		this.responsible = responsible;
	}

	
	//in Basisklasse
	@Column(name = "SUB_RESPONSIBLE")
	public String getSubResponsible() {
		return subResponsible;
	}

	public void setSubResponsible(String subResponsible) {
		this.subResponsible = subResponsible;
	}

	//in Basisklasse
	@Column(name = "GXP_FLAG")
	public String getGxpFlag() {
		return gxpFlag;
	}

	public void setGxpFlag(String gxpFlag) {
		this.gxpFlag = gxpFlag;
	}

	@Column(name = "EINSATZ_STATUS_ID")
	public Long getOperationalStatusId() {
		return operationalStatusId;
	}

	public void setOperationalStatusId(Long operationalStatusId) {
		this.operationalStatusId = operationalStatusId;
	}

	//in Basisklasse
	@Column(name = "ITSET")
	public Long getItset() {
		return itset;
	}

	public void setItset(Long itset) {
		this.itset = itset;
	}

	//in Basisklasse
	@Column(name = "TEMPLATE")
	public Long getTemplate() {
		return template;
	}

	public void setTemplate(Long template) {
		this.template = template;
	}

	//in Basisklasse
	@Column(name = "ITSEC_GRUPPE_ID")
	public Long getItsecGroupId() {
		return itsecGroupId;
	}

	public void setItsecGroupId(Long itsecGroupId) {
		this.itsecGroupId = itsecGroupId;
	}

	//in Basisklasse
	@Column(name = "REF_ID")
	public Long getRefId() {
		return refId;
	}

	public void setRefId(Long refId) {
		this.refId = refId;
	}

	//in Basisklasse
	@Column(name = "RELEVANCE_ICS")
	public Long getRelevanceICS() {
		return relevanceICS;
	}

	public void setRelevanceICS(Long relevanceICS) {
		this.relevanceICS = relevanceICS;
	}

	//in Basisklasse
	@Column(name = "RELEVANZ_ITSEC")
	public Long getRelevanzITSEC() {
		return relevanzITSEC;
	}

	public void setRelevanzITSEC(Long relevanzITSEC) {
		this.relevanzITSEC = relevanzITSEC;
	}

	@Column(name = "RELEVANCE_2059")
	public Long getRelevance2059() {
		return relevance2059;
	}

	public void setRelevance2059(Long relevance2059) {
		this.relevance2059 = relevance2059;
	}

	@Column(name = "RELEVANCE_2008")
	public Long getRelevance2008() {
		return relevance2008;
	}

	public void setRelevance2008(Long relevance2008) {
		this.relevance2008 = relevance2008;
	}

	//in Basisklasse
	@Column(name = "BUSINESS_ESSENTIAL_ID")
	public Long getBusinessEssentialId() {
		return businessEssentialId;
	}

	public void setBusinessEssentialId(Long businessEssentialId) {
		this.businessEssentialId = businessEssentialId;
	}

// task 142
//	/**
//	 * @return the riskAnalysisYN
//	 */
//	@Column(name = "RISK_ANALYSIS_YN")
//	public String getRiskAnalysisYN() {
//		return riskAnalysisYN;
//	}
//
//	/**
//	 * Sets the value of the {@link #riskAnalysisYN} field.
//	 * 
//	 * @param riskAnalysisYN
//	 *            The value to set.
//	 */
//	public void setRiskAnalysisYN(String riskAnalysisYN) {
//		this.riskAnalysisYN = riskAnalysisYN;
//	}

	@Column(name = "LICENSE_TYPE_ID")
	public Long getLicenseTypeId() {
		return licenseTypeId;
	}

	public void setLicenseTypeId(Long licenseTypeId) {
		this.licenseTypeId = licenseTypeId;
	}

	@Column(name = "DEDICATED_Y_N")
	public String getDedicated() {
		return dedicated;
	}

	public void setDedicated(String dedicated) {
		this.dedicated = dedicated;
	}

	@Column(name = "ACCESSING_USER_COUNT")
	public Long getAccessingUserCount() {
		return accessingUserCount;
	}

	public void setAccessingUserCount(Long accessingUserCount) {
		this.accessingUserCount = accessingUserCount;
	}

	@Column(name = "ACCESSING_USER_COUNT_MEASURED")
	public Long getAccessingUserCountMeasured() {
		return accessingUserCountMeasured;
	}

	public void setAccessingUserCountMeasured(Long accessingUserCountMeasured) {
		this.accessingUserCountMeasured = accessingUserCountMeasured;
	}

	@Column(name = "LOAD_CLASS")
	public String getLoadClass() {
		return loadClass;
	}

	public void setLoadClass(String loadClass) {
		this.loadClass = loadClass;
	}

	@Column(name = "VERSION")
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Column(name = "COST_RUN_PA")
	public Long getCostRunPa() {
		return costRunPa;
	}

	public void setCostRunPa(Long costRunPa) {
		this.costRunPa = costRunPa;
	}

	@Column(name = "COST_CHANGE_PA")
	public Long getCostChangePa() {
		return costChangePa;
	}

	public void setCostChangePa(Long costChangePa) {
		this.costChangePa = costChangePa;
	}

	@Column(name = "CURRENCY_ID")
	public Long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}

	@Column(name = "COST_RUN_ACCOUNT_ID")
	public Long getCostRunAccountId() {
		return costRunAccountId;
	}

	public void setCostRunAccountId(Long costRunAccountId) {
		this.costRunAccountId = costRunAccountId;
	}

	@Column(name = "COST_CHANGE_ACCOUNT_ID")
	public Long getCostChangeAccountId() {
		return costChangeAccountId;
	}

	public void setCostChangeAccountId(Long costChangeAccountId) {
		this.costChangeAccountId = costChangeAccountId;
	}

	//in Basisklasse
	@Column(name = "ITSEC_SB_VERFG_ID")
	public Long getItSecSbAvailability() {
		return itSecSbAvailability;
	}

	public void setItSecSbAvailability(Long itSecSbAvailability) {
		this.itSecSbAvailability = itSecSbAvailability;
	}

	//in Basisklasse
	@Column(name = "ITSEC_SB_VERFG_TXT")
	public String getItSecSbAvailabilityText() {
		return itSecSbAvailabilityText;
	}

	public void setItSecSbAvailabilityText(String itSecSbAvailabilityText) {
		this.itSecSbAvailabilityText = itSecSbAvailabilityText;
	}
	

	@Column(name = "CLASS_INFORMATION_ID")
	public Long getClassInformationId() {
		return classInformationId;
	}

	public void setClassInformationId(Long classInformationId) {
		this.classInformationId = classInformationId;
	}

	@Column(name = "CLASS_INFORMATION_EXPLANATION")
	public String getClassInformationExplanation() {
		return classInformationExplanation;
	}

	public void setClassInformationExplanation(String classInformationExplanation) {
		this.classInformationExplanation = classInformationExplanation;
	}
	
	

	//in Basisklasse
	@Column(name = "SLA_ID")
	public Long getSlaId() {
		return slaId;
	}

	public void setSlaId(Long slaId) {
		this.slaId = slaId;
	}

	@Column(name = "PRIORITY_LEVEL_ID")
	public Long getPriorityLevelId() {
		return priorityLevelId;
	}

	public void setPriorityLevelId(Long priorityLevelId) {
		this.priorityLevelId = priorityLevelId;
	}
	
	//in Basisklasse
	@Column(name = "SERVICE_CONTRACT_ID")
	public Long getServiceContractId() {
		return serviceContractId;
	}

	public void setServiceContractId(Long serviceContractId) {
		this.serviceContractId = serviceContractId;
	}

	//in Basisklasse
	@Column(name = "SEVERITY_LEVEL_ID")
	public Long getSeverityLevelId() {
		return severityLevelId;
	}

	public void setSeverityLevelId(Long severityLevelId) {
		this.severityLevelId = severityLevelId;
	}

	@Column(name = "APPLICATION_OWNER")
	public String getApplicationOwner() {
		return applicationOwner;
	}

	public void setApplicationOwner(String applicationOwner) {
		this.applicationOwner = applicationOwner;
	}

	@Column(name = "APPLICATION_OWNER_DELEGATE")
	public String getApplicationOwnerDelegate() {
		return applicationOwnerDelegate;
	}

	public void setApplicationOwnerDelegate(String applicationOwnerDelegate) {
		this.applicationOwnerDelegate = applicationOwnerDelegate;
	}
	
	
	@Column(name = "APPLICATION_STEWARD")
	public String getApplicationSteward() {
		return applicationSteward;
	}
	public void setApplicationSteward(String applicationSteward) {
		this.applicationSteward = applicationSteward;
	}

	@Column(name = "CATEGORY_BUSINESS_ID")
	public Long getCategoryBusiness() {
		return categoryBusiness;
	}

	public void setCategoryBusiness(Long categoryBusiness) {
		this.categoryBusiness = categoryBusiness;
	}
	
	@Column(name = "CLASS_DATA_ID")
	public Long getClassDataId() {
		return classDataId;
	}

	public void setClassDataId(Long classDataId) {
		this.classDataId = classDataId;
	}


	@Column(name = "SERVICE_MODEL")
	public String getServiceModel() {
		return serviceModel;
	}

	public void setServiceModel(String serviceModel) {
		this.serviceModel = serviceModel;
	}

	@Column(name = "ORG_SCOPE")
	public String getOrganisationalScope() {
		return organisationalScope;
	}

	public void setOrganisationalScope(String organisationalScope) {
		this.organisationalScope = organisationalScope;
	}

	@Column(name = "BAR_RELEVANCE_Y_N")
	public String getBarRelevance() {
		return barRelevance;
	}

	public void setBarRelevance(String barRelevance) {
		this.barRelevance = barRelevance;
	}
}