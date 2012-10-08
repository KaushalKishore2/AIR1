Ext.namespace('AIR');

AIR.CiSpecificsView = Ext.extend(AIR.AirView, {//Ext.Panel
	initComponent: function() {
		this.objectAliasAllowedStore = AIR.AirStoreFactory.getObjectAliasAllowedStore();
		
		Ext.apply(this, {
			labelWidth: 200, // label settings here cascade unless overridden
		    title: 'Specifics',
		    
		    border: false,
		    bodyStyle: 'padding:10px',

		    layout: 'form',
		    
		    items: [{
		        xtype: 'textfield',
		    	fieldLabel: 'Application ID',
		        id: 'applicationId',
		        disabled: true,
		        hideLabel: true,
		        hidden: true,
//		        width: 230,
		        anchor: '70%'
		    },{
		        xtype: 'textfield',
		    	fieldLabel: 'Object Type',
		        id: 'objectType',
		        disabled: true,
		        hideLabel: true,
		        hidden: true,
//		        width: 230,
		        anchor: '70%'
		    }/*,{
		    	xtype: 'textfield',
//		        width: 230,
		        fieldLabel: 'Application Name',
		        id: 'applicationName',
		        hidden: true,
		        anchor: '70%',
		        
		        enableKeyEvents: true
		    }*/
		    ,{
	            xtype: 'radiogroup',
    			id: 'rgBARrelevance',
    			width: 250,
    			fieldLabel: 'BAR relevant',
    			
    			columns: 3,

	            items: [
                    { id: 'rgBARrelevanceYes',		itemId: 'rgBARrelevanceYes', 		boxLabel: 'Yes',	name: 'rgBARrelevance', inputValue: 'Y', width: 80 },//, width: 80 wenn gedatscht
	                { id: 'rgBARrelevanceNo',		itemId: 'rgBARrelevanceNo',			boxLabel: 'No',	name: 'rgBARrelevance', inputValue: 'N', width: 80 },
	                { id: 'rgBARrelevanceUndefined',itemId: 'rgBARrelevanceUndefined', 	boxLabel: 'Undefined',	name: 'rgBARrelevance', inputValue: 'U', width: 80, checked: true }//, checked: true
	            ]
		    },{
		    	xtype: 'textfield',
//		        width: 230,
		        fieldLabel: 'BAR Application Id',
		        anchor: '70%',
		        disabled: true,
		        cls: 'required',
		        
		        id: 'barApplicationId'
	        },{
		    	xtype: 'textfield',
//		        width: 230,
		        fieldLabel: 'Application Alias',
		        anchor: '70%',
		        
		        cls: 'required',
		        
		        id: 'applicationAlias'
//		        enableKeyEvents: true //Stop IE to always set the cursor at the end when pushing left/right or setting the cursor with the mouse
		    },{
		    	xtype: 'textfield',
//		    	width: 230,
		    	anchor: '70%',
		        fieldLabel: 'Version',
		        id: 'applicationVersion',
		        enableKeyEvents: true,
		        allowBlank: true,
		        
		        style: {
		        	marginBottom: 10
		        }
		    },{
		        xtype: 'filterCombo',//combo
		        width: 230,
//		        anchor: '70%',//siehe (*1)
		        fieldLabel: 'Category',
				
				//transform: this.getEl(),
				//disabledClass: Ext.isIE ? 'x-item-disabled-ie' : 'x-item-disabled',
		        
		        id: 'applicationCat2',
		        store: AIR.AirStoreManager.getStoreByName('applicationCat2ListStore'),//applicationCat2ListStore,
		        valueField: 'id',
		        displayField: 'text',
				lastQuery: '',
		        
//		        typeAhead: true,
//		        forceSelection: true,
//		        autoSelect: false,
		        
		        triggerAction: 'all',//all query
		        lazyRender: true,
		        lazyInit: false,
		        mode: 'local'
			},{
		        xtype: 'combo',
		        width: 230,
//		        anchor: '70%',//siehe (*1)
		        fieldLabel: 'Lifecycle',
		        
		        id: 'lifecycleStatus',
		        store: AIR.AirStoreManager.getStoreByName('lifecycleStatusListStore'),//lifecycleStatusListStore,
		        valueField: 'id',
		        displayField: 'text',
		        
//		        typeAhead: true,
//		        forceSelection: true,
//		        autoSelect: false,
		        
		        triggerAction: 'all',//all query
		        lazyRender: true,
		        lazyInit: false,
		        mode: 'local'
		    },{
		        xtype: 'combo',
		        width: 230,
//		        anchor: '70%',//siehe (*1)
		        fieldLabel: 'Operational status',

//		        style: {
//		    		marginTop: 20
//		    	},
		        
		        id: 'operationalStatus',
		        store: AIR.AirStoreManager.getStoreByName('operationalStatusListStore'),//operationalStatusListStore,
		        valueField: 'id',
		        displayField: 'text',
		        
//		        typeAhead: true,
//		        forceSelection: true,
//		        autoSelect: false,
		        
		        triggerAction: 'all',//all query
		        lazyRender: true,
		        lazyInit: false,
		        mode: 'local'
		    },{
//		        xtype: 'fieldset',
//		        id: 'fsOrganisationalScope',
//		        title: ' ',
//		        labelWidth: 5,//5 200
////		        anchor: '60%',
//		        width: 120,
//		        
//		        style: {
//		    		marginTop: 20
//		    	},
//		        
//				items: [{
			        xtype: 'listview',//grid
			        width: 80,
			        //height: 170,//150
	//		        frame: true,
			        border: false,
			        fieldLabel: 'Organisational scope',

//			        style: {
//			    		marginBottom: 20
//			    	},
			        
			        id: 'organisationalScope',
			        store: AIR.AirStoreManager.getStoreByName('organisationalScopeListStore'),
			        
			        singleSelect: false,
			        multiSelect: true,
			        simpleSelect: true,
			        hideHeaders: true,
			        
			        columns: [
						{dataIndex: 'id', hidden: true, hideLabel: true, width: .001},
						{dataIndex: 'name'}
			        ]
		    	},{
		    		//because listview applicationUsingRegions is still editable when using applicationUsingRegions.disable();
		    		xtype: 'textarea',
		        	id: 'organisationalScopeHidden',
		        	fieldLabel: 'Organisational scope',		    		

//			        style: {
//			    		marginBottom: 20
//			    	},
			        
		        	width: 80,
		        	height: 130,
		        	hidden: true,
		        	disabled: true
//		    	}]
	        },{
		    	xtype: 'textarea',
		        id: 'comments',

		        anchor: '70%',
//		        grow: true,
		        height: 50,
		        fieldLabel: 'Comments',
		        
		        enableKeyEvents: true,
		        allowBlank: true,
		        autoScroll: true
		    },{
		        xtype: 'fieldset',
		        id: 'specificsCategory',
		        
		        title: 'Categories',
		        labelWidth: 190,
//		        height: 170,//sollte nicht notwendig sein!
		        anchor: '70%',
		        layout: 'form',//orig: auskommentiert, anchor
		        
				items: [{
	    			xtype: 'combo',
	    	        id: 'cbApplicationBusinessCat',
	    	        
	    	        store: AIR.AirStoreManager.getStoreByName('categoryBusinessListStore'),//categoryBusinessListStore,
	    	        valueField: 'id',
	    	        displayField: 'text',
	    	        fieldLabel: 'applicationBusinessCat',
	    	        
//	    	        anchor: '100%',
	    	        width: 250,
			        
//			        typeAhead: true,
//			        forceSelection: true,
//			        autoSelect: false,
			        
			        triggerAction: 'all',//all query
	    	        lazyRender: true,
	    	        lazyInit: false,
	    	        mode: 'local'
				},{
	    			xtype: 'combo',
	    	        id: 'cbDataClass',
	    	        
	    	        store: AIR.AirStoreManager.getStoreByName('dataClassListStore'),//dataClassListStore,//dataClassListStore operationalStatusListStore,
	    	        valueField: 'id',
	    	        displayField: 'text',
	    	        fieldLabel: 'dataClass',	        
	    	        
//	    	        anchor: '100%',
	    	        width: 250,
			        
//			        typeAhead: true,
//			        forceSelection: true,
//			        autoSelect: false,
			        
			        triggerAction: 'all',//all query
	    	        lazyRender: true,
	    	        lazyInit: false,
	    	        mode: 'local'
    			},{
    				xtype: 'panel',
    				id: 'pBusiness',
//    				width: 250,
    				anchor: '100%',
    				layout: 'column',//hbox
//    				pack: 'start',
    				border: false,
    				
    				items: [{
    					xtype: 'label',
    					id: 'labelbusinessProcess',
    					width: 195,
    					
    					style: {
    						fontSize: 12
    					}
		    		}, {
						xtype: 'textarea',
				        id: 'businessProcess',
//						anchor: '70%',
				        width: 300,
				        height: 100,
				        
				        autoScroll: true,
				        readOnly: true
//				        flex: 20
				    },{
						xtype: 'hidden',
				        id: 'businessProcessHidden'
//				        flex: 1
				    }, {
				    	xtype: 'commandlink',
				    	id: 'businessProcessaddimg',
				    	img: img_AddBusinessProcess
				    },{
				    	xtype: 'commandlink',
				    	id: 'businessProcessremoveimg',
				    	img: img_RemoveBusinessProcess
				    }]
    			}]
			}]
		});
		
		AIR.CiSpecificsView.superclass.initComponent.call(this);
		
		this.addEvents('ciBeforeChange', 'ciChange', 'ciInvalid');
		
//		var tfApplicationName = this.getComponent('applicationName');
		var tfApplicationAlias = this.getComponent('applicationAlias');
		var tfApplicationVersion = this.getComponent('applicationVersion');
		var rgBARrelevance = this.getComponent('rgBARrelevance');
		
		var cbApplicationCat2 = this.getComponent('applicationCat2');
		var cbLifecycleStatus = this.getComponent('lifecycleStatus');
//		var cbOrganisationalScope = this.getComponent('organisationalScope');
		var lvOrganisationalScope = this.getComponent('organisationalScope');//.getComponent('fsOrganisationalScope')
		lvOrganisationalScope.on('selectionchange', this.onOrganisationalScopeChange, this);
		var cbOperationalStatus = this.getComponent('operationalStatus');
		
		var tfComments = this.getComponent('comments');
		
		var cbApplicationBusinessCat = this.getComponent('specificsCategory').getComponent('cbApplicationBusinessCat');
		var cbDataClass = this.getComponent('specificsCategory').getComponent('cbDataClass');
			
		
//		tfApplicationName.on('change', this.onApplicationNameChange, this);
		tfApplicationAlias.on('change', this.onApplicationAliasChange, this);
		tfApplicationVersion.on('change', this.onApplicationVersionChange, this);
		
		rgBARrelevance.on('change', this.onBARrelevanceChange, this);
		
		cbApplicationCat2.on('select', this.onApplicationCat2Select, this);
		cbApplicationCat2.on('beforeselect', this.onApplicationCat2BeforeSelect, this);
		cbApplicationCat2.on('change', this.onApplicationCat2Change, this);
//		cbApplicationCat2.getStore().on('datachanged', this.updateInternal, this);
//		cbApplicationCat2.getStore().clearFilter(true);
//		cbApplicationCat2.getStore().filter('applicationCat1Id', '5');
		
		
		
		cbLifecycleStatus.on('select', this.onLifecycleStatusSelect, this);
		cbLifecycleStatus.on('change', this.onLifecycleStatusChange, this);
		
//		cbOrganisationalScope.on('select', this.onOrganisationalScopeSelect, this);
//		cbOrganisationalScope.on('change', this.onOrganisationalScopeChange, this);
		
		cbOperationalStatus.on('select', this.onOperationalStatusSelect, this);
		cbOperationalStatus.on('change', this.onOperationalStatusChange, this);
		
		tfComments.on('change', this.onCommentsChange, this);
		
		cbApplicationBusinessCat.on('select', this.onApplicationBusinessCatSelect, this);
		cbApplicationBusinessCat.on('change', this.onApplicationBusinessCatChange, this);
		
		cbDataClass.on('select', this.onDataClassSelect, this);
		cbDataClass.on('change', this.onDataClassChange, this);
		
		
		var pBusiness = this.getComponent('specificsCategory').getComponent('pBusiness');
		var clBusinessProcessAdd = pBusiness.getComponent('businessProcessaddimg');
		var clBusinessProcessRemove = pBusiness.getComponent('businessProcessremoveimg');
		clBusinessProcessAdd.on('click', this.onBusinessProcessAdd, this);
		clBusinessProcessRemove.on('click', this.onBusinessProcessRemove, this);
	},
	
	onOrganisationalScopeChange: function(listview, selections) {
		var scopeRecords = listview.getSelectedRecords();

		var firstRecord = scopeRecords[0];
		var lastRecord = scopeRecords[scopeRecords.length - 1];
		
		if(scopeRecords.length > 0 && (firstRecord.get('name') === AC.ORG_SCOPE_DEFAULT || lastRecord.get('name') === AC.ORG_SCOPE_DEFAULT)) {
			var defaultRecord = firstRecord;
			if(defaultRecord.get('name') !== AC.ORG_SCOPE_DEFAULT)
				defaultRecord = lastRecord;
				
			listview.clearSelections();
			listview.select(defaultRecord, true, true);
		}
		
		this.fireEvent('ciChange', this, listview, selections);
	},
	
	onApplicationNameChange: function(textfield, newValue, oldValue) {
//		activateButtonSaveApplication();
		this.fireEvent('ciChange', this, textfield, newValue);
	},
	
	onApplicationAliasChange: function (textfield, newValue, oldValue) {
//		// data changed - activate save button
//		activateButtonSaveApplication();
//
//		// set the value back to default
//		this.getComponent('applicationAlias').clearInvalid();
		
    	// check vor valid application alias name. Funktioniert noch nicht. Wenn bereits erxistierender Alias (z.B. Test) eingegeben wird:
		//geht das Speicehrn bis zum Server durhc. Muss sofort kommen und ggf. save/canel Buttons deaktiviert werden
		if(newValue.length > 0 && newValue != AIR.AirApplicationManager.getAppDetail().applicationAlias) {//nur aufrufen wenn alias != geladenem alias
			this.objectAliasAllowedStore.setBaseParam('query', newValue);
			this.objectAliasAllowedStore.load({
				callback: function() {
					// the function to handle the response
	        		if(this.objectAliasAllowedStore.getAt(0) === undefined ? true : this.objectAliasAllowedStore.getAt(0).data.countResultSet == 0 ? true : false) {
						this.getComponent('applicationAlias').clearInvalid();
		    			// data changed - activate save button	
		    			// activateButtonSaveApplication();
						this.fireEvent('ciChange', this, textfield, newValue);
					} else {
						this.getComponent('applicationAlias').markInvalid();
		    			// data changed - but not valid deactivate save button
		    			// deactivateButtonSaveApplication();
						
						this.fireEvent('ciInvalid', this, textfield, newValue);
					}
				}.createDelegate(this)
			});
		}
	},
	
	
	onApplicationVersionChange: function(textfield, newValue, oldValue) {
		this.fireEvent('ciChange', this, textfield, newValue);
	},
	
	onBARrelevanceChange: function(rgb, checkedRadio) {
		if(checkedRadio.inputValue === 'U')
			rgb.setValue(AIR.AirApplicationManager.getAppDetail().barRelevance);
		
		this.fireEvent('ciChange', this, rgb, checkedRadio);
	},

	
	onApplicationCat2Select: function(combo, record, index) {
//		var applicationName = AIR.AirApplicationManager.getAppDetail().applicationName;
//		
//		
//		var sapRegex = '[0-9a-zA-Z#=\+\-\_\/\\. ]+M[0-9]+C[0-9]+';
//		var isSapName = applicationName.match(AC.REGEX_SAP_NAME) != null;
		
    	this.fireEvent('ciChange', this, combo, record);
    },
	onApplicationCat2BeforeSelect: function(combo, record, index) {
		return this.checkSapName(record);		
		
		/*
		var applicationName = AIR.AirApplicationManager.getAppDetail().applicationName;
		
		var isSapCat2 = record.get('guiSAPNameWizard') === 'Y';//this.getComponent('applicationCat2').getStore().getById(cat2Id).get('guiSAPNameWizard') === 'Y';//AC.CI_CAT1_SAP_CAT2_ID.indexOf(cat2Id) > -1;
		var isSapName = applicationName.match(AC.REGEX_SAP_NAME) != null;
		
		var isValidCat2 = (isSapCat2 && !isSapName) || (!isSapCat2 && isSapName) ? false : true;
		
		if(!isValidCat2) {
			var data = {
				airErrorId: AC.AIR_ERROR_INVALID_CAT2_SAP,
				applicationName: applicationName,
				applicationCat1: AIR.AirApplicationManager.getAppDetail().applicationCat1Txt,
				isSapApp: !isSapCat2 && isSapName,
				applicationCat2: record.get('text')
			};
			this.fireEvent('airAction', this, 'airError', data);
		}
			//error message || fire AIR action to display error on bottom toolbar
		
		return isValidCat2;*/
    },
    onApplicationCat2Change: function(combo, newValue, oldValue) {
    	if(this.isComboValueValid(combo, newValue, oldValue)) {
//    		var record = combo.getStore().getById(newValue);
//    		if(!record)
//    			combo.getStore().getAt(combo.getStore().findExact('text', newValue));
    		
    		var record = combo.getStore().getById(newValue) || combo.getStore().getAt(combo.getStore().findExact('text', newValue));
    		
    		if(record)
    			if(this.checkSapName(record))
    				this.fireEvent('ciChange', this, combo, newValue);
    			else
    				combo.setValue(oldValue);//record.get('id')
    	}
    },
    
    checkSapName: function(record) {
    	var applicationName = AIR.AirApplicationManager.getAppDetail().applicationName;
    	
		var isSapCat2 = record.get('guiSAPNameWizard') === 'Y';//this.getComponent('applicationCat2').getStore().getById(cat2Id).get('guiSAPNameWizard') === 'Y';//AC.CI_CAT1_SAP_CAT2_ID.indexOf(cat2Id) > -1;
		var isSapName = applicationName.match(AC.REGEX_SAP_NAME) != null;
		
		var isValidCat2 = (isSapCat2 && !isSapName) || (!isSapCat2 && isSapName) ? false : true;
		
		if(!isValidCat2) {
			var data = {
				airErrorId: AC.AIR_ERROR_INVALID_CAT2_SAP,
				applicationName: applicationName,
//				applicationCat1: AIR.AirApplicationManager.getAppDetail().applicationCat1Txt,
				isSapApp: !isSapCat2 && isSapName,
				sapApplicationCat2: isSapCat2 ? record.get('text') : AIR.AirApplicationManager.getAppDetail().applicationCat2Txt,
				applicationCat2: isSapCat2 ? AIR.AirApplicationManager.getAppDetail().applicationCat2Txt : record.get('text')
			};
			this.fireEvent('airAction', this, 'airError', data);
		}
		
		return isValidCat2;
    },
    
    onLifecycleStatusSelect: function(combo, record, index) {
    	this.fireEvent('ciChange', this, combo, record);
    },
    onLifecycleStatusChange: function (combo, newValue, oldValue) {
    	if(this.isComboValueValid(combo, newValue, oldValue))
    		this.fireEvent('ciChange', this, combo, newValue);
    },
    
    
//    onOrganisationalScopeSelect: function(combo, record, index) {
//    	this.fireEvent('ciChange', this, combo, record);
//    },
//    onOrganisationalScopeChange: function (combo, newValue, oldValue) {
//    	if(this.isComboValueValid(combo, newValue, oldValue))
//    		this.fireEvent('ciChange', this, combo, newValue);
//    },
    
    
    onOperationalStatusSelect: function(combo, record, index) {
    	this.fireEvent('ciChange', this, combo, record);
    },
    onOperationalStatusChange: function(combo, newValue, oldValue) {
    	if(this.isComboValueValid(combo, newValue, oldValue))
    		this.fireEvent('ciChange', this, combo, newValue);
    },
    
    
    onCommentsChange: function(textarea, newValue, oldValue) {
		this.fireEvent('ciChange', this, textarea, newValue);
	},
    
    
	onApplicationBusinessCatSelect: function(combo, record, index) {
//        activateButtonSaveApplication();
    	this.fireEvent('ciChange', this, combo, record);
            
        // reload data class
        if(record) {//!==undefined
            selectedCategoryBusinessId = record.data['id'];
            this.getComponent('specificsCategory').getComponent('cbDataClass').store.load();
            // selectedDataClassId = '0';
            this.getComponent('specificsCategory').getComponent('cbDataClass').setValue('');
            
            
     		// activate data class
//            if (isRelevance(this.getComponent('cbDataClass'))) {
            
            var appDetail = AIR.AirApplicationManager.getAppDetail();
            if (AIR.AirAclManager.isRelevance(this.getComponent('specificsCategory').getComponent('cbDataClass'), appDetail)) {
				this.getComponent('specificsCategory').getComponent('cbDataClass').enable();
				this.getComponent('specificsCategory').getComponent('cbDataClass').setHideTrigger(false);
			} else {
				if(Ext.isIE)
					this.getComponent('specificsCategory').getComponent('cbDataClass').el.dom.disabled = true;
				else
					this.getComponent('specificsCategory').getComponent('cbDataClass').disable();

				this.getComponent('specificsCategory').getComponent('cbDataClass').setHideTrigger(true);
			}
        }
        else {
        	this.getComponent('specificsCategory').getComponent('cbDataClass').setValue('');
        }
    },
    onApplicationBusinessCatChange: function(combo, newValue, oldValue) { 
		if (newValue !== oldValue) {
			this.getComponent('specificsCategory').getComponent('cbDataClass').setValue('');
		}
		
		if(newValue.length === 0) {
			if(Ext.isIE)
				this.getComponent('specificsCategory').getComponent('cbDataClass').el.dom.disabled = true;
			else
				this.getComponent('specificsCategory').getComponent('cbDataClass').disable();
				
			this.getComponent('specificsCategory').getComponent('cbDataClass').setHideTrigger(true);
		}
		
		if(this.isComboValueValid(combo, newValue, oldValue))
			this.fireEvent('ciChange', this, combo, newValue);
    },

    onDataClassSelect: function(combo, record, index) {
    	this.fireEvent('ciChange', this, combo, record);
    },
    onDataClassChange: function(combo, newValue, oldValue) {
    	if(this.isComboValueValid(combo, newValue, oldValue))
    		this.fireEvent('ciChange', this, combo, newValue);
    },
    
    
    
	
	onBusinessProcessAdd: function(link, event) {
		AIR.AirPickerManager.openRecordPicker(
			this.onRecordAdded.createDelegate(this), this.getComponent('specificsCategory').getComponent('pBusiness').getComponent('businessProcess'), event, 'process');
	},
	onBusinessProcessRemove: function(link, event) {
		AIR.AirPickerManager.openRemovePicker(
			this.onRecordRemoved.createDelegate(this), this.getComponent('specificsCategory').getComponent('pBusiness').getComponent('businessProcess'), event);
	},
	
	onRecordAdded: function(element, hiddenElement) {
		this.fireEvent('ciChange', this, element, hiddenElement);
	},
	onRecordRemoved: function(element, hiddenElement) {
		this.fireEvent('ciChange', this, element, hiddenElement);
	},
	
	update: function(data) {
		//this.updateAccessMode(data);
		
//		selectedCiCat1Id = data.applicationCat1Id;//muss gesetzt werden f�r andere Codestellen?
		this.getComponent('objectType').setValue(data.applicationCat1Id);//selectedCiCat1Id
		this.getComponent('applicationId').setValue(data.applicationId);
		this.getComponent('applicationAlias').setValue(data.applicationAlias);
		

		this.getComponent('applicationVersion').setValue(data.applicationVersion);
		
		var rgBARrelevance = this.getComponent('rgBARrelevance');
		if(data.barRelevance.length === 0)
			data.barRelevance = 'U';

		rgBARrelevance.setValue(data.barRelevance);
		
		
		var cbApplicationCat2 = this.getComponent('applicationCat2');
		//cbApplicationCat2.getStore().filter('applicationCat1Id', data.applicationCat1Id);
		var filterData = {//falls app w/o cat soll trotzdem die cat2 von cat1=Application ausw�hlbar sein
			applicationCat1Id: data.applicationCat1Id != '0' ? data.applicationCat1Id : AC.APP_CAT1_APPLICATION
		};
		cbApplicationCat2.filterByData(filterData);
		cbApplicationCat2.getStore().sort('text', 'ASC');
		
		if(data.applicationCat2 !== '0') cbApplicationCat2.setValue(data.applicationCat2);
		else cbApplicationCat2.clearValue();
		
		
		// ------
//		selectedCategoryBusinessId = data.categoryBusinessId;//muss gesetzt werden f�r andere Codestellen?
		if (data.categoryBusinessId && data.categoryBusinessId != 0) {
			this.getComponent('specificsCategory').getComponent('cbApplicationBusinessCat').setValue(data.categoryBusinessId);
		} else {
			this.getComponent('specificsCategory').getComponent('cbApplicationBusinessCat').setValue('');
		}
		
//		selectedDataClassId = data.dataClassId;//muss gesetzt werden f�r andere Codestellen?
		if (data.dataClassId && data.dataClassId != 0) {// && data.dataClassId != '0'	selectedDataClassId statt data.dataClassId
			this.getComponent('specificsCategory').getComponent('cbDataClass').setValue(data.dataClassId);//selectedDataClassId
			
			//wenn cbApplicationBusinessCat ge�ndert wird, m�ssen die Werte f�r dataClassListStore aktualisiert werden
//			dataClassListStore.load();
		} else {
			this.getComponent('specificsCategory').getComponent('cbDataClass').clearValue();
			Util.disableCombo(this.getComponent('specificsCategory').getComponent('cbDataClass'));
		}
				
//		selectedLifecycleStatusId = data.lifecycleStatusId;
		if (data.lifecycleStatusId && data.lifecycleStatusId != 0) {
			this.getComponent('lifecycleStatus').setValue(data.lifecycleStatusId);
		} else {
			this.getComponent('lifecycleStatus').setValue('');
		}
		
//		if (data.organisationalScope && data.organisationalScope != 0) {
//			this.getComponent('organisationalScope').setValue(data.organisationalScope);
//		} else {
//			this.getComponent('organisationalScope').setValue('');
//		}
		
		var lvOrganisationalScope = this.getComponent('organisationalScope');//.getComponent('fsOrganisationalScope')
		lvOrganisationalScope.clearSelections(true);
		var taOrganisationalScope = this.getComponent('organisationalScopeHidden');//.getComponent('fsOrganisationalScope')
		taOrganisationalScope.reset();
		
		if(data.organisationalScope.length > 0) {
			var scopes = data.organisationalScope.split(',');
			var store = lvOrganisationalScope.getStore();
			
			
			if(AIR.AirAclManager.isRelevance(lvOrganisationalScope, data)) {//lvApplicationUsingRegions.isVisible()
				Ext.each(scopes, function(item, index, all) {
					var r = store.getAt(store.findExact('name', item));
					lvOrganisationalScope.select(r, true, true);//r
				});
			} else {
				var organisationalScope = data.organisationalScope;
				
				Ext.each(scopes, function(item, index, all) {
					var r = store.getAt(store.findExact('name', item));
					organisationalScope = organisationalScope.replace(item, r.data.name);
				});
				taOrganisationalScope.setValue(organisationalScope.replace(/,/g,'\n'));//data.licenseUsingRegions.replace(',','\n')
			}
		}
		
		
		
//		selectedOperationalStatusId = data.operationalStatusId;
		if (data.operationalStatusId && data.operationalStatusId != 0) {
			this.getComponent('operationalStatus').setValue(data.operationalStatusId);
		} else {
			this.getComponent('operationalStatus').setValue('');
		}

		if (data.comments && data.comments != 0) {
			this.getComponent('comments').setValue(data.comments);
		} else {
			this.getComponent('comments').setValue('');
		}
		
		this.updateAccessMode(data);
		
		
		if(data.barRelevance === 'Y') {
			this.getComponent('barApplicationId').setValue(data.barApplicationId);
			rgBARrelevance.disable();
		} else {
			this.getComponent('barApplicationId').setValue('');
		}
		
			
		this.loadApplicationBusinessProcesses(data.applicationId);
		
		//kein Effekt zur Verhinderung der Verkleinerung der combo Breite nachdem wenn auf Contacts die Primary Person
		//gel�scht und wieder hinzugef�gt wurde. (*1)
	},
	
	updateAccessMode: function(data) {
		AIR.AirAclManager.setAccessMode(this.getComponent('applicationAlias'), data);
		AIR.AirAclManager.setAccessMode(this.getComponent('barApplicationId'), data);
		AIR.AirAclManager.setAccessMode(this.getComponent('rgBARrelevance'), data);
		AIR.AirAclManager.setAccessMode(this.getComponent('applicationVersion'), data);
		AIR.AirAclManager.setAccessMode(this.getComponent('applicationCat2'), data);
		AIR.AirAclManager.setAccessMode(this.getComponent('lifecycleStatus'), data);
		
//		AIR.AirAclManager.setAccessMode(this.getComponent('fsOrganisationalScope').getComponent('organisationalScope'), data);
		AIR.AirAclManager.setAccessMode(this.getComponent('organisationalScope'), data);
//		AIR.AirAclManager.setAccessMode(this.getComponent('organisationalScopeHidden'), data);
//		AIR.AirAclManager.setNecessity(this.getComponent('organisationalScopeHidden'));
		AIR.AirAclManager.setNecessityInternal(this.getComponent('organisationalScopeHidden').label, 'mandatory');
		
		
		AIR.AirAclManager.setAccessMode(this.getComponent('operationalStatus'), data);
		AIR.AirAclManager.setAccessMode(this.getComponent('comments'), data);
		AIR.AirAclManager.setAccessMode(this.getComponent('specificsCategory').getComponent('cbApplicationBusinessCat'), data);
		AIR.AirAclManager.setAccessMode(this.getComponent('specificsCategory').getComponent('cbDataClass'), data);
		AIR.AirAclManager.setAccessMode(this.getComponent('specificsCategory').getComponent('pBusiness').getComponent('businessProcess'), data);
	},

	
	loadApplicationBusinessProcesses: function(applicationId) {
		var applicationProcessStore = AIR.AirStoreFactory.createApplicationProcessStore();
		applicationProcessStore.on('load', this.onApplicationProcessLoad, this);
		
		var params = {
			cwid: AIR.AirApplicationManager.getCwid(),
			token: AIR.AirApplicationManager.getToken(),
			applicationId: applicationId//AIR.AirApplicationManager.getCiId()
		};
		
		applicationProcessStore.load({
			params: params
		});
	},
	
	onApplicationProcessLoad: function(store, records, options) {
		var values = '';
		var hiddenValues = '';
		
		for(var i = 0; i < records.length; ++i) {
			if(i === 0) {
				values = records[i].data.text;
				hiddenValues = records[i].data.id;
			} else {
				values += '\n' + records[i].data.text;
				hiddenValues += ',' + records[i].data.id;
			}
		}
		
		field = this.getComponent('specificsCategory').getComponent('pBusiness').getComponent('businessProcess');
		field.setValue(values);

		field = this.getComponent('specificsCategory').getComponent('pBusiness').getComponent('businessProcessHidden');
		field.setValue(hiddenValues);
	},
	
	//getData: function() {
	setData: function(data) {
		//var data = {};
		
		var field = this.getComponent('applicationId');
		data.applicationId = field.getValue();

//		field = this.getComponent('applicationName');
//		if(!field.disabled)
//			data.applicationName = field.getValue();
		

		field = this.getComponent('applicationAlias');
		if(!field.disabled)
			data.applicationAlias = field.getValue();
		

		field = this.getComponent('applicationVersion');
		if(!field.disabled)
			data.version = field.getValue();
		
		field = this.getComponent('rgBARrelevance');
		if(!field.disabled)
			data.barRelevance = field.getValue().inputValue;
		

		field = this.getComponent('specificsCategory').getComponent('cbApplicationBusinessCat');
		if(!field.disabled)
			if(field.getValue().length > 0)
				data.categoryBusinessId = field.getValue();
			else 
				data.categoryBusinessId = -1;
		

		field = this.getComponent('specificsCategory').getComponent('cbDataClass');
		if(!field.disabled)
			if(field.getValue().length > 0)
				data.classDataId = field.getValue();
			else 
				data.classDataId = -1;
		
		field = this.getComponent('applicationCat2');
		if(!field.disabled)
			if (field.getValue().length > 0)
				data.applicationCat2Id = field.getValue();
			else data.applicationCat2Id = -1;

		field = this.getComponent('lifecycleStatus');
		if(!field.disabled)
			if(field.getValue().length > 0)
				data.lifecycleStatusId = field.getValue();
			else 
				data.lifecycleStatusId = -1;
		
//		field = this.getComponent('organisationalScope');
//		if(!field.disabled)
//			if(field.getValue().length > 0)
//				data.organisationalScope = field.getValue();
//			else 
//				data.organisationalScope = -1;
		var lvOrganisationalScope = this.getComponent('organisationalScope');//.getComponent('fsOrganisationalScope')
		if(!lvOrganisationalScope.disabled) {
			var scopeRecords = lvOrganisationalScope.getSelectedRecords();
			var scopes = '';
			for(var i = 0; i < scopeRecords.length; i++) {
				if(scopes.length > 0)
					scopes += ',';
				
				scopes += scopeRecords[i].get('id');
			}
			if(scopes.length > 0)
				data.organisationalScope = scopes;
			else
				data.organisationalScope = '-1';
		}
		
		field = this.getComponent('operationalStatus');
		if(!field.disabled)
			if(field.getValue().length > 0)
				data.operationalStatusId = field.getValue();
			else 
				data.operationalStatusId = -1;

		field = this.getComponent('comments');
		if(!field.disabled)
			data.comments = field.getValue();
		
		field = this.getComponent('specificsCategory').getComponent('pBusiness').getComponent('businessProcess');
		if(!field.disabled)
			data.businessProcess = field.getValue();

		field = this.getComponent('specificsCategory').getComponent('pBusiness').getComponent('businessProcessHidden');
		if(!field.disabled)
			data.businessProcessHidden = field.getValue();
		
		return data;
	},
	
	updateLabels: function(labels) {
		this.setTitle(labels.specificsPanelTitle);
//		this.setFieldLabel(this.getComponent('applicationName'), labels.applicationName);
		this.setFieldLabel(this.getComponent('applicationAlias'), labels.applicationAlias);
		this.setFieldLabel(this.getComponent('barApplicationId'), labels.barApplicationId);
		this.setFieldLabel(this.getComponent('rgBARrelevance'), labels.rgBARrelevance);
		this.setBoxLabel(this.getComponent('rgBARrelevance').items.items[0], labels.general_yes);
		this.setBoxLabel(this.getComponent('rgBARrelevance').items.items[1], labels.general_no);
		this.setBoxLabel(this.getComponent('rgBARrelevance').items.items[2], labels.complianceUndefined);
		
		this.setFieldLabel(this.getComponent('applicationVersion'), labels.applicationVersion);
		this.setFieldLabel(this.getComponent('applicationCat2'), labels.applicationCat2);
		this.setFieldLabel(this.getComponent('lifecycleStatus'), labels.lifecycleStatus);
		
//		this.getComponent('fsOrganisationalScope').setTitle(labels.organisationalScope);
		this.setFieldLabel(this.getComponent('organisationalScope'), labels.organisationalScope);
		this.setFieldLabel(this.getComponent('organisationalScopeHidden'), labels.organisationalScope);
		
		this.setFieldLabel(this.getComponent('operationalStatus'), labels.operationalStatus);
		this.setFieldLabel(this.getComponent('comments'), labels.comments);
		
		this.getComponent('specificsCategory').setTitle(labels.specificsCategory);
		this.setFieldLabel(this.getComponent('specificsCategory').getComponent('cbApplicationBusinessCat'), labels.applicationBusinessCat);
		this.setFieldLabel(this.getComponent('specificsCategory').getComponent('cbDataClass'), labels.dataClass);
		this.getComponent('specificsCategory').getComponent('pBusiness').getComponent('labelbusinessProcess').setText(labels.businessProcess);
	},
	
	updateToolTips: function(toolTips) {
//		this.setTooltipData(this.getComponent('applicationName').label,  toolTips.applicationName,  toolTips.applicationNameText);
		this.setTooltipData(this.getComponent('applicationAlias').label,  toolTips.applicationAlias, toolTips.applicationAliasText);
		this.setTooltipData(this.getComponent('rgBARrelevance').label,  toolTips.barApplicationRelevant, toolTips.barApplicationRelevantText);
		this.setTooltipData(this.getComponent('barApplicationId').label,  toolTips.barApplicationId, toolTips.barApplicationIdText);
      	this.setTooltipData(this.getComponent('applicationVersion').label, toolTips.version, toolTips.versionText);
		this.setTooltipData(this.getComponent('applicationCat2').label,  toolTips.applicationCat2, toolTips.applicationCat2Text);
		this.setTooltipData(this.getComponent('lifecycleStatus').label, toolTips.lifecycleStatus, toolTips.lifecycleStatusText);
		this.setTooltipData(this.getComponent('organisationalScope').label, toolTips.organisationalScope, toolTips.organisationalScopeText);
		this.setTooltipData(this.getComponent('organisationalScopeHidden').label, toolTips.organisationalScope, toolTips.organisationalScopeText);
		this.setTooltipData(this.getComponent('operationalStatus').label, toolTips.operationalStatus, toolTips.operationalStatusText);
		this.setTooltipData(this.getComponent('comments').label, toolTips.comments, toolTips.commentsText);
		this.setTooltipData(this.getComponent('specificsCategory').getComponent('cbApplicationBusinessCat').label, toolTips.applicationBusinessCat, toolTips.applicationBusinessCatText);
		this.setTooltipData(this.getComponent('specificsCategory').getComponent('cbDataClass').label, toolTips.dataClass, toolTips.dataClassText);
		this.setTooltipData(this.getComponent('specificsCategory').getComponent('pBusiness').getComponent('labelbusinessProcess'), toolTips.businessProcess, toolTips.businessProcessText);
	}
});
Ext.reg('AIR.CiSpecificsView', AIR.CiSpecificsView);