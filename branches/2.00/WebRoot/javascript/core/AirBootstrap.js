Ext.namespace('AIR');

AIR.AirBootstrap = Ext.extend(Object, {
	run: function() {
//		if(Ext.isIE) {
//			Ext.Msg.show({
//				title: 'Microsoft Internet Explorer',
//				msg: 'Microsoft Internet Explorer is not supported by AIR. Please use Mozilla Firefox.<br/>Microsoft Internet Explorer wird von AIR nicht unterst&uuml;zt. Bitte benutzen Sie Mozilla Firefox.',
//				buttons: Ext.Msg.OK
//			});
//			return;
//		}
		
		this.init();
		AIR.AirApplicationManager.processLogin(this.initAir.createDelegate(this), this.initLogin.createDelegate(this));
		
//		var airCookie = Ext.state.Manager.get('airCookie');
//		var isLoggedIn = airCookie ? true : false;
//
//		if(isLoggedIn) {
//			this.initAir(airCookie);
//		} else {
//			this.airLoginWindow = new AIR.AirLoginWindow();
//			this.airLoginWindow.on('login', this.onLogin, this);
//			this.airLoginWindow.show();
//		}
	},
	
	initLogin: function() {
		this.airLoginWindow = new AIR.AirLoginWindow();
		this.airLoginWindow.on('login', this.onLogin, this);
		this.airLoginWindow.show();	
	},
	
	init: function() {
		Ext.state.Manager.setProvider(new Ext.state.CookieProvider());
		
		Ext.QuickTips.init();
		Ext.apply(Ext.QuickTips.getQuickTip(), {
			maxWidth: 200,
			minWidth: 100,
			showDelay: 2000,
			trackMouse: true
		});
		
		
	    Ext.BLANK_IMAGE_URL = 'javascript/lib/extjs/resources/images/default/s.gif';//AIR/htdocs/lib/extjs/resources/images/default/s.gif lib/extjs/resources/images/default/s.gif
	    Ext.SSL_SECURE_URL = 'javascript/lib/extjs/resources/images/default/s.gif';//AIR/htdocs/lib/extjs/resources/images/default/s.gif lib/extjs/resources/images/default/s.gif
	    
		Ext.form.Field.prototype.msgTarget = 'side';
		Ext.isSecure = true;
		Ext.Ajax.timeout = 3000000;
		
		
		//disable F5,F6
		if(Ext.isIE) {
			document.attachEvent('onkeydown', AIR.AirApplicationManager.disableInvalidKeys, true);//onkeydown onkeypress
			document.attachEvent('onunload', AIR.AirApplicationManager.disableInvalidKeys, true);
		} else {
			document.addEventListener('keypress', AIR.AirApplicationManager.disableInvalidKeys, true);
			document.addEventListener('unload', AIR.AirApplicationManager.disableInvalidKeys, true);
		}
	},
	
	onLogin: function(cwid, password) {
        var params = {
    		cwid: cwid,
            password: password
        };
    	
        Ext.Ajax.request({
            url: 'jsp/loginAction.jsp',//(*9) /AIR/loginAction.jsp ../loginAction.jsp
            params: params,
            success: this.onLoginSuccessful.createDelegate(this),
            failure: this.airLoginWindow.onLoginFailure.createDelegate(this)
        });
	},
	
	onLoginSuccessful: function(response, options) {
        var responseData = Ext.util.JSON.decode(response.responseText);
        
        if(responseData.success) {
			var tokenCheckUrl = 'jsp/checkTokenAction.jsp';//(*9) /AIR/checkTokenAction.jsp ../checkTokenAction.jsp  ?cwid=' + cwid + '&token=' + token;
			
	        var params = {
	    		cwid: responseData.cwid,
	    		token: responseData.token
	        };
						
			Ext.Ajax.request({
		        url: tokenCheckUrl,
		        params: params,
		        success: this.onTokenCheckSuccessful.createDelegate(this),
		        failure: this.airLoginWindow.onLoginFailure.createDelegate(this)
		    });
        } else {
        	this.airLoginWindow.onLoginFailure(response, options);//handled server error
        }
	},
	
	onTokenCheckSuccessful: function(response, options) {
        var loginData = Ext.util.JSON.decode(response.responseText);
        
        if(loginData.success) {
        	this.createCookie(loginData);
        	this.initAir(loginData);
        } else {
        	this.airLoginWindow.onLoginFailure(response, options);//handled server error
        }
	},
	
	createCookie: function(loginData) {
		var cookieData = {
			cwid: loginData.cwid,
			token: loginData.token,
			username: loginData.username,
			lastlogon: loginData.lastlogon
		};
		
		if(AAM.isAnwendungsEinsprung()) {
			var einsprungData = AAM.getEinsprungData();
			
//			for(var key in einsprungData)
//				cookieData[key] = einsprungData[key];
			
			cookieData.tableId = einsprungData.tableId;
			cookieData.ciId = einsprungData.ciId;
			cookieData.navigation = einsprungData.navigation;
		}
		
		AAM.updateCookie(cookieData);
	},
	
	initAir: function(loginData) {
    	AIR.AirApplicationManager.init(loginData);
    	AIR.AirPickerManager.init();
    	
    	var storeIds = AIR.AirApplicationManager.getStoreIds();
    	var storeCount = AIR.AirApplicationManager.getStoreCount();
    	
        var storeLoader = new AIR.AirStoreLoader();
        storeLoader.on('storesLoaded', this.onStoresLoaded, this);

        if(this.airLoginWindow) {
	        this.airLoginWindow.setStoreCount(storeCount);
	        storeLoader.on('storeLoaded', this.airLoginWindow.onStoreLoaded, this.airLoginWindow);
        }
        
        storeLoader.init(storeIds, storeCount);
        storeLoader.load();
	},
	
	updateTheme: function() {
		Ext.util.CSS.removeStyleSheet('standardTheme');
		Ext.util.CSS.swapStyleSheet('extAllNotheme', 'lib/extjs/resources/css/ext-all-notheme.css');
		Ext.util.CSS.swapStyleSheet('appRepTheme', 'lib/extjs/resources/css/appreptheme.css');
	},
	
    onStoresLoaded: function(storeLoader, storeMap) {        
        AIR.AirStoreManager.setStores(storeMap);
        storeLoader.destroy();
        
		
		/*
		//TEST
		var rolePersonListStore = AIR.AirStoreManager.getStoreByName('rolePersonListStore');
		var roleRecord = new Ext.data.Record({
			id: '4711',
			cwid: 'ERCVA',
			roleName: AC.USER_ROLE_AIR_APPLICATION_LAYER//USER_ROLE_AIR_INFRASTRUCTURE_LAYER USER_ROLE_AIR_ADMINISTRATOR USER_ROLE_DEFAULT
		});
		
		rolePersonListStore.removeAll();
		rolePersonListStore.add(roleRecord);
		rolePersonListStore.commitChanges();
		//TEST
		*/
		
		
        
        //neu
        this.checkItsecUserOptions(this.launchAir.createDelegate(this));
    	//neu
        
        //alt
//    	AIR.AirApplicationManager.setLanguage();
//        AIR.AirAclManager.init();
//        
//        var task = new Ext.util.DelayedTask(function() {
//        	if(this.airLoginWindow)
//        		this.airLoginWindow.close();
//        	
//	    		var startupMask = AIR.AirApplicationManager.getMask('startupMask');
//	    		startupMask.show();
////        		myStartupMask.show();
//	    		
////	        this.updateTheme();
//        }.createDelegate(this));
//        task.delay(500);
//        
//        task = new Ext.util.DelayedTask(this.openUi.createDelegate(this));
//        task.delay(1000);
    },
    
    launchAir: function() {
    	AIR.AirApplicationManager.setLanguage();
        AIR.AirAclManager.init();
                
        var task = new Ext.util.DelayedTask(function() {
        	if(this.airLoginWindow)
        		this.airLoginWindow.close();
        	
	    		var startMask = AAM.getMask(AC.MASK_TYPE_START);
	    		startMask.show();
//        		myStartupMask.show();
	    		
//	        this.updateTheme();
        }.createDelegate(this));
        task.delay(500);
        
        task = new Ext.util.DelayedTask(this.openUi.createDelegate(this));
        task.delay(1000);
    },
    
    
    checkItsecUserOptions: function(callback) {
    	var itsecUserOptionListStore = AIR.AirStoreManager.getStoreByName('itsecUserOptionListStore');
    	
    	if(itsecUserOptionListStore.getCount() == 0) {
    		var loadCallback = function() {
	    		var params = {
	    			cwid: AIR.AirApplicationManager.getCwid()
	    		};
	    		
	    		itsecUserOptionListStore.load({
	    			params: params,
	    			callback: callback
	    		});
    		}.createDelegate(this);
    		
    		this.createDefaultItsecUserOptions(loadCallback);
    	} else {
    		callback();
    	}
    },
    createDefaultItsecUserOptions: function(loadCallback) {
		var params = {
			cwid: AIR.AirApplicationManager.getCwid(),
			token: AIR.AirApplicationManager.getToken()
//			language: 'DE'
		};
		
		var userOptionSaveStore = AIR.AirStoreFactory.createUserOptionSaveStore();
		userOptionSaveStore.load({
			params: params,
			callback: loadCallback
		});
    },
    
	
	openUi: function() {
//		this.airLoginWindow.close();
        
//    	var airMainPanel = new AIR.AirMainPanel();
//    	airMainPanel.on('beforerender', this.onBeforeAirRendered , this);
//    	airMainPanel.on('afterrender', this.onAirRendered , this);//render
//		new AIR.AirViewport(airMainPanel);
		
		this.airMainPanel = new AIR.AirMainPanel();
//		this.airMainPanel.on('render', this.onBeforeAirRendered , this);
		
		
		AIR.AirApplicationManager.afterInit(this.airMainPanel);
		
		
		//move to AirApplicationManager::afterInit ?	ciCreateWizardView
		var lastRenderedView = this.airMainPanel/*.getComponent('pLCiCenterView')*/.getComponent('ciCenterView').getComponent('ciCreateView').getComponent('ciCreatePagesView').getComponent('ciCreateWizardView').getComponent('ciCreateWizardP2').getComponent('ciCreateAppRequiredView').getComponent('fsContactsGPSCW').getComponent('pGPSCOwningBusinessGroup').getComponent('labeltaGPSCOwningBusinessGroup');//this.airMainPanel.getComponent('ciCenterView').getComponent('ciCreateView').getComponent('ciCreatePagesView').getComponent('CiCopyFromView');//this.airMainPanel.getComponent('ciCenterView').getComponent('ciCreateView').getComponent('ciCreatePagesView').getComponent('ciDeleteView');////this.airMainPanel.getComponent('ciCenterView').getComponent('ciCreateView').getComponent('ciCreatePagesView').getComponent('ciCreateWizardPagesView').getComponent('ciCreateWizardPage3').getComponent('wizardCiowner').getComponent('tbWizardciResponsible');//this.airMainPanel.getComponent('ciCenterView').getComponent('ciCreateView').getComponent('ciCreatePagesView').getComponent('CiDeleteView');//this.airMainPanel.getComponent('ciCenterView').getComponent('ciCreateView').getComponent('ciCreatePagesView').getComponent('ciCreateWizardPagesView').getComponent('ciCreateWizardPage3').getComponent('wizardCiowner').getComponent('tbWizardciResponsible');
		lastRenderedView.on('afterrender', this.onAirRendered , this);//render
		
		//ODER das hier ?
//		if(Ext.isIE) {
//			document.attachEvent('onload', this.onAirRendered.createDelegate(this), true);
//		} else {
//			document.addEventListener('load', this.onAirRendered.createDelegate(this), true);
//		}
		
		
		new AIR.AirViewport(this.airMainPanel);

//		AIR.AirApplicationManager.afterInit(this.airMainPanel);//airViewport
		
		
		var delayedTask = new Ext.util.DelayedTask(function() {
			var airTaskManager = new AIR.AirTaskManager();
//			airTaskManager.createCheckAuthStore(AIR.AirApplicationManager.getCwid(), AIR.AirApplicationManager.getToken());//responseData.cwid, responseData.token
			airTaskManager.startDbSessionCheckTask(AIR.AirApplicationManager.getCwid(), AIR.AirApplicationManager.getToken());//orig: keine params
		});
		delayedTask.delay(10000);
	},
	
	onTokenCheckFailure: function(response, options) {
//		obj = Ext.util.JSON.decode(response.responseText);
		
		AAM.logout();
	},
	
	onBeforeAirRendered: function(ct) {
		if(window.console)
			window.console.log('onBeforeAirRendered');
		
//		this.airMainPanel.getEl().mask();
//		var startupMask = AIR.AirApplicationManager.getMask('startupMask');
//		startupMask.show();
	},
	
	onAirRendered: function(lastRenderedView) {//airMainPanel
//		var ciInfoView = this.airMainPanel.getComponent('ciCenterView');
//		var ciTitleView = this.airMainPanel.getComponent('ciTitleView');
//		var navigationV = this.airMainPanel.getComponent('ciNavigationView');
//		var myPlaceHomeView = this.airMainPanel.getComponent('ciCenterView').getComponent('myplaceHomePanel');
//		
//		ciInfoView.update(AC.HELP_ID_INFOTEXT);
//		ciTitleView.update(language);
//		myPlaceHomeView.update();
//		navigationV.update();
//		this.airMainPanel.update();
//		AIR.AirAclManager.init();//here instead of launchAir to be done only at stratup?
		
		AAM.restoreUiState(this.airMainPanel);//schon hier, da n�tig f�r CiEditView.updateLabels() wegen tableId
		
		this.airMainPanel.update();
		this.airMainPanel.updateLabels(AAM.getLabels());
		//performance Verbesserung: erst rendern wenn user zum ersten Mal �ber einem Label anh�lt, anstatt alles nach App Start.
		this.airMainPanel.updateToolTips(AAM.getToolTips());//AIR.AirApplicationManager.getLanguage()
		
		var startMask = AAM.getMask(AC.MASK_TYPE_START);
		startMask.hide();
		
		AAM.restoreUi(this.airMainPanel);
				
		//Angabe in einer StatusBar: wie lange das Rendern und das Store Laden gedauert hat. Und nach sp�testens 5 Sekunden verschwindet Meldung
//		this.fireEvent('airAction', this, 'airReady');//, data
	}
});
Ext.reg('AIR.AirBootstrap', AIR.AirBootstrap);