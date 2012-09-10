Util = {
	log: function(message) {
    	if(window.console)
            window.console.log(message);
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
	
	isCWID: function(value) {
		var isNoCWID = value.indexOf(',') > -1;//value.match(AC.REGEX_CWID) != null;
		if(isNoCWID)
			return -1;
		
		return value.length > 2 && value.length < 6 ? 0 : 1;//value.length > 2 && value.length < 6 && value.match(AC.REGEX_CWID) != null
	}
	
	/*
	checkComboValueValid: function(combo, newValue, oldValue) {
		//wenn blur listener auf combo kann newValue und oldValue undefined sein
//		if(!newValue || !oldValue)
//			return true;
		
		var nValue = parseInt(newValue);
		
		//parseInt Bugfix: if newValue is i.e. '111Bayer Group' nValue would successfully converted to int, namely 111. This must must not happen
		var nValueString = nValue.toString();
		var isReallyNoInt = nValueString.length !== newValue.length || nValueString === 'NaN';
		//parseInt Bugfix: if newValue is i.e. '111Bayer Group' nValue would successfully converted to int, namely 111. This must must not happen
		
    	if(isReallyNoInt && isNaN(isReallyNoInt ? newValue : nValue) && newValue.length > 0) {//nValue nValueString
    		var index = combo.getStore().findExact('name', nValue);
    		if(index === -1)
    			this.restorePreviousValue(combo, oldValue);
	    	
	    	return false;
    	} else {//if numbers or other nonsense is directly entered in the combo
    		var index = combo.getStore().findExact('name', newValue);
    		if(index === -1)
    			index = combo.getStore().findExact('id', newValue);
    			//if item is selected it must be searched for the id, otherwise valid values would be treated as invalid
    		
    		if(newValue.length > 0 && index === -1) {
    			this.restorePreviousValue(combo, oldValue);
    			return false;
    		}
    		
    		return true;
    	}
    	
	},
	restorePreviousValue: function(combo, oldValue) {
		combo.getStore().clearFilter();
		combo.setValue(oldValue);
	}*/
};