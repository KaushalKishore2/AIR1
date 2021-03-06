package com.bayerbbs.applrepos.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;

@Immutable
@Entity
@Table(name = "V_MD_CLASS_INFORMATION")
public class ClassInformation implements Serializable {
	private static final long serialVersionUID = 4501233402812738360L;

	private Long classInformationId;
	private String classInformationName;
	private String classProtectionName;

	@Transient
	public Long getId() {
		return getClassInformationId();
	}
	
	@Id
	@Column(name = "CLASS_INFORMATION_ID")
	public Long getClassInformationId() {
		return classInformationId;
	}
	public void setClassInformationId(Long classInformationId) {
		this.classInformationId = classInformationId;
	}

	@Column(name = "CLASS_INFORMATION_NAME")
	public String getClassInformationName() {
		return classInformationName;
	}
	public void setClassInformationName(String classInformationName) {
		this.classInformationName = classInformationName;
	}

	@Column(name = "CLASS_PROTECTION_NAME")
	public String getClassProtectionName() {
		return classProtectionName;
	}

	public void setClassProtectionName(String classProtectionName) {
		this.classProtectionName = classProtectionName;
	}
}