Ext.namespace('AIR');

AIR.CiDeleteView = Ext.extend(Ext.Panel, {
	CI_TYPE_APPLICATION: 2,
	
	initComponent: function() {
		Ext.apply(this, {
			title: 'Select CI to delete',//languagestore.data.items[0].data['CiDeleteViewTitle'],//
			
			//irgendwo muss leider die initiale Groesse f�r die child container angegeben sein
//			width: 600,
			height: 1200,
//			autoScroll: true,
			
			border: false,
			layout: 'form',//form anchor vbox
			
			items: [{
//        		xtype: 'panel',
//        		id: 'pTop',
//				hidden: true,
//				border: false,
				
        		
//        		items: [{
					xtype: 'button',
					id: 'bDelete',
					text: 'Delete',
					hidden: true,
					
//					hidden: true,
					width: 50,
					style: {
						marginTop: 10,
						marginBottom: 10
					}

//				}]
        	}, {
				xtype: 'AIR.CiResultGrid',//hier nur applications anzeigen
				id: 'CiDeleteResultGrid',
				anchor: '100%'
//				height: 1150
			}]
		});

		AIR.CiDeleteView.superclass.initComponent.call(this);
		
		
//		var bDeleteSearch = this.getComponent('pDeleteCiSearch').getComponent('bDeleteSearch');
		var bDelete = this.getComponent('bDelete');//.getComponent('pTop') this.getComponent('pDeleteCiSearch').getComponent('bDelete');
		
//		bDeleteSearch.on('click', this.onSearch, this);
		bDelete.on('click', this.onDelete, this);
		
		
		var grid = this.getComponent('CiDeleteResultGrid');
		grid.on('rowclick', this.onRowClick, this);
		grid.on('rowdblclick', Ext.emptyFn);
		grid.getStore().on('beforeload', this.onGridBeforeLoaded , this);
		grid.getStore().on('load', this.onGridLoaded, this);
	},
	
	onDelete: function(button, event) {
		var yesCallback = function() {
			this.deleteCallback();
		};

		var callbackMap = {
			'yes': yesCallback.createDelegate(this)
		};
		
		var data = {
			applicationName: this.applicationName,
			applicationCat1Txt: this.applicationCat1Txt
		};
		
		var deleteConfirmWindow = AIR.AirWindowFactory.createDynamicMessageWindow('DELETE_CONFIRMATION', callbackMap, data);
		deleteConfirmWindow.show();
	},
	
	deleteCallback: function() {
		var store = AIR.AirStoreFactory.createApplicationDeleteStore();
		store.on('load', this.onApplicationDeleted, this);
		
		var params = { 
		 	cwid: AIR.AirApplicationManager.getCwid(),
		 	token: AIR.AirApplicationManager.getToken(),
			applicationId: this.ciId//Flag f�r Owner und Delegate Apps!
		};

		store.load({
			params: params
		});
	},
	
	onApplicationDeleted: function(store, records, options) {
		//delete row from grid
		
		var grid = this.getComponent('CiDeleteResultGrid');
		
		var record = grid.getStore().getAt(this.selectedCiIndex);
		grid.getStore().remove(record);
		
		var data = {
			applicationName: record.data.applicationName,
			applicationCat1: record.data.applicationCat1Txt
		};
		this.fireEvent('airAction', this, 'appDeleteSuccess', data);
	},
	
	onRowClick: function(grid, rowIndex, e) {
		var record = grid.getStore().getAt(rowIndex);
		this.selectedCiIndex = rowIndex;
		this.ciId = record.data.applicationId;
		this.applicationName = record.data.applicationName;
		this.applicationCat1Txt = record.data.applicationCat1Txt;
		
//		var bDelete = this.getComponent('bDelete');//this.getComponent('pDeleteCiSearch').getComponent('bDelete');
//		if(bDelete.hidden) {
//			var pSpace1 = this.getComponent('pSpace1');
//			pSpace1.show();
//			
//			bDelete.show();
//			this.doLayout();//this.getComponent('pDeleteCiSearch').doLayout();
//		}
		
//		var pTop = this.getComponent('pTop');
//		if(pTop.hidden) {
//			pTop.show();
//			this.doLayout();
//		}
		
		var bDelete = this.getComponent('bDelete');
		bDelete.show();
	},

	
	loadDeleteGrid: function() {
		this.reset();
		
		var params = {
		 	cwid: AIR.AirApplicationManager.getCwid(),
		 	token: AIR.AirApplicationManager.getToken(),
			start: 0,
			limit: 20,
//			sort: 'applicationName',
			onlyapplications: 'true',
			searchAction: 'myCisForDelete'// Parameter f�r Owner und Delegate Apps anstatt myCis!
		};
		
		var grid = this.getComponent('CiDeleteResultGrid');
		grid.getStore().load({
			params: params
		});
		
		delete params.start;
		delete params.limit;
		grid.setPagingParams(params);
	},
	
	
	onGridBeforeLoaded: function(store, options) {
		myLoadMask.show();
	},
	
	onGridLoaded: function(store, records, options) {
		myLoadMask.hide();
		var grid = this.getComponent('CiDeleteResultGrid');
		grid.setVisible(true);
		grid.updateHeight();
	},
	
	updateLabels: function(labels) {
		this.setTitle(labels.CiDeleteViewTitle);
		
		var bDelete = this.getComponent('bDelete');//.getComponent('pTop')
		if(!bDelete.hidden)
			bDelete.setText(labels.CiDeleteViewButtonDelete);
		
		var grid = this.getComponent('CiDeleteResultGrid');
		grid.getColumnModel().setColumnHeader(0, labels.searchResultName);
		grid.getColumnModel().setColumnHeader(1, labels.searchResultAlias);
		grid.getColumnModel().setColumnHeader(2, labels.searchResultType);
		grid.getColumnModel().setColumnHeader(3, labels.searchResultCategory);
		grid.getColumnModel().setColumnHeader(4, labels.searchResultAppOwner);
		grid.getColumnModel().setColumnHeader(5, labels.searchResultAppOwnerDelegate);
		grid.getColumnModel().setColumnHeader(6, labels.searchResultAppSteward);
		grid.getColumnModel().setColumnHeader(7, labels.applicationManager);
		grid.getColumnModel().setColumnHeader(8, labels.applicationManagerDelegate);
	},
	
	reset: function() {
//		var pSpace1 = this.getComponent('pSpace1');
//		pSpace1.hide();
//		
//		var bDelete = this.getComponent('bDelete');//this.getComponent('pDeleteCiSearch').getComponent('bDelete');
//		bDelete.hide();
		
//		var pTop = this.getComponent('pTop');
//		pTop.hide();
		
		var bDelete = this.getComponent('bDelete');
		bDelete.hide();
		
		this.doLayout();
	}
});
Ext.reg('AIR.CiDeleteView', AIR.CiDeleteView);