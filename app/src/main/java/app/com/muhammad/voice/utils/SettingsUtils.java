package app.com.muhammad.voice.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import app.com.muhammad.voice.HomeScreenActivity;
import app.com.muhammad.voice.ProfileSettingsActivity;
import app.com.muhammad.voice.R;
import app.com.muhammad.voice.SignUpActivity;

public class SettingsUtils {
    public SettingsUtils(){}

    public void addCitytoList(ArrayList aCityListView, ArrayList aCityList, ArrayAdapter arrayAdapter, Place place, Context context){
        if (!aCityListView.contains(place.getAddress())){
            String allCityInfo = place.getId() + "-" + place.getName() + "-" + place.getAddress();
            aCityList.add(allCityInfo);
            aCityListView.add((String)place.getAddress());
            arrayAdapter.notifyDataSetChanged();
            Toast.makeText(context,  place.getName() + " added to you settings", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(context, "The City is already saved in your preferences", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadUserName(FirebaseUser user, UserInformation userInformation, EditText tUserName){
        if(user != null)
        {
            String mName = user.getDisplayName();

            if(userInformation.getUserName() == "NA" || userInformation.getUserName() == "") {
                if (mName == ""){
                    tUserName.setText("Anonymous");
                }else{
                    tUserName.setText(mName);
                }
            } else {
                tUserName.setText(userInformation.getUserName());
            }
        }
    }

    public void saveUserInfo(EditText tUserName, String mUID, FirebaseUser user, UserInformation userInformation){
        String userInfo;
        if(tUserName.getText().length() > 2){
            userInfo = mUID + "/" + tUserName.getText() + "/" + user.getEmail();
        }else userInfo = mUID + "/" + "Anonymous" + "/" + user.getEmail();
        userInformation.setUserInformation(userInfo);
    }

    public void loadCities(LocalCity localCities, ArrayList<String> aCityList, ArrayList<String> aCityListView, ArrayAdapter arrayAdapter, Context context){
        try{
            ArrayList<String> citiesAddressArray = localCities.getCitiesAddress();
            ArrayList<String> citiesArray = localCities.getCities();
            if (context.getClass().getName() == SignUpActivity.class.getName()) {
                if (citiesArray.contains("empty")) {
                    int i = 0;
                    for (String aCitiesArray : citiesArray) {
                        if (citiesArray.get(0) != "empty") {
                            aCityList.add(0, aCitiesArray);
                            aCityListView.add(0, citiesAddressArray.get(i));
                            arrayAdapter.notifyDataSetChanged();
                        }
                        i++;
                    }
                    Toast.makeText(context, "Local Cities Loaded", Toast.LENGTH_SHORT).show();
                } else{
                    context.startActivity(new Intent(context, HomeScreenActivity.class));
                    ((Activity)(context)).finish();
                }
            }else if (context.getClass().getName() == ProfileSettingsActivity.class.getName()){
                if (!citiesArray.contains("empty")) {
                    int i = 0;
                    for (String aCitiesArray : citiesArray) {
                        aCityList.add(0, aCitiesArray);
                        aCityListView.add(0, citiesAddressArray.get(i));
                        arrayAdapter.notifyDataSetChanged();
                        i++;
                    }
                    Toast.makeText(context, "Local Cities Loaded", Toast.LENGTH_SHORT).show();
                }
            }

        }catch(Exception e) {
            System.err.println("Error while retrieving cities from Shared Preferences");
            e.printStackTrace();
            localCities.clearSharedPreferences();
            Toast.makeText(context, "Error Loading Cities", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveCities(ArrayList<String> aCityList, LocalCity localCities, Context context){
        String citiesInfo = "";
        if (aCityList.size() > 0){
            if (aCityList.get(0) != "" || aCityList.get(0) != "empty"){
                for (String aCitiesArray : aCityList) {
                    citiesInfo = aCitiesArray + "/" + citiesInfo;
                }
                localCities.setCities(citiesInfo);
            }
            Toast.makeText(context, "Settings Saved", Toast.LENGTH_SHORT).show();
        }
    }

}
