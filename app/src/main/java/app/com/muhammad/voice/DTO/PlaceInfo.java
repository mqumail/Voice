package app.com.muhammad.voice.DTO;

import java.io.Serializable;
import java.util.ArrayList;

public class PlaceInfo implements Serializable
{
    public PlaceInfo(String id, String name, String address, String phoneNumber, String websiteUri, com.google.firebase.firestore.GeoPoint latLng, double rating, String attributions, int markerIndex, ArrayList<CheckIn> checkIns) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.websiteUri = websiteUri;
        this.latLng = latLng;
        this.rating = rating;
        this.attributions = attributions;
        this.markerIndex = markerIndex;
        this.checkIns = checkIns;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getWebsiteUri() {
        return websiteUri;
    }

    public void setWebsiteUri(String websiteUri) {
        this.websiteUri = websiteUri;
    }

    public com.google.firebase.firestore.GeoPoint getLatLng() {
        return latLng;
    }

    public void setLatLng(com.google.firebase.firestore.GeoPoint latLng) {
        this.latLng = latLng;
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

    public int getMarkerIndex() {
        return markerIndex;
    }

    public void setMarkerIndex(int markerIndex) {
        this.markerIndex = markerIndex;
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
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", websiteUri='" + websiteUri + '\'' +
                ", latLng=" + latLng +
                ", rating=" + rating +
                ", attributions='" + attributions + '\'' +
                ", markerIndex=" + markerIndex +
                ", checkIns=" + checkIns +
                '}';
    }

    private String id;
    private String name;
    private String address;
    private String phoneNumber;
    private String websiteUri;
    private com.google.firebase.firestore.GeoPoint latLng;
    private double rating;
    private String attributions;
    private int markerIndex;
    private ArrayList<CheckIn> checkIns;

    public PlaceInfo() { }
}
