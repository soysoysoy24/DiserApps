package com.lemonsquare.diserapps.ListAdapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lemonsquare.diserapps.Models.DiserModel;
import com.lemonsquare.diserapps.Models.InOutModel;
import com.lemonsquare.diserapps.R;

import java.util.ArrayList;
import java.util.List;


public class AttendanceAdapter  extends ArrayAdapter {

    private Context context;
    private int resource;
    private List<InOutModel> attndnceData;
    private int type;

    public AttendanceAdapter(Context context, int resource, List<InOutModel> attndnceData, int type) {
        super(context, resource, attndnceData);

        this.context = context;
        this.resource = resource;
        this.attndnceData = attndnceData;
        this.type = type;
    }


    @Override
    public int getCount() {

        if (attndnceData==null)
        {
            return 0;
        }
        else
        {
            return attndnceData.size();
        }

    }

    @Override
    public Object getItem(int position) {
        return attndnceData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class ViewAttndnceHldr
    {
        public TextView dateTxtHldr,CustTxtHldr,TmeChkInHldr,TmeChkOutHldr,inTitleHldr,outTitleHldr;
        public ImageView locHldr;
        public RelativeLayout custLayHldr;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewAttndnceHldr holder = new ViewAttndnceHldr();

        if (rowView ==null)
        {
            LayoutInflater BoInflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = BoInflate.inflate(resource,parent,false);

            holder.dateTxtHldr = (TextView) rowView.findViewById(R.id.dteTxt);
            holder.CustTxtHldr = (TextView) rowView.findViewById(R.id.custTxt);
            holder.TmeChkInHldr = (TextView) rowView.findViewById(R.id.inTxt);
            holder.TmeChkOutHldr = (TextView) rowView.findViewById(R.id.outTxt);
            holder.inTitleHldr = (TextView) rowView.findViewById(R.id.inTitleTxt);
            holder.outTitleHldr = (TextView) rowView.findViewById(R.id.outTitleTxt);
            holder.locHldr = (ImageView) rowView.findViewById(R.id.locBtn);
            holder.custLayHldr = (RelativeLayout) rowView.findViewById(R.id.custLay);
            rowView.setTag(holder);
        }
        else
        {
            holder = (ViewAttndnceHldr) rowView.getTag();
        }


        if (type == 1)
        {
            holder.custLayHldr.setVisibility(View.GONE);
            holder.inTitleHldr.setText("TIME IN: ");
            holder.outTitleHldr.setText("TIME OUT: ");
            holder.dateTxtHldr.setText(attndnceData.get(position).getDate());
            holder.TmeChkInHldr.setText(attndnceData.get(position).getTmein());
            holder.TmeChkOutHldr.setText(attndnceData.get(position).getTmeout());

        }
        else
        {
            holder.inTitleHldr.setText("CHECK IN: ");
            holder.outTitleHldr.setText("CHECK OUT: ");
            holder.dateTxtHldr.setText(attndnceData.get(position).getDate());
            holder.CustTxtHldr.setText(attndnceData.get(position).getCustalias());
            holder.TmeChkInHldr.setText(attndnceData.get(position).getTmein());
            holder.TmeChkOutHldr.setText(attndnceData.get(position).getTmeout());
        }

        return  rowView;
    }

}
