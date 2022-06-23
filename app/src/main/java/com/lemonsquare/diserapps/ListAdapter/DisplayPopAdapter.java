package com.lemonsquare.diserapps.ListAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lemonsquare.diserapps.Models.DisplayModel;
import com.lemonsquare.diserapps.R;

import java.util.List;


public class DisplayPopAdapter  extends ArrayAdapter {

    private Context context;
    private int resource;
    private List<DisplayModel> getDataDisplay;

    public DisplayPopAdapter(Context context, int resource, List<DisplayModel> getDataDisplay) {
        super(context, resource, getDataDisplay);

        this.context = context;
        this.resource = resource;
        this.getDataDisplay = getDataDisplay;

    }

    @Override
    public int getCount() {

        if (getDataDisplay==null)
        {
            return 0;
        }
        else
        {
            return getDataDisplay.size();
        }

    }


    @Override
    public Object getItem(int position) {
        return getDataDisplay.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class ViewDisplyHldr
    {
        public TextView assetHldr,dispHldr;
        public ImageView dispImgHldr;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewDisplyHldr holder = new ViewDisplyHldr();

        if (rowView ==null)
        {
            LayoutInflater BoInflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = BoInflate.inflate(resource,parent,false);

            holder.dispHldr = (TextView) rowView.findViewById(R.id.displayTpTxt);
            holder.assetHldr = (TextView) rowView.findViewById(R.id.assetTxt);
            holder.dispImgHldr = (ImageView) rowView.findViewById(R.id.imgDisp);
            rowView.setTag(holder);
        }
        else
        {
            holder = (ViewDisplyHldr) rowView.getTag();
        }


        byte[] decodedString = Base64.decode(getDataDisplay.get(position).getImgDisp(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        //dispImgHldr.setImageBitmap(decodedByte);

        holder.dispImgHldr.setImageBitmap(decodedByte);
        holder.dispHldr.setText(getDataDisplay.get(position).getDispcat());
        holder.assetHldr.setText(getDataDisplay.get(position).getAsstno());


        return  rowView;
    }

}
