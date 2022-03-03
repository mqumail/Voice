package app.com.muhammad.voice;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import app.com.muhammad.voice.databinding.ActivityBaseBinding;

import static app.com.muhammad.voice.utils.UiHelperMethods.replaceContentContainer;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private static final String ACTIVITY_TAG = "BaseActivity";
    private static final String MY_PREFERENCES = "my_preferences";

    protected AppBarConfiguration appBarConfiguration;
    private ActivityBaseBinding binding;
    private Toolbar toolbar;
    protected DrawerLayout drawer;
    private NavigationView navigationView;
    private NavController navController;
    private NavHostFragment navHostFragment;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Make sure this is before calling super.onCreate
        setTheme(R.style.AppTheme);

        //TODO: Maybe a bug, after the onboardingactivity is finished, it calls the baseactivity again,
        // should go drirectly to the osmFragment
        super.onCreate(savedInstanceState);
        Log.i("%s starting", ACTIVITY_TAG);

        binding = ActivityBaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //toolbar
        toolbar = findViewById(R.id.toolbar_base);
        setSupportActionBar(toolbar);

        //drawer
        drawer = binding.baseDrawerLayout;
        appBarConfiguration = new AppBarConfiguration.
                Builder(R.id.osmFragment,
                R.id.profileFragment,
                R.id.settingsFragment)
                .setOpenableLayout(drawer)
                .build();

        binding.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.openDrawer(GravityCompat.START);
                } else {
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });

        //UIs
        navigationView = binding.baseNavView;
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(this);

        // Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        //Fragment OSM - UIs
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        showOnboard();
    }

    private boolean showOnboard() {
        SharedPreferences preferences =  getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);

        if(!preferences.getBoolean("onboard_complete", false)){
            Intent onboard = new Intent(this, OnBoardingActivity.class);
            startActivity(onboard);

            finish();
            return true;
        }
        return false;
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        navController = navHostFragment.getNavController();
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
        replaceContentContainer(item.getItemId(), getSupportFragmentManager());
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            // send user to login page
            // TODO: When this intent is sent, it also checks for permissions. Its too early. Dont show it, wait
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
        }
    }
}