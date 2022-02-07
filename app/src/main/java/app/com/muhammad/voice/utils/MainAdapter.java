package app.com.muhammad.voice.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import app.com.muhammad.voice.R;
import app.com.muhammad.voice.ui.settings.SettingsFragment;

public class MainAdapter extends BaseAdapter
{
    SettingsFragment settingsFragment;
    String[] localCities;
    Animation animation;


    public MainAdapter(SettingsFragment settingsFragment, String[] localCities) {

        this.settingsFragment = settingsFragment;
        this.localCities = localCities;
    }

    public static int getRandom(int max){
        return (int) (Math.random()*max);
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount()
    {
        return localCities.length;
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position)
    {
        return position;
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.local_city_item, parent, false);
        animation = AnimationUtils.loadAnimation(parent.getContext(), R.anim.animation1);

        TextView textView = convertView.findViewById(R.id.local_cities_city_text_view);
        LinearLayout linearLayout = convertView.findViewById(R.id.local_cities_city);

        //TODO: when user updates theirs cities, re show the list and refresh it so new entry user entered is visibile

//        int number = getRandom(8);
//
//        switch (number){
//            case 0: linearLayout.setBackground(ContextCompat.getDrawable(parent.getContext(), R.drawable.gradient_1));
//                    break;
//            case 1: linearLayout.setBackground(ContextCompat.getDrawable(parent.getContext(), R.drawable.gradient_2));
//                    break;
//            case 3: linearLayout.setBackground(ContextCompat.getDrawable(parent.getContext(), R.drawable.gradient_3));
//                break;
//            case 4: linearLayout.setBackground(ContextCompat.getDrawable(parent.getContext(), R.drawable.gradient_4));
//                break;
//            case 5: linearLayout.setBackground(ContextCompat.getDrawable(parent.getContext(), R.drawable.gradient_5));
//                break;
//            case 6: linearLayout.setBackground(ContextCompat.getDrawable(parent.getContext(), R.drawable.gradient_6));
//                break;
//            case 7: linearLayout.setBackground(ContextCompat.getDrawable(parent.getContext(), R.drawable.gradient_7));
//                break;
//            case 8: linearLayout.setBackground(ContextCompat.getDrawable(parent.getContext(), R.drawable.gradient_8));
//                break;
//            default: linearLayout.setBackground(ContextCompat.getDrawable(parent.getContext(), R.drawable.snow_bg));
//                break;
//        }

        textView.setText("Weimar, DE");
        textView.setAnimation(animation);

        return convertView;
    }
}
