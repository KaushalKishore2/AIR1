Ext.namespace('AIR');

AIR.FilterComboBox = Ext.extend(Ext.form.ComboBox, {
    
    
    
    
    

    
    defaultAutoCreate : {tag: "input", type: "text", size: "24", autocomplete: "off"},
    
    
    
    
    
    
    
    listClass : '',
    
    selectedClass : 'x-combo-selected',
    
    listEmptyText: '',
    
    triggerClass : 'x-form-arrow-trigger',
    
    shadow : 'sides',
    
    listAlign : 'tl-bl?',
    
    maxHeight : 300,
    
    minHeight : 90,
    
    triggerAction : 'query',
    
    minChars : 4,
    
    autoSelect : true,
    
    typeAhead : false,
    
    queryDelay : 500,
    
    pageSize : 0,
    
    selectOnFocus : false,
    
    queryParam : 'query',
    
    loadingText : 'Loading...',
    
    resizable : false,
    
    handleHeight : 8,
    
    allQuery: '',
    
    mode: 'remote',
    
    minListWidth : 70,
    
    forceSelection : false,
    
    typeAheadDelay : 250,
    

    
    lazyInit : true,

    
    clearFilterOnReset : true,//true false

    
    submitValue: undefined,

    

    
    initComponent : function(){
        Ext.form.ComboBox.superclass.initComponent.call(this);
        this.addEvents(
            
            'expand',
            
            'collapse',

            
            'beforeselect',
            
            'select',
            
            'beforequery'
        );
        if(this.transform){
            var s = Ext.getDom(this.transform);
            if(!this.hiddenName){
                this.hiddenName = s.name;
            }
            if(!this.store){
                this.mode = 'local';
                var d = [], opts = s.options;
                for(var i = 0, len = opts.length;i < len; i++){
                    var o = opts[i],
                        value = (o.hasAttribute ? o.hasAttribute('value') : o.getAttributeNode('value').specified) ? o.value : o.text;
                    if(o.selected && Ext.isEmpty(this.value, true)) {
                        this.value = value;
                    }
                    d.push([value, o.text]);
                }
                this.store = new Ext.data.ArrayStore({
                    idIndex: 0,
                    fields: ['value', 'text'],
                    data : d,
                    autoDestroy: true
                });
                this.valueField = 'value';
                this.displayField = 'text';
            }
            s.name = Ext.id(); 
            if(!this.lazyRender){
                this.target = true;
                this.el = Ext.DomHelper.insertBefore(s, this.autoCreate || this.defaultAutoCreate);
                this.render(this.el.parentNode, s);
            }
            Ext.removeNode(s);
        }
        
        else if(this.store){
            this.store = Ext.StoreMgr.lookup(this.store);
            if(this.store.autoCreated){
                this.displayField = this.valueField = 'field1';
                if(!this.store.expandData){
                    this.displayField = 'field2';
                }
                this.mode = 'local';
            }
        }

        this.selectedIndex = -1;
        if(this.mode == 'local'){
            if(!Ext.isDefined(this.initialConfig.queryDelay)){
                this.queryDelay = 10;
            }
            if(!Ext.isDefined(this.initialConfig.minChars)){
                this.minChars = 0;
            }
        }
    },

    
    onRender : function(ct, position){
        if(this.hiddenName && !Ext.isDefined(this.submitValue)){
            this.submitValue = false;
        }
        Ext.form.ComboBox.superclass.onRender.call(this, ct, position);
        if(this.hiddenName){
            this.hiddenField = this.el.insertSibling({tag:'input', type:'hidden', name: this.hiddenName,
                    id: (this.hiddenId || Ext.id())}, 'before', true);

        }
        if(Ext.isGecko){
            this.el.dom.setAttribute('autocomplete', 'off');
        }

        if(!this.lazyInit){
            this.initList();
        }else{
            this.on('focus', this.initList, this, {single: true});
        }
    },

    
    initValue : function(){
        Ext.form.ComboBox.superclass.initValue.call(this);
        if(this.hiddenField){
            this.hiddenField.value =
                Ext.value(Ext.isDefined(this.hiddenValue) ? this.hiddenValue : this.value, '');
        }
    },

    getParentZIndex : function(){
        var zindex;
        if (this.ownerCt){
            this.findParentBy(function(ct){
                zindex = parseInt(ct.getPositionEl().getStyle('z-index'), 10);
                return !!zindex;
            });
        }
        return zindex;
    },
    
    getZIndex : function(listParent){
        listParent = listParent || Ext.getDom(this.getListParent() || Ext.getBody());
        var zindex = parseInt(Ext.fly(listParent).getStyle('z-index'), 10);
        if(!zindex){
            zindex = this.getParentZIndex();
        }
        return (zindex || 12000) + 5;
    },

    
    initList : function(){
        if(!this.list){
            var cls = 'x-combo-list',
                listParent = Ext.getDom(this.getListParent() || Ext.getBody());

            this.list = new Ext.Layer({
                parentEl: listParent,
                shadow: this.shadow,
                cls: [cls, this.listClass].join(' '),
                constrain:false,
                zindex: this.getZIndex(listParent)
            });

            var lw = this.listWidth || Math.max(this.wrap.getWidth(), this.minListWidth);
            this.list.setSize(lw, 0);
            this.list.swallowEvent('mousewheel');
            this.assetHeight = 0;
            if(this.syncFont !== false){
                this.list.setStyle('font-size', this.el.getStyle('font-size'));
            }
            if(this.title){
                this.header = this.list.createChild({cls:cls+'-hd', html: this.title});
                this.assetHeight += this.header.getHeight();
            }

            this.innerList = this.list.createChild({cls:cls+'-inner'});
            this.mon(this.innerList, 'mouseover', this.onViewOver, this);
            this.mon(this.innerList, 'mousemove', this.onViewMove, this);
            this.innerList.setWidth(lw - this.list.getFrameWidth('lr'));

            if(this.pageSize){
                this.footer = this.list.createChild({cls:cls+'-ft'});
                this.pageTb = new Ext.PagingToolbar({
                    store: this.store,
                    pageSize: this.pageSize,
                    renderTo:this.footer
                });
                this.assetHeight += this.footer.getHeight();
            }

            if(!this.tpl){
                
                this.tpl = '<tpl for="."><div class="'+cls+'-item">{' + this.displayField + '}</div></tpl>';
                
            }

            
            this.view = new Ext.DataView({
                applyTo: this.innerList,
                tpl: this.tpl,
                singleSelect: true,
                selectedClass: this.selectedClass,
                itemSelector: this.itemSelector || '.' + cls + '-item',
                emptyText: this.listEmptyText,
                deferEmptyText: false
            });

            this.mon(this.view, {
                containerclick : this.onViewClick,
                click : this.onViewClick,
                scope :this
            });

            this.bindStore(this.store, true);

            if(this.resizable){
                this.resizer = new Ext.Resizable(this.list,  {
                   pinned:true, handles:'se'
                });
                this.mon(this.resizer, 'resize', function(r, w, h){
                    this.maxHeight = h-this.handleHeight-this.list.getFrameWidth('tb')-this.assetHeight;
                    this.listWidth = w;
                    this.innerList.setWidth(w - this.list.getFrameWidth('lr'));
                    this.restrictHeight();
                }, this);

                this[this.pageSize?'footer':'innerList'].setStyle('margin-bottom', this.handleHeight+'px');
            }
        }
    },

    
    getListParent : function() {
        return document.body;
    },

    
    getStore : function(){
        return this.store;
    },

    
    bindStore : function(store, initial){
        if(this.store && !initial){
            if(this.store !== store && this.store.autoDestroy){
                this.store.destroy();
            }else{
                this.store.un('beforeload', this.onBeforeLoad, this);
                this.store.un('load', this.onLoad, this);
                this.store.un('exception', this.collapse, this);
            }
            if(!store){
                this.store = null;
                if(this.view){
                    this.view.bindStore(null);
                }
                if(this.pageTb){
                    this.pageTb.bindStore(null);
                }
            }
        }
        if(store){
            if(!initial) {
                this.lastQuery = null;
                if(this.pageTb) {
                    this.pageTb.bindStore(store);
                }
            }

            this.store = Ext.StoreMgr.lookup(store);
            this.store.on({
                scope: this,
                beforeload: this.onBeforeLoad,
                load: this.onLoad,
                exception: this.collapse
            });

            if(this.view){
                this.view.bindStore(store);
            }
        }
    },

    reset : function(){
        if(this.clearFilterOnReset && this.mode == 'local'){
            this.store.clearFilter();
        }
        Ext.form.ComboBox.superclass.reset.call(this);
    },

    
    initEvents : function(){
        Ext.form.ComboBox.superclass.initEvents.call(this);

        
        this.keyNav = new Ext.KeyNav(this.el, {
            "up" : function(e){
                this.inKeyMode = true;
                this.selectPrev();
            },

            "down" : function(e){
                if(!this.isExpanded()){
                    this.onTriggerClick();
                }else{
                    this.inKeyMode = true;
                    this.selectNext();
                }
            },

            "enter" : function(e){
                this.onViewClick();
            },

            "esc" : function(e){
                this.collapse();
            },

            "tab" : function(e){
                if (this.forceSelection === true) {
                    this.collapse();
                } else {
                    this.onViewClick(false);
                }
                return true;
            },

            scope : this,

            doRelay : function(e, h, hname){
                if(hname == 'down' || this.scope.isExpanded()){
                    
                    var relay = Ext.KeyNav.prototype.doRelay.apply(this, arguments);
                    if(!Ext.isIE && Ext.EventManager.useKeydown){
                        
                        this.scope.fireKey(e);
                    }
                    return relay;
                }
                return true;
            },

            forceKeyDown : true,
            defaultEventAction: 'stopEvent'
        });
        this.queryDelay = Math.max(this.queryDelay || 10,
                this.mode == 'local' ? 10 : 250);
        this.dqTask = new Ext.util.DelayedTask(this.initQuery, this);
        if(this.typeAhead){
            this.taTask = new Ext.util.DelayedTask(this.onTypeAhead, this);
        }
        if(!this.enableKeyEvents){
            this.mon(this.el, 'keyup', this.onKeyUp, this);
        }
    },


    
    onDestroy : function(){
        if (this.dqTask){
            this.dqTask.cancel();
            this.dqTask = null;
        }
        this.bindStore(null);
        Ext.destroy(
            this.resizer,
            this.view,
            this.pageTb,
            this.list
        );
        Ext.destroyMembers(this, 'hiddenField');
        Ext.form.ComboBox.superclass.onDestroy.call(this);
    },

    
    fireKey : function(e){
        if (!this.isExpanded()) {
            Ext.form.ComboBox.superclass.fireKey.call(this, e);
        }
    },

    
    onResize : function(w, h){
        Ext.form.ComboBox.superclass.onResize.apply(this, arguments);
        if(!isNaN(w) && this.isVisible() && this.list){
            this.doResize(w);
        }else{
            this.bufferSize = w;
        }
    },

    doResize: function(w){
        if(!Ext.isDefined(this.listWidth)){
            var lw = Math.max(w, this.minListWidth);
            this.list.setWidth(lw);
            this.innerList.setWidth(lw - this.list.getFrameWidth('lr'));
        }
    },

    
    onEnable : function(){
        Ext.form.ComboBox.superclass.onEnable.apply(this, arguments);
        if(this.hiddenField){
            this.hiddenField.disabled = false;
        }
    },

    
    onDisable : function(){
        Ext.form.ComboBox.superclass.onDisable.apply(this, arguments);
        if(this.hiddenField){
            this.hiddenField.disabled = true;
        }
    },

    
    onBeforeLoad : function(){
        if(!this.hasFocus){
            return;
        }
        this.innerList.update(this.loadingText ?
               '<div class="loading-indicator">'+this.loadingText+'</div>' : '');
        this.restrictHeight();
        this.selectedIndex = -1;
    },

    
    onLoad : function(){
        if(!this.hasFocus){
            return;
        }
        if(this.store.getCount() > 0 || this.listEmptyText){
            this.expand();
            this.restrictHeight();
            if(this.lastQuery == this.allQuery){
                if(this.editable){
                    this.el.dom.select();
                }

                if(this.autoSelect !== false && !this.selectByValue(this.value, true)){
                    this.select(0, true);
                }
            }else{
                if(this.autoSelect !== false){
                    this.selectNext();
                }
                if(this.typeAhead && this.lastKey != Ext.EventObject.BACKSPACE && this.lastKey != Ext.EventObject.DELETE){
                    this.taTask.delay(this.typeAheadDelay);
                }
            }
        }else{
            this.collapse();
        }

    },

    
    onTypeAhead : function(){
        if(this.store.getCount() > 0){
            var r = this.store.getAt(0);
            var newValue = r.data[this.displayField];
            var len = newValue.length;
            var selStart = this.getRawValue().length;
            if(selStart != len){
                this.setRawValue(newValue);
                this.selectText(selStart, newValue.length);
            }
        }
    },

    
    assertValue : function(){
        var val = this.getRawValue(),
            rec;

        if(this.valueField && Ext.isDefined(this.value)){
            rec = this.findRecord(this.valueField, this.value);
        }
        if(!rec || rec.get(this.displayField) != val){
            rec = this.findRecord(this.displayField, val);
        }
        if(!rec && this.forceSelection){
            if(val.length > 0 && val != this.emptyText){
                this.el.dom.value = Ext.value(this.lastSelectionText, '');
                this.applyEmptyText();
            }else{
                this.clearValue();
            }
        }else{
            if(rec && this.valueField){
                
                
                
                if (this.value == val){
                    return;
                }
                val = rec.get(this.valueField || this.displayField);
            }
            this.setValue(val);
        }
    },

    
    onSelect : function(record, index){
        if(this.fireEvent('beforeselect', this, record, index) !== false){
            this.setValue(record.data[this.valueField || this.displayField]);
            this.collapse();
            this.fireEvent('select', this, record, index);
        }
    },

    
    getName: function(){
        var hf = this.hiddenField;
        return hf && hf.name ? hf.name : this.hiddenName || Ext.form.ComboBox.superclass.getName.call(this);
    },

    
    getValue : function(){
        if(this.valueField){
            return Ext.isDefined(this.value) ? this.value : '';
        }else{
            return Ext.form.ComboBox.superclass.getValue.call(this);
        }
    },

    
    clearValue : function(){
        if(this.hiddenField){
            this.hiddenField.value = '';
        }
        this.setRawValue('');
        this.lastSelectionText = '';
        this.applyEmptyText();
        this.value = '';
    },

    
    setValue : function(v){
        var text = v;
        if(this.valueField){
            var r = this.findRecord(this.valueField, v);
            if(r){
                text = r.data[this.displayField];
            }else if(Ext.isDefined(this.valueNotFoundText)){
                text = this.valueNotFoundText;
            }
        }
        this.lastSelectionText = text;
        if(this.hiddenField){
            this.hiddenField.value = Ext.value(v, '');
        }
        Ext.form.ComboBox.superclass.setValue.call(this, text);
        this.value = v;
        return this;
    },

    
    findRecord : function(prop, value){
        var record;
        if(this.store.getCount() > 0){
            this.store.each(function(r){
                if(r.data[prop] == value){
                    record = r;
                    return false;
                }
            });
        }
        return record;
    },

    
    onViewMove : function(e, t){
        this.inKeyMode = false;
    },

    
    onViewOver : function(e, t){
        if(this.inKeyMode){ 
            return;
        }
        var item = this.view.findItemFromChild(t);
        if(item){
            var index = this.view.indexOf(item);
            this.select(index, false);
        }
    },

    
    onViewClick : function(doFocus){
        var index = this.view.getSelectedIndexes()[0],
            s = this.store,
            r = s.getAt(index);
        if(r){
            this.onSelect(r, index);
        }else {
            this.collapse();
        }
        if(doFocus !== false){
            this.el.focus();
        }
    },


    
    restrictHeight : function(){
        this.innerList.dom.style.height = '';
        var inner = this.innerList.dom,
            pad = this.list.getFrameWidth('tb') + (this.resizable ? this.handleHeight : 0) + this.assetHeight,
            h = Math.max(inner.clientHeight, inner.offsetHeight, inner.scrollHeight),
            ha = this.getPosition()[1]-Ext.getBody().getScroll().top,
            hb = Ext.lib.Dom.getViewHeight()-ha-this.getSize().height,
            space = Math.max(ha, hb, this.minHeight || 0)-this.list.shadowOffset-pad-5;

        h = Math.min(h, space, this.maxHeight);

        this.innerList.setHeight(h);
        this.list.beginUpdate();
        this.list.setHeight(h+pad);
        this.list.alignTo.apply(this.list, [this.el].concat(this.listAlign));
        this.list.endUpdate();
    },

    
    isExpanded : function(){
        return this.list && this.list.isVisible();
    },

    
    selectByValue : function(v, scrollIntoView){
        if(!Ext.isEmpty(v, true)){
            var r = this.findRecord(this.valueField || this.displayField, v);
            if(r){
                this.select(this.store.indexOf(r), scrollIntoView);
                return true;
            }
        }
        return false;
    },

    
    select : function(index, scrollIntoView){
        this.selectedIndex = index;
        this.view.select(index);
        if(scrollIntoView !== false){
            var el = this.view.getNode(index);
            if(el){
                this.innerList.scrollChildIntoView(el, false);
            }
        }

    },

    
    selectNext : function(){
        var ct = this.store.getCount();
        if(ct > 0){
            if(this.selectedIndex == -1){
                this.select(0);
            }else if(this.selectedIndex < ct-1){
                this.select(this.selectedIndex+1);
            }
        }
    },

    
    selectPrev : function(){
        var ct = this.store.getCount();
        if(ct > 0){
            if(this.selectedIndex == -1){
                this.select(0);
            }else if(this.selectedIndex !== 0){
                this.select(this.selectedIndex-1);
            }
        }
    },

    
    onKeyUp : function(e){
        var k = e.getKey();
        if(this.editable !== false && this.readOnly !== true && (k == e.BACKSPACE || !e.isSpecialKey())){

            this.lastKey = k;
            this.dqTask.delay(this.queryDelay);
        }
        Ext.form.ComboBox.superclass.onKeyUp.call(this, e);
    },

    
    validateBlur : function(){
        return !this.list || !this.list.isVisible();
    },

    
    initQuery : function(){
        this.doQuery(this.getRawValue());
    },

    
    beforeBlur : function(){
        this.assertValue();
    },

    
    postBlur  : function(){
        Ext.form.ComboBox.superclass.postBlur.call(this);
        this.collapse();
        this.inKeyMode = false;
    },

    
    doQuery : function(q, forceAll){
        q = Ext.isEmpty(q) ? '' : q;
        var qe = {
            query: q,
            forceAll: forceAll,
            combo: this,
            cancel:false
        };
        if(this.fireEvent('beforequery', qe)===false || qe.cancel){
            return false;
        }
        q = qe.query;
        forceAll = qe.forceAll;
        if(forceAll === true || (q.length >= this.minChars)){
            if(this.lastQuery !== q){
                this.lastQuery = q;
                if(this.mode == 'local'){// || this.isFilterLocal
                    this.selectedIndex = -1;
                    if(forceAll){
                        //this.store.clearFilter();
						this.filterByData();
                    }else{
						this.filterByDataAndQuery(this.displayField, q);
                        //this.store.filter(this.displayField, q);
                    }
                    this.onLoad();
                }else{
                    this.store.baseParams[this.queryParam] = q;
                    this.store.load({
                        params: this.getParams(q)
                    });
                    this.expand();
                }
            }else{
                this.selectedIndex = -1;
                this.onLoad();
            }
        }
    },

    
    getParams : function(q){
        var params = {},
            paramNames = this.store.paramNames;
        if(this.pageSize){
            params[paramNames.start] = 0;
            params[paramNames.limit] = this.pageSize;
        }
        return params;
    },

    
    collapse : function(){
        if(!this.isExpanded()){
            return;
        }
        this.list.hide();
        Ext.getDoc().un('mousewheel', this.collapseIf, this);
        Ext.getDoc().un('mousedown', this.collapseIf, this);
        this.fireEvent('collapse', this);
    },

    
    collapseIf : function(e){
        if(!this.isDestroyed && !e.within(this.wrap) && !e.within(this.list)){
            this.collapse();
        }
    },

    
    expand : function(){
        if(this.isExpanded() || !this.hasFocus){
            return;
        }

        if(this.title || this.pageSize){
            this.assetHeight = 0;
            if(this.title){
                this.assetHeight += this.header.getHeight();
            }
            if(this.pageSize){
                this.assetHeight += this.footer.getHeight();
            }
        }

        if(this.bufferSize){
            this.doResize(this.bufferSize);
            delete this.bufferSize;
        }
        this.list.alignTo.apply(this.list, [this.el].concat(this.listAlign));

        
        this.list.setZIndex(this.getZIndex());
        this.list.show();
        if(Ext.isGecko2){
            this.innerList.setOverflow('auto'); 
        }
        this.mon(Ext.getDoc(), {
            scope: this,
            mousewheel: this.collapseIf,
            mousedown: this.collapseIf
        });
        this.fireEvent('expand', this);
    },

    
    
    
    onTriggerClick : function(){
        if(this.readOnly || this.disabled){
            return;
        }
        if(this.isExpanded()){
            this.collapse();
            this.el.focus();
        }else {
            this.onFocus({});
            if(this.triggerAction == 'all') {
                this.doQuery(this.allQuery, true);
            } else {
                this.doQuery(this.getRawValue());
            }
            this.el.focus();
        }
    },

    
    
	filterByData: function(filterData) {
		if(filterData)
			this.filterData = filterData;
			
		var filterFn = function(record) {
			for(var key in this.filterData)
				if(record.get(key) != this.filterData[key])//!==
					return false;
			
			return true;
		}.createDelegate(this);
		
		this.getStore().filterBy(filterFn);
	},
    
	filterByDataAndQuery: function(displayField, query) {
		if(query.length === 0) {
			this.filterByData();
		} else {
			var filterFn = function(record) {
				for(var key in this.filterData)
					if(record.get(key) != this.filterData[key])//!==
						return false;
				
				return record.get(displayField).toUpperCase().substr(0, query.length) == query.toUpperCase();// startsWith(query.toUpperCase())
			}.createDelegate(this);
			
			this.getStore().filterBy(filterFn);
		}
	},
	
	
	
	
	
	filterByDataValueList: function(filterData) {
		if(filterData)
			this.filterData = filterData;
			
		var filterFn = function(record) {
			for(var key in this.filterData) {
				var value = this.filterData[key];
				if(Array.isArray(value)) {
					for(var i = 0; i < value.length; i++)
						if(value[i] == record.get(key))
							return true;
					return false;
				
//				if(value.indexOf(',') > -1) {
//					var valueList = value.split(',');
//					for(var i = 0; i < valueList.length; i++)
//						if(valueList[i] == record.get(key))
//							return true;
//					return false;
				} else {
					if(record.get(key) != data)//!==
						return false;
				}
			}
			return true;
		}.createDelegate(this);
		
		this.getStore().filterBy(filterFn);
	}
	
	
//	filterCiTypes: function(data) {
//		this.reset();
//		var r = Util.getComboRecord(this, 'ciTypeId', parseInt(data.ciTypeId));//tableId
//		var ciTypeSource = r.get('text');
//		
//		var records = this.getStore().getRange();
//		var matches = [];
//		for(var i = 0; i < records.length; i++) {
//			var ciTypeDest = records[i].get('text');
//			if(AAM.isConnectionCiTypeAllowed(ciTypeSource, ciTypeDest))
//				matches.push(ciTypeDest);
//		}
//		
//		var filterFn = function(record) {
//			/*var ciTypeDest = record.get('text');
//			var b = AAM.isConnectionCiTypeAllowed(ciTypeSource, ciTypeDest);
//			Util.log(b + ': '+ ciTypeSource + ' '+ciTypeDest);
//			return b;//AAM.isConnectionCiTypeAllowed(ciTypeSource, ciTypeDest);*/
//			
//			for(var i = 0; i < matches.length; i++)
//				if(record.get('text') == matches[i])
//					return true;
//			return false;
//		};
//		
//		this.getStore().filterBy(filterFn);
//	}
});
Ext.reg('filterCombo', AIR.FilterComboBox);