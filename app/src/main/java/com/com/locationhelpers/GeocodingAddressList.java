package com.com.locationhelpers;

import java.util.ArrayList;

public class GeocodingAddressList {
	
	String formatted_address;
	ArrayList<AddressComponents> address_components;
	ArrayList<String> types;
	
	public String getFormatted_address() {
		return formatted_address;
	}

	public void setFormatted_address(String formatted_address) {
		this.formatted_address = formatted_address;
	}
	
	public ArrayList<AddressComponents> getAddress_components() {
		return address_components;
	}
	public void setAddress_components(
			ArrayList<AddressComponents> address_components) {
		this.address_components = address_components;
	}

	public ArrayList<String> getTypes() {
		return types;
	}
}
