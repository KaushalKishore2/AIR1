Ext.namespace('AIR');

AIR.DwhEntityGrid = Ext.extend(Ext.grid.GridPanel, {
	complete: true,
	
	initComponent: function() {
	    var columns = AIR.AirConfigFactory.createDwhEntityGridConfig(this.complete);
		this.defaultColumnConfig = columns;
	    
		var colModel = new Ext.grid.ColumnModel(columns);
		var selModel = new Ext.grid.RowSelectionModel({
			singleSelect: true
		});

		var dwhEntityListStore = AIR.AirStoreFactory.createDwhEntityListStore();
		
		
		var pagingBar = new AIR.AirPagingToolbar({
			store: dwhEntityListStore,
			complete: this.complete,
			ownerPrefix: this.ownerPrefix
			
//			pagingParams: this.pagingParams
//			pageSize: 25
		});
		
		
		Ext.apply(this, {
			colModel: colModel,
			selModel: selModel,
			store: dwhEntityListStore,
			
			frame: false,
			border: false,
//			enableColumnHide: false,
			loadMask: false,
			autoScroll: true,
			stripeRows: true,
			stateful: true,
//			plugins: expander,

			
			viewConfig: {
				emptyText: 'Nothing found or no filter set'
			},
			
		    bbar: pagingBar
		});
		
		AIR.DwhEntityGrid.superclass.initComponent.call(this);
		
		if(this.complete) {
			var rbgPageSize = pagingBar.getComponent('rbg' + pagingBar.getId());//items.items[14];//14 12
			rbgPageSize.on('change', this.onPageSizeChange, this);
		}
		
		this.updateHeight();
	},

	
	onPageSizeChange: function (group, radio) {
		if(radio != null) {
			this.pageSize = parseInt(radio.inputValue);
			var pagingBar = this.getBottomToolbar();
			pagingBar.pageSize = this.pageSize;
			
			var params = {
				start: 0,
				limit: this.pageSize
			};
			
			if(this.pagingParams)
				for(var key in this.pagingParams)
					params[key] = this.pagingParams[key];
			
//			params.start = 0;
//			params.limit = this.pageSize;
			
			this.getStore().load({
				params: params,
				callback: function() {
					this.updateHeight();
				}.createDelegate(this)
			});
		}
	},
	
	updateHeight: function() {
		if(!this.pageSize)
			this.pageSize = 20;//25
		
		switch(this.pageSize) {
			case 10:
				this.setHeight(280);
				break;
			case 20://25
				this.setHeight(490);//595
				break;
			case 50:
			case 100:
				this.setHeight(1120);
				break;
		}
	},
	
	setPagingParams: function(pagingParams) {
		this.pagingParams = pagingParams;
		var pagingBar = this.getBottomToolbar();
		pagingBar.pagingParams = pagingParams;
	},
	
	getDefaultColumnConfig: function() {
		return this.defaultColumnConfig;
	}
});
Ext.reg('AIR.DwhEntityGrid', AIR.DwhEntityGrid);