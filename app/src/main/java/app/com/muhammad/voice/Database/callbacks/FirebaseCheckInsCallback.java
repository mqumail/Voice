package app.com.muhammad.voice.Database.callbacks;

import java.util.ArrayList;

import app.com.muhammad.voice.DTO.CheckIn;

public interface FirebaseCheckInsCallback {
    void getCheckInsCallBack(ArrayList<CheckIn> checkIns);
}
