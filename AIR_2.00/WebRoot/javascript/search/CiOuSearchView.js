Ext.namespace('AIR');

AIR.CiOuSearchView = Ext.extend(AIR.AirView, {
	initComponent: function() {
		Ext.apply(this, {
			title: 'Org. Unit Search',
	    	layout: 'form',//form fit
	    	border: false,
	        
	    	padding: 10,
	    				    
			items: [{
				xtype: 'combo',
				id: 'cbOuSearchObjectType',
			    store: AIR.AirStoreManager.getStoreByName('applicationCat1ListStore'),
				
			    fieldLabel: 'Type',
			    valueField: 'id',
		        displayField: 'english',
//		        editable: false,
		        
		        typeAhead: true,
		        forceSelection: true,
		        autoSelect: false,
		        
		        triggerAction: 'all',
		        lazyRender: true,
		        lazyInit: false,
		        mode: 'local',
		        
		        width: 230,
		        style: {
		        	marginBottom: 10
		        }
			},{
				xtype: 'container',
				id: 'pOrgUnit',
				
				layout: 'hbox',//column toolbar hbox
//						width: 500,
				
				items: [{
					xtype: 'panel',
					id: 'pOrgUnit1',
					border: false,
					
					layout: 'column',//column hbox
					width: 400,
					
					items: [{
						xtype: 'label',
						id: 'labeltfOrgUnit',
						text: 'Org. Unit',
						
						width: 105,
						style: {
							fontSize: 12,
							marginTop: 3
						}
					},{
						xtype: 'textfield',
						width: 230,
						id: 'tfOrgUnit',
//						readOnly: true,
//						editable: false
						disabled: true
					},{
						xtype: 'hidden',
						id: 'tfOrgUnitHidden'
					},{
						xtype: 'commandlink',
						id: 'clOrgUnitAdd',
						img: img_AddGroup
					},{
						xtype: 'commandlink',
						id: 'clOrgUnitRemove',
						img: img_RemovePerson
					}]
					
//						margins: '40 0 0 0'
			    },{
					xtype: 'panel',
					id: 'pOrgUnit2',
					border: false,
					
					layout: 'form',//form column vbox
					labelWidth: 140,
					
					items: [{
						xtype: 'radiogroup',
						id: 'rbgOUSearchQueryMode',
						
//						labelWidth: 120,
						fieldLabel: 'Incl. Sub Org. Units',
						//hideLabel: true,//sonst 100px Abstand zum linken Element wenn kein Label gewollt ist!
						
						columns: 2,

						items: [{
							id: 'rbgOUSearchBeginsWith', boxLabel: 'Yes', name: 'rbgOUSearchQueryMode', inputValue: 'START', checked: true, width: 50//BEGINS_WITH
						}, { 
							id: 'rbgOUSearchExact', boxLabel: 'No', name: 'rbgOUSearchQueryMode', inputValue: 'EXACT', width: 50
						}]
					},{
						xtype: 'container',
						html: '<hr>',
						border: false
					},{
						xtype: 'radiogroup',//checkboxgroup
						id: 'rbgOUSearchOwnerType',
						
//						labelWidth: 120,
						fieldLabel: 'Search Org. Unit as',//Owner/Manager
						//hideLabel: true,
						
						columns: 1,
						width: 130,

						items: [{
							id: 'rbgOUSearchOwnerTypeAppOwner', boxLabel: 'Application Owner', name: 'rbgOUSearchOwnerType', inputValue: 'APP'
						}, { 
							id: 'rbgOUSearchOwnerTypeAppMgr', boxLabel: 'Application Manager', name: 'rbgOUSearchOwnerType', inputValue: 'CI'
						}, { 
							id: 'rbgOUSearchOwnerTypeBoth', boxLabel: 'Both', name: 'rbgOUSearchOwnerType', inputValue: 'ALL', checked: true
						}]
						
						/*style: {
							marginTop: 20
						}*/
						//margins: '20 0 0 0'
					}]
			    }]
			},{
				xtype: 'panel',
				id: 'pOuSearch',
				layout: 'column',
				border: false,
				
				items: [{
					xtype: 'button',//commandlink
					id: 'bOuSearch',
					
	//                img: img_Search_offMouse,
					
		        	cls: 'x-btn-text-icon',
		        	icon: 'images/search_16x16.png',
		        	text: ''			
				},{
		        	xtype: 'button',
		        	id: 'bUpdateOuSearch',
		        	hidden: true,
		        	text: '',
		        	
		        	cls: 'x-btn-text-icon',
		        	icon: 'images/refresh_16x16.png',
		        	
					style: {
						marginLeft: 5
					}
				}]
	        }/*, {
		    	xtype: 'commandlink',
				id: 'clOuSearch',
				
                img: img_Search_offMouse
			}*/]
		});
		
		AIR.CiOuSearchView.superclass.initComponent.call(this);
		this.getComponent('cbOuSearchObjectType').getStore().sort('english', 'ASC');

	},
	
	reset: function() {
		var cbOuSearchObjectType = this.getComponent('cbOuSearchObjectType');
		cbOuSearchObjectType.reset();
		
		var tfOrgUnit = this.getComponent('pOrgUnit').getComponent('pOrgUnit1').getComponent('tfOrgUnit');
		tfOrgUnit.reset();
		
		var rbgOUSearchQueryMode = this.getComponent('pOrgUnit').getComponent('pOrgUnit2').getComponent('rbgOUSearchQueryMode');
		var rbgOUSearchOwnerType = this.getComponent('pOrgUnit').getComponent('pOrgUnit2').getComponent('rbgOUSearchOwnerType');

		rbgOUSearchQueryMode.setValue('START');
		rbgOUSearchOwnerType.setValue('ALL');
	},
	
	update: function(params) {
		var cbOuSearchObjectType = this.getComponent('cbOuSearchObjectType');
		if(params.ouCiType.length > 0)
			cbOuSearchObjectType.setValue(cbOuSearchObjectType.getStore().getAt(cbOuSearchObjectType.getStore().findExact('english', params.ouCiType)).get('id'));//text
//		if(params.ciTypeId)
//			cbOuSearchObjectType.setValue(params.ciTypeId);
		else
			cbOuSearchObjectType.reset();
		
		var tfOrgUnit = this.getComponent('pOrgUnit').getComponent('pOrgUnit1').getComponent('tfOrgUnit');
		tfOrgUnit.setValue(params.ouUnit);
		
		
		var rbgOUSearchQueryMode = this.getComponent('pOrgUnit').getComponent('pOrgUnit2').getComponent('rbgOUSearchQueryMode');
		var rbgOUSearchOwnerType = this.getComponent('pOrgUnit').getComponent('pOrgUnit2').getComponent('rbgOUSearchOwnerType');

		rbgOUSearchQueryMode.setValue(params.ouQueryMode);
		rbgOUSearchOwnerType.setValue(params.ciOwnerType);
	},
	
	updateLabels: function(labels) {
		this.setTitle(labels.CiOuSearchViewTitle);
		
		var cbOuSearchObjectType = this.getComponent('cbOuSearchObjectType');
		var lOrgUnit = this.getComponent('pOrgUnit').getComponent('pOrgUnit1').getComponent('labeltfOrgUnit');
		
		var rbgOUSearchQueryMode = this.getComponent('pOrgUnit').getComponent('pOrgUnit2').getComponent('rbgOUSearchQueryMode');
		var rbgOUSearchOwnerType = this.getComponent('pOrgUnit').getComponent('pOrgUnit2').getComponent('rbgOUSearchOwnerType');
		
		this.setFieldLabel(cbOuSearchObjectType, labels.objectType);
		lOrgUnit.setText(labels.CiOuSearchViewOrgUnit);
		
		this.setFieldLabel(rbgOUSearchQueryMode, labels.CiOuSearchViewOUSearchQueryMode);
		this.setBoxLabel(rbgOUSearchQueryMode.items.items[0], labels.general_yes);
		this.setBoxLabel(rbgOUSearchQueryMode.items.items[1], labels.general_no);
		
		this.setFieldLabel(rbgOUSearchOwnerType, labels.CiOuSearchViewOUSearchOwnerType);
//		this.setBoxLabel(rbgOUSearchOwnerType.items.items[0], labels.applicationOwner);
//		this.setBoxLabel(rbgOUSearchOwnerType.items.items[1], labels.applicationManager);
		this.setBoxLabel(rbgOUSearchOwnerType.items.items[2], labels.both);
		
		
		this.getComponent('pOuSearch').getComponent('bOuSearch').setText(labels.newSearch);
		this.getComponent('pOuSearch').getComponent('bUpdateOuSearch').setText(labels.bUpdateCiSearchResult);
	},
	
	updateToolTips: function(toolTips) {
		
	}
});
Ext.reg('AIR.CiOuSearchView', AIR.CiOuSearchView);