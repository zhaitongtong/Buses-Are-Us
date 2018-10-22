package ca.ubc.cs.cpsc210.translink.parsers;

import ca.ubc.cs.cpsc210.translink.model.Arrival;
import ca.ubc.cs.cpsc210.translink.model.Route;
import ca.ubc.cs.cpsc210.translink.model.RouteManager;
import ca.ubc.cs.cpsc210.translink.model.Stop;
import ca.ubc.cs.cpsc210.translink.parsers.exception.ArrivalsDataMissingException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A parser for the data returned by the Translink arrivals at a stop query
 */
public class ArrivalsParser {

    /**
     * Parse arrivals from JSON response produced by TransLink query.  All parsed arrivals are
     * added to the given stop assuming that corresponding JSON object has a RouteNo: and an
     * array of Schedules:
     * Each schedule must have an ExpectedCountdown, ScheduleStatus, and Destination.  If
     * any of the aforementioned elements is missing, the arrival is not added to the stop.
     *
     * @param stop             stop to which parsed arrivals are to be added
     * @param jsonResponse    the JSON response produced by Translink
     * @throws JSONException  when JSON response does not have expected format
     * @throws ArrivalsDataMissingException  when no arrivals are found in the reply
     */
    public static void parseArrivals(Stop stop, String jsonResponse)throws JSONException, ArrivalsDataMissingException {
        int count=0;
        JSONArray arrivals = new JSONArray(jsonResponse);
        for (int index = 0; index < arrivals.length(); index++) {
            JSONObject arrival = arrivals.getJSONObject(index);
            parseArrival(stop,arrival);
        }
        for( Arrival a: stop){
            count++;
        }
        if(count==0){
            throw new  ArrivalsDataMissingException("Arrival is missing some elements!");
        }
    }

    public static void parseArrival(Stop stop, JSONObject arrival)throws JSONException, ArrivalsDataMissingException {
        String routNo;
        JSONArray schedules;

        try {
            routNo=arrival.getString("RouteNo");
        }catch (JSONException e){
            routNo=null;
        }

        try {
            schedules = arrival.getJSONArray("Schedules");
        }catch (JSONException e){
            schedules= new JSONArray();
        }

        parseSchedules(stop,schedules,routNo);
    }

    public static void parseSchedules(Stop stop,JSONArray schedules,String routNo)throws JSONException, ArrivalsDataMissingException {
        for (int index = 0; index < schedules.length(); index++) {
            JSONObject  schedule= schedules.getJSONObject(index);
            parseSchedule(stop,schedule,routNo);
        }
    }
    public static void parseSchedule(Stop stop,JSONObject schedule,String routNo)throws JSONException, ArrivalsDataMissingException {
        Integer expectedCountdown;
        String destination;
        String scheduleStatus;

        try{
            expectedCountdown = schedule.getInt("ExpectedCountdown");
        }catch (JSONException e){
            expectedCountdown=Integer.MAX_VALUE;
        }

        try{
            destination=schedule.getString("Destination");
        }catch (JSONException e)
        {
            destination=null;
        }

        try{
            scheduleStatus=schedule.getString("ScheduleStatus");
        }catch (JSONException e){
            scheduleStatus=null;
        }

        if(routNo!=null && expectedCountdown!=Integer.MAX_VALUE && destination!=null && scheduleStatus!=null){
            RouteManager rm = RouteManager.getInstance();
            Route route = rm.getRouteWithNumber(routNo);
            Arrival newArrival= new Arrival(expectedCountdown,destination,route);
            stop.addArrival(newArrival);
        }
    }
}