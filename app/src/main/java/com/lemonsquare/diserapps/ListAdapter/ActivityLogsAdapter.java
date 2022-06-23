package com.lemonsquare.diserapps.ListAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lemonsquare.diserapps.Controls.ViewLogs.ViewLogsActivity;
import com.lemonsquare.diserapps.Controls.ViewLogs.ViewLogsActivityDetails;
import com.lemonsquare.diserapps.MainActivity;
import com.lemonsquare.diserapps.Models.CustomerModel;
import com.lemonsquare.diserapps.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActivityLogsAdapter  extends ArrayAdapter {

    private Context context;
    private int resource;
    private List<CustomerModel> actList;
    private List<CustomerModel> filteredData = null;
    public ActivityLogsAdapter(Context context, int resource, List<CustomerModel> actList) {
        super(context, resource,actList);

        this.context = context;
        this.resource = resource;
        this.actList = actList;
        this.filteredData = new ArrayList<CustomerModel>();
        this.filteredData.addAll(actList);
    }

    @Override
    public int getCount() {

        if (actList==null)
        {
            return 0;
        }
        else
        {
            return actList.size();
        }

    }

    @Override
    public Object getItem(int position) {
        return actList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class ViewLogsHldr
    {
        public TextView actvtyTxtHldr,CustTxtHldr,dteTxtHldr,viewDetsTxtHldr;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewLogsHldr holder = new ViewLogsHldr();

        if (rowView ==null)
        {
            LayoutInflater BoInflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = BoInflate.inflate(resource,parent,false);

            holder.actvtyTxtHldr = (TextView) rowView.findViewById(R.id.actTypeTxt);
            holder.CustTxtHldr = (TextView) rowView.findViewById(R.id.custTxt);
            holder.dteTxtHldr = (TextView) rowView.findViewById(R.id.actDteTxt);
            holder.viewDetsTxtHldr = (TextView) rowView.findViewById(R.id.viewDetsTxt);
            rowView.setTag(holder);
        }
        else
        {
            holder = (ViewLogsHldr) rowView.getTag();
        }


        holder.actvtyTxtHldr.setText(actList.get(position).getActvty());
        holder.CustTxtHldr.setText(actList.get(position).getCustalias());
        holder.dteTxtHldr.setText(actList.get(position).getSbmtddte());

        holder.viewDetsTxtHldr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewLogsActivityDetails.class);
                intent.putExtra("Custalias",actList.get(position).getCustalias());
                intent.putExtra("Actvty",actList.get(position).getActvty());
                context.startActivity(intent);

            }
        });


        return  rowView;
    }


    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        actList.clear();

        if (charText.length() == 0) {
            actList.addAll(filteredData);
        }
        else
        {
            for (CustomerModel wp : filteredData) {
                if (wp.getActvty().toLowerCase(Locale.getDefault()).contains(charText)) {
                    actList.add(wp);
                }
                else if (wp.getCustalias().toLowerCase(Locale.getDefault()).contains(charText)) {
                    actList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
