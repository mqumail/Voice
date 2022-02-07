package app.com.muhammad.voice;

import android.content.Intent;
import android.os.Bundle;
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
import app.com.muhammad.voice.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity  {

    private static final String ACTIVITY_TAG = "SignUpActivity";

    private ActivitySignUpBinding binding;

    private EditText fullName;
    private EditText userName;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private Button register;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        fullName = binding.fullNameRegister;
        userName = binding.usernameRegister;
        email = binding.emailRegister;
        password = binding.passwordRegister;
        confirmPassword = binding.confirmPasswordRegister;
        register = binding.buttonRegister;

        //TODO: code goes here to sign a new user up
        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = binding.emailRegister.getText().toString();
                String password = binding.passwordRegister.getText().toString();
                signUp(email, password);

                continueHome();
            }
        });
    }

    private void signUp(String email, String password) {

        //Todo: Put all Auth and Firebase stuff in a util class
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(ACTIVITY_TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //TODO: store new user in SP
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(ACTIVITY_TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void continueHome()
    {
        Intent intent = new Intent(SignUpActivity.this, BaseActivity.class);
        startActivity(intent);
    }
}