package app.com.muhammad.voice;

import static app.com.muhammad.voice.util.Constants.MY_PREFERENCES;
import static app.com.muhammad.voice.util.Constants.USER_INFO_KEY;
import static app.com.muhammad.voice.util.UiHelperMethods.hideSoftKeyboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashSet;
import java.util.Set;

import app.com.muhammad.voice.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity
{
    int count = 1;
    private static final String TAG = "LoginActivity";

    private ActivityLoginBinding binding;

    private EditText email;
    private EditText password;
    private Button login;
    private Button register;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        // TODO: Validate the email and password when user presses login button
        email = binding.emailLoginText;
        password = binding.passwordLoginText;
        login = binding.buttonLogin;
        register = binding.buttonRegister;

        login.setOnClickListener(v -> {
            hideSoftKeyboard(this);
            String email = binding.emailLoginText.getText().toString();
            String password = binding.passwordLoginText.getText().toString();
            signIn(email, password);
        });

        register.setOnClickListener(v -> {
            Intent register = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(register);
            finish();
        });
        Log.d(TAG, "onCreateView: Called - " + count + " times");
        count = count+1;
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "SignIn: " + email);
        if (!validateForm()) return; // Do not process before validating. TODO - need to validate all or use library
        // start auth process
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        saveUserInfo(email, mAuth.getCurrentUser().getDisplayName());
                        // Sign in success, update UI with the signed-in user's information - store to SharedPreferences TODO
                        Log.d(TAG, "signInWithEmail:success: Signed in as - " + mAuth.getCurrentUser().getEmail());
                        Intent login = new Intent(this, BaseActivity.class);
                        startActivity(login);
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // TODO: Move this to string utils
    private boolean validateForm() {
        String email = binding.emailLoginText.getText().toString();
        boolean valid = true;
        if (TextUtils.isEmpty(email)) {
            binding.emailLoginText.setError("Required.");
            valid = false;
        } else {
            binding.emailLoginText.setError(null);
        }
        String password = binding.passwordLoginText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            binding.passwordLoginText.setError("Required.");
            valid = false;
        } else {
            binding.passwordLoginText.setError(null);
        }
        return valid;
    }

    public void saveUserInfo(String email, String userName) {
        Set<String> userInfo = new HashSet<>();
        userInfo.add(email);
        userInfo.add(userName);
        SharedPreferences preferences = getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);
        preferences.edit().putStringSet(USER_INFO_KEY,userInfo).apply();

        Toast.makeText(this, "Login Successful! Continuing as: " + userName,
                Toast.LENGTH_LONG).show();
    }
}