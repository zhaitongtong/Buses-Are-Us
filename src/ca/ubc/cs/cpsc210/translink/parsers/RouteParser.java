package ca.ubc.cs.cpsc210.translink.parsers;

import ca.ubc.cs.cpsc210.translink.model.Route;
import ca.ubc.cs.cpsc210.translink.model.RouteManager;
import ca.ubc.cs.cpsc210.translink.model.RoutePattern;
import ca.ubc.cs.cpsc210.translink.parsers.exception.RouteDataMissingException;
import ca.ubc.cs.cpsc210.translink.providers.DataProvider;
import ca.ubc.cs.cpsc210.translink.providers.FileDataProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Parse route information in JSON format.
 */
public class RouteParser {
    private String filename;

    public RouteParser(String filename) {
        this.filename = filename;
    }

    /**
     * Parse route data from the file and add all route to the route manager.
     */
    public void parse() throws IOException, RouteDataMissingException, JSONException {
        DataProvider dataProvider = new FileDataProvider(filename);

        parseRoutes(dataProvider.dataSourceToString());
    }

    /**
     * Parse route information from JSON response produced by Translink.
     * Stores all routes and route patterns found in the RouteManager.
     *
     * @param jsonResponse string encoding JSON data to be parsed
     * @throws JSONException             when
     *                                   JSON data does not have expected format
     *                                   JSON data is not an array
     * @throws RouteDataMissingException when
     *                                   JSON data is missing RouteNo, Name, or Patterns elements for any route
     *                                   The value of the Patterns element is not an array for any route
     *                                   JSON data is missing PatternNo, Destination, or Direction elements for any route pattern
     */

    public void parseRoutes(String jsonResponse) throws JSONException, RouteDataMissingException {
        JSONArray jsonArray = new JSONArray(jsonResponse);

        // Parse Routes
        for (int m = 0; m < jsonArray.length(); m++) {
            JSONObject jsonObject = jsonArray.getJSONObject(m);
            // Check Route elements.
            if (!(jsonObject.has("Name") && jsonObject.has("RouteNo") && jsonObject.has("Patterns"))) {
                throw new RouteDataMissingException("Route is missing some elements.");
            }
            // Retrieve Route info.
            String lineName = jsonObject.getString("Name");
            String lineId = jsonObject.getString("RouteNo");
            // Stores all routes found in the RouteManager.
            Route r = RouteManager.getInstance().getRouteWithNumber(lineId, lineName);

            // Parse RoutePattern
            JSONArray jsonSequences = jsonObject.getJSONArray("Patterns");
            for (int i = 0; i < jsonSequences.length(); i++) {
                JSONObject sequence = jsonSequences.getJSONObject(i);
                // Check RoutePattern elements.
                if (!(sequence.has("Destination") && sequence.has("Direction") && sequence.has("PatternNo"))) {
                    throw new RouteDataMissingException("RoutePattern is missing some elements.");
                }
                // Retrieve RoutePattern info.
                String destination = sequence.getString("Destination");
                String direction = sequence.getString("Direction");
                String name = sequence.getString("PatternNo");
                // Stores all route patterns of this route that is found in the RouteManager.
                RoutePattern rp = r.getPattern(name, destination, direction);
                rp.setDestination(destination);
                rp.setDirection(direction);
            }
        }
    }
}