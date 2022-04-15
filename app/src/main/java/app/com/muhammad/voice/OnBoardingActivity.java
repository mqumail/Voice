package app.com.muhammad.voice;

import static app.com.muhammad.voice.util.Constants.MY_PREFERENCES;
import static app.com.muhammad.voice.util.Constants.SHOW_ON_BOARD_KEY;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import app.com.muhammad.voice.ui.onboarding.OnBoardingFragment1;
import app.com.muhammad.voice.ui.onboarding.OnBoardingFragment2;
import app.com.muhammad.voice.ui.onboarding.OnBoardingFragment3;

public class OnBoardingActivity extends FragmentActivity {

private int count = 1;
    private ViewPager2 pager;
    private TabLayout tabLayout;
    private Button continueHome;
    private FragmentStateAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.activity_onboarding);

        pager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tabLayout);
        continueHome = findViewById(R.id.button_continue_onboard);
        adapter = new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0 : return new OnBoardingFragment1();
                    case 1 : return new OnBoardingFragment2();
                    case 2 : return new OnBoardingFragment3();
                    default: return null;
                }
            }
            @Override
            public int getItemCount() {
                return 3;
            }
        };

        pager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, pager,
                (tab, position) -> tab.setIcon(R.drawable.tab_indicator_default)).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.setIcon(R.drawable.tab_indicator_selected);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setIcon(R.drawable.tab_indicator_default);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                tab.setIcon(R.drawable.tab_indicator_selected);
            }
        });

        continueHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishOnboard();
            }
        });
    }

    private void finishOnboard() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SHOW_ON_BOARD_KEY, false);
        editor.commit();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
        }
        finish();
    }
}
