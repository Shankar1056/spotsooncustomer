package com.threembed.homebean;

import java.io.Serializable;

/**
 * Created by embed on 12/6/15.
 */
public class ProductImage implements Serializable {
    private String Url;
    private String Height;
    private String Width;

    public String getUrl() {
        return Url;
    }



    public String getHeight() {
        return Height;
    }



    public String getWidth() {
        return Width;
    }


}
