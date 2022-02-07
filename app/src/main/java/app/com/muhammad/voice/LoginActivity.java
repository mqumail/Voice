package app.com.muhammad.voice;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import app.com.muhammad.voice.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity
{
    private static final String ACTIVITY_TAG = "LoginActivity";

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

        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = binding.emailLoginText.getText().toString();
                String password = binding.passwordLoginText.getText().toString();
                signIn(email, password);
            }
        });

        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent register = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(register);
            }
        });
    }

    private void signIn(String email, String password)
    {
        Log.d(ACTIVITY_TAG, "SignIn: " + email);

        if (!validateForm()) {
            return;
        }

        // start auth process
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information - store to SharedPreferences TODO
                            Log.d(ACTIVITY_TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // add the above user info to SP - TODO
                            // continue to the home screen
                            Intent baseActivity = new Intent(LoginActivity.this, BaseActivity.class);
                            startActivity(baseActivity);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(ACTIVITY_TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // TODO: Move this to string utils
    private boolean validateForm()
    {
        boolean valid = true;

        String email = binding.emailLoginText.getText().toString();
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
}