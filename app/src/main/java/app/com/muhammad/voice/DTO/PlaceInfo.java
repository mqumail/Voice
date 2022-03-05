package app.com.muhammad.voice.DTO;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class PlaceInfo
{
    @NonNull
    private String id;
    private String name;
    private String address;
    private String phoneNumber;
    private Uri websiteUri;
    private double rating;
    private String attributions;
    private ArrayList<CheckIn> checkIns;

    public PlaceInfo() { }

    public PlaceInfo(String name, String address, String phoneNumber, String id, Uri websiteUri, double rating, String attributions, ArrayList<CheckIn> checkIns, boolean isIdentifiedCheckIn, boolean isHearted, boolean isLocalCheckIn) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.websiteUri = websiteUri;
        this.rating = rating;
        this.attributions = attributions;
        this.checkIns = checkIns;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Uri getWebsiteUri() {
        return websiteUri;
    }

    public void setWebsiteUri(Uri websiteUri) {
        this.websiteUri = websiteUri;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getAttributions() {
        return attributions;
    }

    public void setAttributions(String attributions) {
        this.attributions = attributions;
    }

    public ArrayList<CheckIn> getCheckIns() {
        return checkIns;
    }

    public void setCheckIns(ArrayList<CheckIn> checkIns) {
        this.checkIns = checkIns;
    }



    @Override
    public String toString() {
        return "PlaceInfo{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", id='" + id + '\'' +
                ", websiteUri=" + websiteUri +
                ", rating=" + rating +
                ", attributions='" + attributions + '\'' +
                ", checkIns=" + checkIns +
                '}';
    }
}
