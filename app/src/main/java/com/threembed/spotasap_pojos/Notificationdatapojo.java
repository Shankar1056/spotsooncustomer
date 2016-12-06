package com.threembed.spotasap_pojos;

/**
 * Created by embed-pc on 27/10/15.
 */
public class Notificationdatapojo implements Comparable
{
    private String Business_id;
    private String Messgae;
    private String datetime;
    private String businesstype;
    private boolean readcheck;
    private String status;
    private String simpleBusiness_Id;

    public String getSimpleBusiness_Id() {
        return simpleBusiness_Id;
    }

    public void setSimpleBusiness_Id(String simpleBusiness_Id) {
        this.simpleBusiness_Id = simpleBusiness_Id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isReadcheck()
    {
        return readcheck;
    }

    public void setReadcheck(boolean readcheck)
    {
        this.readcheck = readcheck;
    }

    public String getBusinesstype()
    {
        return businesstype;
    }
    public void setBusinesstype(String businesstype)
    {
        this.businesstype = businesstype;
    }

    public String getDatetime()
    {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getBusiness_id()
    {
        return Business_id;
    }

    public void setBusiness_id(String business_id)
    {
       this.Business_id = business_id;
    }

    public String getMessgae()
    {
        return Messgae;
    }

    public void setMessgae(String messgae)
    {
        this.Messgae = messgae;
    }
/**
 * Implementing the Comparable method to do sorting of the arrayList..
 * <p>
 *  Here First checking if the Item is clicked or not .
 *  by checking the Flag of the {@code isReadcheck() } method ;
 *  and if checked then pushing that item to first.
 *  If both the item have Checked then Arranging them to a according to the Given time.
 * </p>
 * @param another new Object to compare to the initial*/
    @Override
    public int compareTo(Object another)
    {
        Notificationdatapojo notificationdatapojo=(Notificationdatapojo)another;

        if(notificationdatapojo.isReadcheck()&&this.isReadcheck())
        {
            return notificationdatapojo.getDatetime().compareTo(this.getDatetime());

        }else if(!this.isReadcheck()&&!notificationdatapojo.isReadcheck())
        {
            return notificationdatapojo.getDatetime().compareTo(this.getDatetime());

        }else
        {
            if(this.isReadcheck())
            {
                return -1;
            }
            else
            {
                return +1;
            }
        }
    }

}
