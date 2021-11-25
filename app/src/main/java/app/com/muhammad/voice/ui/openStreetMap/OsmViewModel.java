package app.com.muhammad.voice.ui.openStreetMap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OsmViewModel extends ViewModel
{

    private MutableLiveData<String> mText;

    public OsmViewModel()
    {
        mText = new MutableLiveData<>();
        mText.setValue("This is Osm fragment");
    }

    public LiveData<String> getText()
    {
        return mText;
    }
}