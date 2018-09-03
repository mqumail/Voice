package app.com.muhammad.voice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
{

    public static final String ANONYMOUS = "anonymous";

    //Firebase
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private SharedPreferences preferences;

    private static final int RC_SIGN_IN = 1;

    private String mUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = ANONYMOUS;

        //Initialize Firebase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        //Initialize Firebase listener
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build());

                if(user != null){
                    //Toast.makeText(MainActivity.this, "You are now signed in", Toast.LENGTH_SHORT).show();
                    onSignedInInitialize(user.getDisplayName());
                    //function to change screen automatically
                    if (readPreferences("localCities", getApplicationContext())){
                        redirect(new HomeScreenActivity());
                    } else if (!readPreferences("localCities", getApplicationContext())){
                        redirect(new SignUpActivity());
                    }

                }else{
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

    }

    //Firebase authentication states
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                //Toast.makeText(this, "Signed in", Toast.LENGTH_SHORT).show();
                //function to change screen automatically
                if(readPreferences("localCities", getApplicationContext())){
                    redirect(new HomeScreenActivity());
                } else if (!readPreferences("localCities", getApplicationContext())){
                    redirect(new SignUpActivity());
                }
            }else if(resultCode == RESULT_CANCELED){
                //Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void redirect(final Activity activity){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), activity.getClass()));
            }
        },2000);
    }

    @Override
    protected  void onResume()
    {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    private void onSignedInInitialize(String username){
        mUsername = username;
    }

    private void onSignedOutCleanup()
    {
        mUsername = ANONYMOUS;
    }

    private boolean readPreferences(String key, Context context) {
        boolean isData;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String savedCities = preferences.getString(key, "empty");
        Toast.makeText(this, savedCities, Toast.LENGTH_SHORT).show();
        if(savedCities.equals("empty") || !savedCities.equals("")){
            isData = true; }
            else{
            isData = false;
        }
        return isData;
    }

}