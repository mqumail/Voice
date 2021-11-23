package app.com.muhammad.voice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentActivity;

import androidx.drawerlayout.widget.DrawerLayout;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeScreenActivity extends FragmentActivity
{
    private static final String TAG = "HomeScreenActivity";

    public DrawerLayout mDrawerLayout;

    private TextView email;
    private TextView userName;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        init();

        NavigationDrawerSetup();

        GoogleAPIClientSetup();
    }

    private void init()
    {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        ImageButton checkInButton = findViewById(R.id.checkInButton);
        checkInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                CheckIn();
            }
        });
    }

    public void CheckIn() {
        Log.i(TAG, "startActivityForResult finished");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PLACE_PICKER_REQUEST) {
//            if (resultCode == RESULT_OK) {
////                UserCheckIns userCheckIns = new UserCheckIns(HomeScreenActivity.this, null, null);
////
////                userCheckIns.PlacePicker(data);
//            }
//        }
    }

    private void GoogleAPIClientSetup() {
        // Construct a GeoDataClient


        // Construct a PlaceDetectionClient.

    }

    private void NavigationDrawerSetup()
    {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        int id = menuItem.getItemId();

                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        switch (id){
                            case R.id.local_cities:
                                //Toast.makeText(getApplicationContext(), "Local Cities clicked", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(HomeScreenActivity.this, ProfileSettingsActivity.class);
                                intent.putExtra("caller", TAG);
                                startActivity(intent);
                                HomeScreenActivity.this.finish();
                            return true;

                            case R.id.log_out:

                                //startActivity(new Intent(getApplicationContext(), MainActivity.class));

                                HomeScreenActivity.this.finish();
                            return true;

                            default:
                                return HomeScreenActivity.super.onOptionsItemSelected(menuItem);
                        }
                    }
                });
    }

    public void profile(View view)
    {
        // Get current user info before drawer is opened
        email = findViewById(R.id.navigationHeaderTextView);
        userName = findViewById(R.id.navigationHeaderTextUSerName);
        assignProfileView();

        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    private void assignProfileView() {
    }

    private class GetCoordinates extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog = new ProgressDialog(HomeScreenActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait....");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String response;
            try{
                String address = strings[0];
                GeocodingDataHandler http = new GeocodingDataHandler();


                return null;
            }
            catch (Exception ex)
            {

            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonObject = new JSONObject(s);

                String lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lat").toString();
                String lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lng").toString();

                String textCoordinates = String.format("Coordinates : %s / %s ", lat, lng);

                if(dialog.isShowing())
                    dialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
