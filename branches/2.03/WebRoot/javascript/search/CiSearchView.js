Ext.namespace('AIR');

AIR.CiSearchView = Ext.extend(AIR.AirView, {
	initComponent: function() {
		Ext.apply(this, {
		    border: false,
		    autoScroll: true,//hier ein muss

		    padding: 20,
		    
		    bodyStyle: {
		    	backgroundColor: AC.AIR_BG_COLOR,
		    	color: AC.AIR_FONT_COLOR,
		    	fontFamily: AC.AIR_FONT_TYPE
		    },
		    
		    items: [{
		    	xtype: 'label',
		    	id: 'searchpanelheader',
				
				style: {
					textAlign: 'left',
					backgroundColor: AC.AIR_BG_COLOR,
					color: AC.AIR_FONT_COLOR,
					fontFamily: AC.AIR_FONT_TYPE,
					fontWeight: 'bold',
					fontSize: '12pt'
				}
			}, { 
				xtype: 'container',	  
				html: '<hr>',
//				id: 'searchpanelhr',
				
				cls: 'x-plain',
				
				style: {
					color: '#d0d0d0',
					backgroundColor: '#d0d0d0',
					height: '1px',//ohne: grauer Hintergrund und schwarze Strichfarbe
					
					marginTop: 25,
					marginBottom: 20
				}
			}, {
				xtype: 'panel',
				id: 'ciSearchViewPages',
				
			    layout: 'card',
			    activeItem: 0,
			    border: false,
			    //height: 50,//300! 50
				//autoScroll: true,

			    items: [{
					xtype: 'AIR.CiStandardSearchView',
					id: 'ciStandardSearchView'
				},{
					xtype: 'AIR.CiOuSearchView',
					id: 'ciOuSearchView'
				}]
			}, {
		    	xtype: 'AIR.CiResultView',
		    	id: 'ciSearchResultView',
		    	border: false
		    	
//		    	hidden: true,
////		    	height: 1000,
//		    	
//		    	style: {
//		    		marginTop: 20
//		    	},
//		    	
//		    	items: [{
		    	
			    	/*xtype: 'AIR.CiResultGrid',
			    	id: 'ciSearchGrid',
			    	layout: 'fit',
			    	
			    	ownerPrefix: 'ciSearchGrid',
			    	hidden: true,
			    	border: false,
			    	title: 'Results'*/
		    	
//		    	}]
		    }]
		});
		
		AIR.CiSearchView.superclass.initComponent.call(this);
		
		this.addEvents('ciSelect', 'beforeCiSelect', 'externalNavigation');
		/*
		this.ciSearchGrid = this.getComponent('ciSearchGrid');//.getComponent('ciSearchResultView')
		this.ciSearchGrid.getStore().on('beforeload', this.onGridBeforeLoaded , this);
		this.ciSearchGrid.getStore().on('load', this.onGridLoaded, this);
		this.ciSearchGrid.on('rowclick', this.onRowClick, this);
		this.ciSearchGrid.on('rowdblclick', this.onRowDoubleClick, this);//onRowDoubleClick onRowClick
		*/
		
		var clSearch = this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView').getComponent('pSearch').getComponent('clSearch');
		clSearch.on('click', this.onSearch, this);
		
//	    var clAdvancedSearch = this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView').getComponent('pSearch').getComponent('clAdvancedSearch');
//	    clAdvancedSearch.on('click', this.handleUiAdvancedSearch, this);
	    
	    var tfSearch = this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView').getComponent('pSearch').getComponent('tfSearch');
	    tfSearch.on('specialkey', this.onSearchEnter, this);
	    
//		var clOrgUnit = this.getComponent('ciSearchViewPages').getComponent('ciOuSearchView').getComponent('pOrgUnit').getComponent('clOrgUnit');
//		clOrgUnit.on('click', this.onOrgUnitClick, this);
	    
		
		var clOrgUnitAdd = this.getComponent('ciSearchViewPages').getComponent('ciOuSearchView').getComponent('pOrgUnit').getComponent('pOrgUnit1').getComponent('clOrgUnitAdd');
		var clOrgUnitRemove = this.getComponent('ciSearchViewPages').getComponent('ciOuSearchView').getComponent('pOrgUnit').getComponent('pOrgUnit1').getComponent('clOrgUnitRemove');
		clOrgUnitAdd.on('click', this.onOrgUnitAdd, this);
		clOrgUnitRemove.on('click', this.onOrgUnitRemove, this);
		
		
//		var clOuSearch = this.getComponent('ciSearchViewPages').getComponent('ciOuSearchView').getComponent('clOuSearch');
//		clOuSearch.on('click', this.onOuSearch, this);
		var bOuSearch = this.getComponent('ciSearchViewPages').getComponent('ciOuSearchView').getComponent('pOuSearch').getComponent('bOuSearch');
		bOuSearch.on('click', this.onOuSearch, this);
		var bUpdateOuSearch = this.getComponent('ciSearchViewPages').getComponent('ciOuSearchView').getComponent('pOuSearch').getComponent('bUpdateOuSearch');
		bUpdateOuSearch.on('click', this.onUpdateCiSearchResult, this);
		
		
		/*
		var pagingBar = this.ciSearchGrid.getBottomToolbar();
		var clExcelExport = pagingBar.getComponent('ciSearchGrid_clExcelExport');
		clExcelExport.on('click', this.onExcelExport, this);*/
		
		var ciAdvancedSearchView = this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView').getComponent('ciAdvancedSearchView');
		ciAdvancedSearchView.on('expand', this.onAdvSearchExpand, this);
		ciAdvancedSearchView.on('collapse', this.onAdvSearchCollapse, this);
		
		var cbCiType = ciAdvancedSearchView.getComponent('pAdvSearchSingleAttrsFrame').getComponent('pAdvSearchSingleAttrs').getComponent('cbCiType');
		cbCiType.on('select', this.onCat1Select, this);
		cbCiType.on('change', this.onCat1Change, this);
		
		var ciSearchResultView = this.getComponent('ciSearchResultView');
//		ciSearchResultView.on('tabclose', this.onSearchTabClose, this);
		ciSearchResultView.getComponent('tpCiSearchResultTables').on('tabchange', this.onTabChange, this);
		
		
//		ciSearchResultView.getComponent('bUpdateCiSearchResult').on('click', this.onUpdateCiSearchResult, this);
		
		ciSearchResultView.getComponent('pSearchResultOptions').getComponent('bExpandAdvSearchParams').on('click', this.onExpandAdvSearchParams, this);
		ciSearchResultView.getComponent('pSearchResultOptions').getComponent('bSearchReset').on('click', this.onReset, this);
		this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView').getComponent('pSearch').getComponent('bUpdateCiSearchResult').on('click', this.onUpdateCiSearchResult, this);
	},
	
	onCat1Select: function(store, record, options) {
		this.updateAdvSearchHeight(this.isAdvSearchExt, true);
	},
	onCat1Change: function(combo, newValue, oldValue) {
		this.updateAdvSearchHeight(this.isAdvSearchExt, true);
	},
	
	onAdvSearchExpand: function(panel) {
		var bExpandAdvSearchParams = this.getComponent('ciSearchResultView').getComponent('pSearchResultOptions').getComponent('bExpandAdvSearchParams');
		bExpandAdvSearchParams.setText(AAM.getLabels().bCollapseAdvSearchParams);
		bExpandAdvSearchParams.toggle(true, true);
		
		this.updateAdvSearchHeight(this.isAdvSearchExt, true);
	},
	onAdvSearchCollapse: function(panel) {
		var bExpandAdvSearchParams = this.getComponent('ciSearchResultView').getComponent('pSearchResultOptions').getComponent('bExpandAdvSearchParams');
		bExpandAdvSearchParams.setText(AAM.getLabels().bExpandAdvSearchParams);
		bExpandAdvSearchParams.toggle(false, true);
		
		this.collapseAdvSearch();
	},
	
	
	onExcelExport: function(link, event) {
		//a)
//		var tfSearch = this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView').getComponent('pSearch').getComponent('tfSearch');
//		var exportWindow = window.open('/AIR/excelexport?query='+tfSearch.getValue()+'&cwid='+AIR.AirApplicationManager.getCwid()+'&searchPoint=Search');
		
		//b)
//		var exportForm = AIR.AirApplicationManager.getExportFormAsExtElement();//AIR.AirApplicationManager.getExportForm();//this.getComponent('ciSearchResultView').getComponent('exportForm').getEl().dom.children[0].children[0];//document.createElement('form');//
		var form = AIR.AirApplicationManager.getExportForm();//exportForm.getEl().dom.children[0].children[0];
		
		form.action = '/AIR/excelexport';//exportForm
		form.method = 'POST';//exportForm
		form.target = '_blank';//exportForm
		
		//query searchPoint cwid: already rendered <hidden> fields!
//		exportForm.query.value = tfSearch.getValue();
//		exportForm.cwid.value = AIR.AirApplicationManager.getCwid();
//		exportForm.searchPoint.value = 'Search';
		
	    if(this.isOuSearch) {
	    	var params = this.getOuSearchParams();
//	    	params.limit = 100000;
	    	
	    	form.searchAction.value = params.searchAction;//searchAction searchType
	    	
	    	for(var key in params)
	    		if(form['h'+key])
	    			form['h'+key].value = params[key];
	    } else {
			var params = this.getSearchParams();
			params.limit = 100000;//delete params.limit;
			
	    	for(var key in params)
	    		if(form[key])
	    			form[key].value = params[key];//exportForm
	//    		exportForm.getComponent(key).setValue(params[key]);
	    	
	    	
		    if(this.isAdvSearch) {
		    	var params = this.getAdvancedSearchParams(params);
		    	for(var key in params)
		    		if(form['h'+key])
		    			form['h'+key].value = params[key];//exportForm
	//	    		if(exportForm.getComponent('h'+key))
	//	    			exportForm.getComponent('h'+key).setValue(params[key]);
		    }
	    }

	    form.submit();
	    
//		exportForm.getForm().submit();//exportForm.submit()
	},
	
//	onExcelExportSuccess: function(response, options) {
//		Util.log('export success');
//	},
//	
//	onExcelExportFailure: function(response, options) {
//		Util.log('export failure');
//	},
	
	onSearchEnter: function(field, e) {
        if(e.getKey() == e.ENTER) {
	    	var clSearch = this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView').getComponent('pSearch').getComponent('clSearch');
	    	clSearch.fireEvent('click', clSearch);
	    }
	},
	
	
//	onOrgUnitClick: function(link, event) {
//		AIR.AirPickerManager.openGroupPicker(
//			null, this.getComponent('ciAdvancedSearchView').getComponent('pOrgUnit').getComponent('tfOrgUnit'), event, 'none');
//	},
	
	onOrgUnitAdd: function(link, event) {
		AIR.AirPickerManager.openGroupPicker(
			null, this.getComponent('ciSearchViewPages').getComponent('ciOuSearchView').getComponent('pOrgUnit').getComponent('pOrgUnit1').getComponent('tfOrgUnit'), event, 'none');
	},
	
	onOrgUnitRemove: function(link, event) {
		AIR.AirPickerManager.openRemovePicker(
			null, this.getComponent('ciSearchViewPages').getComponent('ciOuSearchView').getComponent('pOrgUnit').getComponent('pOrgUnit1').getComponent('tfOrgUnit'), event);
	},
	
	onSearch: function() {
		var params = this.getSearchParams();
		
	    if(this.isAdvSearch)
	    	params = this.getAdvancedSearchParams(params);

	    var ciAdvancedSearchView = this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView').getComponent('ciAdvancedSearchView');
	    ciAdvancedSearchView.collapse(false);
//	    ciAdvancedSearchView.setVisible(false);
	    
	    var searchAction = this.isAdvSearch ? AC.SEARCH_TYPE_ADV_SEARCH : AC.SEARCH_TYPE_SEARCH;//searchType
	    params.searchAction = searchAction;//searchType
	    
	    this.processSearch(params);
	},
	
	getBaseSearchParams: function() {
	    var params = { 
    		start: 0,
    		limit: 20,//this./*getComponent('ciSearchResultView').*/getComponent('ciSearchGrid').pageSize,//20,
    		
		 	cwid: AIR.AirApplicationManager.getCwid(),
		 	token: AIR.AirApplicationManager.getToken(),
			searchAction: 'search'
    	};
	    
	    return params;
	},
	
	getSearchParams: function() {
		searchAction = 'search';//still needed?
		this.getComponent('ciSearchViewPages').doLayout();//because auf setHeight() in navigation handling
		
		var field = this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView').getComponent('pSearch').getComponent('tfSearch');
		var searchString = field.getRawValue().trim();
		
	    if (searchString != field.getRawValue())
	    	field.setValue(searchString);

	    while(searchString.indexOf('*') > -1)
	    	searchString = searchString.replace('*', '%');
	    
	    while(searchString.indexOf('?') > -1)
	    	searchString = searchString.replace('?', '_');
	    
	    var i = searchString.indexOf('\'');
	    if(i > -1) {
	    	var hasSecondApos = searchString.charAt(i + 1) === '\'';
	    	if(!hasSecondApos)
	    		searchString = searchString.replace('\'', '\'\'');
	    }
	    
		
		var rbgQueryMode = this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView').getComponent('pSearch').getComponent('rbgQueryMode');
		var rbQueryMode = rbgQueryMode.getValue();
		var queryMode = rbQueryMode.inputValue;
		
		
	    var params = this.getBaseSearchParams();
	    
	    var store = AIR.AirStoreManager.getStoreByName('itsecUserOptionListStore');
	    var isAppOnly = AAM.hasRole(AC.USER_ROLE_AIR_APPLICATION_LAYER) || store.findExact('itsecUserOptionName', 'AIR_APPLICATION_ONLY') > -1;
	    params.isOnlyApplications = '' + isAppOnly;//onlyapplications;

	    params.showDeleted = 'N';
	    if (this.isShowDeleted()) {
		    params.showDeleted = 'Y';
		}
	    
	    params.ciNameAliasQuery = searchString;//params.query
	    params.queryMode = queryMode;
		params.isAdvSearch = '' + this.isAdvSearch;//params.advancedsearch
		
		return params;
	},
	
	getAdvancedSearchParams: function(params) {
		params.isAdvSearch = this.isAdvSearch;
		params.isAdvSearchExt = this.isAdvSearchExt;
		
	    params.showDeleted = 'N';
	    if (this.isShowDeleted()) {
		    params.showDeleted = 'Y';
		}

		
//		var ciAdvancedSearchView = this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView').getComponent('ciAdvancedSearchView');
		
		var ciStandardSearchView = this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView');
		ciStandardSearchView.setData(params);
		
		return params;
	},
	
	isCat1OrNoneSelected: function() {
		var params = this.getComponent('ciSearchResultView').getSearchParams();
		var cat1;
		
		if(params) {
			cat1 = params.advsearchObjectTypeId;//params.advsearchObjectTypeId kann undefined sein
		} else {
			var ciAdvancedSearchView = this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView').getComponent('ciAdvancedSearchView');
		    var cbCat1 = ciAdvancedSearchView.getComponent('pAdvSearchSingleAttrsFrame').getComponent('pAdvSearchSingleAttrs').getComponent('cbCiType');
		    cat1 = cbCat1.getValue();
		}
		
	    return cat1 === undefined || cat1 === AC.APP_CAT1_APPLICATION || cat1.length === 0;
	},

	
	handleSearch: function(link) {
		this.isOuSearch = false;
		this.isAdvSearch = false;
		
		var ciSearchResultView = this.getComponent('ciSearchResultView');
		var params = ciSearchResultView.getSearchParams();
		
		if(params)
			this.setUpdateSearchAvailable(link, this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView').getComponent('pSearch').getComponent('bUpdateCiSearchResult'));

		var ciSearchViewPages = this.getComponent('ciSearchViewPages');
		var ciStandardSearchView = ciSearchViewPages.getComponent('ciStandardSearchView');
		
		ciSearchViewPages.getLayout().setActiveItem(0);
		ciStandardSearchView.setHeight(100);//60
		ciSearchViewPages.setHeight(100);//60
		ciSearchViewPages.doLayout();
		
		
//		var link = ciStandardSearchView.getComponent('pSearch').getComponent('clAdvancedSearch');//.getComponent('pSearch').getComponent('clAdvancedSearch')
//		link.updateText(AIR.AirApplicationManager.getLabels().advancedsearchlink);
		
		ciStandardSearchView.getComponent('ciAdvancedSearchView').setVisible(false);
		
		var rbgQueryMode = ciStandardSearchView.getComponent('pSearch').getComponent('rbgQueryMode');//.getComponent('pSearch').getComponent('rbgQueryMode')
		rbgQueryMode.setValue(AC.SEARCH_MODE_CONTAINS);//searchQueryModeContains (#8)
		rbgQueryMode.setVisible(false);
		
		var bExpandAdvSearchParams = this.getComponent('ciSearchResultView').getComponent('pSearchResultOptions').getComponent('bExpandAdvSearchParams');
		bExpandAdvSearchParams.setVisible(false);
	},
	
	handleUiAdvancedSearch: function(link, event) {
		var ciSearchResultView = this.getComponent('ciSearchResultView');
		var params = ciSearchResultView.getSearchParams();
		
		if(params)
			this.setUpdateSearchAvailable(link, this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView').getComponent('pSearch').getComponent('bUpdateCiSearchResult'));
		

		searchAction = 'search';
		this.isOuSearch = false;
		this.isAdvSearch = true;
		
		var ciSearchViewPages = this.getComponent('ciSearchViewPages');
		var ciStandardSearchView = ciSearchViewPages.getComponent('ciStandardSearchView');
		var ciAdvancedSearchView = ciStandardSearchView.getComponent('ciAdvancedSearchView');

//		ciAdvancedSearchView.reset(link);
		ciSearchViewPages.getLayout().setActiveItem(0);
		
		
		var rbgQueryMode = ciStandardSearchView.getComponent('pSearch').getComponent('rbgQueryMode');
		rbgQueryMode.setVisible(true);
		

//		var isAdvSearchExt;
//		var isAdditionalSearchAttributes;
//		var isCat1OrNone;
//		
//		if(link) {//wenn less/more link options geklickt
//			this.isAdvSearchExt = !this.isAdvSearchExt;
//			isAdvSearchExt = this.isAdvSearchExt;
//			
//			var cbCat1 = ciAdvancedSearchView.getComponent('advsearchObjectType');
//			isAdditionalSearchAttributes = true;
//			isCat1OrNone = cbCat1.getValue() === AC.APP_CAT1_APPLICATION || cbCat1.getValue() === '' ? true : false;
//		} else {
//			isAdvSearchExt = params ? params.isAdvSearchExt : this.isAdvSearchExt;
//			link = ciStandardSearchView.getComponent('pSearch').getComponent('clAdvancedSearch');
//		}
//		
//		var label = isAdvSearchExt ? AIR.AirApplicationManager.getLabels().advancedsearchminuslink : AIR.AirApplicationManager.getLabels().advancedsearchpluslink;
//		link.updateText(label);
		
		
//		var cbCat1 = ciAdvancedSearchView.getComponent('pAdvSearchSingleAttrsFrame').getComponent('pAdvSearchSingleAttrs').getComponent('cbCiType');
//		var isCat1OrNone = cbCat1.getValue() === AC.APP_CAT1_APPLICATION || cbCat1.getValue() === '' ? true : false;
		var cbCiType = ciAdvancedSearchView.getComponent('pAdvSearchSingleAttrsFrame').getComponent('pAdvSearchSingleAttrs').getComponent('cbCiType');
//		ciAdvancedSearchView.filterCiTypesByUserRole(cbCiType);
		ciAdvancedSearchView.processCiTypeChange(cbCiType, cbCiType.getValue());
		
		var record = Util.getComboRecord(cbCiType, 'ciTypeId', AC.TABLE_ID_APPLICATION);
		var isCat1OrNone = record && record.get('ciSubTypeId') == AC.APP_CAT1_APPLICATION || cbCiType.getValue().length === 0 ? true : false;
		
		var bExpandAdvSearchParams = this.getComponent('ciSearchResultView').getComponent('pSearchResultOptions').getComponent('bExpandAdvSearchParams');
		bExpandAdvSearchParams.setVisible(true);
		
		
		this.isAdvSearchExt = true;
		var isAdvSearchExt = params ? params.isAdvSearchExt : this.isAdvSearchExt;
		ciAdvancedSearchView.getComponent('pAdditionalSearchAttributes').setVisible(isAdvSearchExt);
		ciAdvancedSearchView.show();
		
		var isAdditionalSearchAttributes = true;
//		if(!ciAdvancedSearchView.collapsed)
			this.updateAdvSearchHeight(isAdditionalSearchAttributes, isCat1OrNone);
	},
	
	handleUiOuSearch: function(link, event) {
		var ciSearchResultView = this.getComponent('ciSearchResultView');
		var params = ciSearchResultView.getSearchParams();
		
		if(params)
			this.setUpdateSearchAvailable(link, this.getComponent('ciSearchViewPages').getComponent('ciOuSearchView').getComponent('pOuSearch').getComponent('bUpdateOuSearch'));
		
		this.isOuSearch = true;
		this.isAdvSearch = false;
		this.isAdvSearchExt = false;
		
		
		var ciSearchViewPages = this.getComponent('ciSearchViewPages');
		var height = ciSearchViewPages.getComponent('ciOuSearchView').getHeight() + 30;
		
		ciSearchViewPages.setHeight(250);//300 460 | height=0 direkt nach startup oder zum ersten Mal auf clOuSearch. Warum?
		ciSearchViewPages.getLayout().setActiveItem(1);
		ciSearchViewPages.getComponent('ciOuSearchView').doLayout();
		
		var bExpandAdvSearchParams = this.getComponent('ciSearchResultView').getComponent('pSearchResultOptions').getComponent('bExpandAdvSearchParams');
		bExpandAdvSearchParams.setVisible(false);
	},
	
	setUpdateSearchAvailable: function(link, button) {
		var tabSearchType = this.getComponent('ciSearchResultView').getCurrentSearchType().replace(' ', '');//z.B. Advanced Search --> AdvancedSearch //oder �ber params.searchType?
		var navigationSearchType = link.getId().substring(2, link.getId().length);
		
		var isUpdateSearchAvailable = tabSearchType.indexOf(navigationSearchType) === 0;//indexOf(navigationSearchType, 0) > -1
//		var bUpdateCiSearchResult = this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView').getComponent('pSearch').getComponent('bUpdateCiSearchResult');

		if(isUpdateSearchAvailable) {
			button.show();//bUpdateCiSearchResult
		} else {
			button.hide();//bUpdateCiSearchResult
		}
	},
	
	
	updateAdvSearchHeight: function(isAdditionalSearchAttributes, isCat1OrNone) {//ciSearchViewPages, ciStandardSearchView
		var ciSearchViewPages = this.getComponent('ciSearchViewPages');
		var ciStandardSearchView = ciSearchViewPages.getComponent('ciStandardSearchView');
		var ciAdvancedSearchView = ciStandardSearchView.getComponent('ciAdvancedSearchView');
		
		var height;
		
		if(ciAdvancedSearchView.collapsed) {
			height = 100;
		} else {
	//		var height = 450;//50 + this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView').getComponent('ciAdvancedSearchView').getHeight();//150;//200 300 400 630
			//250 200
			height = 280 + ciAdvancedSearchView.getComponent('pAdvSearchCIOwnerFrame').getComponent('fs' + ciAdvancedSearchView.ownerId + 'CIOwner').getHeight();
			if(isCat1OrNone)// || this.isCat1OrNoneSelected()
				height += ciAdvancedSearchView.getComponent('pAdvSearchAppOwnerFrame').getComponent('fs' + ciAdvancedSearchView.ownerId + 'ApplicationOwner').getHeight() +
						  ciAdvancedSearchView.getComponent('pAdvSearchAppStewardFrame').getComponent('fs' + ciAdvancedSearchView.ownerId + 'ApplicationSteward').getHeight();
			
			if(isAdditionalSearchAttributes || this.isAdditionalSearchAttributesActive()) {
	//			height += ciAdvancedSearchView.getComponent('pAdditionalSearchAttributes').getHeight();// - 300;//300;//250
				var pAdditionalSearchAttributes = ciAdvancedSearchView.getComponent('pAdditionalSearchAttributes');
				height += pAdditionalSearchAttributes.getHeight();
			}
			
			if(ciAdvancedSearchView.getWidth() < 955)//wenn niedrige (Beamer) Ausl�sung, die H�he vergr��ern
				height += 30;
		}
		
		ciStandardSearchView.setHeight(height);
		ciSearchViewPages.setHeight(height);
		ciSearchViewPages.doLayout();//because of setHeight() in navigation handling
	},
	collapseAdvSearch: function() {
		this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView').getComponent('ciAdvancedSearchView').collapse();

		this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView').setHeight(100);//100
		this.getComponent('ciSearchViewPages').setHeight(100);//100
//		this.getComponent('ciSearchViewPages').doLayout();
	},
	
	
	
	onOuSearch: function(link, event) {
		this.isOuSearch = true;
		this.isAdvSearch = false;
		this.isAdvSearchExt = false;
		
//		var ciSearchResultView = this.getComponent('ciSearchResultView');
//		var params = this.getOuSearchParams();//ciSearchResultView.getSearchParams();
		

		var params = this.getOuSearchParams();
		if(params.ouUnit.length > 0)
			this.processSearch(params);
		
	},
	
	getOuSearchParams: function() {
		var ciOuSearchView = this.getComponent('ciSearchViewPages').getComponent('ciOuSearchView');
		var rbgOUSearchQueryMode = ciOuSearchView.getComponent('pOrgUnit').getComponent('pOrgUnit2').getComponent('rbgOUSearchQueryMode');
		var rbgOUSearchOwnerType = ciOuSearchView.getComponent('pOrgUnit').getComponent('pOrgUnit2').getComponent('rbgOUSearchOwnerType');
		var cbOuSearchObjectType = ciOuSearchView.getComponent('cbOuSearchObjectType');
		var tfOrgUnit = ciOuSearchView.getComponent('pOrgUnit').getComponent('pOrgUnit1').getComponent('tfOrgUnit');
		
	    var params = this.getBaseSearchParams();
		
	    params.ouUnit = tfOrgUnit.getValue();
		params.ouCiType = cbOuSearchObjectType.getRawValue();
//	    params.ciTypeId = cbOuSearchObjectType.getValue();
		params.ciOwnerType = rbgOUSearchOwnerType.getValue().inputValue;
		params.ouQueryMode = rbgOUSearchQueryMode.getValue().inputValue;
//		params.searchType = AC.SEARCH_TYPE_OU_SEARCH;
		params.searchAction = AC.SEARCH_TYPE_OU_SEARCH;
		params.isOuSearch = true;
		
		return params;
	},
	
	
	processSearch: function(params, isUpdate) {
		if(Ext.isIE && !this.isMoved) {
			this.isMoved = true; 
			//-----------------
			//ohne dies vertauscht der IE willk�rlich Spalten
//			this.ciSearchGrid.getColumnModel().setConfig(this.ciSearchGrid.getDefaultColumnConfig());//this.myOwnCIsGrid.getColumnModel().config
			
			/*
			var ciSearchGrid = this.getComponent('ciSearchGrid');//getComponent('ciSearchResultView').
			ciSearchGrid.getColumnModel().setConfig(AIR.AirConfigFactory.createCiResultGridConfig(true));
			this.updateColumnLabels(AIR.AirApplicationManager.getLabels());*/
		}
		/*
	    this.ciSearchGrid.getStore().load({
	    	params: params
	    });
	    
		delete params.start;
		delete params.limit;
		this.ciSearchGrid.setPagingParams(params);*/
		
		
		
		params.isAdvSearchExt = this.isAdvSearchExt;
		this.getComponent('ciSearchResultView').search(params, isUpdate, this.onExcelExport.createDelegate(this));
	},
	
	onTabChange: function(tabPanel, tab, options) {
		if(tabPanel) {
			var bUpdateCiSearchResult = this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView').getComponent('pSearch').getComponent('bUpdateCiSearchResult');
			var bUpdateOuSearch = this.getComponent('ciSearchViewPages').getComponent('ciOuSearchView').getComponent('pOuSearch').getComponent('bUpdateOuSearch');
			
			if(tabPanel.items.items.length > 0) {
				bUpdateCiSearchResult.setVisible(true);
				bUpdateOuSearch.setVisible(true);
			} else {
				bUpdateCiSearchResult.setVisible(false);
				bUpdateOuSearch.setVisible(false);
				this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView').getComponent('ciAdvancedSearchView').expand(false);
			}
		}
		
		
		if(!tab) {
			tab = this.getComponent('ciSearchResultView').getComponent('tpCiSearchResultTables').getActiveTab();
			if(!tab) {
				if(options)
					this.fireEvent('externalNavigation', this, tab, options.viewId, options);
				return;
			}
		}
		
		var searchType = tab.getId().substring(0, tab.getId().indexOf('_'));
		var params = this.getComponent('ciSearchResultView').getSearchParams(tab.getId());
		var viewId;
		
		switch(searchType) {
			case AC.SEARCH_TYPE_SEARCH:
				viewId = 'clSearch';
				break;
			case AC.SEARCH_TYPE_ADV_SEARCH:
				viewId = 'clAdvancedSearch';
				break;
			case AC.SEARCH_TYPE_OU_SEARCH:
				viewId = 'clOuSearch';
				break;
			default:
				break;
		};
		
		if(options) {
			options.forceNavigation = true;
			options.skipHistory = true;
		} else {
			options = { forceNavigation: true, skipHistory: true };
		}
		
		this.updateParams(params, searchType);
		if(options.viewId && options.viewId !== viewId)//wenn durch MenuLinkklick und Adv. Seach Tab Klick versch. navMenuLink IDs in den options stehen, gleichsetzen. N�tig/sinnvoll ?
			viewId = options.viewId;//options.viewId = viewId
		
		this.fireEvent('externalNavigation', this, tab, viewId, options);
//		this.updateParams(params, searchType);
	},
	
	updateParams: function(params, searchAction) {//searchType
		var ciStandardSearchView = this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView');
		
		switch(searchAction) {
			case AC.SEARCH_TYPE_ADV_SEARCH:
				
				ciStandardSearchView.update(params);
			case AC.SEARCH_TYPE_SEARCH:
				
				var tfSearch = ciStandardSearchView.getComponent('pSearch').getComponent('tfSearch');
				tfSearch.setValue(params.ciNameAliasQuery);//query
				
				break;

			case AC.SEARCH_TYPE_OU_SEARCH:
				this.getComponent('ciSearchViewPages').getComponent('ciOuSearchView').update(params);
				
				break;
			default:
				break;
		};
	},
	
	onUpdateCiSearchResult: function(button, event) {
		var params = this.getComponent('ciSearchResultView').getSearchParams();
		
		var searchAction = params.searchAction;//searchType
		
		switch(searchAction) {
			case AC.SEARCH_TYPE_ADV_SEARCH://!!
			case AC.SEARCH_TYPE_SEARCH:
				params = this.getSearchParams();
				
				if(searchAction === AC.SEARCH_TYPE_SEARCH)//searchType
					break;
			case AC.SEARCH_TYPE_ADV_SEARCH:
				params = this.getAdvancedSearchParams(params);
				
			    var ciAdvancedSearchView = this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView').getComponent('ciAdvancedSearchView');
			    ciAdvancedSearchView.collapse(false);
			    
				break;
			case AC.SEARCH_TYPE_OU_SEARCH:
				params = this.getOuSearchParams();
				break;
			default:
				break;
		};
		
		params.searchAction = searchAction;//searchType
		this.processSearch(params, true);
	},
	
	onExpandAdvSearchParams: function(button, event) {
		var ciAdvancedSearchView = this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView').getComponent('ciAdvancedSearchView');

		if(button.pressed) {
			ciAdvancedSearchView.expand(false);
			button.setText(AAM.getLabels().bCollapseAdvSearchParams);
		} else {
			ciAdvancedSearchView.collapse(false);
			button.setText(AAM.getLabels().bExpandAdvSearchParams);
		}
	},
	
	onReset: function(button, event) {
		var ciStandardSearchView = this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView');
		
		if(this.isOuSearch) {
			searchView = this.getComponent('ciSearchViewPages').getComponent('ciOuSearchView').reset();
		} else if(this.isAdvSearch) {
			ciStandardSearchView.reset();
			ciStandardSearchView.getComponent('ciAdvancedSearchView').reset();
		} else {
			ciStandardSearchView.reset();
		}
	},
	
	isAdditionalSearchAttributesActive: function() {
		return true;
		
//		var params = this.getComponent('ciSearchResultView').getSearchParams();
//		var isVisible;
//		
//		if(params) {
//			isVisible = params.isAdvSearchExt;
//		} else {
//			var pAdditionalSearchAttributes = this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView').getComponent('ciAdvancedSearchView').getComponent('pAdditionalSearchAttributes');
//			isVisible = pAdditionalSearchAttributes.isVisible();//oder immer true
//		}
//		
//		return isVisible;
	},
	
	updateLabels: function(labels) {
		this.getComponent('searchpanelheader').setText(labels.searchpanelheader);
		
		var ciSearchViewPages = this.getComponent('ciSearchViewPages');
		var ciStandardSearchView = ciSearchViewPages.getComponent('ciStandardSearchView');
		var ciAdvancedSearchView = ciStandardSearchView.getComponent('ciAdvancedSearchView');
		
		var pAdditionalSearchAttributes = this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView').getComponent('ciAdvancedSearchView').getComponent('pAdditionalSearchAttributes');
//		var clAdvancedSearch = this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView').getComponent('pSearch').getComponent('clAdvancedSearch');
		
//		if(pAdditionalSearchAttributes.isVisible()) {//this.isAdvSearchExt
//			clAdvancedSearch.updateText(labels.advancedsearchpluslink);
//		} else {
//			clAdvancedSearch.updateText(labels.advancedsearchlink);
//		}
		
		var rbgQueryMode = this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView').getComponent('pSearch').getComponent('rbgQueryMode');
		this.setBoxLabel(rbgQueryMode.items.items[0], labels.rbgQueryModeContains);
		this.setBoxLabel(rbgQueryMode.items.items[1], labels.rbgQueryModeBeginsWith);
		this.setBoxLabel(rbgQueryMode.items.items[2], labels.rbgQueryModeExact);
		
//		this.updateColumnLabels(labels);

		var ciStandardSearchView = this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView');
		ciStandardSearchView.updateLabels(labels);
		var ciOuSearchView = this.getComponent('ciSearchViewPages').getComponent('ciOuSearchView');
		ciOuSearchView.updateLabels(labels);
		var ciSearchResultView = this.getComponent('ciSearchResultView');
		ciSearchResultView.updateLabels(labels);
	},
	
	updateToolTips: function(toolTips) {
		this.getComponent('ciSearchViewPages').getComponent('ciStandardSearchView').updateToolTips(toolTips);
		this.getComponent('ciSearchViewPages').getComponent('ciOuSearchView').updateToolTips(toolTips);
	},
	
	setAdvSearch: function(isAdvSearch) {
		this.isAdvSearch = isAdvSearch;
	},
	
	isShowDeleted: function() {
		var isShowDeleted = false;
		var store = AIR.AirStoreManager.getStoreByName('itsecUserOptionListStore');
	    var userOptionRecord = Util.getStoreRecord(store, 'itsecUserOptionName', 'AIR_SHOW_DELETED');

	    if (userOptionRecord && 'YES' == userOptionRecord.get('itsecUserOptionValue')) {
	    	isShowDeleted = true;
		}
	    return isShowDeleted;
	}
	
	
});
Ext.reg('AIR.CiSearchView', AIR.CiSearchView);