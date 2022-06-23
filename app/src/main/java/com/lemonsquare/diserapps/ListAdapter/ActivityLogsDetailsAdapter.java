package com.lemonsquare.diserapps.ListAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lemonsquare.diserapps.Models.MaterialModel;
import com.lemonsquare.diserapps.R;

import java.util.List;

public class ActivityLogsDetailsAdapter extends ArrayAdapter {

    private Context context;
    private int resource;
    private List<MaterialModel> logsData;

    public ActivityLogsDetailsAdapter(@NonNull Context context, int resource,List<MaterialModel> logsData) {
        super(context, resource);

        this.context = context;
        this.resource = resource;
        this.logsData = logsData;
    }
    @Override
    public int getCount() {

        if (logsData==null)
        {
            return 0;
        }
        else
        {
            return logsData.size();
        }

    }

    @Override
    public Object getItem(int position) {
        return logsData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class ViewLogsDetsHldr
    {
        public TextView actvtyTxtHldr,CustTxtHldr,dteTxtHldr,viewDetsTxtHldr;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
      //  ActivityLogsAdapter.ViewLogsHldr holder = new ActivityLogsAdapter.ViewLogsHldr();

        if (rowView ==null)
        {
            LayoutInflater BoInflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = BoInflate.inflate(resource,parent,false);

        }
        else
        {
           // holder = (ActivityLogsAdapter.ViewLogsHldr) rowView.getTag();
        }


/*

        holder.viewDetsTxtHldr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
*/


        return  rowView;
    }
}
