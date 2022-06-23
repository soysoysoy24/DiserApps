package com.lemonsquare.diserapps.ListAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lemonsquare.diserapps.Models.PriceSurveyModel;
import com.lemonsquare.diserapps.R;

import java.util.List;

public class PriceSurveyAdapter  extends ArrayAdapter {

    private  Context context;
    private int resource;
    private List<PriceSurveyModel> prceList;

    public PriceSurveyAdapter(Context context, int resource, List<PriceSurveyModel> prceList) {
        super(context, resource, prceList);

        this.context = context;
        this.resource = resource;
        this.prceList = prceList;
    }

    @Override
    public int getCount() {

        if (prceList==null)
        {
            return 0;
        }
        else
        {
            return prceList.size();
        }

    }


    @Override
    public Object getItem(int position) {
        return prceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class ViewPrcHldr
    {
        public TextView prceHldr,prdctHldr;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewPrcHldr holder = new ViewPrcHldr();

        if (rowView ==null)
        {
            LayoutInflater BoInflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = BoInflate.inflate(resource,parent,false);

            holder.prdctHldr = (TextView) rowView.findViewById(R.id.qtyTxt);
            holder.prceHldr = (TextView) rowView.findViewById(R.id.ExpDteTxt);
            rowView.setTag(holder);
        }
        else
        {
            holder = (ViewPrcHldr) rowView.getTag();
        }


       holder.prdctHldr.setText(prceList.get(position).getPrdct());


        holder.prceHldr.setGravity(Gravity.LEFT);
        holder.prceHldr.setText(""+prceList.get(position).getPrce());


        return  rowView;
    }

}
