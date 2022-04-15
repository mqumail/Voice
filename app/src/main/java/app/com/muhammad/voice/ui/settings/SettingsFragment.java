package app.com.muhammad.voice.ui.settings;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static app.com.muhammad.voice.util.Constants.MY_PREFERENCES;
import static app.com.muhammad.voice.util.Constants.USER_INFO_KEY;
import static app.com.muhammad.voice.util.UiHelperMethods.hideSoftKeyboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Iterator;
import java.util.Set;

import app.com.muhammad.voice.LoginActivity;
import app.com.muhammad.voice.R;
import app.com.muhammad.voice.databinding.FragmentSettingsBinding;

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

    private TextView userEmailTextView;
    private Button logOutButton;
    private Button logInButton;
    private EditText displayName;
    private Button saveDisplayButton;

    public SettingsFragment()
    {
        // Required empty public constructor
    }


    public static SettingsFragment newInstance() {
        return new SettingsFragment();
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

        SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);
        localCity = binding.localCityTextView;

        binding.buttonUpdateLocalCity.setOnClickListener(v -> {
            binding.viewsLocalCitiesUpdate.setVisibility(GONE);
            binding.editTextLocalCities.setVisibility(View.VISIBLE);
        });
        displayName = binding.userDisplayName;
        saveDisplayButton = binding.userDisplayNameButton;
        saveDisplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (displayName.getText().toString().equals("")) {
                    displayName.setError("Please enter a Display Name! You can enter 'Anonymous' for anonymous viewing.");
                } else if (!displayName.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
                } else {
                    hideSoftKeyboard(getActivity());
                    Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("displayName", binding.userDisplayName.getText().toString());
                    editor.apply();
                }
            }
        });

        LinearLayout userSignInContent = binding.userIdentityContent;
        logInButton = userSignInContent.findViewById(R.id.log_in_button);
        logOutButton = userSignInContent.findViewById(R.id.logout_button);

        userEmailTextView = userSignInContent.findViewById(R.id.user_email);

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signIn = new Intent(getActivity(), LoginActivity.class);
                startActivity(signIn);
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                userEmailTextView.setText("Not signed in yet!");
                logInButton.setVisibility(View.VISIBLE);
                logOutButton.setVisibility(GONE);
                displayName.setText("");
            }
        });

        binding.buttonEnterLocalCity.setOnClickListener(v -> {
            String localCityText = binding.enterLocalCityEditText.getText().toString();

            if (localCityText.equals("")) {
                binding.enterLocalCityEditText.setError("Required.");
            } else {
                hideSoftKeyboard(getActivity());

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("localCity", localCityText);
                editor.apply();
                localCity.setText(localCityText);

                binding.editTextLocalCities.setVisibility(GONE);
                binding.viewsLocalCitiesUpdate.setVisibility(View.VISIBLE);
            }
        });

        binding.buttonAddLocalCity.setOnClickListener(v -> {
            // hide the other two views (Already rendered with visibility gone for the other two views)
            binding.viewsLocalCitiesAdd.setVisibility(GONE);
            binding.viewsLocalCitiesUpdate.setVisibility(GONE);
            binding.editTextLocalCities.setVisibility(View.VISIBLE);
        });

        // get local cities from SP, if none are found, hide the ListView and show only a textView with button to add a home city.
        // if home city is already added, then show a button to update
        String localCitySP = sharedPreferences.getString("localCity", null);
        if (localCitySP == null || localCitySP.equals("")) {
            // no cities, show add views
            binding.viewsLocalCitiesAdd.setVisibility(View.VISIBLE);
            binding.viewsLocalCitiesUpdate.setVisibility(View.INVISIBLE);
        } else {
            animation = AnimationUtils.loadAnimation(getActivity(), R.anim.animation1);
            localCity.setText(localCitySP);
            binding.viewsLocalCitiesUpdate.setVisibility(View.VISIBLE);
        }

        return root;
    }

    //TODO: when user clicks sign in button, after they sign in, update the view to show the signed in user
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences preferences =  getContext().getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);
        Set<String> userInfo = preferences.getStringSet(USER_INFO_KEY, null);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        String userName = "Anonymous";
        String email = "";
        if (userInfo != null) {
            Iterator<String> iterator = userInfo.iterator();
            userName = iterator.next();
            email = iterator.next();
        }

        if (null == user) {
            userEmailTextView.setText("Not signed in yet!");
            userEmailTextView.setClickable(false);
            logInButton.setVisibility(View.VISIBLE);
            logOutButton.setVisibility(GONE);
            displayName.setText("");
            saveDisplayButton.setVisibility(GONE);
        } else {
            userEmailTextView.setText("signed in as: " + user.getEmail());
            userEmailTextView.setClickable(true);
            logInButton.setVisibility(GONE);
            logOutButton.setVisibility(View.VISIBLE);
            displayName.setText(userName);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}