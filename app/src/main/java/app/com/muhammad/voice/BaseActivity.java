package app.com.muhammad.voice;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import app.com.muhammad.voice.databinding.ActivityBaseBinding;
import app.com.muhammad.voice.ui.openStreetMap.OsmFragment;
import app.com.muhammad.voice.ui.profile.ProfileFragment;
import app.com.muhammad.voice.ui.settings.SettingsFragment;
import timber.log.Timber;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    protected AppBarConfiguration appBarConfiguration;
    private ActivityBaseBinding binding;
    private Toolbar toolbar;
    protected DrawerLayout drawer;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Timber Logger
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        binding = ActivityBaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
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

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void displayView(int viewId) {
        Fragment fragment = null;
//        String title = getString(R.id.app_name);

        switch (viewId) {
            case R.id.nav_osm: {
                //start home activity
                Intent intent = new Intent(this, OsmFragment.class);
                startActivity(intent);
                Timber.d("Starting osm activity");
                break;
            }
            case R.id.nav_profile: {
                //start gallery
                Intent intent = new Intent(this, ProfileFragment.class);
                startActivity(intent);
                Timber.d("Starting profile activity");
                break;
            }
            case R.id.nav_settings: {
                //start slideshow activity
                Intent intent = new Intent(this, SettingsFragment.class);
                startActivity(intent);
                Timber.d("Starting settings activity");
                break;
            }
        }

        if (fragment != null) {
//            FragmentTra
        }

    }
}