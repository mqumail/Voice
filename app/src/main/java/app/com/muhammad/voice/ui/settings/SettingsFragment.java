package app.com.muhammad.voice.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import app.com.muhammad.voice.R;
import app.com.muhammad.voice.databinding.FragmentSettingsBinding;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment
{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView localCity;
    private Animation animation;

    private FragmentSettingsBinding binding;
    private SettingsViewModel viewModel;

    private static final String MY_PREFERENCES = "my_preferences";

    public SettingsFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2)
    {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);
        localCity = binding.localCityTextView;

        binding.buttonUpdateLocalCity.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                binding.viewsLocalCitiesUpdate.setVisibility(GONE);
                binding.editTextLocalCities.setVisibility(View.VISIBLE);
            }
        });

        binding.buttonEnterLocalCity.setOnClickListener(v -> {
            String localCityText = binding.enterLocalCityEditText.getText().toString();

            if (localCityText.equals("")) {
                binding.enterLocalCityEditText.setError("Required.");

            } else {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("localCity", localCityText);
                editor.apply();

                localCity.setText(localCityText);

                binding.editTextLocalCities.setVisibility(GONE);
                //TODO: hiding keyboard here does not work. figure out to hide keyboard
                hideSoftKeyboard();
                binding.viewsLocalCitiesUpdate.setVisibility(View.VISIBLE);
            }
        });

        binding.buttonAddLocalCity.setOnClickListener(v -> {
            // hide the other two views (Already rendered with visibility gone for the other two views)
            binding.viewsLocalCitiesAdd.setVisibility(GONE);
            binding.editTextLocalCities.setVisibility(View.VISIBLE);
        });

        // get local cities from SP, if none are found, hide the ListView and show only a textView with button to add a home city.
        // if home city is already added, then show a button to update
        String localCitySP = sharedPreferences.getString("localCity", null);
        if (localCitySP == null || localCitySP.equals("")) {
            // no cities, show add views
            binding.viewsLocalCitiesAdd.setVisibility(View.VISIBLE);
        } else {
            animation = AnimationUtils.loadAnimation(getActivity(), R.anim.animation1);
            localCity.setText(localCitySP);
            binding.viewsLocalCitiesUpdate.setVisibility(View.VISIBLE);
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

        private void hideSoftKeyboard(){
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}