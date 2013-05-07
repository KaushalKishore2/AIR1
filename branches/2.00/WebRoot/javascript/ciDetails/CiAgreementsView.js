Ext.namespace('AIR');

AIR.CiAgreementsView = Ext.extend(AIR.AirView, {//Ext.Panel
	
	initComponent: function() {
		Ext.apply(this, {
			labelWidth: 200,
		    title: 'Agreements',
		    border: false,
		    bodyStyle: 'padding:10px',
		    
		    layout: 'form',
		    height: 150,
		    
		    items: [{
		        xtype: 'filterCombo',//combo
		        width: 230,
		        fieldLabel: 'SLA',
		        id: 'sla',
		        store: AIR.AirStoreManager.getStoreByName('slaListStore'),//slaListStore,
		        valueField: 'id',
		        displayField: 'text',
		        
//		        typeAhead: true,
//		        forceSelection: true,
//		        autoSelect: false,
		        
		        triggerAction: 'all',
//		        lazyRender: true,
//		        lazyInit: false,
		        mode: 'local'
		    }, {
		        xtype: 'filterCombo',//combo
		        width: 230,
		        fieldLabel: 'Service Contract',
		        id: 'serviceContract',
		        store: AIR.AirStoreManager.getStoreByName('serviceContractListStore'),//serviceContractListStore,
		        valueField: 'id',
		        displayField: 'text',
		        
//		        typeAhead: true,
//		        forceSelection: true,
//		        autoSelect: false,
		        
		        triggerAction: 'all',
//		        lazyRender: true,
//		        lazyInit: false,
		        mode: 'local',
		        
		        listEmptyText: 'No matching items found'
		    },{
		        xtype: 'combo',
		        width: 230,
		        fieldLabel: 'Priority Level',
//		        name: 'priorityLevel',
		        
		        id: 'priorityLevel',
		        store: AIR.AirStoreManager.getStoreByName('priorityLevelListStore'),//priorityLevelListStore,
		        valueField: 'id',
		        displayField: 'text',
		        
//		        typeAhead: true,
//		        forceSelection: true,
//		        autoSelect: false,
		        
		        triggerAction: 'all',
//		        lazyRender: true,
//		        lazyInit: false,
		        mode: 'local'
		    },{
		        xtype: 'filterCombo',//combo
		        width: 230,
		        fieldLabel: 'Severity Level',
		        
		        id: 'severityLevel',
		        store: AIR.AirStoreManager.getStoreByName('severityLevelListStore'),//severityLevelListStore,
		        valueField: 'id',
		        displayField: 'text',
		        
//		        typeAhead: true,
//		        forceSelection: true,
//		        autoSelect: false,
		        
		        triggerAction: 'all',
//		        lazyRender: true,
//		        lazyInit: false,
		        mode: 'local'
		    },{
		        xtype: 'filterCombo',//combo
		        width: 230,
		        fieldLabel: 'Business Essential',
//		        name: 'businessEssential',
		        
		        id: 'businessEssential',
		        store: AIR.AirStoreManager.getStoreByName('businessEssentialListStore'),//businessEssentialListStore,
		        valueField: 'id',
		        displayField: 'text',
		        
//		        typeAhead: true,
//		        forceSelection: true,
//		        autoSelect: false,
		        
		        triggerAction: 'all',
//		        lazyRender: true,
//		        lazyInit: false,
		        mode: 'local'
		        
//		        disabledClass: 'x-item-disabled disabled'
		    }]
		});
		
		AIR.CiAgreementsView.superclass.initComponent.call(this);
		
		//in eine AirDetailView f�r alle CI Detail Seiten?
		this.addEvents('ciBeforeChange', 'ciChange');
		
		var cbSla = this.getComponent('sla');
		var cbServiceContract = this.getComponent('serviceContract');
		var cbPriorityLevel = this.getComponent('priorityLevel');
		var cbSeverityLevel = this.getComponent('severityLevel');
		var cbBusinessEssential = this.getComponent('businessEssential');
		
		
		cbSla.on('select', this.onSlaSelect, this);
		cbSla.on('change', this.onSlaChange, this);
//		cbSla.on('blur', this.onSlaBlur, this);
		
		cbServiceContract.on('select', this.onServiceContractSelect, this);
		cbServiceContract.on('change', this.onServiceContractChange, this);
		
		cbPriorityLevel.on('select', this.onPriorityLevelSelect, this);
		cbPriorityLevel.on('change', this.onPriorityLevelChange, this);
		
		cbSeverityLevel.on('select', this.onSeverityLevelSelect, this);
		cbSeverityLevel.on('change', this.onSeverityLevelChange, this);
		
		cbBusinessEssential.on('select', this.onBusinessEssentialSelect, this);
		cbBusinessEssential.on('change', this.onBusinessEssentialChange, this);
		
		//Deaktiviert, damit beim Laden des Stores von combo wizardsla load handler von Wizard Seite 2
		//(durch ehemals an Stelle this.getComponent('wizardAgreements').getComponent('wizardserviceContract').store.load();)
		//kein Fehler in Methode this.onServiceContractLoad kommt und diese Methode erst sinnvollerweise gar nicht
		//automatisch aufgerufen wird wie es beim folgendem Statement der Fall w�re. Statt dessen wird jeweils ein callback
		//listener in der this.getComponent('serviceContract').getStore().load() Funktion verwendet, wo der serviceContract oder
		//wizardserviceContract combo store geladen werden muss. Dieser Store ist durch
		//store: AIR.AirStoreManager.getStoreByName('serviceContractListStore') immer der SELBE! Evtl. muss hier eine stabilere
		//L�sung als die callbacks gefunden werden, wenn diese SLA - SERVICECONTRACT combo Abh�ngigkeitskonstalltion h�ufiger
		//oder bei anderen combos und deren stores auftritt/ben�tigt wird.
//		this.getComponent('serviceContract').getStore().on('load', this.onServiceContractLoad, this);
	},
	
	onBusinessEssentialSelect: function(combo, record, index) {
    	this.fireEvent('ciChange', this, combo);
    },
    onBusinessEssentialChange: function (combo, newValue, oldValue) {
    	if(this.isComboValueValid(combo, newValue, oldValue))
    		this.fireEvent('ciChange', this, combo);
    },
	
	
	onSeverityLevelSelect: function(combo, record, index) {
    	this.fireEvent('ciChange', this, combo);
    },
    onSeverityLevelChange: function (combo, newValue, oldValue) {
    	if(this.isComboValueValid(combo, newValue, oldValue))
    		this.fireEvent('ciChange', this, combo);
    },
	
	onPriorityLevelSelect: function(combo, record, index) {
    	this.fireEvent('ciChange', this, combo);
    },
    onPriorityLevelChange: function (combo, newValue, oldValue) {
    	if(this.isComboValueValid(combo, newValue, oldValue))
    		this.fireEvent('ciChange', this, combo);
    },
	
	
	onServiceContractSelect: function(combo, record, index) {
		var cbSla = this.getComponent('sla');
		cbSla.setValue(record.get('slaId'));
		
		this.fireEvent('ciChange', this, combo);
	},
	onServiceContractChange: function (combo, newValue, oldValue) {
		if(this.isComboValueValid(combo, newValue, oldValue)) {
			this.fireEvent('ciChange', this, combo);
			
			var cbSla = this.getComponent('sla');
			var r = Util.getComboRecord(combo, 'id', parseInt(newValue));//cbServiceContract.getStore().getById(parseInt(data.serviceContractId));
			if(r)
				cbSla.setValue(r.get('slaId'));
		}
	},
	
	onSlaSelect: function(combo, record, index) {
		var cbServiceContract = this.getComponent('serviceContract');
		cbServiceContract.reset();

		var filterData = { slaId: record.data.id };
		cbServiceContract.filterByData(filterData);
		
		if(cbServiceContract.getStore().getCount() === 1)
			cbServiceContract.setValue(cbServiceContract.getStore().getAt(0).get('id'));
    	
    	this.fireEvent('ciChange', this, combo);
	},
	
	onSlaChange: function(combo, newValue, oldValue) {
		if(this.isComboValueValid(combo, newValue, oldValue))
			this.fireEvent('ciChange', this, combo);
		
		var cbServiceContract = this.getComponent('serviceContract');

		if(typeof newValue === 'string' && newValue.length === 0) {
			combo.reset();
			cbServiceContract.reset();
		} else {
			cbServiceContract.reset();

			var filterData = { slaId: newValue };
			cbServiceContract.filterByData(filterData);
			
			if(cbServiceContract.getStore().getCount() === 1)
				cbServiceContract.setValue(cbServiceContract.getStore().getAt(0).get('id'));
		}
	},
	
	clear: function(data) {
		this.update(data);
	},    
	
	
	update: function(data) {
		var cbSla = this.getComponent('sla');
		var cbServiceContract = this.getComponent('serviceContract');

		if (data.slaId != 0 && !data.isCiCreate) {//selectedSlaId !== undefined && selectedSlaId != 0
			this.getComponent('sla').setValue(data.slaId);

			var filterData = { slaId: data.slaId };
			cbServiceContract.filterByData(filterData);
		} else {
			cbSla.reset();//setValue('');
			cbServiceContract.reset();//.setValue('');
		}
		
		if (data.serviceContractId && data.serviceContractId != 0 && !data.isCiCreate) {
			cbServiceContract.setValue(data.serviceContractId);
			
			var sla = cbSla.getValue();
			if(!sla || sla.length === 0) {
				var r = Util.getComboRecord(cbServiceContract, 'id', parseInt(data.serviceContractId));//cbServiceContract.getStore().getById(parseInt(data.serviceContractId));
				if(r)
					cbSla.setValue(r.get('slaId'));
			}
		} else {
			cbServiceContract.setValue('');
		}
		
		
		var cbPriorityLevel = this.getComponent('priorityLevel');
		if(parseInt(data.tableId) === AC.TABLE_ID_APPLICATION) {
			cbPriorityLevel.setVisible(true);
			
			if (data.priorityLevelId && data.priorityLevelId != 0 && !data.isCiCreate) {
				cbPriorityLevel.setValue(data.priorityLevelId);
			} else {
				cbPriorityLevel.setValue('');
			}
		} else {
			cbPriorityLevel.reset();
			cbPriorityLevel.setVisible(false);
		}
		
		

		var cbSeverityLevel = this.getComponent('severityLevel');
		var cbBusinessEssential = this.getComponent('businessEssential');
		
		if(parseInt(data.tableId) === AC.TABLE_ID_APPLICATION ||
		   parseInt(data.tableId) === AC.TABLE_ID_ROOM ||
		   parseInt(data.tableId) === AC.TABLE_ID_POSITION) {
			
			cbSeverityLevel.setVisible(true);
			cbBusinessEssential.setVisible(true);
			
			if (data.severityLevelId && data.severityLevelId != 0 && !data.isCiCreate) {
				cbSeverityLevel.setValue(data.severityLevelId);
			} else {
				cbSeverityLevel.setValue('');
			}
			
			if(data.isCiCreate)
				cbBusinessEssential.setValue('');
			else
				cbBusinessEssential.setValue(data.businessEssentialId);
		} else {
			cbSeverityLevel.reset();
			cbBusinessEssential.reset();
			cbSeverityLevel.setVisible(false);
			cbBusinessEssential.setVisible(false);
		}
		
		this.updateAccessMode(data);
		
		this.doLayout();
	},
	
	updateAccessMode: function(data) {
		AIR.AirAclManager.setAccessMode(this.getComponent('sla'), data);
		AIR.AirAclManager.setAccessMode(this.getComponent('serviceContract'), data);
		
		if(parseInt(data.tableId) === AC.TABLE_ID_APPLICATION)
			AIR.AirAclManager.setAccessMode(this.getComponent('priorityLevel'), data);
		
		if(parseInt(data.tableId) === AC.TABLE_ID_APPLICATION ||
		   parseInt(data.tableId) === AC.TABLE_ID_ROOM ||
		   parseInt(data.tableId) === AC.TABLE_ID_POSITION) {
			AIR.AirAclManager.setAccessMode(this.getComponent('severityLevel'), data);
			
			var cbBusinessEssential = this.getComponent('businessEssential');
			if(AIR.AirApplicationManager.hasRole(AC.USER_ROLE_AIR_BUSINESS_ESSENTIAL_EDITOR)) {

				// nur f�r die Rolle BusinessEssential-Editor
				// unter Pr�fung der Insert-Source mittels isEditable
				if (AIR.AirAclManager.isEditable(cbBusinessEssential)) {
					Util.enableCombo(cbBusinessEssential);
					
					//alt
//					AIR.AirAclManager.setMandatory(cbBusinessEssential, 'mandatory');
					//neu
//					AIR.AirAclManager.setNecessity(cbBusinessEssential);
					AIR.AirAclManager.setNecessityInternal(cbBusinessEssential.label, 'mandatory');
					
					// this.setEditable(aclItemCmp); // diese Methode pr�ft die Rechte und verhindert das Editieren...
					// deshalb setzen wir das FormElement einzeln auf true
	//				AIR.AirAclManager.setFormElementEnable(cbBusinessEssential, true);
				}
			} else {
				//cbBusinessEssential.disable();
				Util.disableCombo(cbBusinessEssential);
				
//				AIR.AirAclManager.setMandatory(cbBusinessEssential, 'optional');
				AIR.AirAclManager.setNecessityInternal(cbBusinessEssential.label, 'optional');
			}


			if ('' === data.deleteTimestamp) {
				// normal ci data
			}
			else {
				// we can't edit deleted entries
				Util.disableCombo(cbBusinessEssential);
			}
			
		}
	},
	
	setData: function(data) {
		var field = this.getComponent('sla');
//		if (!field.disabled)
//			if(field.getValue().length > 0)
//				data.slaId = field.getValue();
		if (!field.disabled) {
			if(field.getValue()) {//.length > 0
				data.slaId = field.getValue();
			} else {
				data.slaId = -1;
			}
		}
		
		field = this.getComponent('serviceContract');
//		if (!field.disabled)
//			if(field.getValue().length > 0)
//				data.serviceContractId = field.getValue();
		if (!field.disabled) {
			if(field.getValue()) {//.length > 0
				data.serviceContractId = field.getValue();
			} else {
				data.serviceContractId = -1;
			}
		}
		
		if(parseInt(data.tableId) === AC.TABLE_ID_APPLICATION) {
			field = this.getComponent('priorityLevel');
			if (!field.disabled) {
				if(field.getValue().length > 0) {
					data.priorityLevelId = field.getValue();
				} else {
					data.priorityLevelId = -1;
				}
			}
		}
		
		if(parseInt(data.tableId) === AC.TABLE_ID_APPLICATION ||
		   parseInt(data.tableId) === AC.TABLE_ID_ROOM ||
		   parseInt(data.tableId) === AC.TABLE_ID_POSITION) {
		
			field = this.getComponent('severityLevel');
			if (!field.disabled) {
				if(field.getValue().length > 0) {
					data.severityLevelId = field.getValue();
				} else {
					data.severityLevelId = -1;
				}
			}
			
			field = this.getComponent('businessEssential');
			if (!field.disabled)
				if(field.getValue().length > 0)
					data.businessEssentialId = field.getValue();
		}
		
		return data;
	},
	
	onServiceContractLoad: function(store, records, options) {
		var data = AIR.AirApplicationManager.getAppDetail();
		
		if (data.serviceContractId && data.serviceContractId != 0) {
			this.getComponent('serviceContract').setValue(data.serviceContractId);//setRawValue data.serviceContract
		} else {
			this.getComponent('serviceContract').setValue('');
		}
	},
	
	updateLabels: function(labels) {
		this.setTitle(labels.agreementsPanelTitle);
		this.setFieldLabel(this.getComponent('sla'), labels.sla);
		this.setFieldLabel(this.getComponent('priorityLevel'), labels.priorityLevel);
		this.setFieldLabel(this.getComponent('serviceContract'), labels.serviceContract);
		this.setFieldLabel(this.getComponent('severityLevel'), labels.severityLevel);
		this.setFieldLabel(this.getComponent('businessEssential'), labels.businessEssential);
	},
	
	updateToolTips: function(toolTips) {
		this.setTooltipData(this.getComponent('sla').label, toolTips.slaName, toolTips.slaNameText);
		this.setTooltipData(this.getComponent('priorityLevel').label, toolTips.priorityLevel, toolTips.priorityLevelText);
		this.setTooltipData(this.getComponent('serviceContract').label, toolTips.serviceContract, toolTips.serviceContractText);
		this.setTooltipData(this.getComponent('severityLevel').label, toolTips.severityLevel, toolTips.severityLevelText);
		this.setTooltipData(this.getComponent('businessEssential').label, toolTips.businessEssential, toolTips.businessEssentialText);
	}
});
Ext.reg('AIR.CiAgreementsView', AIR.CiAgreementsView);