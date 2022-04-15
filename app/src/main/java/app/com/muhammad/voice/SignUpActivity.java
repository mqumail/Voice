package app.com.muhammad.voice;

import static android.content.ContentValues.TAG;
import static app.com.muhammad.voice.util.Constants.MY_PREFERENCES;
import static app.com.muhammad.voice.util.Constants.USER_INFO_KEY;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.HashSet;
import java.util.Set;

import app.com.muhammad.voice.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity  {

    private static final String ACTIVITY_TAG = "SignUpActivity";

    private ActivitySignUpBinding binding;

    private EditText fullNameEditText;
    private EditText userNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button registerButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        fullNameEditText = binding.fullNameRegister;
        userNameEditText = binding.usernameRegister;
        emailEditText = binding.emailRegister;
        passwordEditText = binding.passwordRegister;
        confirmPasswordEditText = binding.confirmPasswordRegister;
        registerButton = binding.buttonRegisterSignup;

        registerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO: Run validation here. This can happen after thesis is done

                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String userName = userNameEditText.getText().toString();
                signUp(email, password, userName);
            }
        });
    }

    private void signUp(String email, String password, String userName) {
        //Todo: Put all Auth and Firebase stuff in a util class
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(ACTIVITY_TAG, "createUserWithEmail:success");
                            saveUserInfo(email, userName);
                            finish();
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Log.w(ACTIVITY_TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "We are unable to register you at this time. Please try again later.",
                                    Toast.LENGTH_SHORT).show();
                            //TODO: Give user an option on what todo when they can not register
                        }
                    }
                });
    }

    public void saveUserInfo(String email, String userName) {
        Set<String> userInfo = new HashSet<>();
        userInfo.add(email);
        userInfo.add(userName);
        SharedPreferences preferences = getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);
        preferences.edit().putStringSet(USER_INFO_KEY,userInfo).apply();

        Toast.makeText(this, "Registration Successful! Continuing as: " + userName,
                Toast.LENGTH_LONG).show();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .build();

        mAuth.getCurrentUser().updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "User profile updated.");
                        }
                    }
                });
    }
}