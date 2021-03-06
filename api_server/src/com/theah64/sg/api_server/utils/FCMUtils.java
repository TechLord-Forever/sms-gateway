package com.theah64.sg.api_server.utils;

import com.theah64.sg.api_server.database.tables.Recipients;
import com.theah64.sg.api_server.database.tables.SMSRequests;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by theapache64 on 14/9/16,6:07 PM.
 */
public class FCMUtils {

    private static final String FCM_SEND_URL = "https://fcm.googleapis.com/fcm/send";


    public static final String KEY_TYPE = "type";
    private static final String TYPE_SMS_REQUEST = "sms_request";
    public static final String KEY_DATA = "data";
    public static final String KEY_TO = "to";
    private static final String FCM_NOTIFICATION_KEY = "AIzaSyCq_V-Hu0qn4jZhdWosj3j5cRxjTc22R6s";

    public static JSONObject sendSMS(final JSONArray jaRecipients, final String message, final String fcmId) {

        final JSONObject joFcm = new JSONObject();
        try {

            joFcm.put(FCMUtils.KEY_TO, fcmId);

            final JSONObject joData = new JSONObject();
            joData.put(FCMUtils.KEY_TYPE, TYPE_SMS_REQUEST);
            joData.put(Recipients.TABLE_NAME, jaRecipients);
            joData.put(SMSRequests.COLUMN_MESSAGE, message);

            joFcm.put(FCMUtils.KEY_DATA, joData);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return sendPayload(joFcm.toString());
    }

    private static JSONObject sendPayload(String payload) {


        try {
            final URL url = new URL(FCM_SEND_URL);
            final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.addRequestProperty("Authorization", "key=" + FCM_NOTIFICATION_KEY);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");

            OutputStream os = urlConnection.getOutputStream();
            os.write(payload.getBytes());
            os.flush();
            os.close();

            final BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            final StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line).append("\n");
            }

            br.close();

            return new JSONObject(response.toString());

        } catch (java.io.IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }


}
