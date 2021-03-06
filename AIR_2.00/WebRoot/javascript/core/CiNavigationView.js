Ext.namespace('AIR');

AIR.CiNavigationView = Ext.extend(Ext.Panel, {

	initComponent: function() {

		// The Mapping between function and allowed roles
		this.functionRoleMapping = new Array(
				// my place
				new Array('clMyPlace',	 			
						new Array(	
								AC.USER_ROLE_AIR_DEFAULT,
								AC.USER_ROLE_AIR_APPLICATION_LAYER,
								AC.USER_ROLE_AIR_INFRASTRUCTURE_LAYER,
								AC.USER_ROLE_AIR_APPLICATION_MANAGER,
								AC.USER_ROLE_AIR_INFRASTRUCTURE_MANAGER,
								AC.USER_ROLE_AIR_ADMINISTRATOR,
								AC.USER_ROLE_AIR_LOCATION_DATA_MAINTENANCE,
								AC.USER_ROLE_AIR_BUSINESS_ESSENTIAL_EDITOR,
								AC.USER_ROLE_AIR_COMPLIANCE_EDITOR,
								AC.USER_ROLE_AIR_BAR_EDITOR)),

								// search
								new Array('clSearch',
										new Array(	
												AC.USER_ROLE_AIR_DEFAULT,
												AC.USER_ROLE_AIR_APPLICATION_LAYER,
												AC.USER_ROLE_AIR_INFRASTRUCTURE_LAYER,
												AC.USER_ROLE_AIR_APPLICATION_MANAGER,
												AC.USER_ROLE_AIR_INFRASTRUCTURE_MANAGER,
												AC.USER_ROLE_AIR_ADMINISTRATOR,
												AC.USER_ROLE_AIR_LOCATION_DATA_MAINTENANCE,
												AC.USER_ROLE_AIR_BUSINESS_ESSENTIAL_EDITOR,
												AC.USER_ROLE_AIR_BAR_EDITOR)),

												// advanced search
												new Array('clAdvancedSearch',
														new Array(	
																AC.USER_ROLE_AIR_DEFAULT,
																AC.USER_ROLE_AIR_APPLICATION_LAYER,
																AC.USER_ROLE_AIR_INFRASTRUCTURE_LAYER,
																AC.USER_ROLE_AIR_APPLICATION_MANAGER,
																AC.USER_ROLE_AIR_INFRASTRUCTURE_MANAGER,
																AC.USER_ROLE_AIR_ADMINISTRATOR,
																AC.USER_ROLE_AIR_LOCATION_DATA_MAINTENANCE,
																AC.USER_ROLE_AIR_BUSINESS_ESSENTIAL_EDITOR,
																AC.USER_ROLE_AIR_BAR_EDITOR)),

																// ou search
																new Array('clOuSearch',
																		new Array(	
																				AC.USER_ROLE_AIR_DEFAULT,
																				AC.USER_ROLE_AIR_APPLICATION_LAYER,
																				AC.USER_ROLE_AIR_INFRASTRUCTURE_LAYER,
																				AC.USER_ROLE_AIR_APPLICATION_MANAGER,
																				AC.USER_ROLE_AIR_INFRASTRUCTURE_MANAGER,
																				AC.USER_ROLE_AIR_ADMINISTRATOR,
																				AC.USER_ROLE_AIR_LOCATION_DATA_MAINTENANCE,
																				AC.USER_ROLE_AIR_BUSINESS_ESSENTIAL_EDITOR,
																				AC.USER_ROLE_AIR_BAR_EDITOR)),


																				// new and delete
																				new Array('clCiCreate',	 			
																						new Array(	
																								AC.USER_ROLE_AIR_DEFAULT,
																								AC.USER_ROLE_AIR_APPLICATION_LAYER,
																								AC.USER_ROLE_AIR_INFRASTRUCTURE_LAYER,
																								AC.USER_ROLE_AIR_APPLICATION_MANAGER,
																								AC.USER_ROLE_AIR_INFRASTRUCTURE_MANAGER,
																								AC.USER_ROLE_AIR_ADMINISTRATOR,
																								AC.USER_ROLE_AIR_LOCATION_DATA_MAINTENANCE)),

																								new Array('clAssetManagement',
																										new Array(AC.USER_ROLE_AIR_ASSET_MANAGER,
																												AC.USER_ROLE_AIR_ASSET_EDITOR)),

																												new Array('clISMS',//ISMS
																														new Array(AC.USER_ROLE_ISM_MANAGER,
																																AC.USER_ROLE_ISM_EDITOR,
																																AC.USER_ROLE_ISM_READER))


		);

		Ext.apply(this, {
			border: false,
			layout: 'form',//vbox

			autoScroll: true,

			bodyStyle: {//style: kein Effekt
				backgroundColor: AC.AIR_BG_COLOR,
				color: AC.AIR_FONT_COLOR,
				fontFamily: AC.AIR_FONT_TYPE,
				backgroundImage: 'url("' + img_gradientNavi_Info + '")',//img_MenuLeft
				backgroundRepeat: 'repeat-x',//'no-repeat',
				backgroundPosition: 'left top'
			},

			items: [{
				xtype: 'container',
				html: '<i><span id="label_menu_loggedinas">Logged in as</span></i><br><span id="username">-</span><br><span id="label_menu_lastlogin">Last login: </span><span id="lastlogin">-</span><br>',
				id: 'usernamecontainer',
				height: 40,

				style: {
					color: '#ededed',
					fontWeight: 'bold',
					fontSize: '7pt',
					fontFamily: AC.AIR_FONT_TYPE,
					textAlign: 'center',
					backgroundColor: '#12638e'
				}
			}, {
				xtype: 'commandlink',
				id: 'clMyPlace',//myplacemenuitem myplacemenu
				text: 'My Place',
				img: 'images/Transparent.png',//images/Transparent.png
				cls: 'menuMainLink',
				style: {
					marginTop: 5
				}
			}, {
				xtype: 'panel',
				id: 'pMyPlaceMenuItems',
				border: false,
				hidden: true,
				bodyStyle: {
					background: 'transparent'
				},

				items: [{
					xtype: 'commandlink',
					id: 'clMyPlaceMyCIs',
					text: 'My CIs',
					img: 'images/Transparent.png',//images/Transparent.png
					cls: 'menuSubLink'
				},{
					xtype: 'commandlink',
					id: 'clMyPlaceMyCIsDelegate',
					text: 'My CIs (Delegate)',
					img: 'images/Transparent.png',//images/Transparent.png
					cls: 'menuSubLink'
				}]
			},{ 
				xtype: 'container',
				html: '<hr>',
//				height: 12,
				width: 160
			},{
				xtype: 'commandlink',
				id: 'clSearch',//clSearch searchmenuitem
				text: 'Search',
				img: 'images/Transparent.png',//images/Transparent.png
				cls: 'menuMainLink',
				bodyStyle: {
					marginTop: 5
				}
			},{
				xtype: 'commandlink',
				id: 'clAdvancedSearch',
				text: 'Advanced Search',
				img: 'images/Transparent.png',//images/Transparent.png
				cls: 'menuSubLink'
			},{
				xtype: 'commandlink',
				id: 'clOuSearch',
				text: 'Org. Unit Search',
				img: 'images/Transparent.png',//images/Transparent.png
				cls: 'menuSubLink'
			},{ 
				xtype: 'container',
				html: '<hr>',
				width: 160
			},{
				xtype: 'commandlink',
				id: 'clCiCreate',
				text: 'New',
				img: 'images/Transparent.png',//images/Transparent.png
				cls: 'menuMainLink',
				style: {
					marginTop: 5
				}
			},{
				xtype: 'panel',
				id: 'pCreateDeleteMenuItems',
				border: false,
				hidden: true,
				bodyStyle: {
					background: 'transparent'
				},

				items: [{
					xtype: 'commandlink',
					id: 'clCiCreateWizard',
					text: 'Wizard',
					img: 'images/Transparent.png',//images/Transparent.png
					cls: 'menuSubLink'
				},{
					xtype: 'commandlink',
					id: 'clCiCreateCopyFrom',
					text: 'Copy From',
					img: 'images/Transparent.png',//images/Transparent.png
					cls: 'menuSubLink'
				},{
					xtype: 'commandlink',
					id: 'clCiDelete',
					text: 'Delete',
					img: 'images/Transparent.png',//images/Transparent.png
					cls: 'menuSubLink'
				}]
			},{
				xtype: 'panel',
				id: 'pCiDetailsMenuItems',
				border: false,
				hidden: true,
				bodyStyle: {
					background: 'transparent'
				},

				items: [{ 
					xtype: 'container',
					html: '<hr>',
					width: 160
				},{
					xtype: 'commandlink',
					id: 'clCiDetails',
					text: 'Details',
					img: 'images/Transparent.png',//images/Transparent.png
					cls: 'menuMainLink'
				},{
					xtype: 'commandlink',
					id: 'clCiSpecifics',
					text: 'Specifics',
					img: 'images/Transparent.png',//images/Transparent.png
					cls: 'menuSubLink'
				},{
					xtype: 'commandlink',
					id: 'clCiContacts',
					text: 'Contacts',
					img: 'images/Transparent.png',//images/Transparent.png
					cls: 'menuSubLink'
				},{
					xtype: 'commandlink',
					id: 'clCiAgreements',
					text: 'Agreements',
					img: 'images/Transparent.png',//images/Transparent.png
					cls: 'menuSubLink'
				},{
					xtype: 'commandlink',
					id: 'clCiProtection',
					text: 'Protection',
					img: 'images/Transparent.png',//images/Transparent.png
					cls: 'menuSubLink'
				},{
					xtype: 'commandlink',
					id: 'clCiCompliance',
					text: 'Compliance',
					img: 'images/Transparent.png',//images/Transparent.png
					cls: 'menuSubLink'
				},{
					xtype: 'commandlink',
					id: 'clCiLicense',
					text: 'License',
					img: 'images/Transparent.png',//images/Transparent.png
					cls: 'menuSubLink'
				},{
					xtype: 'commandlink',
					id: 'clCiSpecialAttributes',
					text: 'Special Attributes',
					img: 'images/Transparent.png',//images/Transparent.png
					cls: 'menuSubLink'
				},{
					xtype: 'commandlink',
					id: 'clCiNetwork',
					text: 'Network',
					img: 'images/Transparent.png',//images/Transparent.png
					cls: 'menuSubLink'
				},{
					xtype: 'commandlink',
					id: 'clCiConnections',
					text: 'Connections',
					img: 'images/Transparent.png',//images/Transparent.png
					cls: 'menuSubLink'
				},{
					xtype: 'commandlink',
					id: 'clCiSupportStuff',
					text: 'Support Stuff',
					img: 'images/Transparent.png',//images/Transparent.png
					cls: 'menuSubLink'
				},{
					xtype: 'container',
					html: '<hr>',
					width: 120
				},{
					xtype: 'commandlink',
					id: 'clCiHistory',
					text: 'History',
					img: 'images/Transparent.png',//images/Transparent.png
					cls: 'menuSubLink'
				}]
			},{ 
				xtype: 'container',
				html: '<hr>',
				width: 160
			},{
				xtype: 'commandlink',
				id: 'clAssetManagement',
				text: 'Asset Management',
				img: 'images/Transparent.png',//images/Transparent.png
				cls: 'menuMainLink',
				style: {
					marginTop: 5
				}
			},{
				xtype: 'panel',
				id: 'pCreateNewAsset',
				border: false,
				hidden: true,
				bodyStyle: {
					background: 'transparent'
				},
				items: [{
					xtype: 'commandlink',
					id: 'clCiIntangibleAsset',
					text: 'Software',
					img: 'images/Transparent.png',//images/Transparent.png
					cls: 'menuSubLink'
				},{
					xtype: 'commandlink',
					id: 'clCiTangibleAsset',
					text: 'Hardware',
					img: 'images/Transparent.png',//images/Transparent.png
					cls: 'menuSubLink'
				}]
			},{
				xtype: 'panel',
				id: 'pTangibleAsset',
				border: false,
				hidden: true,
				bodyStyle: {
					background: 'transparent'
				},
				items: [{
					xtype: 'commandlink',
					id: 'clCiAssetwithInventory',
					text: 'Asset with Inventory',
					img: 'images/Transparent.png',//images/Transparent.png
					cls: 'menuSubLink'
				},{
					xtype: 'commandlink',
					id: 'clCiAssetwithoutInventory',
					text:'Asset without Inventory',
					img: 'images/Transparent.png',//images/Transparent.png
					cls: 'menuSubLink'
				}]
			},
			{ 
				xtype: 'container',
				html: '<hr>',
				width: 160
			},
			{
				xtype: 'commandlink',
				id: 'clISMS',
				text: 'ISMS',
				img: 'images/Transparent.png',//images/Transparent.png
				cls: 'menuMainLink',
				style: {
					marginTop: 5
				}
			}]
		});


		AIR.CiNavigationView.superclass.initComponent.call(this);

		//ISMS
		this.getComponent('clISMS').on('click',function(){


			var showLoginData = AIR.AirApplicationManager.getUserName() +' (' + AIR.AirApplicationManager.getCwid().toUpperCase() + ')';//username cwid.toUpperCase()
			console.log("Logi name "+showLoginData);
			
			
			var role;
			
			if(AIR.AirApplicationManager.hasRole(AC.USER_ROLE_ISM_MANAGER)){
				role=AC.USER_ROLE_ISM_MANAGER;
			} else if(AIR.AirApplicationManager.hasRole(AC.USER_ROLE_ISM_EDITOR)){
				role=AC.USER_ROLE_ISM_EDITOR;
			} else {
				role=AC.USER_ROLE_ISM_READER;
			}
			
			console.log("role has "+role);
			
			//var link ="http://localhost:9090/ISMS/js/doLogin?";
			var link ;//= "https://air-d1.de.bayer.cnb/ISMS/js/doLogin?";
			 var res = encodeURI(link);
			 console.log("Log URI "+res);
			
			 
			link+="cwid="+AIR.AirApplicationManager.getCwid().toUpperCase();
			link+="&userName="+AIR.AirApplicationManager.getUserName();
			link+="&role="+role;
			console.log("link "+link);
			
			var res = encodeURI(link);
			 console.log("Log URI "+res); 
			 link = 'jsp/redirectISMS.jsp';
			window.open(link,"_blank");

			/*Ext.Ajax.request({
				url: "http://localhost:9090/ISMS/doLogin",
				method: 'GET',
				headers: { 'Content-Type': 'application/json' },
				params:{
					cwid:AIR.AirApplicationManager.getCwid().toUpperCase(),
					userName:AIR.AirApplicationManager.getUserName(),
					roles:['manager']
				},

				success: function (resp, ops) {
					var myWindow = window.open("http://localhost:9090/ISMS/js/index.html", "_blank");
					$(win.document.body).html(response.data) ;
					
					myWindow.document.body.parentNode.innerHTML??=??resp.responseText;
					
					//myWindow.document.write(resp.responseText);
					//myWindow.location.reload(esp.responseText); 
				},
				error: function(error) {
					Ext.Msg.alert('Error', 'An error occurred while opening ISMS application.');
					//console.log(error);
				}
			});
*/

			
		});




		//ISMS


		var links = this.findByType('commandlink');
		for(var i = 0; i < links.length; i++){
			links[i].on('click', this.onMenuSelect , this);
		}

		this.addEvents('beforeNavigation', 'navigation');
	},

	onMenuSelect: function(link, event, options) {
		//add beforeNavigation handlers evtl. returning false to stop navigation

		//evtl. Problem wenn nach Wizard Finish auf new record gelickt wird, dass dann die automatische Weiterleitung zur ersten Wizard Seite nicht geht.
		if(this.previousSelected && link.getId() == this.previousSelected.getId() && (!options || !options.forceNavigation))//(options && !options.forceFireEvent) || 
			return;


		var pMyPlaceMenuItems = this.getComponent('pMyPlaceMenuItems');
		var pCiDetailsMenuItems = this.getComponent('pCiDetailsMenuItems');
		var pCreateDeleteMenuItems = this.getComponent('pCreateDeleteMenuItems');
		var pCreateNewAsset = this.getComponent('pCreateNewAsset');
		var pTangibleAsset=this.getComponent('pTangibleAsset');

		var navigationCallback = function() {
			switch(link.getId()) {//ORIG: no surrounding callback

			case 'clMyPlace':
			case 'clMyPlaceMyCIs':
			case 'clMyPlaceMyCIsDelegate':
				pMyPlaceMenuItems.setVisible(true);
				pCiDetailsMenuItems.setVisible(false);
				pCreateDeleteMenuItems.setVisible(false);
				pCreateNewAsset.setVisible(false);
				pTangibleAsset.setVisible(false);
				break;
			case 'clSearch':
			case 'clAdvancedSearch':
			case 'clOuSearch':
				pCreateDeleteMenuItems.setVisible(false);
				pMyPlaceMenuItems.setVisible(false);
				pCiDetailsMenuItems.setVisible(false);
				pCreateNewAsset.setVisible(false);
				pTangibleAsset.setVisible(false);
				break;
			case 'clCiCreate':
			case 'clCiCreateWizard':
			case 'clCiCreateCopyFrom':
			case 'clCiDelete':
				pCreateDeleteMenuItems.setVisible(true);
				pMyPlaceMenuItems.setVisible(false);
				pCiDetailsMenuItems.setVisible(false);
				pCreateNewAsset.setVisible(false);
				pTangibleAsset.setVisible(false);
				break;
			case 'clAssetManagement' :
				pCreateNewAsset.setVisible(true);
				pCreateDeleteMenuItems.setVisible(false);
				pMyPlaceMenuItems.setVisible(false);
				pCiDetailsMenuItems.setVisible(false);
				pTangibleAsset.setVisible(false);
				break;
			case 'clCiIntangibleAsset' :
				pCreateNewAsset.setVisible(true);
				pCreateDeleteMenuItems.setVisible(false);
				pMyPlaceMenuItems.setVisible(false);
				pCiDetailsMenuItems.setVisible(false);
				pTangibleAsset.setVisible(false);
				break;
			case 'clCiTangibleAsset':
			case 'clCiAssetwithInventory':
			case 'clCiAssetwithoutInventory':
				pCreateNewAsset.setVisible(true);
				pCreateDeleteMenuItems.setVisible(false);
				pMyPlaceMenuItems.setVisible(false);
				pCiDetailsMenuItems.setVisible(false);
				pTangibleAsset.setVisible(true);
				break;

			default:
				break;
			}

			if(!options || !options.skipHistory) {
				AIR.AirApplicationManager.addHistoryItem(link);
			}

			link.updateIcon('images/navmarker_on3.png');//images/navmarker_on3.png
			if(this.previousSelected && link.getId() != this.previousSelected.getId()){
				this.previousSelected.updateIcon('images/Transparent.png');//images/Transparent.png
			}

			this.doLayout();
			this.previousSelected = link;
			this.fireEvent('airAction', this, 'clear');
		}.createDelegate(this);

		if(!options) {
			options = {
					callback: navigationCallback
			};
		} else {
			options.callback = navigationCallback;
		}

		this.fireEvent('navigation', link.getId(), link, options);//navigationCallback
	},

	onExternalNavigation: function(source, uiComponent, target, options) {
		//ISMS

		console.log("target "+target);

		switch(target) {
		case 'clCiDetails':
		case 'clCiSpecifics':
			//nicht ausreichend, da z.B. nach reload kein Menuupdate stattfindet
			if(options && options.isCiCreate) {
				this.updateMenu(options.tableId);//, options.ciSubTypeId

				//REFAC/konsolidieren
				var pCiDetailsMenuItems = this.getComponent('pCiDetailsMenuItems');
				pCiDetailsMenuItems.setVisible(true);
				var link = this.getComponent('pCiDetailsMenuItems').getComponent(target);
				link.fireEvent('click', link, null, options);

				break;
			}
		case 'clCiContacts':
		case 'clCiAgreements':
		case 'clCiProtection':
		case 'clCiCompliance':
		case 'clCiLicense':
		case 'clCiNetwork':	
		case 'clCiConnections':
		case 'clCiSupportStuff':
		case 'clCiHistory':
			var pCiDetailsMenuItems = this.getComponent('pCiDetailsMenuItems');
			pCiDetailsMenuItems.setVisible(true);

			this.updateMenu(AAM.getTableId(), AAM.getCiSubTypeId());

			var link = this.getComponent('pCiDetailsMenuItems').getComponent(target);//clCiDetails

			if(options) {
//				options.reset = true;
				//auskommentiert damit kein reload bei klick auf ci detail menupunkt. Siehe AirHistoryManager.onBackForwardClick
			} else {
				var options = {
						reset: true
				};
			}
			link.fireEvent('click', link, null, options);

			break;
		case 'clSearch':
		case 'clAdvancedSearch':
		case 'clOuSearch':
		case 'clMyPlace':
		case 'clCiCreate':
		case 'clAssetManagement':
			var link = this.getComponent(target);
			link.fireEvent('click', link, null, options);
			break;
		case 'clMyPlaceMyCIs':
		case 'clMyPlaceMyCIsDelegate':
			var link = this.getComponent('pMyPlaceMenuItems').getComponent(target);
			link.fireEvent('click', link, null, options);
			break;
		case 'clCiCreateCopyFrom':
		case 'clCiDelete':
		case 'clCiCreateWizard':
			var link = this.getComponent('pCreateDeleteMenuItems').getComponent(target);
			link.fireEvent('click', link, null, options);
			break;
		case 'clCiIntangibleAsset':
			var link = this.getComponent('pCreateNewAsset').getComponent(target);
			link.fireEvent('click', link, null, options);
			break;
		case 'clCiAssetwithInventory':
		case 'clCiAssetwithoutInventory':
			var link = this.getComponent('pTangibleAsset').getComponent(target);
			link.fireEvent('click', link, null, options);
			break;
			//ISMS
		case 'clISMS':
			var showLoginData = AIR.AirApplicationManager.getUserName() +' (' + AIR.AirApplicationManager.getCwid().toUpperCase() + ')';//username cwid.toUpperCase()
			console.log("Logi name "+showLoginData);
			console.log("role has "+AIR.AirApplicationManager.hasRole(AC.USER_ROLE_ISM_MANAGER));
			var link = this.getComponent(target);
			link.fireEvent('click', link, null, options);
			break;
		default: break;
		}
	},

	onCiSelected: function(source, ciId, target, record) {
		switch(ciId) {
		case -1:
			var pCiDetailsMenuItems = this.getComponent('pCiDetailsMenuItems');
			pCiDetailsMenuItems.setVisible(false);

			break;
		default:
			if(target) {
				this.onExternalNavigation(source, target);
			} else {
				var pCiDetailsMenuItems = this.getComponent('pCiDetailsMenuItems');
				pCiDetailsMenuItems.setVisible(true);

				this.updateMenu(AAM.getTableId(), AAM.getCiSubTypeId());
			}
		break;
		}

		this.doLayout();
	},

	updateOneMenuRight: function(componentName) {

		var show = false;

		var rolePersonListStore = AIR.AirStoreManager.getStoreByName('rolePersonListStore');
		var allowedRoleNames = new Array(30);

		// set the visibility
		for (var gt = 0; gt < this.functionRoleMapping.length ; gt++) {
			if (this.functionRoleMapping[gt][0] === componentName) {

				allowedRoleNames = this.functionRoleMapping[gt][1];

				for (var i = 0; i < allowedRoleNames.length; i++) {
					var rolename = allowedRoleNames[i];
					// Rollen zugreifen					
					rolePersonListStore.each(function(item) {
						var value = item.data.roleName;
						if (rolename == value) {
							show = true;
							return false; // Schleifenabbruch fuer speed
						}
					});
				}
			}
		}

		if (show == false) {
			var comp = this.getComponent(componentName);
			comp.setVisible(false);
		}
	},

	updateMenuRights: function() {
		this.updateOneMenuRight('clMyPlace');

		this.updateOneMenuRight('clSearch');
		this.updateOneMenuRight('clAdvancedSearch');
		this.updateOneMenuRight('clOuSearch');

		this.updateOneMenuRight('clCiCreate');

		this.updateOneMenuRight('clAssetManagement');
		this.updateOneMenuRight('clISMS');//ISMS
	},

	updateMenu: function(tableId, ciSubType) {
		this.updateMenuRights();

		var pCiDetailsMenuItems = this.getComponent('pCiDetailsMenuItems');
		var clCiLicense = pCiDetailsMenuItems.getComponent('clCiLicense');
		var clCiSupportStuff = pCiDetailsMenuItems.getComponent('clCiSupportStuff');
		var clCiNetwork = pCiDetailsMenuItems.getComponent('clCiNetwork');

		switch(tableId) {
		case AC.TABLE_ID_APPLICATION:
			if(ciSubType == AC.APP_CAT1_APPLICATION) {
				clCiLicense.setVisible(true);
				clCiSupportStuff.setVisible(true);
				clCiNetwork.setVisible(false);
				break;
			}
		case AC.TABLE_ID_IT_SYSTEM:
			if(ciSubType == AC.CI_SUB_TYPE_TRANSIENT_SYSTEM) {
				clCiNetwork.setVisible(true);
				break;
			}
		default:
			clCiLicense.setVisible(false);
		clCiSupportStuff.setVisible(false);
		clCiNetwork.setVisible(false);
		break;
		}
	},

	update: function() {
		var userNameC = this.getComponent('usernamecontainer');
		var usernameSpan = userNameC.el.dom.children[2];

		var showLoginData = AIR.AirApplicationManager.getUserName() +' (' + AIR.AirApplicationManager.getCwid().toUpperCase() + ')';//username cwid.toUpperCase()

		var usernameSpan = Ext.get('username');//username usernamecontainer
		usernameSpan.dom.innerHTML = showLoginData;

		var lastloginSpan = Ext.get('lastlogin');
		lastloginSpan.dom.innerHTML = AIR.AirApplicationManager.getLastLogon();

		this.updateMenuRights();
	},

	updateLabels: function(labels) {
		Ext.get('label_menu_loggedinas').dom.innerHTML = labels.label_menu_loggedinas;

		var link = this.getComponent('clMyPlace');
		link.updateText(labels.label_menu_myplacemenuitem);

		link = this.getComponent('pMyPlaceMenuItems').getComponent('clMyPlaceMyCIs');
		link.updateText(labels.label_menu_myplacemycismenuitem);

		link = this.getComponent('pMyPlaceMenuItems').getComponent('clMyPlaceMyCIsDelegate');
		link.updateText(labels.label_menu_myplacemycissubsmenuitem);

		link = this.getComponent('clSearch');
		link.updateText(labels.label_menu_searchmenuitem);

		link = this.getComponent('clAdvancedSearch');
		link.updateText(labels.label_menu_advancedsearchmenuitem);

		link = this.getComponent('clOuSearch');
		link.updateText(labels.lMenuItemOuSearch);

		link = this.getComponent('clCiCreate');
		link.updateText(labels.label_menu_createmenuitem);

		link = this.getComponent('pCreateDeleteMenuItems').getComponent('clCiCreateWizard');
		link.updateText(labels.label_menu_wizardmenuitem);

		link = this.getComponent('pCreateDeleteMenuItems').getComponent('clCiCreateCopyFrom');
		link.updateText(labels.label_menu_copyfrommenuitem);

		link = this.getComponent('pCreateDeleteMenuItems').getComponent('clCiDelete');
		link.updateText(labels.label_menu_delete);

		link = this.getComponent('pCiDetailsMenuItems').getComponent('clCiDetails');
		link.updateText(labels.label_menu_detailsdetails);

		link = this.getComponent('pCiDetailsMenuItems').getComponent('clCiSpecifics');
		link.updateText(labels.label_menu_detailsspecific);

		link = this.getComponent('pCiDetailsMenuItems').getComponent('clCiContacts');
		link.updateText(labels.label_menu_detailscontacts);

		link = this.getComponent('pCiDetailsMenuItems').getComponent('clCiAgreements');
		link.updateText(labels.label_menu_detailsagreements);

		link = this.getComponent('pCiDetailsMenuItems').getComponent('clCiProtection');
		link.updateText(labels.label_menu_detailsprotection);

		link = this.getComponent('pCiDetailsMenuItems').getComponent('clCiCompliance');
		link.updateText(labels.label_menu_detailscompliance);

		link = this.getComponent('clAssetManagement');
		link.updateText(labels.lMenuAssetManagement);

		link = this.getComponent('pCreateNewAsset').getComponent('clCiIntangibleAsset');
		link.updateText(labels.rSoftwarecomponent);

		link = this.getComponent('pCreateNewAsset').getComponent('clCiTangibleAsset');
		link.updateText(labels.rHardwarecomponent);

		link = this.getComponent('pTangibleAsset').getComponent('clCiAssetwithInventory');
		link.updateText(labels.lAssetwithInventory);

		link = this.getComponent('pTangibleAsset').getComponent('clCiAssetwithoutInventory');
		link.updateText(labels.lAssetwithoutInventory);

		link = this.getComponent('pCiDetailsMenuItems').getComponent('clCiLicense');
		link.updateText(labels.label_menu_detailslicense);

		link = this.getComponent('pCiDetailsMenuItems').getComponent('clCiNetwork');
		link.updateText(labels.label_menu_detailsnetwork);

		link = this.getComponent('pCiDetailsMenuItems').getComponent('clCiConnections');
		link.updateText(labels.label_menu_detailsconnections);

		link = this.getComponent('pCiDetailsMenuItems').getComponent('clCiSupportStuff');
		link.updateText(labels.label_menu_detailssupportstuff);

		link = this.getComponent('pCiDetailsMenuItems').getComponent('clCiHistory');
		link.updateText(labels.label_menu_detailshistory);
	}
});
Ext.reg('AIR.CiNavigationView', AIR.CiNavigationView);