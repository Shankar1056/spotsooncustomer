package com.threembed;

/**
 * Created by VARUN on 12/6/2015.
 */
public class NavigationItem {

    String name,email;
    int resourceId;
    boolean isHeader;

    public NavigationItem(String name, String email, int resourceId, boolean isHeader) {
        this.name = name;
        this.email = email;
        this.resourceId = resourceId;
        this.isHeader = isHeader;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setIsHeader(boolean isHeader) {
        this.isHeader = isHeader;
    }
}
