package app.com.muhammad.voice;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import app.com.muhammad.voice.utils.LocalCity;
import app.com.muhammad.voice.utils.SettingsUtils;
import app.com.muhammad.voice.utils.UserInformation;

public class ProfileSettingsActivity extends AppCompatActivity  {

    private SettingsUtils settingsUtils = new SettingsUtils();
    private EditText tUserName;
    private ListView mListView;
    private ArrayList<String> aCityList;
    private ArrayList<String> aCityListView;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tUserName = findViewById(R.id.tUserName_settings);
        mListView = findViewById(R.id.citiesList_settings);

        aCityList = new ArrayList<>();
        aCityListView = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(ProfileSettingsActivity.this, android.R.layout.simple_list_item_1, aCityListView);
        mListView.setAdapter(arrayAdapter);

//        settingsUtils.loadUserName(user, userInformation, tUserName);
//        settingsUtils.loadCities(localCities, aCityList, aCityListView, arrayAdapter, this);

        // Construct a GeoDataClient.

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                this.startActivity(upIntent);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void continueHome(View view)
    {
//        settingsUtils.saveCities(aCityList, localCities, this);
//        settingsUtils.saveUserInfo(tUserName, mUID, user, userInformation);
//        Intent intent = new Intent(this, HomeScreenActivity.class);
//        this.startActivity(intent);
//        finish();
    }
}