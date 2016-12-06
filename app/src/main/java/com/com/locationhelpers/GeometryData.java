package com.com.locationhelpers;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;


public class GeometryData {
	@SerializedName("location")
	private LocationData objLocation;

	private LocationData distance;
	private LocationData duration;
	private String long_name,short_name;
	private ArrayList<String> types;

	public LocationData getObjLocation() {
		return objLocation;
	}

	public LocationData getDistance() {
		return distance;
	}

	public void setDistance(LocationData distance) {
		this.distance = distance;
	}

	public LocationData getDuration() {
		return duration;
	}

	public void setDuration(LocationData duration) {
		this.duration = duration;
	}

	public String getLong_name() {
		return long_name;
	}

	public void setLong_name(String long_name) {
		this.long_name = long_name;
	}

	public String getShort_name() {
		return short_name;
	}

	public void setShort_name(String short_name) {
		this.short_name = short_name;
	}

	public ArrayList<String> getTypes() {
		return types;
	}

	public void setTypes(ArrayList<String> types) {
		this.types = types;
	}
}
