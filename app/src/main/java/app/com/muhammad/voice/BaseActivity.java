package app.com.muhammad.voice;


import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

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
import timber.log.Timber;

import static app.com.muhammad.voice.utils.UiHelperMethods.replaceContentContainer;

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
        Timber.i("Item selected: %s", item.toString());
        replaceContentContainer(item.getItemId(), getSupportFragmentManager());
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}