package com.threembed.spotasap_pojos;

import java.io.Serializable;

/**
 * Created by embed on 15/9/15.
 */

public class Items implements Serializable
{
    private String Status;

    private String ItemId;

    private String Quantity;

    private String ItemName;

    private String Price;

    public String getStatus ()
    {
        return Status;
    }



    public String getItemId ()
    {
        return ItemId;
    }



    public String getQuantity ()
    {
        return Quantity;
    }



    public String getItemName ()
    {
        return ItemName;
    }



    public String getPrice ()
    {
        return Price;
    }

    public void setPrice (String Price)
    {
        this.Price = Price;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [Status = "+Status+", ItemId = "+ItemId+", Quantity = "+Quantity+", ItemName = "+ItemName+", Price = "+Price+"]";
    }
}