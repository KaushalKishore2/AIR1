Ext.namespace('AIR');

AIR.MyPlaceTabView = Ext.extend(Ext.Panel, {
	CI_TYPE_APPLICATION: 2,
	
	initComponent: function() {
		Ext.apply(this, {
			layout: 'card',
//			anchor: '100%',
//			autoScroll: true,//hier ein muss(?)
			
//			hidden: false,
//			plain: true,
			border: false,
			
			items: [{ 
				id: 'card-mycis',
				border: false,
//				height: 1200,//!!
//				autoScroll: true,
				
			    items: [{
			    	xtype: 'AIR.CiResultView',
			    	id: 'myOwnCisView',
//			    	autoScroll: true,
			    	height: 1200,
			        
			    	items: [{
				    	xtype: 'AIR.CiResultGrid',
				    	id: 'myOwnCIsGrid',
				    	ownerPrefix: 'myCis'
//				    	layout: 'anchor',
//				    	anchor: '100%'
			    	}]
			    }]
			}, {
				id: 'card-myapps',
				border: false,
//				height: 1200,//!!
//				autoScroll: true,
				
			    items: [{
			    	xtype: 'AIR.CiResultView',
			    	id: 'myDelegateCisView',
//			    	autoScroll: true,
			    	height: 1200,
			        
			    	items: [{
				    	xtype: 'AIR.CiResultGrid',
				    	id: 'myDelegateCIsGrid',
				    	ownerPrefix: 'myCisSubstitute'
//				    	layout: 'anchor',
//				    	anchor: '100%'
			    	}]
		    	}] 
			}]
		});
		
		AIR.MyPlaceTabView.superclass.initComponent.call(this);
		
		this.addEvents('ciSelect', 'beforeCiSelect');
		
		//evtl. store params/baseParams Initialisierung in AirStoreFactory
		this.myOwnCIsGrid = this.getComponent('card-mycis').getComponent('myOwnCisView').getComponent('myOwnCIsGrid');//  findById=depr
		this.myOwnCIsGrid.getStore().on('beforeload', this.onGridBeforeLoaded, this);
		this.myOwnCIsGrid.getStore().on('load', this.onMyOwnCIsGridLoaded, this);
		this.myOwnCIsGrid.on('rowclick', this.onRowClick, this);
		this.myOwnCIsGrid.on('rowdblclick', this.onRowDoubleClick, this);//onRowDoubleClick onRowClick
		
		this.myDelegateCIsGrid = this.getComponent('card-myapps').getComponent('myDelegateCisView').getComponent('myDelegateCIsGrid');//  findById=depr
		this.myDelegateCIsGrid.getStore().on('beforeload', this.onGridBeforeLoaded, this);
		this.myDelegateCIsGrid.getStore().on('load', this.onMyDelegateCIsGridLoaded, this);
		this.myDelegateCIsGrid.on('rowclick', this.onRowClick, this);
		this.myDelegateCIsGrid.on('rowdblclick', this.onRowDoubleClick, this);//onRowDoubleClick onRowClick
		
		var pagingBar = this.myOwnCIsGrid.getBottomToolbar();
		var clExcelExport = pagingBar.getComponent('myCis_clExcelExport');
		clExcelExport.on('click', this.onExcelExport, this);
		
		var pagingBar = this.myDelegateCIsGrid.getBottomToolbar();
		var clExcelExport = pagingBar.getComponent('myCisSubstitute_clExcelExport');
		clExcelExport.on('click', this.onExcelExport, this);
	},
	
	onExcelExport: function(link, event) {
//		var exportWindow = window.open('/AIR/excelexport?cwid='+AIR.AirApplicationManager.getCwid()+'&token='+AIR.AirApplicationManager.getToken()+'&searchPoint='+link.getId().substring(0, link.getId().indexOf('_')));
		
		var exportForm = AIR.AirApplicationManager.getExportForm();//this.getComponent('ciSearchResultView').getComponent('exportForm').getEl().dom.children[0].children[0];//document.createElement('form');//
		
		exportForm.action = '/AIR/excelexport';
		exportForm.method = 'POST';
		exportForm.target = '_blank';
		
		//query searchPoint cwid: already rendered <hidden> fields!
//		exportForm.query.value = tfSearch.getValue();
		exportForm.searchAction.value = link.getId().substring(0, link.getId().indexOf('_'));
		exportForm.cwid.value = AIR.AirApplicationManager.getCwid();
		exportForm.token.value = AIR.AirApplicationManager.getToken();
		
		exportForm.submit();
	},
	
	loadMyOwnCIsGrid: function(searchAction) {
		var params = {
			start: 0,
			limit: 20,
			cwid: AIR.AirApplicationManager.getCwid(),
			token: AIR.AirApplicationManager.getToken(),
			searchAction: searchAction//'myCis'
		};
		
		
		this.myOwnCIsGrid.getStore().load({
			params: params
		});
		
		
		delete params.start;
		delete params.limit;
		this.myOwnCIsGrid.setPagingParams(params);
	},
	
	loadDelegateCIsGrid: function(searchAction) {
		var params = {
			start: 0,
			limit: 20,
			cwid: AIR.AirApplicationManager.getCwid(),
			token: AIR.AirApplicationManager.getToken(),
			searchAction: searchAction//'myCisSubstitute'
		};
		

		this.myDelegateCIsGrid.getStore().load({
			params: params
		});		
		
		
		delete params.start;
		delete params.limit;
		this.myDelegateCIsGrid.setPagingParams(params);
	},
	
	//onMyOwnCIsGridBeforeLoaded
	onGridBeforeLoaded: function(store, options) {
		myLoadMask.show();
	},
	
	onMyOwnCIsGridLoaded: function(store, records, options) {
		myLoadMask.hide();
		this.myOwnCIsGrid.updateHeight();
	},
	
	onMyDelegateCIsGridLoaded: function(store, records, options) {
		myLoadMask.hide();
		this.myDelegateCIsGrid.updateHeight();
	},
	
	
	onRowClick: function(grid, rowIndex, e) {
		this.fireEvent('beforeCiSelect');//if(this.fireEvent('beforeCiSelect') == false) return;//hier if(record.data.tableId == this.CI_TYPE_APPLICATION) behandfeln
		
		var record = grid.getStore().getAt(rowIndex);
		var ciId = record.id;
		
		if(record.data.tableId == this.CI_TYPE_APPLICATION) {
//			selectedCIId = ciId;//grid.store.getAt(rowIndex).id;
			AIR.AirApplicationManager.setCiId(ciId);
		} else {
//			selectedCIId = -1;
			AIR.AirApplicationManager.setCiId(-1);
			
//			if(Ext.isIE) {
//				var windowTitle = labels.dynamicWindowCiTypeNotSupportedWarningTitle;
//				var windowText = labels.dynamicWindowCiTypeNotSupportedWarningText;//languagestore.data.items[0].data['dynamicWindowCiTypeNotSupportedWarningText
//				
//				alert(windowTitle, windowText);//Fenster zerschossen bei IE. Grund unbekannt.
//			} else {
				var ciTypeWarningWindow = AIR.AirWindowFactory.createDynamicMessageWindow('CI_TYPE_NOT_SUPPORTED_WARNING');
				ciTypeWarningWindow.show();
//			}
		}
		
		this.fireEvent('ciSelect', this, ciId);//selectedCIId ciId
	},
	
	onRowDoubleClick: function (grid, rowIndex, e) {
//		selectedCIId = grid.store.getAt(rowIndex).id;
//		this.fireEvent('ciSelect', this, selectedCIId, 'clCiDetails');//selectedCIId ciId
		
		this.onRowClick(grid, rowIndex, e);
		this.fireEvent('externalNavigation', this, grid, 'clCiDetails');
	},

	
	updateLabels: function(labels) {
		var myOwnCisView = this.getComponent('card-mycis').getComponent('myOwnCisView');
		
		myOwnCisView.setTitle(labels.searchResultPanelTitle);
		myOwnCisView.getComponent('myOwnCIsGrid').getColumnModel().setColumnHeader(0, labels.searchResultName);
		myOwnCisView.getComponent('myOwnCIsGrid').getColumnModel().setColumnHeader(1, labels.searchResultAlias);
		myOwnCisView.getComponent('myOwnCIsGrid').getColumnModel().setColumnHeader(2, labels.searchResultType);
		myOwnCisView.getComponent('myOwnCIsGrid').getColumnModel().setColumnHeader(3, labels.searchResultCategory);
		myOwnCisView.getComponent('myOwnCIsGrid').getColumnModel().setColumnHeader(4, labels.searchResultResponsible);
		myOwnCisView.getComponent('myOwnCIsGrid').getColumnModel().setColumnHeader(5, labels.searchResultSubResponsible);
		myOwnCisView.getComponent('myOwnCIsGrid').getColumnModel().setColumnHeader(6, labels.searchResultAppOwner);
		myOwnCisView.getComponent('myOwnCIsGrid').getColumnModel().setColumnHeader(7, labels.searchResultAppSteward);
		myOwnCisView.getComponent('myOwnCIsGrid').getColumnModel().setColumnHeader(8, labels.searchResultAppOwnerDelegate);

		
		var myDelegateCisView = this.getComponent('card-myapps').getComponent('myDelegateCisView');
		
		myDelegateCisView.setTitle(labels.searchResultPanelTitle);
		myDelegateCisView.getComponent('myDelegateCIsGrid').getColumnModel().setColumnHeader(0, labels.searchResultName);
		myDelegateCisView.getComponent('myDelegateCIsGrid').getColumnModel().setColumnHeader(1, labels.searchResultAlias);
		myDelegateCisView.getComponent('myDelegateCIsGrid').getColumnModel().setColumnHeader(2, labels.searchResultType);
		myDelegateCisView.getComponent('myDelegateCIsGrid').getColumnModel().setColumnHeader(3, labels.searchResultCategory);
		myDelegateCisView.getComponent('myDelegateCIsGrid').getColumnModel().setColumnHeader(4, labels.searchResultResponsible);
		myDelegateCisView.getComponent('myDelegateCIsGrid').getColumnModel().setColumnHeader(5, labels.searchResultSubResponsible);
		myDelegateCisView.getComponent('myDelegateCIsGrid').getColumnModel().setColumnHeader(6, labels.searchResultAppOwner);
		myDelegateCisView.getComponent('myDelegateCIsGrid').getColumnModel().setColumnHeader(7, labels.searchResultAppSteward);
		myDelegateCisView.getComponent('myDelegateCIsGrid').getColumnModel().setColumnHeader(8, labels.searchResultAppOwnerDelegate);
	}
	
});
Ext.reg('AIR.MyPlaceTabView', AIR.MyPlaceTabView);