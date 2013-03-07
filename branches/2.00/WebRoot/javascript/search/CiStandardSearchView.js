Ext.namespace('AIR');

AIR.CiStandardSearchView = Ext.extend(AIR.AirView, {//Ext.Panel
	initComponent: function() {
		Ext.apply(this, {
			border: false,
			layout: 'form',
			
			items: [{
				id: 'pSearch',
				border: false,
				
				layout: 'table',
			    layoutConfig: {
			    	columns: 3//2
			    },
				
	//			layout: 'column',
			    
			    items: [{
		        	xtype: 'textfield',
		        	id: 'tfSearch',
		        	
		        	emptyText: 'Enter CI name or alias...',
		        	width: 300,
//		        	height: 28,
		        	
		        	hideLabel: true,
		        	padding: 5,
		        	
		        	hasSearch: false,
		        	maskRe: /[0-9a-zA-Z%#=\+\-\_\/\\.:*? ]/,
		        	maxLength: 656
		        },{
					xtype: 'button',//commandlink
					id: 'clSearch',
					
//	                img: img_Search_offMouse,
					
		        	cls: 'x-btn-text-icon',
		        	icon: imgcontext+'/search_16x16.png',//'images/search_16x16.png'
		        	text: '',
					
					style: {
						marginLeft: 5//2
//						marginTop: 15
					}
				},{
		        	xtype: 'button',
		        	id: 'bUpdateCiSearchResult',
		        	hidden: true,
		        	
		        	cls: 'x-btn-text-icon',
		        	icon: imgcontext+'/refresh_16x16.png',//'images/refresh_16x16.png',
		        	
		        	text: 'Update',
		        	
					style: {
						marginLeft: 5
					}
		        },{
		            xtype: 'radiogroup',
					id: 'rbgQueryMode',
					hidden: true,
	
		            items: [{
		            	id: 'rbgQueryModeContains', boxLabel: 'Contains', name: 'queryMode', inputValue: 'CONTAINS', checked: true
		            }, {
		            	id: 'rbgQueryModeBeginsWith', boxLabel: 'Begins with', name: 'queryMode', inputValue: 'BEGINS_WITH'
		            }, { 
		            	id: 'rbgQueryModeExact', boxLabel: 'Exact', name: 'queryMode', inputValue: 'EXACT'
		            }]
				}/*, {
					xtype: 'commandlink',//
					id: 'clAdvancedSearch',
					
					text: 'Advanced Search',
	                img: 'lib/extjs/docs/resources/s.gif',
	                cls: 'menuSubLink',
	                
	                style: {
	                	float: 'right'
	                }
				}*/]
	        },{
		    	xtype: 'AIR.CiAdvancedSearchView',
		    	id: 'ciAdvancedSearchView',

		    	hidden: true,
		    	
		    	style: {
		    		marginTop: 15
		    	}
			}]
		});
		
		AIR.CiStandardSearchView.superclass.initComponent.call(this);
	},
	
	reset: function() {
		this.getComponent('pSearch').getComponent('tfSearch').reset();
	},
	
	update: function(data) {
		this.getComponent('ciAdvancedSearchView').update(data);
	},
	
	setData: function(params) {
		this.getComponent('ciAdvancedSearchView').setData(params);
	},
	
	updateLabels: function(labels) {
		var clSearch = this.getComponent('pSearch').getComponent('clSearch');
		clSearch.setText(labels.newSearch);
		
		this.getComponent('pSearch').getComponent('bUpdateCiSearchResult').setText(labels.bUpdateCiSearchResult);
		
		var ciAdvancedSearchView = this.getComponent('ciAdvancedSearchView');
		ciAdvancedSearchView.updateLabels(labels);
	},
	
	updateToolTips: function(toolTips) {
		
	}
});
Ext.reg('AIR.CiStandardSearchView', AIR.CiStandardSearchView);