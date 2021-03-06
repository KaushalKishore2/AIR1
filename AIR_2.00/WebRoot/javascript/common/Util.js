Util = {
		log: function(message) {
	    	if(window.console)
	            window.console.log(message);
		},
		
		String2XML: function (text){
		    if (window.ActiveXObject){
		      var doc=new ActiveXObject('Microsoft.XMLDOM');
		      doc.async='false';
		      doc.loadXML(text);
		    } else {
		      var parser=new DOMParser();
		      var doc=parser.parseFromString(text,'text/xml');
		    }
		    return doc;
		},
		
		//work around if store.findExact does not get the correct id field value, no matter why
		findStoreKeyValueByAttributeValue: function(store, attrName, attrValue, keyFieldName) {
			if(attrValue.length === 0)
				return '';
			
			for(var i = 0; i < store.data.items.length; i++)
				if(store.data.items[i].data[attrName] == attrValue)
					return store.data.items[i].data[keyFieldName];
			
			return null;
		},
		
		setFieldLabel: function(field, labelText) {
			if(Ext.isIE) {
				field.el.up('.x-form-item', 10, true).child('.x-form-item-label').update(labelText);
			} else {
				field.label.dom.textContent = labelText;//innerHTML textContent
			}
		},
		
		enableCombo: function(combo) {
			combo.setHideTrigger(false);
			
			if(Ext.isIE)
				combo.el.dom.disabled = false;
			else
				combo.enable();
		},
		
		disableCombo: function(combo) {
			combo.setHideTrigger(true);
			
			if(Ext.isIE)
				combo.el.dom.disabled = true;
			else
				combo.disable();
		},
		
		clearCombo: function(combo) {
			combo.reset();
			combo.getStore().removeAll();
		},
		
		setChbGroup: function(chbGroup, yesNoValues) {
			var values = yesNoValues.split(',');
			var data = [];
			
			for(var i = 0; i < values.length; i++)
				data.push(values[i] === 'Y' ? true : false);
			
			
			chbGroup.setValue(data);
		},
		
		getChbYesNoValues: function(chbGroup) {
			var values = chbGroup.getValue();
			var value = '';
			
			for(var i = 0; i < values.length; i++) {
				if(value && value.length > 0)
					value += ',';
				
				value += values[i].checked ? 'Y' : 'N';
			}
			
			return value;
		},
		
		/**
		 * avoid firing the change event from Ext.form.Field.onBlur() originally initiated by Ext.form.DateField.setValue()
		 */
		setDateFieldValue: function(dateField, date) {
			var v = dateField.formatDate(dateField.parseDate(date));
			dateField.value = v;
			dateField.el.dom.value = v;
		},
		
		setFieldValue: function(field, value) {
			field.value = value;
			field.el.dom.value = value;
		},
		
		isCWID: function(value) {
			var isNoCWID = value.indexOf(',') > -1;//value.match(AC.REGEX_CWID) != null;
			if(isNoCWID)
				return -1;
			
			return value.length > 2 && value.length < 6 ? 0 : 1;//value.length > 2 && value.length < 6 && value.match(AC.REGEX_CWID) != null
		},
		
		setObjectProperties: function(source, target) {
			for(var key in source) {
				if(!target[key]) {
					target[key] = source[key];
				} else {
					if(typeof source[key] === 'object')
						Util.setObjectProperties(source[key], target[key]);
				}
			}
		},
		
		getSelectedListViewValuesAsCommaString: function(listView) {
			var records = listView.getSelectedRecords();
			
			var scopes = '';
			for(var i = 0; i < records.length; i++) {
				if(scopes.length > 0)
					scopes += ',';
				
				scopes += records[i].get('id');
			}
			
			return scopes;
		},
		
		isValueInCommaString: function(commaString, value) {
			var arrayString = commaString.split(',');
			for(var i = 0; i < arrayString.length; i++)
				if(arrayString[i] === value)
					return true;
			
			return false;
		},
		
		createMask: function(message, parentEl) {
			return new Ext.LoadMask(parentEl, { msg: message });
		},
		
		isCiId: function(value) {
			var regExp = new RegExp(/^\d+$/);//das ^ und $ Zeichen bewirken die exakte Validierung. Ohne diese liefert string=23v454 liefert die Funktion nicht false!
			var result = value && typeof value === 'number' || (value.length > 0 && value.match(regExp)) ? true : false;
			return result;
		},
		
		getComboRecord: function(combo, sourceAttr, sourceValue) {
			return this.getStoreRecord(combo.getStore(), sourceAttr, sourceValue);
		},
		
		getStoreRecord: function(store, sourceAttr, sourceValue) {
			var record = store.getAt(store.findExact(sourceAttr, sourceValue.toString()));
			
			return record;
		},
		
		getCiTypeRecord: function(ciData) {
			var store = AIR.AirStoreManager.getStoreByName('ciTypeListStore');
			var r = Util.getStoreRecord(store, this.getCiTypeField(ciData), this.getCiTypeFieldId(ciData));
			return r;
		},
		getCiTypeField: function(ciData) {
			return ciData.applicationCat1Id || ciData.ciSubTypeId ? 'ciSubTypeId' : 'ciTypeId';
		},
		getCiTypeFieldId: function(ciData) {
			return ciData.applicationCat1Id || ciData.ciSubTypeId || ciData.tableId;
		},
		
		getCiTypeByTableId: function(tableId) {
			var store = AIR.AirStoreManager.getStoreByName('ciTypeListStore');
			var r = Util.getStoreRecord(store, 'ciTypeId', tableId);
			
			return r;
		},
		
		addClass: function(domElement, cssClass) {
			if(Ext.isIE) {
				if(domElement.className.indexOf(cssClass) == -1)
					domElement.className += ' ' + cssClass;
			} else {
				domElement.addClass(cssClass);
			}
		},
		
		removeClass: function(domElement, cssClass) {
			if(Ext.isIE) {
				if(domElement.className.indexOf(cssClass) > -1) {
					var className = domElement.className;
					className = className.substring(0, className.indexOf(cssClass)) + className.substring(className.indexOf(cssClass) + cssClass.length, className.length);
					
					domElement.className = className.trim();
				}
			} else {
				domElement.removeClass(cssClass);
			}
		},
		
		/**/
		isComboValueValid: function(combo, newValue, oldValue) {
			//wenn blur listener auf combo kann newValue und oldValue undefined sein
	//		if(!newValue || !oldValue)
	//			return true;
			
			var nValue = parseInt(newValue);
			
			//parseInt Bugfix: if newValue is i.e. '111Bayer Group' nValue would successfully converted to int, namely 111. This must must not happen
			var nValueString = newValue.toString();
			var isReallyNoInt = (nValueString.length !== newValue.length) || nValueString === 'NaN';
			//parseInt Bugfix: if newValue is i.e. '111Bayer Group' nValue would successfully converted to int, namely 111. This must must not happen
			
	    	if(isReallyNoInt && isNaN(isReallyNoInt ? newValue : nValue) && newValue.length > 0) {//nValue nValueString
	    		var index = combo.getStore().findExact('name', nValue);
	    		if(index === -1)
	    			Util.restorePreviousValue(combo, oldValue);
		    	
		    	return false;
	    	} else {//if numbers or other nonsense is directly entered in the combo
	    		var index = combo.getStore().findExact('name', newValue);
	    		if(index === -1)
	    			index = combo.getStore().indexOf(combo.getStore().getById(newValue));
	    			//index = combo.getStore().findExact('id', newValue);
	    			//if item is selected it must be searched for the id, otherwise valid values would be treated as invalid
	    		
	    		if(newValue.length > 0 && index === -1) {
	    			Util.restorePreviousValue(combo, oldValue);
	    			return false;
	    		}
	    		
	    		return true;
	    	}
	    	
		},
		restorePreviousValue: function(combo, oldValue) {
			combo.getStore().clearFilter();
			combo.setValue(oldValue);
		},
		
		updateFieldLabel: function(field, label){//changes done by emria
			if (!field.rendered){
				field.fieldLabel = label;
			} else{
				field.label.update(label);
			}
		},
		
		updateLabel: function(field, label){
			if (!field.rendered){
	    		field.fieldLabel = label;
			} else{
				field.update(label);
			}
		}

};
