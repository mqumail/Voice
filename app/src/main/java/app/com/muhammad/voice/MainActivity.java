package app.com.muhammad.voice;

import android.app.Activity;
import android.content.Intent;
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

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import app.com.muhammad.voice.utils.LocalCity;
import app.com.muhammad.voice.utils.SharedPreferencesManagement;
import app.com.muhammad.voice.utils.UserInformation;

public class MainActivity extends AppCompatActivity
{

    //Firebase
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    //function to change screen automatically
                    if (readPreferences(user.getUid())){
                        redirect(new HomeScreenActivity(), 1000);
                    } else if (!readPreferences(user.getUid())){
                        redirect(new SignUpActivity(), 1000);
                    }
                }else{
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
                //The user is signed in
            }else if(resultCode == RESULT_CANCELED){
                //The sign in process is canceled
                finish();
            }
        }
    }

    private void redirect(final Activity activity, int time){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), activity.getClass()));
                finish();
            }
        },time);
    }

    @Override
    protected void onResume()
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

    private boolean readPreferences(String UID) {
        boolean isData;
        String mUser = UID;
        LocalCity localCities = new LocalCity(mUser, this);
        String savedCities = localCities.getCitiesString();
        if(savedCities.equals("NA") || savedCities.equals("")){
            isData = false; }
            else{
            isData = true;
        }
        return isData;
    }

}