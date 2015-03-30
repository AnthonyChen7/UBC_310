package ubc.cs.cpsc310.rackbuddy.client;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import ubc.cs.cpsc310.rackbuddy.client.JDOService;
import ubc.cs.cpsc310.rackbuddy.client.JDOServiceAsync;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.TextBox;

import com.google.gwt.user.client.ui.TabPanel;

import com.google.gwt.user.client.ui.VerticalPanel;

public class RackBuddy implements EntryPoint {

	private static final String ALL_DATA_SUCCESSFULLY_REMOVED_FROM_DATASTORE = "All data successfully removed from datastore.";
	protected static final String DATA_LOADED = "Data loaded into database.";
	private LoginInfo loginInfo = null;
	private FlowPanel loginPanel = new FlowPanel();
	private Button loadData = new Button("Load Data");
	private Button removeData = new Button("Remove all Data");
	private String url = "http://m.uploadedit.com/ba3a/1426016101419.txt";
	private TextBox urlbox = new TextBox();
	private Label loginLabel = new Label(
			"Please sign in to your Google Account to access the RackBuddy application.");
	private Anchor signInLink = new Anchor("Sign In");
	private String signOutLink = new String();
	private Button signOutButton = new Button("Sign Out");
	private JDOServiceAsync jdoService = GWT.create(JDOService.class);

	private String LOAD_FROM = "Load from: ";
	private HorizontalPanel loadPanel = new HorizontalPanel();

	private MapDisplay mapDisplay;
	
	private ListBox yrListBox = new ListBox();
	private String p2010 = "Prior 2010";
	private String _2011 = "2011";
	private String _2012 = "2012";
	private String _2013 = "2013";
	private String _2014 = "2014";
	
	private String sharecode = "FB.ui({ method: 'share', href: 'https://developers.facebook.com/docs/',}, function(response){});";
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		// Check login status using login service.
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.login(GWT.getHostPageBaseURL(),
				new AsyncCallback<LoginInfo>() {
					public void onFailure(Throwable error) {
					}

					public void onSuccess(LoginInfo result) {
						loginInfo = result;
						if (loginInfo.isLoggedIn()) {
							loadRackBuddy();
						} else {
							loadLogin();
						}
					}
				});
	}
	/**
	 * Assemble login panel
	 */
	private void loadLogin() {
		signInLink.setHref(loginInfo.getLoginUrl());
		loginPanel.add(loginLabel);
		loginPanel.add(signInLink);
		RootPanel.get("rackMap").add(loginPanel);

	}
	
	private void loadRackBuddy() {

		mapDisplay = new MapDisplay(loginInfo);
		
		BikeRackTable bikeRackTable  = new BikeRackTable(loginInfo);
		FavRackTable favRackTable = new FavRackTable(loginInfo);
		
		VerticalPanel bigTable = new VerticalPanel();
		
		final TabPanel p = new TabPanel();
	    p.add(bikeRackTable, BikeRackTable.BIKE_RACK_LOCATIONS_IN_THE_CITY_OF_VANCOUVER, false);
	    p.add(favRackTable, FavRackTable.USER_S_FAVORITE_BIKE_RACK_LOCATION, false);

	    p.selectTab(0);

	    bigTable.add(p);
	    
	    RootPanel.get("bigTable").add(bigTable);
		
		RootPanel.get("rackMap").add(mapDisplay.getMapPanel());
		
		signOutLink = loginInfo.getLogoutUrl();

		// Associate the panels with the HTML host page.
		RootPanel.get("signOut").add(signOutButton);

		signOutButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.Location.assign(signOutLink);
			}
		});
	
		//listen on Load Rack Data Button
		  loadData.addClickHandler(new ClickHandler() {
			  @Override
		      public void onClick(ClickEvent event) {
		        loadRacks();
		      }
		    });
		  
		  //listen on removealldatabutton
		  removeData.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				removeAllData();
				
			}
			});
		  
		  
			if (loginInfo.getAdmin() == false) {
				loadPanel.setVisible(false);
				removeData.setVisible(false);
			} else {
				loadPanel.setVisible(true);
				removeData.setVisible(true);
			}
			
		  //load data panel formatting

			urlbox.addStyleName("paddedRight");
			urlbox.setWidth("40em");
			urlbox.setHeight("10px");
			urlbox.setValue(url);
			
			yrListBox.setVisibleItemCount(1);
			yrListBox.addItem(p2010);
			yrListBox.addItem(_2011);
			yrListBox.addItem(_2012);
			yrListBox.addItem(_2013);
			yrListBox.addItem(_2014);
			
			loadPanel.setStyleName("marginTop");
			loadPanel.add(new Label(LOAD_FROM));
			loadPanel.add(urlbox);
			loadPanel.add(loadData);
			loadPanel.add(yrListBox);
			
			RootPanel.get("loadData").add(loadPanel);
			RootPanel.get("loadData").add(removeData);
		
		  
		/*	refreshDispl.addClickHandler(new ClickHandler(){

				@Override
				public void onClick(ClickEvent event) {
					mapDisplay.displayAllMarkers();
				}
				});*/
	}

	private void loadRacks() {
		 jdoService.loadRacks(urlbox.getValue(), yrListBox.getValue(yrListBox.getSelectedIndex()), new AsyncCallback<Void>() {
		    	public void onFailure(Throwable error) {
			    	 handleError(error);
			      }
		      public void onSuccess(Void ignore) {
		    		Window.alert(DATA_LOADED);

		      }
		    });
	}
	
	private void removeAllData(){
		jdoService.removeAll(new AsyncCallback<Void>(){

			@Override
			public void onFailure(Throwable caught) {
				handleError(caught);
			}

			@Override
			public void onSuccess(Void result) {
				Window.alert(ALL_DATA_SUCCESSFULLY_REMOVED_FROM_DATASTORE);
			}
			
		});
	}
	
	
		
	private void handleError(Throwable error) {
	    Window.alert(error.getMessage());
	
	    }
	
	public static native void shareFb() /*-{ 
		$wnd.FB.ui({
  method: 'share_open_graph',
  action_type: 'og.likes',
  action_properties: JSON.stringify({
      object:'http://1-dot-wxcs310s1final.appspot.com/',
  })
}, function(response){});
	
	}-*/;
}

