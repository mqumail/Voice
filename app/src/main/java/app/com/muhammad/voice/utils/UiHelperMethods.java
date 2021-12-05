package app.com.muhammad.voice.utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import app.com.muhammad.voice.R;
import app.com.muhammad.voice.ui.checkIn.CheckInFragment;
import app.com.muhammad.voice.ui.openStreetMap.OsmFragment;
import app.com.muhammad.voice.ui.profile.ProfileFragment;
import app.com.muhammad.voice.ui.settings.SettingsFragment;

public class UiHelperMethods extends AppCompatActivity {

    public static void replaceContentContainer(int contentViewId, FragmentManager fragmentManager){

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setReorderingAllowed(true);

        if (contentViewId == R.id.nav_osm){
            Fragment fragment = new OsmFragment();
            transaction.replace(R.id.content_container, fragment, null);
        } else if (contentViewId == R.id.nav_profile){
            Fragment fragment = new ProfileFragment();
            transaction.replace(R.id.content_container, fragment, null);
        } else if (contentViewId == R.id.nav_settings){
            Fragment fragment = new SettingsFragment();
            transaction.replace(R.id.content_container, fragment, null);
        } else if (contentViewId == R.id.nav_check_in){
            Fragment fragment = new CheckInFragment();
            transaction.replace(R.id.content_container, fragment, null);
        }

        transaction.commit();
    }

}
