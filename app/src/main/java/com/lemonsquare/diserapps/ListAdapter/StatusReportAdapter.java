package com.lemonsquare.diserapps.ListAdapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lemonsquare.diserapps.Models.StatusReportModel;
import com.lemonsquare.diserapps.R;

import java.util.List;

public class StatusReportAdapter extends ArrayAdapter  {

    private  Context context;
    private int resource;
    private List<StatusReportModel> statsList;

    public StatusReportAdapter(Context context, int resource, List<StatusReportModel> statsList) {
        super(context, resource, statsList);


        this.context = context;
        this.resource = resource;
        this.statsList = statsList;
    }

    @Override
    public int getCount() {

        if (statsList==null)
        {
            return 0;
        }
        else
        {
            return statsList.size();
        }
    }


    @Override
    public Object getItem(int position) {
        return statsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class ViewStatsHldr
    {
        public TextView cstmrTxtHldr,reprtTxtHldr,statsDteHldr,statsTmeHldr;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewStatsHldr holder = new ViewStatsHldr();

        if (rowView ==null)
        {
            LayoutInflater BoInflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = BoInflate.inflate(resource,parent,false);

            holder.cstmrTxtHldr = (TextView) rowView.findViewById(R.id.cstmerTxt);
            holder.reprtTxtHldr = (TextView) rowView.findViewById(R.id.reportTypTxt);
            holder.statsDteHldr = (TextView) rowView.findViewById(R.id.StatsDteTxt);
            holder.statsTmeHldr = (TextView) rowView.findViewById(R.id.StatsTmeTxt);
            rowView.setTag(holder);
        }
        else
        {
            holder = (ViewStatsHldr) rowView.getTag();
        }

        holder.cstmrTxtHldr.setText(statsList.get(position).getCustalias());
        holder.reprtTxtHldr.setText(statsList.get(position).getStatscat());
        holder.statsDteHldr.setText(statsList.get(position).getStatsDate());
        holder.statsTmeHldr.setText(statsList.get(position).getStatsTme());





        return  rowView;
    }

}
