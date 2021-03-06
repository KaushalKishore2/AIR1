Ext.namespace('AIR');

AIR.AirLoginWindow = Ext.extend(Ext.Window, {
	
	initComponent: function() {
		this.index = 0;
			    	
	    Ext.apply(this, {
		    width: 300,
		    
		    closable: false,
		    resizable: false,
		    draggable: false,
		    
		    items: [{
		    	xtype: 'panel',
		    	id: 'pAirLoginWindow1',
		    	//layout: 'form',
		    	hidden:true,
		    	border: false,
		    	plain: true,
//		    	frame: true,
		    	labelWidth: 110,
		    	
	        	bodyStyle: {//siehe ext-all.css :: .x-window-plain .x-window-body
	        		height: 80,
	        		backgroundColor: '#DFE8F6'
	        	},
			    
			    items: [
			    	{
			            xtype: 'label',
			            id: 'tflabel',
			            html: AC.LOGIN_WINDOW_LOAD_TEXT,
			            //style:'align:center'
			            labelStyle: 'font-size:bold;',
			            enableKeyEvents: true,
			            anchor: '100%'
			        }
			    ]
	    	},{
		    	xtype: 'panel',
		    	id: 'pAirLoginWindow',
		    	layout: 'form',
		    	hidden:true,
		    	border: false,
		    	plain: true,
//		    	frame: true,
		    	
			    title: app_shortname + ' Login - ' + app_version,
			    padding: 5,
			    labelWidth: 110,
	        	bodyStyle: {//siehe ext-all.css :: .x-window-plain .x-window-body
	        		height: 90,
	        		backgroundColor: '#DFE8F6'
	        	},
			    
			    items: [{
		            xtype: 'hidden', //changes for CR Kerboros Implementation C0000275214
		            id: 'tfHiddenCwid'
		        },{
		            xtype: 'textfield',
		            id: 'tfCwid',
		            fieldLabel: 'CWID',
		            
		            enableKeyEvents: true,
		            anchor: '100%'
		        }, {
		            xtype: 'textfield',
		            id: 'tfPassword',
		            fieldLabel: 'Intranet Password',
		            inputType: 'password',
		            
		            enableKeyEvents: true,
		            anchor: '100%'
		        },{
		        	xtype: 'container',
		        	id: 'pSpace1',
		        	
		        	height: 15,
		        	hidden: true
		        },{
		        	xtype: 'panel',
		        	id: 'pLoginStatus',
		        	layout: 'card',
		        	activeItem: 0,
		        	border: false,
		        	
		        	bodyStyle: {//siehe ext-all.css :: .x-window-plain .x-window-body
		        		backgroundColor: '#DFE8F6'
		        	},
		        	
		        	items: [{
		        		xtype: 'container',
		        		height: 0,
		        		border: false
		        	}]
		        },{
		        	xtype: 'button',
		        	id: 'bLogin',
		        	text: 'Login',
		        	width: 75,//50
		        	
		            style: {
		            	position: 'absolute',
		            	right: '10px',
		            	marginTop: 10
		            }
		        }]
	    	}],
		    
	        bbar: {
	        	height: 80,
                html: AC.LOGIN_WINDOW_INFO_TEXT
	        }
	    });
	    
	    AIR.AirLoginWindow.superclass.initComponent.call(this);
	    
	    this.addEvents('loginSuccessful');

	    var bLogin = this.getComponent('pAirLoginWindow').getComponent('bLogin');
	    bLogin.on('click', this.onLogin, this);

	    var tfPassword = this.getComponent('pAirLoginWindow').getComponent('tfPassword');
	    var tfCwid = this.getComponent('pAirLoginWindow').getComponent('tfCwid');
	    
	    tfPassword.on('specialkey', this.onEnter, this);
	    tfCwid.on('specialkey', this.onEnter, this);
	    //tfCwid.focus(true, 500);
	    
	    
	    //bei Klaus hat das erste Fokussieren im IE nicht funktioniert. Ein Fixversuch:
		var task = new Ext.util.DelayedTask(function() {
			tfCwid.focus(true, 0);
		}.createDelegate(this));
		task.delay(100);//2000
	},
	
	onEnter: function (field, el) {
        if(el.getKey() === Ext.EventObject.ENTER) {// && login.items.items[0].isValid()
        	var bLogin = this.getComponent('pAirLoginWindow').getComponent('bLogin');
        	bLogin.fireEvent('click', bLogin);
        }
    },
	
	onLogin: function(button, event) {
		
		
		this.disableInputFields();
		
		var cwid = this.getComponent('pAirLoginWindow').getComponent('tfCwid').getValue().toUpperCase();
		var hiddenCwid=this.getComponent('pAirLoginWindow').getComponent('tfHiddenCwid').getValue().toUpperCase(); //changes for CR Kerboros Implementation C0000275214
		console.log("hiddenCwid "+hiddenCwid);
		var password = this.getComponent('pAirLoginWindow').getComponent('tfPassword').getValue();
		
		
		
		this.fireEvent('login', cwid, password,hiddenCwid);//changes for CR Kerboros Implementation C0000275214
	
	},
	
	onLoginFailure: function(response, options) {
		var responseData = Ext.util.JSON.decode(response.responseText);
		
		if(response.status == 200) {
            var alertTitle = 'Login ' + responseData.errors.title + '!';
        	var recipient_admin = 'ITILcenter@bayer.com';

            //{"success":false,"errors":{"title":"failed","reason":"AIR roles are needed to access the application","cwid":"g"}} 
            var alertText = responseData.errors.reason + '<br/> If you need support contact the <a href="mailto:' +
   							recipient_admin + '?subject=' + app_name + ' - User ' + responseData.errors.cwid + ' has problems!">' +
   							app_name + ' administrator</a>.';
            
            Ext.Msg.alert(alertTitle, alertText);
        } else {
        	Ext.Msg.alert('Warning!', 'Authentication server is unreachable: ' + response.responseText);//action.response.responseText
        }

		this.enableInputFields();
	    
		var task = new Ext.util.DelayedTask(function() {
			tfCwid.focus(true, 0);
		}.createDelegate(this));
		task.delay(100);//2000 1000
		this.getComponent('pAirLoginWindow1').setVisible(false);
		this.getComponent('pAirLoginWindow').setVisible(true);
		var toolbar=this.getBottomToolbar();
		toolbar.setVisible(true);
		this.getComponent('pAirLoginWindow').getComponent('tfCwid').reset();
		this.getComponent('pAirLoginWindow').getComponent('tfPassword').reset();
		//changes for CR Kerboros Implementation C0000275214
		this.getComponent('pAirLoginWindow').getComponent('tfHiddenCwid').setValue('-1');
		var hiddenCWID = this.getComponent('pAirLoginWindow').getComponent('tfHiddenCwid').getValue();
		console.log("CWID is hidden "+hiddenCWID);
		
		//changes end for CR Kerboros Implementation C0000275214
	},
	
	setStoreCount: function(storeCount) {
		this.storeCount = storeCount;
	},
	
	onStoreLoaded: function(store, records, options) {
//	    var bLogin = this.getComponent('pAirLoginWindow').getComponent('bLogin');
//	    bLogin.disable();
//
//	    var tfCwid = this.getComponent('pAirLoginWindow').getComponent('tfCwid');
//	    tfCwid.disable();
//	    var tfPassword = this.getComponent('pAirLoginWindow').getComponent('tfPassword');
//	    tfPassword.disable();
	    
		
		var pLoginStatus = this.getComponent('pAirLoginWindow').getComponent('pLoginStatus');
		pLoginStatus.getLayout().setActiveItem(1);
		
		var pSpace1 = this.getComponent('pAirLoginWindow').getComponent('pSpace1');
		pSpace1.setVisible(true);
	
		this.index++;
	},
	
	enableInputFields: function() {
	    var bLogin = this.getComponent('pAirLoginWindow').getComponent('bLogin');
	    bLogin.enable();

	    var tfCwid = this.getComponent('pAirLoginWindow').getComponent('tfCwid');
	    tfCwid.enable();
	    var tfPassword = this.getComponent('pAirLoginWindow').getComponent('tfPassword');
	    tfPassword.enable();
	},
	disableInputFields: function() {
	    var bLogin = this.getComponent('pAirLoginWindow').getComponent('bLogin');
	    bLogin.disable();

	    var tfCwid = this.getComponent('pAirLoginWindow').getComponent('tfCwid');
	    tfCwid.disable();
	    var tfPassword = this.getComponent('pAirLoginWindow').getComponent('tfPassword');
	    tfPassword.disable();
	}
});