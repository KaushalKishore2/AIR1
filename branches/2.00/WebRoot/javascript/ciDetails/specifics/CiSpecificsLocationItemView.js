Ext.namespace('AIR');

AIR.CiSpecificsLocationItemView = Ext.extend(AIR.AirView, {
	initComponent: function() {
		Ext.apply(this, {
			labelWidth: 200,
		    
		    border: false,
		    layout: 'form',
		    
		    items: [{
		        id: 'tfLocationCiName',
		    	xtype: 'textfield',
		        fieldLabel: 'Name',
		        width: 230,
		        hidden: true
	        },{
		        id: 'tfLocationCiAlias',
		    	xtype: 'textfield',
		        fieldLabel: 'Alias',
		        width: 230
	        },{
		        id: 'tfRoomFloor',
		    	xtype: 'textfield',
		        fieldLabel: 'Floor',
		        width: 230
		    },{
//		        id: 'cbRoom',
//		    	xtype: 'textfield',
//		        fieldLabel: 'Room',
//		        disabled: true,
//		        width: 230
		    	
		        xtype: 'filterCombo',//combo
		        id: 'cbRoom',
		        disabled: true,
		        hideTrigger: true,
		        
		        width: 230,
		        fieldLabel: 'Room',
		        
		        store: AIR.AirStoreFactory.createRoomListStore(),//AIR.AirStoreFactory.createIdNameStore(),//new Ext.data.Store(),//AIR.AirStoreManager.getStoreByName('applicationCat2ListStore'),//applicationCat2ListStore,
		        valueField: 'id',
		        displayField: 'name',
				lastQuery: '',
		        
//		        typeAhead: true,
//		        forceSelection: true,
//		        autoSelect: false,
		        
		        triggerAction: 'all',//all query
//		        lazyRender: true,
//		        lazyInit: false,
//		        mode: 'local',
		        queryParam: 'id'
	        },{
		        xtype: 'filterCombo',//combo
		        id: 'cbBuildingArea',
		        disabled: true,
		        hideTrigger: true,
		        
		        width: 230,
		        fieldLabel: 'Building Area',
		        
		        store: AIR.AirStoreFactory.createBuildingAreaListStore(),//AIR.AirStoreFactory.createIdNameStore(),//new Ext.data.Store(),//AIR.AirStoreManager.getStoreByName('applicationCat2ListStore'),//applicationCat2ListStore,
		        valueField: 'id',
		        displayField: 'name',
				lastQuery: '',
		        
//		        typeAhead: true,
//		        forceSelection: true,
//		        autoSelect: false,
		        
		        triggerAction: 'all',//all query
//		        lazyRender: true,
//		        lazyInit: false,
//		        mode: 'local',
		        queryParam: 'id'
	        },{
		        xtype: 'filterCombo',//combo filterCombo
		        id: 'cbBuilding',
		        disabled: true,
		        hideTrigger: true,
		        
		        width: 230,
		        fieldLabel: 'Building',
		        
		        store: AIR.AirStoreFactory.createBuildingListStore(),//AIR.AirStoreFactory.createBuildingsByBuildingAreaStore(),//createIdNameStore(),//new Ext.data.Store(),//AIR.AirStoreManager.getStoreByName('applicationCat2ListStore'),//applicationCat2ListStore,
		        valueField: 'id',
		        displayField: 'name',
				lastQuery: '',
		        
//		        typeAhead: true,
//		        forceSelection: true,
//		        autoSelect: false,
		        
		        triggerAction: 'all',//all query
		        lazyRender: true,
		        lazyInit: false,
//		        mode: 'remote'//local
		        queryParam: 'id'
		    },{
		        id: 'cbTerrain',//tfTerrain
		    	xtype: 'combo',//textfield
		        fieldLabel: 'Terrain',
		        disabled: true,
		        width: 230,
		        
		        store: AIR.AirStoreFactory.createTerrainListStore(),//new Ext.data.Store(),//AIR.AirStoreFactory.createBuildingsByBuildingAreaStore(),//createIdNameStore(),//new Ext.data.Store(),//AIR.AirStoreManager.getStoreByName('applicationCat2ListStore'),//applicationCat2ListStore,
		        valueField: 'id',
		        displayField: 'name',
				lastQuery: '',
		        
//		        typeAhead: true,
//		        forceSelection: true,
//		        autoSelect: false,
		        
		        triggerAction: 'all',//all query
//		        lazyRender: true,
//		        lazyInit: false
//		        mode: 'remote'//local
		        queryParam: 'id'//id landId
	        },{
		        id: 'cbSite',
		    	xtype: 'combo',//textfield
		    	
		        fieldLabel: 'Site',
		        disabled: true,
		        width: 230,
		        
		        store: AIR.AirStoreFactory.createSiteListStore(),//new Ext.data.Store(),//AIR.AirStoreFactory.createBuildingsByBuildingAreaStore(),//createIdNameStore(),//new Ext.data.Store(),//AIR.AirStoreManager.getStoreByName('applicationCat2ListStore'),//applicationCat2ListStore,
		        valueField: 'id',
		        displayField: 'name',
				lastQuery: '',
		        
//		        typeAhead: true,
//		        forceSelection: true,
//		        autoSelect: false,
		        
		        triggerAction: 'all',//all query
//		        lazyRender: true,
//		        lazyInit: false
//		        mode: 'remote'//local
		        queryParam: 'id'//id landId
		    },{
		        id: 'cbCountry',
		        xtype: 'filterCombo',//'textfield',
		    	
		        fieldLabel: 'Country',
		        disabled: true,
//		        anchor: '70%'
		        width: 230,
		        
//		        anchor: '70%',
				
		        
		        store: AIR.AirStoreFactory.createLandListStore(),//new Ext.data.Store(),//AIR.AirStoreFactory.createBuildingsByBuildingAreaStore(),//createIdNameStore(),//new Ext.data.Store(),//AIR.AirStoreManager.getStoreByName('applicationCat2ListStore'),//applicationCat2ListStore,
		        valueField: 'id',
		        displayField: 'name',
				lastQuery: '',
		        
//		        typeAhead: true,
//		        forceSelection: true,
//		        autoSelect: false,
		        
		        triggerAction: 'all'//all query
//		        lazyRender: true,
//		        lazyInit: false
//		        mode: 'remote'//local
//		        queryParam: 'locale'//id
		    },{
		    	xtype: 'panel',
		    	id: 'pSpecificsLocationStreet',
		    	border: false,
		    	layout: 'column',
		    	hidden: true,
		    	
		    	items: [{
		    		xtype: 'label',
		    		id: 'lStreetAndNumber',
		    		text: 'Street / Number',
		    		
					width: 205,
					style: {
						fontSize: 12,
						marginTop: 3
					}
		    	}, {
			    	xtype: 'textfield',
			        id: 'tfStreet',
			    	width: 250,
			    	disabled: true
		    	},{
			    	xtype: 'textfield',
			        id: 'tfStreetNumber',
			    	width: 50,
			    	
			    	style: {
						marginLeft: 5
					},
			    	disabled: true
		    	}]
		    },{
		    	xtype: 'panel',
		    	id: 'pSpecificsLocationAddress',
		    	border: false,
		    	layout: 'column',
		    	hidden: true,
		    	
		    	style: {
					marginTop: 5
				},
		    	
		    	items: [{
		    		xtype: 'label',
		    		id: 'lPostalCodeLocation',
		    		text: 'Postal Code / Location',
		    		
					width: 205,
					style: {
						fontSize: 12,
						marginTop: 3
					}
		    	}, {
			    	xtype: 'textfield',
			        id: 'tfPostalCode',
			    	width: 50,
			    	disabled: true
		    	},{
			    	xtype: 'textfield',
			        id: 'tfLocation',
			    	width: 150,
			    	
			    	style: {
						marginLeft: 5
					},
			    	disabled: true
		    	}]
		    }]
		});
		
		AIR.CiSpecificsLocationItemView.superclass.initComponent.call(this);
		
		this.addEvents('ciBeforeChange', 'ciChange');
		
//		var cbBuilding = this.getComponent('cbBuilding');
//		cbBuilding.on('beforequery', this.onBeforeBuildingSelect, this);
//		cbBuilding.on('select', this.onBuildingSelect, this);
		
		var tfLocationCiAlias = this.getComponent('tfLocationCiAlias');
		var tfRoomFloor = this.getComponent('tfRoomFloor');
		var cbBuildingArea = this.getComponent('cbBuildingArea');
//		var cbBuilding = this.getComponent('cbBuilding');
		
		cbBuildingArea.on('select', this.onComboSelect, this);
		cbBuildingArea.on('change', this.onComboChange, this);
//		cbBuilding.on('select', this.onComboSelect, this);
//		cbBuilding.on('change', this.onComboChange, this);
		
		tfLocationCiAlias.on('change', this.onFieldChange, this);
		tfRoomFloor.on('change', this.onFieldChange, this);
		
		var cbCountry = this.getComponent('cbCountry');
		cbCountry.on('select', this.onCountrySelect, this);
		cbCountry.allQuery = 'CONSTANT';
		
		var cbSite = this.getComponent('cbSite');
		cbSite.on('select', this.onSiteSelect, this);
		
		var cbTerrain = this.getComponent('cbTerrain');
		cbTerrain.on('select', this.onTerrainSelect, this);
		
		var cbBuilding = this.getComponent('cbBuilding');
		cbBuilding.on('select', this.onBuildingSelect, this);
		
		var cbBuildingArea = this.getComponent('cbBuildingArea');
		cbBuildingArea.on('select', this.onBuildingAreaSelect, this);
	},
	
//	onBeforeBuildingSelect: function(queryEvent) {
//		return this.isInitial;
//	},
//	onBuildingSelect: function(combo, record, index) {
//		this.isInitial = false;
//	},
	
	
	onBuildingAreaSelect: function(combo, record, index) {
		var cbRoom = this.getComponent('cbRoom');
		
		cbRoom.getStore().setBaseParam('id', record.get('id'));
		cbRoom.allQuery = record.get('id');
	},
	
	onBuildingSelect: function(combo, record, index) {
		var cbBuildingArea = this.getComponent('cbBuildingArea');
		
		cbBuildingArea.getStore().setBaseParam('id', record.get('id'));
		cbBuildingArea.allQuery = record.get('id');
	},
	
	onTerrainSelect: function(combo, record, index) {
		var cbBuilding = this.getComponent('cbBuilding');
		
		cbBuilding.getStore().setBaseParam('id', record.get('id'));
		cbBuilding.allQuery = record.get('id');
	},
	
	onSiteSelect: function(combo, record, index) {
		var cbTerrain = this.getComponent('cbTerrain');
		
		cbTerrain.getStore().setBaseParam('id', record.get('id'));//landId ciId
		cbTerrain.allQuery = record.get('id');
	},
	
	onCountrySelect: function(combo, record, index) {
		var cbSite = this.getComponent('cbSite');
		
		cbSite.getStore().setBaseParam('id', record.get('id'));//landId ciId
		cbSite.allQuery = record.get('id');
	},

	onComboSelect: function(combo, record, index) {
		this.ownerCt.fireEvent('ciChange', this, combo);
	},
	onComboChange: function(combo, newValue, oldValue) {
		if(this.isComboValueValid(combo, newValue, oldValue))
			this.ownerCt.fireEvent('ciChange', this, combo);
	},

	onFieldChange: function(textfield, newValue, oldValue) {
		this.ownerCt.fireEvent('ciChange', this, textfield, newValue);
	},
	
	
//	clear: function(data) {
//		
//	},
	
	init: function() {
		var ciDetail = AAM.getAppDetail();
        this.update(ciDetail);
	},
    
	update: function(data) {
		this.ciId = data.id;
		this.name = data.name;
		
		this.updateAccessMode(data);

		
		var tfLocationCiName = this.getComponent('tfLocationCiName');
		
//		this.isInitial = true;
		if(data.isCiCreate) {
			//enable all fields
			tfLocationCiName.setVisible(true);
		} else {
			tfLocationCiName.setVisible(false);
		}
		
		var tfLocationCiAlias = this.getComponent('tfLocationCiAlias');
		
		var tfRoomFloor = this.getComponent('tfRoomFloor');
		var cbRoom = this.getComponent('cbRoom');
		var cbBuildingArea = this.getComponent('cbBuildingArea');
		var cbBuilding = this.getComponent('cbBuilding');
		var cbTerrain = this.getComponent('cbTerrain');
		var cbSite = this.getComponent('cbSite');
		var cbCountry = this.getComponent('cbCountry');
		
//		Util.disableCombo(cbBuilding);
//		Util.disableCombo(cbBuildingArea);

		
		var pSpecificsLocationStreet = this.getComponent('pSpecificsLocationStreet');
		var pSpecificsLocationAddress = this.getComponent('pSpecificsLocationAddress');
		
		//use builder pattern managing each field depending on tableId instead of switch/case?
		switch(parseInt(data.tableId)) {
			case AC.TABLE_ID_POSITION:
				tfLocationCiAlias.setVisible(false);
				tfLocationCiAlias.reset();
				tfRoomFloor.setVisible(false);
				tfRoomFloor.reset();
				cbRoom.setVisible(true);
				cbBuildingArea.setVisible(true);
				cbBuilding.setVisible(true);
				
				cbTerrain.setVisible(true);
				cbSite.setVisible(true);
				cbCountry.setVisible(true);
				cbRoom.reset();
				
				if(data.isCiCreate) {
					Util.clearCombo(cbRoom);
					Util.clearCombo(cbBuildingArea);
					Util.clearCombo(cbBuilding);
					Util.clearCombo(cbTerrain);
					Util.clearCombo(cbSite);
//					Util.clearCombo(cbCountry);
//					cbRoom.reset();
//					cbBuildingArea.reset();//remote statt local combo?
//					cbBuilding.reset();
//					cbTerrain.reset();
//					cbSite.reset();
					cbCountry.reset();
					
					Util.enableCombo(cbRoom);
					Util.enableCombo(cbBuildingArea);
					Util.enableCombo(cbBuilding);
					Util.enableCombo(cbTerrain);
					Util.enableCombo(cbSite);
					Util.enableCombo(cbCountry);
				} else {
					if(AIR.AirAclManager.isRelevance(cbRoom, data)) {
						Util.enableCombo(cbRoom);
						cbRoom.getStore().setBaseParam('id', data.areaId);
						cbRoom.allQuery = data.areaId;
					} else {
						Util.disableCombo(cbRoom);
					}
					
					cbRoom.setValue(data.raumName);
					cbBuildingArea.setValue(data.areaName);
					cbBuilding.setValue(data.gebaeudeName);
					cbTerrain.setValue(data.terrainName);
					cbSite.setValue(data.standortName);
					
					Util.disableCombo(cbBuildingArea);
					Util.disableCombo(cbBuilding);
					Util.disableCombo(cbTerrain);
					Util.disableCombo(cbSite);
					Util.disableCombo(cbCountry);
				}

				
				this.updateLocation(pSpecificsLocationStreet, pSpecificsLocationAddress, data, false);
				break;
			case AC.TABLE_ID_ROOM:
				tfLocationCiAlias.setVisible(true);
				tfRoomFloor.setVisible(true);
				cbRoom.setVisible(false);
				cbBuildingArea.setVisible(true);
				cbBuilding.setVisible(true);
				cbTerrain.setVisible(true);
				cbSite.setVisible(true);
				cbCountry.setVisible(true);
				cbRoom.reset();

				
				if(data.isCiCreate) {
					tfLocationCiAlias.reset();
					tfRoomFloor.reset();
					
					Util.clearCombo(cbBuildingArea);
					Util.clearCombo(cbBuilding);
					Util.clearCombo(cbTerrain);
					Util.clearCombo(cbSite);
//					Util.clearCombo(cbCountry);
//					cbBuildingArea.reset();//remote statt local combo?
//					cbBuilding.reset();
//					cbTerrain.reset();
//					cbSite.reset();
					cbCountry.reset();
					
					Util.enableCombo(cbBuildingArea);
					Util.enableCombo(cbBuilding);
					Util.enableCombo(cbTerrain);
					Util.enableCombo(cbSite);
					Util.enableCombo(cbCountry);
				} else {
					if(AIR.AirAclManager.isRelevance(cbBuildingArea, data)) {
						Util.enableCombo(cbBuildingArea);
						cbBuildingArea.getStore().setBaseParam('id', data.gebaeudeId);
						cbBuildingArea.allQuery = data.gebaeudeId;
					} else {
						Util.disableCombo(cbBuildingArea);
					}
					
					/*
					var buildingAreas = data.buildingAreaData.split(',');
					var buildingAreaObjects = [];
					for(var i = 0; i < buildingAreas.length; i++) {
						var buildingArea = buildingAreas[i].split('=');
						buildingAreaObjects.push(buildingArea);
					}
					cbBuildingArea.getStore().loadData(buildingAreaObjects);
					*/
					
					tfLocationCiAlias.setValue(data.alias);
					tfRoomFloor.setValue(data.floor);
					cbBuildingArea.setValue(data.areaName);//areaId
					cbBuilding.setValue(data.gebaeudeName);
					cbTerrain.setValue(data.terrainName);
					cbSite.setValue(data.standortName);
					cbCountry.setValue(data.landNameEn);
					
					Util.disableCombo(cbBuilding);
					Util.disableCombo(cbTerrain);
					Util.disableCombo(cbSite);
					Util.disableCombo(cbCountry);
				}
				
				this.updateLocation(pSpecificsLocationStreet, pSpecificsLocationAddress, data, !data.isCiCreate);//true

				break;
			case AC.TABLE_ID_BUILDING_AREA://Test Areas: E 39 I400,E 39 T001
				tfLocationCiAlias.setVisible(false);
				tfLocationCiAlias.reset();
				tfRoomFloor.setVisible(false);
				tfRoomFloor.reset();
				cbRoom.setVisible(false);
				cbRoom.reset();
				cbBuildingArea.setVisible(false);
				cbBuildingArea.reset();
				
				
				cbBuilding.setVisible(true);
				cbTerrain.setVisible(true);
				
				if(data.isCiCreate) {
					cbSite.setVisible(true);
					cbCountry.setVisible(true);
					
					Util.clearCombo(cbBuilding);
					Util.clearCombo(cbTerrain);
					Util.clearCombo(cbSite);
//					Util.clearCombo(cbCountry);
//					cbBuilding.reset();
//					cbTerrain.reset();
//					cbSite.reset();
					cbCountry.reset();
					
					Util.enableCombo(cbBuilding);
					Util.enableCombo(cbTerrain);
					Util.enableCombo(cbSite);
					Util.enableCombo(cbCountry);
				} else {
					cbBuilding.setValue(data.gebaeudeName);
					cbTerrain.setValue(data.terrainName);
					cbSite.setValue(data.standortName);
					cbCountry.setValue(data.landNameEn);
					
//					cbBuilding.setValue(data.areaId);
//					cbBuilding.setRawValue(data.areaName);
//					cbBuilding.query = data.areaId;
//					cbBuilding.lastQuery = data.areaId;
					
//					cbBuilding.getStore().setBaseParam('ciId', data.areaId);
//					cbBuilding.allQuery = data.areaId;
					
					cbBuilding.getStore().setBaseParam('id', data.terrainId);
					cbBuilding.allQuery = data.terrainId;
					
					if(AIR.AirAclManager.isRelevance(cbBuilding, data))
						Util.enableCombo(cbBuilding);
					else
						Util.disableCombo(cbBuilding);
					
					Util.disableCombo(cbTerrain);
					Util.disableCombo(cbSite);
					
					cbCountry.setVisible(false);
					cbCountry.reset();
				}
				
				this.updateLocation(pSpecificsLocationStreet, pSpecificsLocationAddress, data, false);//false !data.isCiCreate
				break;
			case AC.TABLE_ID_BUILDING:
				tfLocationCiAlias.setValue(data.alias);
				tfLocationCiAlias.setVisible(true);
				cbRoom.setVisible(false);
				cbRoom.reset();
				tfRoomFloor.setVisible(false);
				tfRoomFloor.reset();
				cbBuildingArea.setVisible(false);
				cbBuildingArea.reset();
				cbBuilding.setVisible(false);
				cbBuilding.reset;
				
				cbTerrain.setVisible(true);
				cbSite.setVisible(true);
				cbCountry.setVisible(true);
				
				if(data.isCiCreate) {
					Util.clearCombo(cbTerrain);
					Util.clearCombo(cbSite);
//					Util.clearCombo(cbCountry);
//					cbTerrain.reset();
//					cbSite.reset();
					cbCountry.reset();
					
					Util.enableCombo(cbTerrain);
					Util.enableCombo(cbSite);
					Util.enableCombo(cbCountry);
				} else {
					cbTerrain.setValue(data.terrainName);
					cbSite.setValue(data.standortName);
					cbCountry.setValue(data.landNameEn);
					
					Util.disableCombo(cbTerrain);
					Util.disableCombo(cbSite);
					Util.disableCombo(cbCountry);
				}
				
				this.updateLocation(pSpecificsLocationStreet, pSpecificsLocationAddress, data, true);//true !data.isCiCreate
				break;

			case AC.TABLE_ID_TERRAIN:
				tfLocationCiAlias.setVisible(false);
				tfLocationCiAlias.reset();
				cbRoom.setVisible(false);
				cbRoom.reset();
				tfRoomFloor.setVisible(false);
				tfRoomFloor.reset();
				cbBuildingArea.setVisible(false);
				cbBuildingArea.reset();
				cbBuilding.setVisible(false);
				cbBuilding.reset();
				

				cbSite.setVisible(true);
				cbCountry.setVisible(true);
				
				if(data.isCiCreate) {
					Util.clearCombo(cbSite);
//					Util.clearCombo(cbCountry);
//					cbSite.reset();
					cbCountry.reset();
					
					Util.enableCombo(cbSite);
					Util.enableCombo(cbCountry);
				} else {
					cbSite.setValue(data.standortName);
					cbCountry.setValue(data.landNameEn);
					
					Util.disableCombo(cbSite);
					Util.disableCombo(cbCountry);
				}
				
				cbTerrain.setVisible(false);
				cbTerrain.reset();
				
				this.updateLocation(pSpecificsLocationStreet, pSpecificsLocationAddress, data, false);
				break;
			case AC.TABLE_ID_SITE:
				tfLocationCiAlias.setVisible(false);
				tfLocationCiAlias.reset();
				cbRoom.setVisible(false);
				cbRoom.reset();
				tfRoomFloor.setVisible(false);
				tfRoomFloor.reset();
				cbBuildingArea.setVisible(false);
				cbBuildingArea.reset();
				cbBuilding.setVisible(false);
				cbBuilding.reset();
				
				cbTerrain.setVisible(false);
				cbTerrain.reset();
				cbSite.setVisible(false);
				cbSite.reset();
				
				cbCountry.setVisible(true);
				
				if(data.isCiCreate) {
//					Util.clearCombo(cbCountry);
					cbCountry.reset();
					
					Util.enableCombo(cbCountry);
				} else {
					cbCountry.setValue(data.landNameEn);
					Util.disableCombo(cbCountry);
				}
				
				this.updateLocation(pSpecificsLocationStreet, pSpecificsLocationAddress, data, false);
				break;
		}
		
		this.doLayout();//n�tig wegen cbBuildingArea, ansonsten wird deren width=20 gesetzt?
	},
	
	updateLocation: function(pSpecificsLocationStreet, pSpecificsLocationAddress, data, exists) {
		if(exists) {
			pSpecificsLocationStreet.setVisible(true);
			pSpecificsLocationAddress.setVisible(true);
			
			if(data.isCiCreate) {
				pSpecificsLocationStreet.getComponent('tfStreet').reset();
				pSpecificsLocationStreet.getComponent('tfStreetNumber').reset();
				
				pSpecificsLocationAddress.getComponent('tfPostalCode').reset();
				pSpecificsLocationAddress.getComponent('tfLocation').reset();
				
				if(data.tableId == AC.TABLE_ID_BUILDING) {
					pSpecificsLocationStreet.getComponent('tfStreet').enable();
					pSpecificsLocationStreet.getComponent('tfStreetNumber').enable();
					
					pSpecificsLocationAddress.getComponent('tfPostalCode').enable();
					pSpecificsLocationAddress.getComponent('tfLocation').enable();
				} else {
					pSpecificsLocationStreet.getComponent('tfStreet').disable();
					pSpecificsLocationStreet.getComponent('tfStreetNumber').disable();
					
					pSpecificsLocationAddress.getComponent('tfPostalCode').disable();
					pSpecificsLocationAddress.getComponent('tfLocation').disable();
				}
			} else {
				pSpecificsLocationStreet.getComponent('tfStreet').setValue(data.street);
				pSpecificsLocationStreet.getComponent('tfStreetNumber').setValue(data.streetNumber);
				
				pSpecificsLocationAddress.getComponent('tfPostalCode').setValue(data.postalCode);
				pSpecificsLocationAddress.getComponent('tfLocation').setValue(data.location);
			}
		} else {
			pSpecificsLocationStreet.getComponent('tfStreet').reset();
			pSpecificsLocationStreet.getComponent('tfStreetNumber').reset();
			pSpecificsLocationStreet.setVisible(false);
			
			pSpecificsLocationAddress.getComponent('tfPostalCode').reset();
			pSpecificsLocationAddress.getComponent('tfLocation').reset();
			pSpecificsLocationAddress.setVisible(false);
		}
	},

	setData: function(data) {
		data.id = this.ciId;
		data.name = this.name;
		
		var tfLocationCiAlias = this.getComponent('tfLocationCiAlias');
		var tfRoomFloor = this.getComponent('tfRoomFloor');
		var cbRoom = this.getComponent('cbRoom');
		var cbBuildingArea = this.getComponent('cbBuildingArea');
		var cbBuilding = this.getComponent('cbBuilding');
		var cbTerrain = this.getComponent('cbTerrain');
		var cbSite = this.getComponent('cbSite');
		var cbCountry = this.getComponent('cbCountry');
		
//		var tfStreet = this.getComponent('pSpecificsLocationStreet').getComponent('tfStreet');
//		var tfStreetNumber = this.getComponent('pSpecificsLocationStreet').getComponent('tfStreetNumber');
//
//		var tfPostalCode = this.getComponent('pSpecificsLocationAddress').getComponent('tfPostalCode');
//		var tfLocation = this.getComponent('pSpecificsLocationAddress').getComponent('tfLocation');
		
//		var field = this.getComponent('applicationId');
//		data.id = field.getValue();
		
		switch(parseInt(data.tableId)) {
			case AC.TABLE_ID_POSITION:
				break;
			case AC.TABLE_ID_ROOM:
				if(!tfLocationCiAlias.disabled)
					data.alias = tfLocationCiAlias.getValue();
				
				if(!tfRoomFloor.disabled)
					data.floor = tfRoomFloor.getValue();
				
				if(!cbBuildingArea.disabled)
					data.areaId = cbBuildingArea.getValue();
				
				break;
			case AC.TABLE_ID_BUILDING:
				if(!tfLocationCiAlias.disabled)
					data.alias = tfLocationCiAlias.getValue();
				break;
			case AC.TABLE_ID_BUILDING_AREA:
				//BuildingHbn.saveBuildingArea(String, BuildingAreaDTO): ORA-20000: Building area 1157 cannot be moved to another building. Set parameter CHECK_LOCATION_INTEGRITY to N to disable this check.
//				data.buildingId = cbBuilding.getValue();
				break;
		}
	},

	
	updateAccessMode: function(data) {
		AIR.AirAclManager.setAccessMode(this.getComponent('tfLocationCiAlias'), data);
		AIR.AirAclManager.setAccessMode(this.getComponent('tfRoomFloor'), data);
//		AIR.AirAclManager.setAccessMode(this.getComponent('cbBuildingArea'), data);
//		AIR.AirAclManager.setAccessMode(this.getComponent('cbBuilding'), data);
		
		//werden nie ge�ndert:
//		AIR.AirAclManager.setAccessMode(this.getComponent('cbRoom'), data);
//		AIR.AirAclManager.setAccessMode(this.getComponent('cbTerrain'), data);
//		AIR.AirAclManager.setAccessMode(this.getComponent('cbSite'), data);
//		AIR.AirAclManager.setAccessMode(this.getComponent('cbCountry'), data);
		
//		var pSpecificsLocationStreet = this.getComponent('pSpecificsLocationStreet');
//		var pSpecificsLocationAddress = this.getComponent('pSpecificsLocationAddress');
//		
//		AIR.AirAclManager.setAccessMode(pSpecificsLocationStreet.getComponent('tfStreet'), data);
//		AIR.AirAclManager.setAccessMode(pSpecificsLocationStreet.getComponent('tfStreetNumber'), data);
//		AIR.AirAclManager.setAccessMode(pSpecificsLocationAddress.getComponent('tfPostalCode'), data);
//		AIR.AirAclManager.setAccessMode(pSpecificsLocationAddress.getComponent('tfLocation'), data);
	},
	
	validate: function(item) {
		
	},
	
	updateLabels: function(labels) {
		this.setFieldLabel(this.getComponent('tfLocationCiName'), labels.name);
		this.setFieldLabel(this.getComponent('tfLocationCiAlias'), labels.alias);
		this.setFieldLabel(this.getComponent('tfRoomFloor'), labels.floor);
		this.setFieldLabel(this.getComponent('cbRoom'), labels.room);
		this.setFieldLabel(this.getComponent('cbBuildingArea'), labels.buildingArea);
		this.setFieldLabel(this.getComponent('cbBuilding'), labels.building);
		this.setFieldLabel(this.getComponent('cbTerrain'), labels.terrain);
		this.setFieldLabel(this.getComponent('cbSite'), labels.site);
		this.setFieldLabel(this.getComponent('cbCountry'), labels.country);
		
		var pSpecificsLocationStreet = this.getComponent('pSpecificsLocationStreet');
		var pSpecificsLocationAddress = this.getComponent('pSpecificsLocationAddress');
		
		pSpecificsLocationStreet.getComponent('lStreetAndNumber').setText(labels.streetAndNumber);
		pSpecificsLocationAddress.getComponent('lPostalCodeLocation').setText(labels.postalCodeLocation);
	},
	
	updateToolTips: function(toolTips) {

	}
});
Ext.reg('AIR.CiSpecificsLocationItemView', AIR.CiSpecificsLocationItemView);