package app.com.muhammad.voice.ui.checkIn;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CheckInViewModel extends ViewModel
{

    private MutableLiveData<String> mText;

    public CheckInViewModel()
    {
        mText = new MutableLiveData<>();
        mText.setValue("This is Check-In fragment");
    }

    public LiveData<String> getText()
    {
        return mText;
    }
}