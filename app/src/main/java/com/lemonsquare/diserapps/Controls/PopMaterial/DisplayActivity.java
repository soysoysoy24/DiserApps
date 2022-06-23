package com.lemonsquare.diserapps.Controls.PopMaterial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.lemonsquare.diserapps.ListAdapter.DisplayPopAdapter;
import com.lemonsquare.diserapps.Models.ArrayModel;
import com.lemonsquare.diserapps.Models.DisplayModel;
import com.lemonsquare.diserapps.R;

import java.util.ArrayList;
import java.util.List;

public class DisplayActivity  extends Fragment {
    private View myView;

    private ListView dispView;
    private TextView titleTxt;
    private DisplayPopAdapter displayPopAdapter;
    private ArrayModel arrayModel;
    private ArrayList<DisplayModel> DispList;
    private int trggr;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_layout_display, container, false);

        dispView = (ListView) myView.findViewById(R.id.displayView);
        titleTxt = (TextView) myView.findViewById(R.id.titleTxt);


        titleTxt.setVisibility(View.GONE);
        dispView.setEmptyView(myView.findViewById(R.id.imageViewNoData));
        trggr = getArguments().getInt("trg",0);


        if (trggr == 1)
        {
            arrayModel = (ArrayModel) getArguments().getSerializable("data");
            DispList = arrayModel.getMyList();
            displayPopAdapter = new DisplayPopAdapter(myView.getContext(), R.layout.layout_displaydata,DispList);
            dispView.setAdapter(displayPopAdapter);
        }



        return myView;
    }
}
