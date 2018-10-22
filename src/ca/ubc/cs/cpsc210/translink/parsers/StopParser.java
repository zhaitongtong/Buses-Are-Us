package ca.ubc.cs.cpsc210.translink.parsers;

import ca.ubc.cs.cpsc210.translink.model.*;
import ca.ubc.cs.cpsc210.translink.parsers.exception.StopDataMissingException;
import ca.ubc.cs.cpsc210.translink.providers.DataProvider;
import ca.ubc.cs.cpsc210.translink.providers.FileDataProvider;
import ca.ubc.cs.cpsc210.translink.util.LatLon;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A parser for the data returned by Translink stops query
 */
public class StopParser{

    private String filename;

    public StopParser(String filename) {
        this.filename = filename;
    }
    /**
     * Parse stop data from the file and add all stops to stop manager.
     *
     */
    public void parse() throws IOException, StopDataMissingException, JSONException{
        DataProvider dataProvider = new FileDataProvider(filename);

        parseStops(dataProvider.dataSourceToString());
    }

    /**
     * Parse stop information from JSON response produced by Translink.
     * Stores all stops and routes found in the StopManager and RouteManager.
     *
     * @param  jsonResponse    string encoding JSON data to be parsed
     * @throws JSONException when
     *     JSON data does not have expected format
     *     JSON data is not an array
     * @throws StopDataMissingException when
     *     JSON data is missing Name, StopNo, Routes or location (Latitude or Longitude) elements for any stop
     */

    public void parseStops(String jsonResponse)throws JSONException, StopDataMissingException {
        JSONArray jsonArray = new JSONArray(jsonResponse);

        // Parse Stops.
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            // Check Stop elements.
            if(!(jsonObject.has("Name") && jsonObject.has("StopNo") && jsonObject.has("Routes") && jsonObject.has("Latitude") && jsonObject.has("Longitude"))){
                throw new StopDataMissingException("Stop is missing some elements.");
            }
            // Retrieve Stop info.
            String stopName = jsonObject.getString("Name");
            int stopNumber = jsonObject.getInt("StopNo");
            double lat = jsonObject.getDouble("Latitude");
            double lon = jsonObject.getDouble("Longitude");
            LatLon lalLon = new LatLon(lat,lon);

            // Stores all stops found in the StopManager.
            Stop stop = StopManager.getInstance().getStopWithId(stopNumber,stopName,lalLon);

            // Retrieve Route info.
            String routesStr = jsonObject.getString("Routes");
            String[] routeString = routesStr.split(",");
            for(int j=0;j<routeString.length;j++) {
                Route route = RouteManager.getInstance().getRouteWithNumber(routeString[j].trim());
                if(!route.getStops().contains(stop)){
                    route.addStop(stop);
                    stop.addRoute(route);
                }
            }
        }
    }
}
