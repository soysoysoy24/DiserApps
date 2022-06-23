package com.lemonsquare.diserapps.Controls.ViewLogs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lemonsquare.diserapps.Controls.InventoryActivity;
import com.lemonsquare.diserapps.DataAccess.DataLogic;
import com.lemonsquare.diserapps.DialogControl;
import com.lemonsquare.diserapps.Models.MaterialModel;
import com.lemonsquare.diserapps.R;

import java.util.ArrayList;
import java.util.List;

public class FragNotSent extends Fragment {

    private View myView;
    private LinearLayout parentInvView,invntView;
    private DataLogic databaseAccess;
    private List<MaterialModel> getInvtItem;
    private String Custcd = "",act = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_layout_logdetails, container, false);
        databaseAccess = DataLogic.getInstance(getActivity());


        Custcd = getArguments().getString("Custcd");
        act = getArguments().getString("Actvty");

        parentInvView = (LinearLayout) myView.findViewById(R.id.layout_logdets);
        invntView  = (LinearLayout) myView.findViewById(R.id.layout_logdets);


        getInvtItem = new ArrayList<MaterialModel>();
        Controls();

        return myView;
    }


    private void Controls()
    {
        MaterialModel cst = new MaterialModel();
        cst.setCustcode(Custcd);

        if (act.equals("Inventory"))
        {
            getInvtItem = databaseAccess.getMatInv(cst);
        }
        else if (act.equals("Delivery"))
        {
            getInvtItem = databaseAccess.getMatDel(cst);
        }
        else if (act.equals("ABIS"))
        {
            getInvtItem = databaseAccess.getMatAbis(cst);
        }

        if (getInvtItem.size() == 0)
        {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View noDataView = inflater.inflate(R.layout.layout_nodata, null);

            invntView.removeAllViews();
            invntView.addView(noDataView);

        }
        else
        {
            invntView.removeAllViews();
            parentInvView.removeAllViews();

            for (int itmInv = 0; itmInv<=getInvtItem.size()-1;itmInv++)
            {
                LayoutInflater inflater = (LayoutInflater)  getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
                View itemDetails = inflater.inflate(R.layout.layout_inventorydata_v2,null);
                itemDetails.setId(itmInv);

                TextView matNameTxt = (TextView) itemDetails.findViewById(R.id.itemMatTxt);
                parentInvView = (LinearLayout) itemDetails.findViewById(R.id.layout_itemDets);

                matNameTxt.setText(getInvtItem.get(itmInv).getMatnme()+" ("+getInvtItem.get(itmInv).getUom() +")");


                MaterialModel matName = new MaterialModel();
                matName.setMatnme(getInvtItem.get(itmInv).getMatnme());
                List<MaterialModel> getMatDets = new ArrayList<MaterialModel>();

                if (act.equals("Inventory"))
                {
                   getMatDets = databaseAccess.getMatInvDets(matName);
                }
                else if (act.equals("Delivery"))
                {
                    getMatDets = databaseAccess.getMatDelDets(matName);
                }
                else if (act.equals("ABIS"))
                {
                    getMatDets = databaseAccess.getMatAbisDets(matName);
                }





                for (int matdets = 0; matdets<= getMatDets.size()-1; matdets++)
                {
                    LayoutInflater Invinflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View InvItm = Invinflater.inflate(R.layout.layout_itemdata_v2,null);
                    InvItm.setId(matdets);

                    TextView QtyTxt = (TextView) InvItm.findViewById(R.id.qtyTxt);
                    TextView expDteTxt = (TextView) InvItm.findViewById(R.id.ExpDteTxt);

                    QtyTxt.setText(""+getMatDets.get(matdets).getQty());
                    expDteTxt.setText(getMatDets.get(matdets).getExpdte());


                    parentInvView.addView(InvItm);
                }
                invntView.addView(itemDetails);
            }
        }
    }
}
