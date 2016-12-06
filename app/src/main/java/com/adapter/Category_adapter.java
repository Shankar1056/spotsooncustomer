package com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.spotsoon.customer.R;
import com.utility.Utility;
import java.util.ArrayList;

/**
 * Created by embed on 7/8/15.
 * class Category_adapter
 */
public class Category_adapter extends ArrayAdapter
{
    private Context mContext;
    ArrayList<String> categories=new ArrayList<String>();

    public Category_adapter( Context mContext, ArrayList<String> categories){
        super(mContext, 0, categories);
        this.mContext = mContext;
        this.categories = categories;

    }

    @Override
    public int getCount() {
        return super.getCount();
    }
    public String getItem(int position){
        return categories.get(position);
    }

    private class ViewHolder
    {
        TextView cat_name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        categories.get(position);
        ViewHolder holder;

        if(convertView==null||convertView.getTag()==null)
        {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.category_item, parent, false);

            holder.cat_name= (TextView) convertView.findViewById(R.id.cat_item);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        Utility.printLog("postion "+getItem(position));
        holder.cat_name.setId(position);

        holder.cat_name.setText(getItem(position));

        return convertView;
    }

}
