package app.com.muhammad.voice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void signUpButtonClick(View view)
    {
        Intent intent = new Intent(this, SignUpActivity.class);
        this.startActivity(intent);
    }

    public void signIn(View view)
    {
        // Validate username/password
        if (validateCredentials())
        {
            Intent intent = new Intent(this, HomeScreenActivity.class);
            this.startActivity(intent);
        }
        else
        {
            TextView textViewInvalidUserPass = findViewById(R.id.textViewInvalidUserPass);
            TextView textViewResetPassword = findViewById(R.id.textViewPasswordReset);

            textViewInvalidUserPass.setVisibility(View.VISIBLE);
            textViewResetPassword.setVisibility(View.VISIBLE);
        }
    }

    public void resetPassword(View view)
    {

    }

    private boolean validateCredentials()
    {
        // TODO: add validation logic.
        return true;
    }

}
