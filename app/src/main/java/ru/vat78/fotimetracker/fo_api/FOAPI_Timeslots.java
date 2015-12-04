package ru.vat78.fotimetracker.fo_api;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ru.vat78.fotimetracker.database.FOTT_DBContract;

/**
 * Created by vat on 04.12.2015.
 */
public class FOAPI_Timeslots {

    public static ArrayList<ContentValues> load(FOAPI_Connector web_service){
        JSONObject jo = web_service.executeAPI(FOAPI_Dictionary.FO_METHOD_LISTING,FOAPI_Dictionary.FO_SERVICE_TASKS,
                new String[] {FOAPI_Dictionary.FO_API_ARG_STATUS, "0"});
        return convertResults(jo);
    }

    private static ArrayList<ContentValues> convertResults(JSONObject data){

        if (data == null) {return null;}
        JSONArray list = null;
        JSONObject jo;
        String tmp;
        ArrayList<ContentValues> res = new ArrayList<>();
        try {
            list = data.getJSONArray(FOAPI_Dictionary.FO_API_MAIN_OBJ);
        } catch (Exception e) {
            Log.e("FOTT", e.getMessage());
        }
        if (list == null) {return null;}

        for (int i = 0; i < list.length(); i++) {
            ContentValues el = new ContentValues();
            try {
                jo = list.getJSONObject(i);
                el.put(FOTT_DBContract.FOTT_DBTimeslots.COLUMN_TIMESLOT_ID,jo.getInt(FOAPI_Dictionary.FO_API_FIELD_ID));
                el.put(FOTT_DBContract.FOTT_DBTimeslots.COLUMN_NAME_TITLE,jo.getString(FOAPI_Dictionary.FO_API_FIELD_NAME));

            }
            catch (Exception e) {
                Log.e("FOTT", e.getMessage());
            }
            finally {
                if (el.size()>0)
                    if (!res.add(el)) {break;}
            }
        }

        return res;
    }
}
