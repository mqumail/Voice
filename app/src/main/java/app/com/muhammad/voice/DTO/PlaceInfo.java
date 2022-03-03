package app.com.muhammad.voice.DTO;

import android.net.Uri;

import androidx.annotation.NonNull;

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
    private String comment;
    private CheckInInfo checkinInfo;

    public PlaceInfo() { }

    public PlaceInfo(String name, String address, String phoneNumber, String id, Uri websiteUri, double rating, String attributions, String comment, CheckInInfo checkinInfo, boolean isIdentifiedCheckIn, boolean isHearted, boolean isLocalCheckIn) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.websiteUri = websiteUri;
        this.rating = rating;
        this.attributions = attributions;
        this.comment = comment;
        this.checkinInfo = checkinInfo;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public CheckInInfo getCheckinInfo() {
        return checkinInfo;
    }

    public void setCheckinInfo(CheckInInfo checkinInfo) {
        this.checkinInfo = checkinInfo;
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
                ", comment='" + comment + '\'' +
                ", checkinInfo=" + checkinInfo +
                '}';
    }
}
