package app.com.muhammad.voice;


import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import app.com.muhammad.voice.databinding.ActivityBaseBinding;
import app.com.muhammad.voice.ui.openStreetMap.OsmFragment;
import app.com.muhammad.voice.ui.profile.ProfileFragment;
import app.com.muhammad.voice.ui.settings.SettingsFragment;
import timber.log.Timber;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private static final String ACTIVITY_TAG = "BaseActivity";
    protected AppBarConfiguration appBarConfiguration;
    private ActivityBaseBinding binding;
    private Toolbar toolbar;
    protected DrawerLayout drawer;
    private NavigationView navigationView;
    private NavController navController;
    private NavHostFragment navHostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Timber.i("%s starting", ACTIVITY_TAG);

        // Timber Logger
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        binding = ActivityBaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        toolbar = findViewById(R.id.toolbar_base);
        setSupportActionBar(toolbar);

        drawer = binding.baseDrawerLayout;
        appBarConfiguration = new AppBarConfiguration.
                Builder(R.id.osmFragment,
                R.id.profileFragment,
                R.id.settingsFragment)
        .setOpenableLayout(drawer)
        .build();

        navigationView = binding.baseNavView;
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(this);
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
        switch (item.getItemId()) {
            case R.id.nav_osm: {
                //replace fragment to fragment_osm
                // Create new fragment and transaction

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setReorderingAllowed(true);

                Fragment fragment = new OsmFragment();

                transaction.replace(R.id.nav_host_fragment, fragment, null);

                // Commit the transaction
                transaction.commit();

                Timber.d("Starting osm activity");
                Log.d(ACTIVITY_TAG,"Starting osm activity");
                break;
            }
            case R.id.nav_profile: {
                //replace fragment to gallery
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setReorderingAllowed(true);

                Fragment fragment = new ProfileFragment();

                transaction.replace(R.id.content_container, fragment, null);
                transaction.commit();

                Toast.makeText(this, "profile", Toast.LENGTH_SHORT).show();
                Timber.d("Starting profile activity");
                Log.d(ACTIVITY_TAG,"Starting profile activity");

                break;
            }
            case R.id.nav_settings: {
                //replace fragment to settings
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setReorderingAllowed(true);

                Fragment fragment = new SettingsFragment();

                transaction.replace(R.id.content_container, fragment, null);
                transaction.commit();

                Timber.d("Starting settings activity");
                Log.d(ACTIVITY_TAG,"Starting settings activity");
                break;
            }
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}