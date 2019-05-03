Ext.namespace('AIR');

AIR.CiAdvancedSearchView = Ext.extend(AIR.AirView, {
	
	initComponent: function() {
		Ext.apply(this, {
			title: 'Advanced Search Options',
		    padding: 10,
		    border: false,
		    hidden: true,
		    layout: 'column',
			
			autoScroll: true,

		    
		    bodyStyle: {
		    	backgroundColor: panelbgcolor,
		    	color: fontColor,
		    	fontFamily: fontType
		    },
		    
		    items: [{
				xtype: 'panel',
				id: 'pAdvancedSearch',
				layout: 'form',
				
				border: false,
				labelWidth: 130,
				
			    style: {
			    	marginRight: 10
			    },

				items: [{
					xtype: 'combo',
					id: 'advsearchObjectType',
				    store: AIR.AirStoreManager.getStoreByName('applicationCat1ListStore'),
					
				    fieldLabel: 'Type',
				    valueField: 'id',
			        displayField: 'english',
			        
//			        typeAhead: true,
//			        autoSelect: false,
//			        triggerAction: 'all',
			        
			        forceSelection: true,
			        triggerAction: 'all',
			        lazyRender: true,
			        lazyInit: false,
			        mode: 'local',
			        
			        width: 230
			    }, {
		        	xtype: 'textfield',
//		        	fieldLabel: 'Description',
//		        	name: 'advsearchdescription',
		        	id: 'advsearchdescription',
		        	
		        	emptyText: '',
		        	width: 230,
		        	hidden: false,
		        	hasSearch: false
		        }, {
			        xtype: 'fieldset',
			        id: 'advsearchowner',
			        title: 'Owner',
			        
//			        layout: 'form',//kein Layout form fit
			        labelWidth: 130,
//			        height: 300,
			        
			        
					items: [{
						xtype: 'container',
						id: 'pAdvSearchAppOwner',
						
						layout: 'column',
						style: {
							marginBottom: 5
						},
						
						items: [{
							xtype: 'label',
							id: 'labeladvsearchappowner',
							text: 'App owner',
							
							width: 130,
							style: {
								fontSize: 12
							}
			    		},{
							xtype: 'textfield',
					        width: 230,
					        id: 'advsearchappowner',
					        allowBlank: true
//					        disabled: false,
//					        readOnly: true
					    },{
							xtype: 'hidden',
					        id: 'advsearchappownerHidden'
					    },{
					    	xtype: 'commandlink',
					    	id: 'clAdvSearchAppOwnerAddPerson',
					    	img: img_AddPerson
					    }/*,{
					    	xtype: 'commandlink',
					    	id: 'clAdvSearchAppOwnerAddGroup',
					    	img: img_AddGroup
					    },{
					    	xtype: 'commandlink',
					    	id: 'clAdvSearchAppOwnerRemove',
					    	img: img_RemovePerson
					    }*/]
					},{
						xtype: 'container',
						id: 'pAdvSearchAppOwnerDelegate',
						
						layout: 'column',
						style: {
							marginBottom: 5
						},
						
						items: [{
							xtype: 'label',
							id: 'labeladvsearchappownerdelegate',
							text: 'App owner delegate',
							
							width: 130,
							style: {
								fontSize: 12
							}
			    		},{
							xtype: 'textfield',
					        width: 230,
					        id: 'advsearchappownerdelegate',
					        allowBlank: true
//					        disabled: false,
//					        readOnly: true
					    },{
							xtype: 'hidden',
					        id: 'advsearchappownerdelegateHidden'
					    },{
					    	xtype: 'commandlink',
					    	id: 'clAdvSearchAppOwnerDelegateAddPerson',
					    	img: img_AddPerson
					    },{
					    	xtype: 'commandlink',
					    	id: 'clAdvSearchAppOwnerDelegateAddGroup',
					    	img: img_AddGroup
					    }/*,{
					    	xtype: 'commandlink',
					    	id: 'clAdvSearchAppOwnerDelegateRemove',
					    	img: img_RemovePerson
					    }*/]
					},{
						xtype: 'container',
						id: 'pAdvSearchCiOwner',
						
						layout: 'column',
						style: {
							marginBottom: 5
						},
						
						items: [{
							xtype: 'label',
							id: 'labeladvsearchciowner',
							text: 'CI owner',
							
							width: 130,
							style: {
								fontSize: 12
							}
			    		},{
							xtype: 'textfield',
					        width: 230,
					        id: 'advsearchciowner',
					        allowBlank: true
//					        disabled: false,
//					        readOnly: true
					    },{
							xtype: 'hidden',
					        id: 'advsearchciownerHidden'
					    },{
					    	xtype: 'commandlink',
					    	id: 'clAdvSearchCiOwnerAddPerson',
					    	img: img_AddPerson
					    }/*,{
					    	xtype: 'commandlink',
					    	id: 'clAdvSearchCiOwnerAddGroup',
					    	img: img_AddGroup
					    },{
					    	xtype: 'commandlink',
					    	id: 'clAdvSearchCiOwnerRemove',
					    	img: img_RemovePerson
					    }*/]
					},{
						xtype: 'container',
						id: 'pAdvSearchCiOwnerDelegate',
						
						layout: 'column',
						style: {
							marginBottom: 5
						},
						
						items: [{
							xtype: 'label',
							id: 'labeladvsearchcidelegate',
							text: 'CI delegate',
							
							width: 130,
							style: {
								fontSize: 12
							}
			    		},{
							xtype: 'textfield',
					        width: 230,
					        id: 'advsearchcidelegate',
					        allowBlank: true
//					        disabled: false,
//					        readOnly: true
					    },{
							xtype: 'hidden',
					        id: 'advsearchcidelegateHidden'
					    },{
					    	xtype: 'commandlink',
					    	id: 'clAdvSearchCiOwnerDelegateAddPerson',
					    	img: img_AddPerson
					    },{
					    	xtype: 'commandlink',
					    	id: 'clAdvSearchCiOwnerDelegateAddGroup',
					    	img: img_AddGroup
					    }/*,{
					    	xtype: 'commandlink',
					    	id: 'clAdvSearchAppOwnerDelegateRemove',
					    	img: img_RemovePerson
					    }*/]
					}/*,{
			        	xtype: 'textfield',
			        	fieldLabel: 'App owner',
			        	id: 'advsearchappowner',
			        	
//			        	anchor: '100%',
			        	
//			        	emptyText: '',
			        	width: 200,
			        	hasSearch: false,
			        	
			        	listeners: {
			                specialkey: function(field, e) {
			                    if (e.getKey() == e.ENTER) {
//			                    	search();
			                    }
			                }
			            }
			        }, {
			        	xtype: 'textfield',
			        	fieldLabel: 'App owner delegate',
//			        	name: 'advsearchappownerdelegate',
			        	id: 'advsearchappownerdelegate',
			        	
//			        	anchor: '100%',
			        	
//			        	emptyText: '',
			        	width: 200,
			        	hasSearch: false,
			        	
			        	listeners: {
			                specialkey: function(field, e) {
			                    if (e.getKey() == e.ENTER) {
//			                    	search();
			                    }
			                }
			            }
			        }, {
			        	xtype: 'textfield',
			        	fieldLabel: 'CI owner',
//			        	name: 'advsearchciowner',
			        	id: 'advsearchciowner',
			        	
//			        	anchor: '100%',
			        	
//			        	emptyText: '',
			        	width: 200,
			        	hasSearch: false,
			        	
			        	listeners: {
			                specialkey: function(field, e) {
			                    if (e.getKey() == e.ENTER) {
//			                    	search();
			                    }
			                }
			            }
			        }, {
			        	xtype: 'textfield',
			        	fieldLabel: 'CI Delegate',
//			        	name: 'advsearchcidelegate',
			        	id: 'advsearchcidelegate',
			        	
//			        	anchor: '100%',
			        	
//			        	emptyText: '',
			        	width: 200,
			        	hasSearch: false,
			        	
			        	listeners: {
			                specialkey: function(field, e) {
			                    if (e.getKey() == e.ENTER) {
//			                    	search();
			                    }
			                }
			            }
			        }*/]
		        }]
		    }, {
			    xtype: 'fieldset',
			    id: 'advsearchplusfieldset',
			    title: 'Advanced Search Plus',
			    
//				columnWidth: 0.33,//0.45
			    width: 430,

			    padding: 10,
			    hidden: true,
			    
			    layout: 'form',//form fit
			    labelWidth: 180,
//			    width: 300,
			    
			    /*style: {
			    	marginLeft: 10
			    },*/
			    
				items: [{
					xtype: 'combo',
					id: 'advsearchoperationalStatus',
				    store: AIR.AirStoreManager.getStoreByName('operationalStatusListStore'),
			        width: 200,

				    fieldLabel: 'Operational Status',
				    valueField: 'id',
			        displayField: 'text',
			        
//			        typeAhead: true,
//			        forceSelection: true,
//			        autoSelect: false,
			        
			        triggerAction: 'all',
			        lazyRender: true,
			        lazyInit: false,
			        mode: 'local'
			    }, {
					xtype: 'filterCombo',//combo
					id: 'advsearchcategory',
				    store: AIR.AirStoreManager.getStoreByName('applicationCat2ListStore'),
			        width: 200,

					lastQuery: '',
				    fieldLabel: 'Category',
				    valueField: 'id',
			        displayField: 'text',
			        
//			        typeAhead: true,
//			        forceSelection: true,
//			        autoSelect: false,
			        
			        triggerAction: 'all',
			        lazyRender: true,
			        lazyInit: false,
			        mode: 'local'
			    }, {
					xtype: 'combo',
					id: 'advsearchlifecyclestatus',
				    store: AIR.AirStoreManager.getStoreByName('lifecycleStatusListStore'),
			        width: 200,
					
				    fieldLabel: 'Lifecycle status',
				    valueField: 'id',
			        displayField: 'text',
			        
//			        typeAhead: true,
//			        forceSelection: true,
//			        autoSelect: false,
			        
			        triggerAction: 'all',
			        lazyRender: true,
			        lazyInit: false,
			        mode: 'local'
			    }, {
					xtype: 'combo',
					id: 'advsearchprocess',
				    store: AIR.AirStoreManager.getStoreByName('processListStore'),
			        width: 200,

				    fieldLabel: 'Business Prozess',
				    valueField: 'id',
			        displayField: 'text',
			        
//			        typeAhead: true,
//			        forceSelection: true,
//			        autoSelect: false,
			        
			        triggerAction: 'all',
			        lazyRender: true,
			        lazyInit: false,
			        mode: 'local'
			    }]
		    }]
		});
		
		AIR.CiAdvancedSearchView.superclass.initComponent.call(this);
		
		var cbCat1 = this.getComponent('pAdvancedSearch').getComponent('advsearchObjectType');
		cbCat1.on('select', this.onCat1Select, this);
		cbCat1.on('change', this.onCat1Change, this);
		
		var cbCat2 = this.getComponent('advsearchplusfieldset').getComponent('advsearchcategory');
		cbCat2.on('change', this.onCat2Change, this);
		
		
		var pAdvSearchAppOwner = this.getComponent('pAdvancedSearch').getComponent('advsearchowner').getComponent('pAdvSearchAppOwner');
		var pAdvSearchAppOwnerDelegate = this.getComponent('pAdvancedSearch').getComponent('advsearchowner').getComponent('pAdvSearchAppOwnerDelegate');
		var pAdvSearchCiOwner = this.getComponent('pAdvancedSearch').getComponent('advsearchowner').getComponent('pAdvSearchCiOwner');
		var pAdvSearchCiOwnerDelegate = this.getComponent('pAdvancedSearch').getComponent('advsearchowner').getComponent('pAdvSearchCiOwnerDelegate');
		
		
		var clAdvSearchAppOwnerAddPerson = pAdvSearchAppOwner.getComponent('clAdvSearchAppOwnerAddPerson');
		var clAdvSearchAppOwnerDelegateAddPerson = pAdvSearchAppOwnerDelegate.getComponent('clAdvSearchAppOwnerDelegateAddPerson');
		var clAdvSearchAppOwnerDelegateAddGroup = pAdvSearchAppOwnerDelegate.getComponent('clAdvSearchAppOwnerDelegateAddGroup');
		
		var clAdvSearchCiOwnerAddPerson = pAdvSearchCiOwner.getComponent('clAdvSearchCiOwnerAddPerson');
		var clAdvSearchCiOwnerDelegateAddPerson = pAdvSearchCiOwnerDelegate.getComponent('clAdvSearchCiOwnerDelegateAddPerson');
		var clAdvSearchCiOwnerDelegateAddGroup = pAdvSearchCiOwnerDelegate.getComponent('clAdvSearchCiOwnerDelegateAddGroup');
		

		
		clAdvSearchAppOwnerAddPerson.on('click', this.onAdvSearchAppOwnerAddPerson, this);
		clAdvSearchAppOwnerDelegateAddPerson.on('click', this.onAdvSearchAppOwnerDelegateAddPerson, this);
		clAdvSearchAppOwnerDelegateAddGroup.on('click', this.onAdvSearchAppOwnerDelegateAddGroup, this);
		
		clAdvSearchCiOwnerAddPerson.on('click', this.onAdvSearchCiOwnerAddPerson, this);
		clAdvSearchCiOwnerDelegateAddPerson.on('click', this.onAdvSearchCiOwnerDelegateAddPerson, this);
		clAdvSearchCiOwnerDelegateAddGroup.on('click', this.onAdvSearchCiOwnerDelegateAddGroup, this);
	},
	
	onCat1Select: function(store, record, options) {
		var cbCat2 = this.getComponent('advsearchplusfieldset').getComponent('advsearchcategory');
		//cbCat2.getStore().filter('applicationCat1Id', record.get('id'));
		var filterData = {
			applicationCat1Id: record.get('id')
		};
		cbCat2.filterByData(filterData);
		cbCat2.clearValue();
	},
	onCat1Change: function(combo, newValue, oldValue) {
		this.isComboValueValid(combo, newValue, oldValue);
		
    	if(newValue.length === 0) {
    		var cbCat2 = this.getComponent('advsearchplusfieldset').getComponent('advsearchcategory');
    		cbCat2.reset();
    		cbCat2.getStore().clearFilter();
    	}
	},
	
	onCat2Change: function(combo, newValue, oldValue) {
		this.isComboValueValid(combo, newValue, oldValue);
	},
	
	onAdvSearchAppOwnerAddPerson: function(link, event) {
		AIR.AirPickerManager.openPersonPicker(
			this.onPersonAdded, this.getComponent('pAdvancedSearch').getComponent('advsearchowner').getComponent('pAdvSearchAppOwner').getComponent('advsearchappowner'), event);
	},
	onAdvSearchAppOwnerDelegateAddPerson: function(link, event) {
		AIR.AirPickerManager.openPersonPicker(
			this.onPersonAdded, this.getComponent('pAdvancedSearch').getComponent('advsearchowner').getComponent('pAdvSearchAppOwnerDelegate').getComponent('advsearchappownerdelegate'), event);
	},
	onAdvSearchAppOwnerDelegateAddGroup: function(link, event) {
		AIR.AirPickerManager.openGroupPicker(
			null, this.getComponent('pAdvancedSearch').getComponent('advsearchowner').getComponent('pAdvSearchAppOwnerDelegate').getComponent('advsearchappownerdelegate'), event, 'none');
	},
	
	
	onAdvSearchCiOwnerAddPerson: function(link, event) {
		AIR.AirPickerManager.openPersonPicker(
			this.onPersonAdded, this.getComponent('pAdvancedSearch').getComponent('advsearchowner').getComponent('pAdvSearchCiOwner').getComponent('advsearchciowner'), event);
	},
	onAdvSearchCiOwnerDelegateAddPerson: function(link, event) {
		AIR.AirPickerManager.openPersonPicker(
			this.onPersonAdded, this.getComponent('pAdvancedSearch').getComponent('advsearchowner').getComponent('pAdvSearchCiOwnerDelegate').getComponent('advsearchcidelegate'), event);
	},
	onAdvSearchCiOwnerDelegateAddGroup: function(link, event) {
		AIR.AirPickerManager.openGroupPicker(
			null, this.getComponent('pAdvancedSearch').getComponent('advsearchowner').getComponent('pAdvSearchCiOwnerDelegate').getComponent('advsearchcidelegate'), event, 'none');
	},
	
	onPersonAdded: function(record, element, hiddenElement) {
		element.setValue(record.data.cwid);
	},
	
	
	updateLabels: function(labels) {
		this.setTitle(labels.advsearchPanelTitle);
		
//		this.getComponent('pAdvancedSearch').getComponent('')
		
		this.setFieldLabel(this.getComponent('pAdvancedSearch').getComponent('advsearchObjectType'), labels.advsearchObjectType);
		this.setFieldLabel(this.getComponent('pAdvancedSearch').getComponent('advsearchdescription'), labels.advsearchdescription);
		
//		this.getComponent('pAdvancedSearch').getComponent('advsearchowner')
		//pAdvancedSearch/advsearchowner
		this.getComponent('pAdvancedSearch').getComponent('advsearchowner').setTitle(labels.advsearchowner);
//		this.setFieldLabel(this.getComponent('pAdvancedSearch').getComponent('advsearchowner').getComponent('advsearchappowner'), labels.advsearchappowner);
//		this.setFieldLabel(this.getComponent('pAdvancedSearch').getComponent('advsearchowner').getComponent('advsearchappownerdelegate'), labels.advsearchappownerdelegate);
//		this.setFieldLabel(this.getComponent('pAdvancedSearch').getComponent('advsearchowner').getComponent('advsearchciowner'), labels.advsearchciowner);
//		this.setFieldLabel(this.getComponent('pAdvancedSearch').getComponent('advsearchowner').getComponent('advsearchcidelegate'), labels.advsearchcidelegate);

		
		this.getComponent('advsearchplusfieldset').setTitle(labels.advsearchplusfieldset);
		this.setFieldLabel(this.getComponent('advsearchplusfieldset').getComponent('advsearchlifecyclestatus'), labels.lifecycleStatus);
		this.setFieldLabel(this.getComponent('advsearchplusfieldset').getComponent('advsearchoperationalStatus'), labels.operationalStatus);
		this.setFieldLabel(this.getComponent('advsearchplusfieldset').getComponent('advsearchcategory'), labels.applicationCat2);
		this.setFieldLabel(this.getComponent('advsearchplusfieldset').getComponent('advsearchprocess'), labels.businessProcess);
	}
});
Ext.reg('AIR.CiAdvancedSearchView', AIR.CiAdvancedSearchView);