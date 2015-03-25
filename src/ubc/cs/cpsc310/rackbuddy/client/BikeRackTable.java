package ubc.cs.cpsc310.rackbuddy.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SelectionModel;

public class BikeRackTable  implements IsWidget {

	public static final int NUM_DATA_PER_PAGE = 10;

	public static final String YEARS_INSTALLED = "Years Installed";

	public static final String NUM_RACKS = "# of Racks";

	public static final String BIA2 = "BIA";

	public static final String SKYTRAIN_STATION_NAME = "Skytrain Station Name";

	public static final String ST_SIDE = "St. Side";

	public static final String ST_NAME = "St. Name";

	public static final String ST_NUMBER = "St. Number";

	public static final String BIKE_RACK_LOCATIONS_IN_THE_CITY_OF_VANCOUVER = "Official Bike Rack Locations in the City of Vancouver";
	
	private JDOServiceAsync jdoService = GWT.create(JDOService.class);
	
	private List<BikeRackData> tableList;
	private List<BikeRackData> racks;
	private List<BikeRackData> tempList;
	public static MapDisplay mapDisplay;
	@Override
	public Widget asWidget() {
				
				racks = new ArrayList<BikeRackData>();
				tableList = new ArrayList<BikeRackData>();
				
				
				// Create a CellTable.
				 final CellTable<BikeRackData> table = new CellTable<BikeRackData>(BikeRackData.KEY_PROVIDER);
				 
				 MultiSelectionModel<BikeRackData> multi_selectionModel = new MultiSelectionModel<BikeRackData>(BikeRackData.KEY_PROVIDER);
				 
				 table.setSelectionModel(multi_selectionModel);
				
				table.setPageSize(NUM_DATA_PER_PAGE);
				
				
				
				  SimplePager pager = new SimplePager(TextLocation.CENTER,true,true);
					
					 pager.setPageSize(NUM_DATA_PER_PAGE);
					pager.setDisplay(table);
					
					

				// Add a text column to show the street num.
				TextColumn<BikeRackData> stNum = new TextColumn<BikeRackData>() {
					@Override
					public String getValue(BikeRackData object) {
						return object.getStreetNumber();
					}
				};
				table.addColumn(stNum, ST_NUMBER);

				// Add a text column to show the street name.
				TextColumn<BikeRackData> stName = new TextColumn<BikeRackData>() {
					@Override
					public String getValue(BikeRackData object) {
						return object.getStreetName();
					}
				};
				table.addColumn(stName, ST_NAME);
				
				TextColumn<BikeRackData> stSide = new TextColumn<BikeRackData>() {
					@Override
					public String getValue(BikeRackData object) {
						return object.getStreetSide();
					}
				};
				table.addColumn(stSide, ST_SIDE);
				
				TextColumn<BikeRackData> skytrainStn = new TextColumn<BikeRackData>() {
					@Override
					public String getValue(BikeRackData object) {
						return object.getSkytrainStation();
					}
				};
				table.addColumn(skytrainStn, SKYTRAIN_STATION_NAME);
				
				TextColumn<BikeRackData> bia = new TextColumn<BikeRackData>() {
					@Override
					public String getValue(BikeRackData object) {
						return object.getBia();
					}
				};
				table.addColumn(bia, BIA2);
				
				TextColumn<BikeRackData> numRacks = new TextColumn<BikeRackData>() {
					@Override
					public String getValue(BikeRackData object) {
						return String.valueOf(object.getNumRacks());
					}
				};
				table.addColumn(numRacks, NUM_RACKS);
				
				TextColumn<BikeRackData> yearsInstalled = new TextColumn<BikeRackData>() {
					@Override
					public String getValue(BikeRackData object) {
						return object.getYearInstalled();
					}
				};
				table.addColumn(yearsInstalled, YEARS_INSTALLED);
				
				Column<BikeRackData, Boolean> checkBoxCol = new Column<BikeRackData, Boolean>(new CheckboxCell()) { 
			        @Override 
			        public Boolean getValue(BikeRackData object) { 
			        	return object.isFave();
			        } 
			    };
			    
			    table.addColumn(checkBoxCol, "Mark as favorite?");
			    
			    mapDisplay = new MapDisplay();
			    tableList = mapDisplay.getNewList();
			    
				AsyncDataProvider<BikeRackData> provider = new AsyncDataProvider<BikeRackData>() {
					@Override
					protected void onRangeChanged(HasData<BikeRackData> display) {
						int start = display.getVisibleRange().getStart();
						int end = start + display.getVisibleRange().getLength();
						end = end >= tableList.size() ? tableList.size() : end;
						List<BikeRackData> sub = tableList.subList(start, end);
						updateRowData(start, sub);	
						
									
						
					}
				};
				
				provider.addDataDisplay(table);
				provider.updateRowCount(tableList.size(), true);
				
						 
				
				
//				jdoService.getAllData(new AsyncCallback<List<BikeRackData>>(){
//
//					@Override
//					public void onFailure(Throwable caught) {
//						
//					}
//					
//					@Override
//					public void onSuccess(final List<BikeRackData> result) {
//						mapdis = new MapDisplay();
//						for(BikeRackData brd : result) {						
//							if (mapdis.getNewList().contains(brd)) {
//								tempList.add(brd);
//						  	}
//						}
//									
//						
//						tableList = tempList;
//						
//						AsyncDataProvider<BikeRackData> provider = new AsyncDataProvider<BikeRackData>() {
//						@Override
//						protected void onRangeChanged(HasData<BikeRackData> display) {
//							int start = display.getVisibleRange().getStart();
//							int end = start + display.getVisibleRange().getLength();
//							end = end >= tableList.size() ? tableList.size() : end;
//							List<BikeRackData> sub = tableList.subList(start, end);
//							updateRowData(start, sub);
//							
//							
//							
//						}
//					};
//					
//					provider.addDataDisplay(table);
//					provider.updateRowCount(tableList.size(), true);
//					
//					}
//					
//				});
//				
				
				
				

				VerticalPanel vp = new VerticalPanel();
				vp.add(new Label(BIKE_RACK_LOCATIONS_IN_THE_CITY_OF_VANCOUVER));
				vp.add(table);
				vp.add(pager);
				
				return vp;
	}
	
	public List<BikeRackData> getList(){
		return this.racks;
	}
	

	public void replaceData(List<BikeRackData> dataToReplaceWith){
		this.racks.clear();
		this.racks.addAll(dataToReplaceWith);
	}
}
