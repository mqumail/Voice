package app.com.muhammad.voice;

import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import app.com.muhammad.voice.databinding.ActivityBaseBinding;

public class BaseActivity extends AppCompatActivity
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
}