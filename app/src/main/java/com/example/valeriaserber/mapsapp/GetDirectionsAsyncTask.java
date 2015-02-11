package com.example.valeriaserber.mapsapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Map;

public class GetDirectionsAsyncTask extends AsyncTask<Map<String, String>, Object, ArrayList>
    {
        public static final String ORIGIN_LAT = "origin_lat";
        public static final String ORIGIN_LONG = "origin_long";
        public static final String DESTINATION_LAT = "destination_lat";
        public static final String DESTINATION_LONG = "destination_long";
        public static final String DIRECTIONS_MODE = "directions_mode";
        private MainActivity activity;
        private Exception exception;
        private ProgressDialog progressDialog;

        public GetDirectionsAsyncTask(MainActivity activity)
        {
            super();
            this.activity = activity;
        }

        public void onPreExecute()
        {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Calculating directions");
            progressDialog.show();
        }

        @Override
        public void onPostExecute(ArrayList result)
        {
            progressDialog.dismiss();
            if (exception == null)
            {
                activity.handleGetDirectionsResult(result);
            }
            else
            {
                processException();
            }
        }

        @Override
        protected ArrayList doInBackground(Map<String, String>... params)
        {
            Map<String, String> paramMap = params[0];
            try
            {
                LatLng fromPosition = new LatLng(Double.valueOf(paramMap.get(ORIGIN_LAT)) , Double.valueOf(paramMap.get(ORIGIN_LONG)));
                LatLng toPosition = new LatLng(Double.valueOf(paramMap.get(DESTINATION_LAT)) , Double.valueOf(paramMap.get(DESTINATION_LONG)));
                Directions md = new Directions();
                Document doc = md.getDocument(fromPosition, toPosition, paramMap.get(DIRECTIONS_MODE));
                ArrayList directionPoints = md.getDirection(doc);
                return directionPoints;
            }
            catch (Exception e)
            {
                exception = e;
                return null;
            }
        }

        private void processException()
        {
            Toast.makeText(activity, "error when retrieving data", Toast.LENGTH_LONG).show();
        }
}
