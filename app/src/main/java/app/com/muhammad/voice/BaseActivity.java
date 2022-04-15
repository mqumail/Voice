package app.com.muhammad.voice;

import static androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE;
import static app.com.muhammad.voice.Database.FirestoreFirebaseClient.getInstance;
import static app.com.muhammad.voice.util.Constants.MY_PREFERENCES;
import static app.com.muhammad.voice.util.Constants.PLACES_COLLECTION_PATH;
import static app.com.muhammad.voice.util.Constants.SHOW_ON_BOARD_KEY;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;

import java.util.ArrayList;

import app.com.muhammad.voice.DTO.PlaceInfo;
import app.com.muhammad.voice.Database.FirestoreFirebaseClient;
import app.com.muhammad.voice.databinding.ActivityBaseBinding;
import app.com.muhammad.voice.ui.checkIn.CheckInFragment;
import app.com.muhammad.voice.ui.openStreetMap.OsmFragment;
import app.com.muhammad.voice.ui.profile.ProfileFragment;
import app.com.muhammad.voice.ui.settings.SettingsFragment;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "BaseActivity";
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityBaseBinding binding;
    private Context CONTEXT;
    protected AppBarConfiguration appBarConfiguration;
    private NavHostFragment navHostFragment;
    private FirestoreFirebaseClient client;
    private ArrayList<PlaceInfo> places = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Handle the splash screen transition.
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        binding = ActivityBaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.activityBaseToolBar.toolbarBase);

        //drawer
        DrawerLayout drawer = binding.baseDrawerLayout;
        NavigationView navigationView = binding.baseNavView;

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.osmFragment, R.id.profileFragment, R.id.settingsFragment, R.id.checkInFragment)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);

        binding.activityBaseToolBar.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.openDrawer(GravityCompat.START);
                } else {
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });

        //Onboard logic
        showOnboard();

        //Get the places from Firestore
        getPlaces();
    }

    private void showOnboard() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);
        if (preferences.getBoolean(SHOW_ON_BOARD_KEY, true)) {
            Intent onboard = new Intent(this, OnBoardingActivity.class);
            startActivity(onboard);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        NavController navController = navHostFragment.getNavController();
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        Log.i("Item selected: %s", item.toString());

        OsmFragment osmFragment = OsmFragment.newInstance();
        CheckInFragment checkInFragment = CheckInFragment.newInstance();
        ProfileFragment profileFragment = ProfileFragment.newInstance();
        SettingsFragment settingsFragment = SettingsFragment.newInstance();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setReorderingAllowed(true);

        if (item.getItemId() == R.id.nav_osm){
            transaction.add(R.id.nav_host_fragment_content_main, osmFragment, null);
        } else if (item.getItemId() == R.id.nav_profile){
            transaction.add(R.id.nav_host_fragment_content_main, profileFragment, null);
        } else if (item.getItemId() == R.id.nav_settings){
            transaction.add(R.id.nav_host_fragment_content_main, settingsFragment, null);
        } else if (item.getItemId() == R.id.nav_check_in){
            if (places != null) {
                Bundle arguments = new Bundle();
                arguments.putSerializable("Places", places);
                checkInFragment.setArguments(arguments);
            }
            transaction.add(R.id.nav_host_fragment_content_main, checkInFragment, null);
        }
        transaction.setTransition(TRANSIT_FRAGMENT_FADE);
        transaction.addToBackStack(null);
        transaction.commit();
        binding.baseDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getPlaces() {
        client = getInstance(CONTEXT);
        CollectionReference collectionRef = client.getDb().collection(PLACES_COLLECTION_PATH);
        collectionRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w(TAG, "Listen error", error);
            } else {
                for (DocumentChange change : value.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        PlaceInfo placeInfo = change.getDocument().toObject(PlaceInfo.class);
                        String placeInfoId = change.getDocument().getId();
                        placeInfo.setId(placeInfoId);
                        places.add(placeInfo);
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.activityBaseToolBar.menuButton.setVisibility(View.VISIBLE);
    }

    public void handleDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.base_drawer_layout);
        if (!drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
        } else {
            drawer.closeDrawer(GravityCompat.START);
        }
    }
}