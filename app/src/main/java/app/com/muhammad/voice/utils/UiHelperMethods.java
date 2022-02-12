package app.com.muhammad.voice.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import app.com.muhammad.voice.R;
import app.com.muhammad.voice.ui.checkIn.CheckInFragment;
import app.com.muhammad.voice.ui.openStreetMap.OsmFragment;
import app.com.muhammad.voice.ui.profile.ProfileFragment;
import app.com.muhammad.voice.ui.settings.SettingsFragment;

public class UiHelperMethods extends AppCompatActivity {

    public static void replaceContentContainer(int contentViewId, FragmentManager fragmentManager){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setReorderingAllowed(true);
        if (contentViewId == R.id.nav_osm){
            Fragment fragment = new OsmFragment();
            transaction.replace(R.id.content_container, fragment, null);
        } else if (contentViewId == R.id.nav_profile){
            Fragment fragment = new ProfileFragment();
            transaction.replace(R.id.content_container, fragment, null);
        } else if (contentViewId == R.id.nav_settings){
            Fragment fragment = new SettingsFragment();
            transaction.replace(R.id.content_container, fragment, null);
        } else if (contentViewId == R.id.nav_check_in){
            Fragment fragment = new CheckInFragment();
            transaction.replace(R.id.content_container, fragment, null);
        }
        transaction.commit();
    }
    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
