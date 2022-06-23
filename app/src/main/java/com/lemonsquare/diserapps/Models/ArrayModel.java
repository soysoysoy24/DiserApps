package com.lemonsquare.diserapps.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class ArrayModel extends DisplayModel implements Serializable {
    private ArrayList<DisplayModel> myList;

    public ArrayModel(ArrayList<DisplayModel> DeLlist)
    {
        this.myList = DeLlist;
    }

    public ArrayList<DisplayModel> getMyList() {
        return this.myList;
    }


}
