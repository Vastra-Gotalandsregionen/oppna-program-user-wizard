AUI().add('vgr-user-wizard',function(A) {
	var Lang = A.Lang,
		isArray = Lang.isArray,
		isFunction = Lang.isFunction,
		isNull = Lang.isNull,
		isObject = Lang.isObject,
		isString = Lang.isString,
		isUndefined = Lang.isUndefined,
		getClassName = A.ClassNameManager.getClassName,
		concat = function() {
			return Array.prototype.slice.call(arguments).join(SPACE);
		},
		
		CSS_USER_WIZARD_DIALOG = 'user-wizard-dialog',
		CSS_SHOW_CONTROL_CHECKED = 'checked',
		
		DIALOG_HEIGHT = 'dialogHeight',
		DIALOG_WIDTH = 'dialogWidth',
		
		HREF = 'href',
		NAME = 'vgr-user-wizard',
		NS = 'vgr-user-wizard',

		SAVE_HIDE_WIZARD_LOGGED_IN_SESSION = 'saveHideWizardLoggedInSession',		
		START_PAGE_URL_FRAGMENT = 'startPageUrlFragment',
		WIZARD_CONTENT_URL = 'wizardContentURL'
	;
	
	var	TPL_FLOFF 	= '<div">{someText}</div>'
	;
	
	var TPL_DIALOG_CONTENT = '' +
		'<div class="user-wizard-content-wrap">' +
			'<div class="user-wizard-intro">{dialogIntro}</div>' +
			'<div class="user-wizard-personalize-wrap">' +
				'<div class="user-wizard-personalize-text">{personalizeText}</div>' +
				'<div class="user-wizard-profile-link-wrap"><a href="{profileLinkUrl}"><span>{profileLinkText}</span></a></div>' +
			'</div>' +
			'<div class="user-wizard-control-wrap"><a class="checked" href="#">{controlText}<span></span></a></div>' +
		'</div>'
	;
	

	var VgrUserWizard = A.Component.create(
		{
			ATTRS: {
			
				dialogHeight: {
					value: 410
				},
				
				dialogWidth: {
					value: 600
				},
				
				saveHideWizardLoggedInSession: {
					value: ''
				},
				
				startPageUrlFragment: {
					value: '/pub-start'
				},
				
				wizardContentURL: {
					value: ''
				}
			
			},
			EXTENDS: A.Component,
			NAME: NAME,
			NS: NS,
			prototype: {
				
				initializer: function(config){
					var instance = this;
					
					instance.messages = {};
					
					instance.messages.dialog = {};
					
					instance.messages.dialog.title = 'V&auml;lkommen till Regionportalen!';
					
					instance.messages.dialog.intro = 'Regionportalen &auml;r din centrala arbetsplats p&aring; n&auml;tet. H&auml;r ges du tillg&aring;ng till regionens olika system och kan f&aring; samordnade notifikationer.';
					instance.messages.dialog.personalizeText = 'Genom att &auml;ndra och skruva p&aring; inst&auml;llningarna kan du personalisera portalen och anpassa den efter just dina behov. Och helt enkelt g&ouml;ra din vardag lite enklare.';
					instance.messages.dialog.profileLinkText = 'G&aring; till min profil';
					instance.messages.dialog.controlText = 'Visa denna information n&auml;sta g&aring;ng jag loggar in';
					instance.messages.dialog.close = 'St&auml;ng';
					
					var wizardContentURL = instance.get(WIZARD_CONTENT_URL);
					wizardContentURL = wizardContentURL.replace('&p_p_state=normal', '&p_p_state=exclusive');
					
					instance.set(WIZARD_CONTENT_URL, wizardContentURL);
				},
				
				renderUI: function(){
					var instance = this;
					
					var winWidth = A.one('body').get('winWidth');
					
					if(winWidth > 600) {
						instance._initUserWizard();	
					}
				},
				
				bindUI: function(){
					var instance = this;
				},
				
				_isStartPage: function() {
					var instance = this;
					
					return (window.location.href.indexOf(instance.get(START_PAGE_URL_FRAGMENT)) != -1);
				},
				
				_initUserWizard: function(){
					var instance = this;
					
					if(!themeDisplay.isSignedIn()) { return; }
					
					var wizardDialog = new A.Dialog({
						bodyContent: '<div class="user-wizard-article-wrap"></div>',
						centered: true,
						constrain2view: true,
						cssClass: CSS_USER_WIZARD_DIALOG,
						destroyOnClose: false, //http://issues.liferay.com/browse/AUI-393 setting this to true results in "this.fn is null"
						height: instance.get(DIALOG_HEIGHT),
						modal: true,
						resizable: false,
						width: instance.get(DIALOG_WIDTH),
						title: instance.messages.dialog.title,
						zIndex: 1000,
						buttons: [
			                        {
				                        text: instance.messages.dialog.close,
				                        handler: function() {
					                        this.close();
				                        }
			                        }
			            ]
					}).render();
					
					wizardDialog.on('render', instance._onWizardDialogRender, instance, wizardDialog);
					
				},
				
				_onControlLinkClick: function(e, wizardDialog) {
					var instance = this;
					
					e.halt();
					
					var currentTarget = e.currentTarget;
					
					A.log('_onControlLinkClick');
					
					var hideWizard = false;
					
					currentTarget.toggleClass(CSS_SHOW_CONTROL_CHECKED);
					
					if(!currentTarget.hasClass(CSS_SHOW_CONTROL_CHECKED)) {
						hideWizard = true;
					}
					
					var url = currentTarget.getAttribute('href');
					
					var controlIO = A.io.request(url, {
						autoLoad : false,
						data: {
							hideWizard: hideWizard
						},
						method : 'POST'
					});
					
					// Attach success handler to io request
					controlIO.on('success', function(e) {
						A.log('controlIO success.');
					}, instance);							
					
					controlIO.start();
					
					/*
					if(currentTarget.hasClass('checked')) {
						currentTarget.removeClass('checked');
						currentTarget.addClass('unchecked');
						
					}
					else if(currentTarget.hasClass('unchecked')) {
						currentTarget.removeClass('unchecked');
						currentTarget.addClass('checked');
					}
					*/
					
					
					
				},
				
				_onWizardDialogRender: function(e, wizardDialog) {
					var instance = this;
					
					var contentBox = wizardDialog.get('contentBox');
					
					var articleWrapNode = contentBox.one('.user-wizard-article-wrap');
					var contentURL = instance.get(WIZARD_CONTENT_URL);
					
					if(contentURL != '') {
						articleWrapNode.plug(A.Plugin.IO, {
							uri: contentURL
						});
					}
					
					contentBox.delegate('click', instance._onControlLinkClick, 'a.user-wizard-show-control', instance, wizardDialog);
					
					
					// Save hide wizard for this logged in session
					var hideWizardLoggedInSession = true; 
					
					var saveHideWizardLoggedInSessionIO = A.io.request(instance.get(SAVE_HIDE_WIZARD_LOGGED_IN_SESSION), {
						autoLoad : false,
						data: {
							hideWizardLoggedInSession: hideWizardLoggedInSession
						},
						method : 'POST'
					});
					
					// Attach success handler to io request
					saveHideWizardLoggedInSessionIO.on('success', function(e) {
						A.log('saveHideWizardLoggedInSessionIO success.');
					}, instance);							
					
					saveHideWizardLoggedInSessionIO.start();
					
				}
			
			
			}
		}
	);

	A.VgrUserWizard = VgrUserWizard;
		
	},1, {
		requires: [
			'aui-base',
			'aui-dialog',
			'aui-io',
			'aui-loading-mask',
			'json-stringify',
			'substitute'
      ]
	}
);
