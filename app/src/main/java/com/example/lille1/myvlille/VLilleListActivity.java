package com.example.lille1.myvlille;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

import com.example.lille1.myvlille.utils.Station;
import com.example.lille1.myvlille.utils.StationsWrapper;
import com.example.lille1.myvlille.utils.XMLTools;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;


public class VLilleListActivity extends Activity {

    private StationsWrapper stations;

    private ListView stationsListView;

    private ArrayAdapter<Station> arrayAdapter;

    private ProgressDialog mProgressDialog;

    private Location currentLocation;

    private boolean onMapView;

    private MapFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.onMapView = false;

        // get view from activity_vlille_list.xml
        setContentView(R.layout.activity_vlille_list);

        // Execute RemoteDataTask AsyncTask
        new RemoteDataTask1().execute();
    }

    // RemoteDataTask AsyncTabooleansk
    private class RemoteDataTask1 extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialogBox("Loading...");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                stations = new StationsWrapper(VLilleListActivity.this.getCurrentLocation());
            } catch (NullPointerException e) {
                showDialogBox("A problem occured while retrieving datas");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            for (Station station : stations.getStationsArray()) {
                // Execute RemoteDataTask AsyncTask to update station
                new RemoteDataTask2(station).execute();
            }

            // Locate the listview in activity_vlille_list.xml
            stationsListView = (ListView) findViewById(R.id.listView);


            // Pass the results into an ArrayAdapter
            arrayAdapter = new ArrayAdapter<Station>(VLilleListActivity.this, android.R.layout.simple_list_item_2, android.R.id.text1, stations.getStationsArray()) {

                // set text for first and second lines
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                    text1.setText(this.getItem(position).getName());
                    text2.setText(this.getItem(position).getInfos());
                    return view;
                }
            };

            // Binds the Adapter to the ListView
            stationsListView.setAdapter(arrayAdapter);

            // Close the progressdialog
            mProgressDialog.dismiss();

            // Capture button clicks on ListView items
            stationsListView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    // Set selected target
                    stations.setTargetByPosition(position);

                    showDialogBox("Go to map");

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    VLilleListActivity.this.fragment = new MapFragment();
                    VLilleListActivity.this.fragment.setLocations(stations);

                    fragmentTransaction.replace(android.R.id.content, VLilleListActivity.this.fragment);
                    fragmentTransaction.commit();

                    // Close progressdialog
                    mProgressDialog.hide();

                    VLilleListActivity.this.onMapView = true;
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (this.onMapView) {
            // remove map fragment
            this.onMapView = false;
            this.getFragmentManager().beginTransaction().remove(this.fragment).commit();
        }
        else
            // quit (default behavior)
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.vlille_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Location getCurrentLocation() {
        // Service
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (this.currentLocation != null ) {
            Log.i("Info", "Current location exists");
            return this.currentLocation;
        }

        // Last known position
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        this.currentLocation = locationManager.getLastKnownLocation(provider);

        // prevent null location by setting it to center of lille
        if (this.currentLocation == null) {
            this.currentLocation = new Location("center");

            this.currentLocation.setLatitude(50.6310675);
            this.currentLocation.setLongitude(3.0471604);
            this.currentLocation.setAltitude(14);
        }

        return this.currentLocation;
    }

    private class RemoteDataTask2 extends AsyncTask<Void, Void, Void>  {

        private final Station station;

        public RemoteDataTask2(Station station) {
            super();
            this.station = station;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                this.station.retrieveInformations();
            } catch (NullPointerException e) {
                showDialogBox("A problem occured while retrieving datas");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            ((ArrayAdapter)stationsListView.getAdapter()).notifyDataSetChanged();
        }
    }

    private void showDialogBox(String text) {
        // Create a progressdialog
        mProgressDialog = new ProgressDialog(VLilleListActivity.this);
        // Set progressdialog title
        mProgressDialog.setTitle("MyVLille");
        // Set progressdialog message
        mProgressDialog.setMessage(text);
        mProgressDialog.setIndeterminate(false);
        // Show progressdialog
        mProgressDialog.show();
    }
}
