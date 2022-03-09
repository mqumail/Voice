package app.com.muhammad.voice.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import app.com.muhammad.voice.R;

public class OnBoardingFragment1 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle s) {

        return inflater.inflate(
                R.layout.onboarding_screen1,
                container,
                false
        );

    }
}
