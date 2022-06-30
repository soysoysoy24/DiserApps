package com.lemonsquare.diserapps.DataAccess;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.widget.Toast;

import com.lemonsquare.diserapps.Controls.DeliveryActivity;
import com.lemonsquare.diserapps.DatabaseConnection.SQLiteConnect;
import com.lemonsquare.diserapps.Models.BoModel;
import com.lemonsquare.diserapps.Models.CustomerModel;
import com.lemonsquare.diserapps.Models.DeliveryModel;
import com.lemonsquare.diserapps.Models.DiserModel;
import com.lemonsquare.diserapps.Models.DisplayModel;
import com.lemonsquare.diserapps.Models.ExpenseModel;
import com.lemonsquare.diserapps.Models.FilingModel;
import com.lemonsquare.diserapps.Models.IncidentReportModel;
import com.lemonsquare.diserapps.Models.MaterialModel;
import com.lemonsquare.diserapps.Models.PriceSurveyModel;
import com.lemonsquare.diserapps.Models.StatusReportModel;

import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class DataLogic {

    private SQLiteOpenHelper openHelper;
    private static DataLogic instance;
    private SQLiteDatabase database;


    private DataLogic(Context context)
    {
        this.openHelper = new SQLiteConnect(context);
        openHelper.getWritableDatabase();

    }

    public static DataLogic getInstance(Context context) {
        if (instance == null) {
            instance = new DataLogic(context);
        }
        return instance;
    }


    public boolean synchDser (DiserModel dser)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();


        int cntInt = 0;

        String query_cnt = "SELECT COUNT(*) FROM TBL_DISER_INFO\n" +
                "WHERE BIO_ID = '" + dser.getBioId() + "'";
        Cursor cursor = database.rawQuery(query_cnt,null);
        if (cursor.moveToFirst())
        {
            do {
                cntInt =  cursor.getInt(0);
            }while (cursor.moveToNext());
            cursor.close();
        }

        if (cntInt == 0)
        {
            String query = "Insert into TBL_DISER_INFO (BIO_ID,FNAME,MNAME,LNAME,COMPNY,ZRXNO) Values (?,?,?,?,?,?)";
            SQLiteStatement insertStmt = database.compileStatement(query);
            insertStmt.clearBindings();
            insertStmt.bindString(1,dser.getBioId());
            insertStmt.bindString(2, dser.getFname());
            insertStmt.bindString(3, dser.getMname());
            insertStmt.bindString(4,  dser.getLname());
            insertStmt.bindString(5,  dser.getCompny());
            insertStmt.bindString(6,  dser.getMobile());
            insertStmt.executeInsert();
            insertStmt.close();

            String querys = "Insert into TBL_RAW_PROCESS (BIOID,CPNO) Values (?,?)";
            SQLiteStatement insertStmtes = database.compileStatement(querys);
            insertStmtes.clearBindings();
            insertStmtes.bindString(1,dser.getBioId());
            insertStmtes.bindString(2,dser.getMobile());
            insertStmtes.executeInsert();
            insertStmtes.close();

        }

        return  true;
    }

    public boolean synchCust(CustomerModel cust)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        int cntInt = 0;

        String query_cnt = "SELECT COUNT(*) FROM TBL_CUSTOMERS\n" +
                "WHERE CUSTCD = '" + cust.getCustcode() + "'";
        Cursor cursor = database.rawQuery(query_cnt,null);
        if (cursor.moveToFirst())
        {
            do {
                cntInt =  cursor.getInt(0);
            }while (cursor.moveToNext());
            cursor.close();
        }

        if (cntInt == 0)
        {
            String query = "Insert into TBL_CUSTOMERS (CUSTCD,CUSTNM,CUSTALIAS) Values (?,?,?)";
            SQLiteStatement insertStmt = database.compileStatement(query);
            insertStmt.clearBindings();
            insertStmt.bindString(1,cust.getCustcode());
            insertStmt.bindString(2, cust.getCustnme());
            insertStmt.bindString(3, cust.getCustalias());
            insertStmt.executeInsert();
        }

        return true;
    }

    public boolean synchMats (MaterialModel mats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        int cntInt = 0;

        String query_cnt = "SELECT COUNT(*) FROM TBL_MATERIALS\n" +
                "WHERE MATCODE = '" + mats.getMatcd() + "'";
        Cursor cursor = database.rawQuery(query_cnt,null);
        if (cursor.moveToFirst())
        {
            do {
                cntInt =  cursor.getInt(0);
            }while (cursor.moveToNext());
            cursor.close();
        }

        if (cntInt == 0)
        {
            String query = "Insert into TBL_MATERIALS (MATCODE,UNIT,MATSHRTDESC,EXTMATGRP) Values (?,?,?,?)";
            SQLiteStatement insertStmt = database.compileStatement(query);
            insertStmt.clearBindings();
            insertStmt.bindString(1,mats.getMatcd());
            insertStmt.bindString(2, mats.getUom());
            insertStmt.bindString(3, mats.getMatnme());
            insertStmt.bindString(4,  mats.getExtmat());
            insertStmt.executeInsert();
        }

        return  true;

    }


    public boolean synchStatCat (StatusReportModel stats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        int cntInt = 0;

        String query_cnt = "SELECT COUNT(*) FROM TBL_STATS_RPRT_CAT\n" +
                "WHERE STATSDESC = '" + stats.getStatscat() + "'";
        Cursor cursor = database.rawQuery(query_cnt,null);
        if (cursor.moveToFirst())
        {
            do {
                cntInt =  cursor.getInt(0);
            }while (cursor.moveToNext());
            cursor.close();
        }

        if (cntInt == 0)
        {


            String query = "Insert into TBL_STATS_RPRT_CAT (STATSDESC) Values (?)";
            SQLiteStatement insertStmt = database.compileStatement(query);
            insertStmt.clearBindings();
            insertStmt.bindString(1,stats.getStatscat());
            insertStmt.executeInsert();
        }

        return  true;

    }


    public boolean synchReportCat (IncidentReportModel stats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        int cntInt = 0;

        String query_cnt = "SELECT COUNT(*) FROM TBL_REPRT_CAT\n" +
                "WHERE RPORDESC = '" + stats.getReportcat() + "'";
        Cursor cursor = database.rawQuery(query_cnt,null);
        if (cursor.moveToFirst())
        {
            do {
                cntInt =  cursor.getInt(0);
            }while (cursor.moveToNext());
            cursor.close();
        }

        if (cntInt == 0)
        {
            String query = "Insert into TBL_REPRT_CAT (RPORDESC) Values (?)";
            SQLiteStatement insertStmt = database.compileStatement(query);
            insertStmt.clearBindings();
            insertStmt.bindString(1,stats.getReportcat());
            insertStmt.executeInsert();
        }
        return  true;
    }




    public boolean synchFilingCat (FilingModel stats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        int cntInt = 0;

        String query_cnt = "SELECT COUNT(*) FROM TBL_FILING_RPRT_CAT_CLSS\n" +
                "WHERE FILINGCATDESC = '" + stats.getFilingcat() + "'";
        Cursor cursor = database.rawQuery(query_cnt,null);
        if (cursor.moveToFirst())
        {
            do {
                cntInt =  cursor.getInt(0);
            }while (cursor.moveToNext());
            cursor.close();
        }

        if (cntInt == 0)
        {
            String query = "Insert into TBL_FILING_RPRT_CAT_CLSS (FILINGCATDESC) Values (?)";
            SQLiteStatement insertStmt = database.compileStatement(query);
            insertStmt.clearBindings();
            insertStmt.bindString(1,stats.getFilingcat());
            insertStmt.executeInsert();
        }
        return  true;
    }




    public boolean synchDispCat (DisplayModel stats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        int cntInt = 0;


        String query_cnt = "SELECT COUNT(*) FROM TBL_DSPLY_CLSS\n" +
                "WHERE DSPLYDESC = '" + stats.getDispcat() + "'";
        Cursor cursor = database.rawQuery(query_cnt,null);
        if (cursor.moveToFirst())
        {
            do {
                cntInt =  cursor.getInt(0);
            }while (cursor.moveToNext());
            cursor.close();
        }

        if (cntInt == 0)
        {
            String query = "Insert into TBL_DSPLY_CLSS (DSPLYDESC) Values (?)";
            SQLiteStatement insertStmt = database.compileStatement(query);
            insertStmt.clearBindings();
            insertStmt.bindString(1,stats.getDispcat());
            insertStmt.executeInsert();
        }
        return  true;
    }

    public boolean synchXpenseCat (ExpenseModel expnsStats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        int cntInt = 0;


        String query_cnt = "SELECT COUNT(*) FROM TBL_XPNS_CLSS\n" +
                "WHERE XPNSDESC = '" + expnsStats.getXpnseDesc() + "'";
        Cursor cursor = database.rawQuery(query_cnt,null);
        if (cursor.moveToFirst())
        {
            do {
                cntInt =  cursor.getInt(0);
            }while (cursor.moveToNext());
            cursor.close();
        }

        if (cntInt == 0)
        {
            String query = "Insert into TBL_XPNS_CLSS (XPNSDESC) Values (?)";
            SQLiteStatement insertStmt = database.compileStatement(query);
            insertStmt.clearBindings();
            insertStmt.bindString(1,expnsStats.getXpnseDesc());
            insertStmt.executeInsert();
        }
        return  true;
    }


    public boolean synchXpenseTranspoCat (ExpenseModel expnsTransStats)
    {


        database = openHelper.getWritableDatabase();
        database.isOpen();

        int cntInt = 0;


        String query_cnt = "SELECT COUNT(*) FROM TBL_TRNSP_XPNS_CLSS\n" +
                "WHERE TRNSPOXPNSDESC = '" + expnsTransStats.getXpnseTranDesc() + "'";
        Cursor cursor = database.rawQuery(query_cnt,null);
        if (cursor.moveToFirst())
        {
            do {
                cntInt =  cursor.getInt(0);
            }while (cursor.moveToNext());
            cursor.close();
        }

        if (cntInt == 0)
        {
            String query = "Insert into TBL_TRNSP_XPNS_CLSS (TRNSPOXPNSDESC) Values (?)";
            SQLiteStatement insertStmt = database.compileStatement(query);
            insertStmt.clearBindings();
            insertStmt.bindString(1,expnsTransStats.getXpnseTranDesc());
            insertStmt.executeInsert();
        }
        return  true;
    }





    public boolean DeleteMaintainTable()
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();


        String querydelcatrep_ = "DELETE FROM TBL_REPRT_CAT";
        database.execSQL(querydelcatrep_);

        String queryAutoIncDelRep = "DELETE FROM sqlite_sequence WHERE name = 'TBL_REPRT_CAT'";
        database.execSQL(queryAutoIncDelRep);


        String querydelcatstats_ = "DELETE FROM TBL_STATS_RPRT_CAT";
        database.execSQL(querydelcatstats_);


        String queryAutoIncDelStat = "DELETE FROM sqlite_sequence WHERE name = 'TBL_STATS_RPRT_CAT'";
        database.execSQL(queryAutoIncDelStat);


        String querydelcatfil_ = "DELETE FROM TBL_FILING_RPRT_CAT_CLSS";
        database.execSQL(querydelcatfil_);

        String queryAutoIncDelFil = "DELETE FROM sqlite_sequence WHERE name = 'TBL_FILING_RPRT_CAT_CLSS'";
        database.execSQL(queryAutoIncDelFil);


        String querydelcatdisp_ = "DELETE FROM TBL_DSPLY_CLSS";
        database.execSQL(querydelcatdisp_);

        String queryAutoIncDelDisp = "DELETE FROM sqlite_sequence WHERE name = 'TBL_DSPLY_CLSS'";
        database.execSQL(queryAutoIncDelDisp);

         
        String querydelcatxpnse_ = "DELETE FROM TBL_XPNS_CLSS";
        database.execSQL(querydelcatxpnse_);

        String queryAutoIncDelXpnse = "DELETE FROM sqlite_sequence WHERE name = 'TBL_XPNS_CLSS'";
        database.execSQL(queryAutoIncDelXpnse);


        String querydelcattranspoxpnse_ = "DELETE FROM TBL_TRNSP_XPNS_CLSS";
        database.execSQL(querydelcattranspoxpnse_);

        String queryAutoIncDelTranspoXpnse = "DELETE FROM sqlite_sequence WHERE name = 'TBL_TRNSP_XPNS_CLSS'";
        database.execSQL(queryAutoIncDelTranspoXpnse);


        String querydelcust_ = "DELETE FROM TBL_CUSTOMERS";
        database.execSQL(querydelcust_);

        String querydelmats_ = "DELETE FROM TBL_MATERIALS";
        database.execSQL(querydelmats_);

        return  true;
    }


    public List<DiserModel> getRaw()
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<DiserModel> getData = new ArrayList<DiserModel>();

        String query = "SELECT BIOID,CPNO FROM TBL_RAW_PROCESS";
        Cursor cursor = database.rawQuery(query,null);

        if (cursor.moveToFirst())
        {
            do {
                DiserModel datadser = new DiserModel();
                datadser.setBioId(cursor.getString(0));
                datadser.setMobile(cursor.getString(1));
                getData.add(datadser);
            }while (cursor.moveToNext());
        }

        return  getData;

    }


    public int getCntVlidateUser(DiserModel dsr, int trg)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        int cnt = 0;


        if (trg == 0)
        {
            String query = "SELECT COUNT(*) FROM TBL_RAW_PROCESS WHERE BIOID = '" + dsr.getBioId() + "'" +
                    "AND ISLOGIN = 1";
            Cursor cursor = database.rawQuery(query,null);

            if (cursor.moveToFirst())
            {
                cnt = cursor.getInt(0);
            }
        }
        else
        {
            String query = "SELECT COUNT(*) FROM TBL_RAW_PROCESS";
            Cursor cursor = database.rawQuery(query,null);

            if (cursor.moveToFirst())
            {
                cnt = cursor.getInt(0);
            }
        }

        return  cnt;

    }

    public boolean UpdateRaw (DiserModel dsr)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        String query = "UPDATE TBL_RAW_PROCESS\n" +
                "SET ISLOGIN = 1\n" +
                "WHERE BIOID = '" + dsr.getBioId() + "'";

        database.execSQL(query);

        return  true;
    }


    public boolean InsertAttndnce(DiserModel dsrMod)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        int cntInt = 0;

        String query = "SELECT COUNT(*) FROM TBL_ATTNDNCE\n" +
                "WHERE BIO_ID = '" + dsrMod.getBioId() + "' AND \n" +
                "DT_TME_OUT = '' AND\n" +
                "strftime('%Y-%m-%d',DT_TME_IN) = DATE('now','localtime')";
        Cursor cursor = database.rawQuery(query,null);
        if (cursor.moveToFirst())
        {
            do {
                cntInt =  cursor.getInt(0);
            }while (cursor.moveToNext());
            cursor.close();
        }


        if (cntInt == 0)
        {
            String query_ins = "Insert into TBL_ATTNDNCE (BIO_ID,DTE_IN_LOC) Values (?,?)";
            SQLiteStatement insertStmt = database.compileStatement(query_ins);
            insertStmt.clearBindings();
            insertStmt.bindString(1,dsrMod.getBioId());
            insertStmt.bindString(2, dsrMod.getLoc());
            insertStmt.executeInsert();
        }
        else
        {
            String query_up = "UPDATE TBL_ATTNDNCE \n" +
                    "SET DT_TME_OUT = datetime('now','localtime'),\n" +
                    "DTE_OUT_LOC = '" + dsrMod.getBioId() + "'\n" +
                    "WHERE BIO_ID = '" + dsrMod.getLoc() + "' AND strftime('%Y-%m-%d',DT_TME_IN) = DATE('now','localtime')";
            database.execSQL(query_up);
        }

        return true;
    }



    public int checkInOutAttndnce(DiserModel dsrMdl)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        int cntAttn = 0,cntNotOut = 0 ;

        String query = "SELECT COUNT(*) FROM TBL_ATTNDNCE\n" +
                "WHERE strftime('%Y-%m-%d',DT_TME_IN) = DATE('now','localtime') \n" +
                "AND strftime('%Y-%m-%d',DT_TME_OUT) = DATE('now','localtime')\n" +
                "AND BIO_ID = '" + dsrMdl.getBioId() + "'";

        Cursor cursor = database.rawQuery(query,null);

        if (cursor.moveToFirst())
        {
            do {
                cntAttn =  cursor.getInt(0);
            }while (cursor.moveToNext());
            cursor.close();
        }


        String query_noout = "SELECT COUNT(*) FROM TBL_ATTNDNCE\n" +
                "WHERE DT_TME_OUT = '' AND  strftime('%Y-%m-%d',DT_TME_IN) < DATE('now','localtime') \n" +
                "AND BIO_ID = '" + dsrMdl.getBioId() + "'";
        Cursor cursor_ = database.rawQuery(query_noout,null);

        if (cursor_.moveToFirst())
        {
            do {
                cntNotOut =  cursor_.getInt(0);
            }while (cursor_.moveToNext());
            cursor_.close();
        }

        if (cntNotOut != 0)
        {
            cntNotOut = 2;
            cntAttn = cntAttn+cntNotOut;
        }


        return cntAttn;
    }

    public int checkAttndnce(DiserModel dsrMdl)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        int cntAttn = 0;

        String query = "SELECT COUNT(*) FROM TBL_ATTNDNCE\n" +
                "WHERE BIO_ID = '" + dsrMdl.getBioId() + "' AND \n" +
                "DT_TME_OUT = '' AND\n" +
                "strftime('%Y-%m-%d',DT_TME_IN) <= DATE('now','localtime')";

        Cursor cursor = database.rawQuery(query,null);
        if (cursor.moveToFirst())
        {
            do {
                cntAttn =  cursor.getInt(0);
            }while (cursor.moveToNext());
            cursor.close();
        }




        return cntAttn;
    }

    public boolean CheckInOut(CustomerModel cst)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();


        int cntChkIn = 0;

        String query = "SELECT COUNT(*) FROM TBL_CHCK_IN_OUT\n" +
                "WHERE BIO_ID = '" + cst.getBioId() + "' AND\n" +
                "CUSTCD = '" + cst.getCustcode() + "' AND \n" +
                "strftime('%Y-%m-%d',CHCK_IN_TME) = DATE('now','localtime')";

        Cursor cursor = database.rawQuery(query,null);
        if (cursor.moveToFirst())
        {
            do {
                cntChkIn =  cursor.getInt(0);
            }while (cursor.moveToNext());
            cursor.close();
        }

        if (cntChkIn == 0)
        {
            String query_ins = "Insert into TBL_CHCK_IN_OUT (BIO_ID,CUSTCD,CHCK_IN_LOC) Values (?,?,?)";
            SQLiteStatement insertStmt = database.compileStatement(query_ins);
            insertStmt.clearBindings();
            insertStmt.bindString(1,cst.getBioId());
            insertStmt.bindString(2, cst.getCustcode());
            insertStmt.bindString(3, cst.getLoc());
            insertStmt.executeInsert();

            String queryRaw = "UPDATE TBL_RAW_PROCESS\n" +
                    "SET CHECK_IN_CUST = '" + cst.getCustalias() + "'\n" +
                    "WHERE BIOID = '" + cst.getBioId() + "'";

            database.execSQL(queryRaw);
        }
        else
        {
            String query_up = "UPDATE TBL_CHCK_IN_OUT \n" +
                    "SET CHCK_OUT_LOC = '" + cst.getLoc() + "', CHCK_OUT_TME = datetime('now','localtime')\n" +
                    "WHERE BIO_ID = '" + cst.getLoc() + "' AND strftime('%Y-%m-%d',CHCK_IN_TME) = DATE('now','localtime') AND \n" +
                    "CUSTCD = '" + cst.getCustcode() + "'";
            database.execSQL(query_up);


            String queryRaw = "UPDATE TBL_RAW_PROCESS\n" +
                    "SET CHECK_IN_CUST = ''\n" +
                    "WHERE BIOID = '" + cst.getBioId() + "'";

            database.execSQL(queryRaw);
        }

        return  true;
    }

    public String getCustCd (CustomerModel cst)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        String custcd = "";

        String query = "SELECT CUSTCD FROM TBL_CUSTOMERS WHERE CUSTALIAS = '"  + cst.getCustalias() + "'";
        Cursor cursor = database.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do{
                custcd = cursor.getString(0);
            }while (cursor.moveToNext());
            cursor.close();
        }

        return custcd;
    }


    public int getStatusCatID (StatusReportModel stats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        int statID = 0;

        String query = "SELECT ID FROM TBL_STATS_RPRT_CAT WHERE STATSDESC = '"  + stats.getStatscat() + "'";
        Cursor cursor = database.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do{
                statID = cursor.getInt(0);
            }while (cursor.moveToNext());
            cursor.close();
        }

        return statID;
    }


    public int getFilingCatID (FilingModel stats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        int filingID = 0;

        String query = "SELECT ID FROM TBL_FILING_RPRT_CAT_CLSS WHERE FILINGCATDESC = '"  + stats.getFilingcat() + "'";
        Cursor cursor = database.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do{
                filingID = cursor.getInt(0);
            }while (cursor.moveToNext());
            cursor.close();
        }

        return filingID;
    }


    public int getReportCatID (IncidentReportModel stats)
    {

        database = openHelper.getWritableDatabase();
        database.isOpen();

        int repID = 0;

        String query = "SELECT ID FROM TBL_REPRT_CAT WHERE RPORDESC = '"  + stats.getReportcat() + "'";
        Cursor cursor = database.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do{
                repID = cursor.getInt(0);
            }while (cursor.moveToNext());
            cursor.close();
        }

        return repID;
    }


    public int getDisplayCatID (DisplayModel stats)
    {

        database = openHelper.getWritableDatabase();
        database.isOpen();

        int dispID = 0;

        String query = "SELECT ID FROM TBL_DSPLY_CLSS WHERE DSPLYDESC = '"  + stats.getDispcat() + "'";
        Cursor cursor = database.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do{
                dispID = cursor.getInt(0);
            }while (cursor.moveToNext());
            cursor.close();
        }

        return dispID;
    }


    public int getExpenseCatID (ExpenseModel stats)
    {

        database = openHelper.getWritableDatabase();
        database.isOpen();

        int xpnseID = 0;

        String query = "SELECT ID FROM TBL_XPNS_CLSS WHERE XPNSDESC = '"  + stats.getXpnseDesc() + "'";
        Cursor cursor = database.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do{
                xpnseID = cursor.getInt(0);
            }while (cursor.moveToNext());
            cursor.close();
        }

        return xpnseID;
    }


    public boolean sbmitFiling(FilingModel fil,String bioid)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();


        String query_ins = "Insert into TBL_OFF_FILING (BIOID,LVETPE,RSON) Values (?,?,?)";
        SQLiteStatement insertStmt = database.compileStatement(query_ins);
        insertStmt.clearBindings();
        insertStmt.bindString(1,bioid);
        insertStmt.bindString(2, fil.getFilingcat());
        insertStmt.bindString(3, fil.getRson());
        insertStmt.executeInsert();

        return  true;
    }


    public List<DeliveryModel> getDeliveryData (DeliveryModel cst)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<DeliveryModel> getInvData = new ArrayList<DeliveryModel>();

        //String custcd = "";
        String query = "SELECT CUSTCD,INVNO,INVDATE FROM TBL_DEL_ITM WHERE CUSTCD = '"  + cst.getCustcode() + "'";
        Cursor cursor = database.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do{
                DeliveryModel del = new DeliveryModel();
                del.setCustcode(cursor.getString(0));
                del.setInvoiceNum(cursor.getString(1));
                del.setInvoiceDate(cursor.getString(2));
                getInvData.add(del);
               //custcd = cursor.getString(0);
            }while (cursor.moveToNext());
            cursor.close();
        }

        return getInvData;
    }

    public List<BoModel> getBoData (BoModel cst)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<BoModel> getAbisData = new ArrayList<BoModel>();

        //String custcd = "";
        String query = "SELECT CUSTCD,RTVNO,RTVDATE FROM TBL_ABIS_ITM WHERE CUSTCD = '"  + cst.getCustcode() + "'";
        Cursor cursor = database.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do{
                BoModel del = new BoModel();
                del.setCustcode(cursor.getString(0));
                del.setRtvnum(cursor.getString(1));
                del.setRtvdate(cursor.getString(2));
                getAbisData.add(del);
                //custcd = cursor.getString(0);
            }while (cursor.moveToNext());
            cursor.close();
        }

        return getAbisData;
    }


    public List<CustomerModel> checkCust(DiserModel dsrMdl)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<CustomerModel> getCust = new ArrayList<CustomerModel>();

        String query = "SELECT COUNT(*) FROM TBL_ATTNDNCE\n" +
                "WHERE BIO_ID = '" + dsrMdl.getBioId() + "' AND \n" +
                "DT_TME_OUT = '' AND\n" +
                "strftime('%Y-%m-%d',DT_TME_IN) = DATE('now','localtime')";

        Cursor cursor = database.rawQuery(query,null);
        if (cursor.moveToFirst())
        {
            do {
                CustomerModel cust = new CustomerModel();
                cust.setCustnme(cursor.getString(0));
                getCust.add(cust);
            }while (cursor.moveToNext());
            cursor.close();
        }

        return getCust;
    }

    public List<CustomerModel> showCustNme()
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<CustomerModel> getCustomers = new ArrayList<CustomerModel>();

        String query = "SELECT * FROM TBL_CUSTOMERS WHERE CUSTALIAS <> '' ";
        Cursor cursor = database.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do{
                CustomerModel cust = new CustomerModel();
                cust.setCustcode(cursor.getString(0));
                cust.setCustnme(cursor.getString(1));
                cust.setCustalias(cursor.getString(2));
                getCustomers.add(cust);
            }while (cursor.moveToNext());
            cursor.close();
        }

        return getCustomers;
    }

    public List<StatusReportModel> showStatsCategory()
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<StatusReportModel> getStats = new ArrayList<StatusReportModel>();

        String query = "SELECT * FROM TBL_STATS_RPRT_CAT";
        Cursor cursor = database.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do{
                StatusReportModel stats = new StatusReportModel();
                stats.setStatscat(cursor.getString(0));
                getStats.add(stats);
            }while (cursor.moveToNext());
            cursor.close();
        }

        return getStats;
    }


    public List<MaterialModel> showMatNme()
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<MaterialModel> getMaterials = new ArrayList<MaterialModel>();

        String query = "SELECT MATSHRTDESC FROM TBL_MATERIALS WHERE UNIT <> 'PC' ORDER BY MATSHRTDESC ASC";
        Cursor cursor = database.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do{
                MaterialModel mats = new MaterialModel();
                mats.setMatnme(cursor.getString(0));
                getMaterials.add(mats);
            }while (cursor.moveToNext());
            cursor.close();
        }

        return getMaterials;
    }

    public String getUom (MaterialModel matnme)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        String uom = "";

        String query = "SELECT CASE WHEN UNIT = 'PAC' THEN 'PACK/S' " +
                "WHEN UNIT = 'PC' THEN 'PC/S' ELSE UNIT END AS UOM " +
                "FROM TBL_MATERIALS WHERE MATSHRTDESC = '"  + matnme.getMatnme() + "'";
        Cursor cursor = database.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do{
                uom = cursor.getString(0);
            }while (cursor.moveToNext());
            cursor.close();
        }

        return uom;
    }

    public String getExtMat (MaterialModel matnme)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        String extmat = "";

        String query = "SELECT EXTMATGRP  FROM TBL_MATERIALS\n" +
                "WHERE MATSHRTDESC = '" + matnme.getMatnme() + "'";
        Cursor cursor = database.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do{
                extmat = cursor.getString(0);
            }while (cursor.moveToNext());
            cursor.close();
        }

        return extmat;
    }

    public boolean AddLogs(CustomerModel actvyCst)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();


        int cntLogs = 0;

        String query = "SELECT COUNT(*) FROM TBL_ACTVTY_LOGS\n" +
                "WHERE SBMTDBY = '" + actvyCst.getBioId() + "' AND\n" +
                "CUSTCD = '" + actvyCst.getCustcode() + "' AND \n" +
                "ACTVTY = '" + actvyCst.getActvty() + "' AND \n" +
                "strftime('%Y-%m-%d',SBMTDDT) = DATE('now','localtime')";

        Cursor cursor = database.rawQuery(query,null);
        if (cursor.moveToFirst())
        {
            do {
                cntLogs =  cursor.getInt(0);
            }while (cursor.moveToNext());
            cursor.close();
        }

        if (cntLogs == 0)
        {
            String query_ = "Insert into TBL_ACTVTY_LOGS (CUSTCD,ACTVTY,SBMTDBY) Values (?,?,?)";
            SQLiteStatement insertStmt = database.compileStatement(query_);
            insertStmt.clearBindings();
            insertStmt.bindString(1,actvyCst.getCustcode());
            insertStmt.bindString(2, actvyCst.getActvty());
            insertStmt.bindString(3, actvyCst.getBioId());
            insertStmt.executeInsert();
        }



        return  true;

    }

    public List<CustomerModel> viewLogs(CustomerModel actCust)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<CustomerModel> getMyLogs = new ArrayList<CustomerModel>();

        String query = "SELECT A.ACTVTY,B.CUSTALIAS,strftime('%m/%d/%Y',SBMTDDT) \n" +
                "FROM TBL_ACTVTY_LOGS A\n" +
                "INNER JOIN TBL_CUSTOMERS B \n" +
                "ON A.CUSTCD = B.CUSTCD\n" +
                "WHERE A.SBMTDBY = '" + actCust.getBioId() + "'";
        Cursor cursor = database.rawQuery(query,null);

        if (cursor.moveToFirst())
        {
            do{
                CustomerModel cstLogs = new CustomerModel();
                cstLogs.setActvty(cursor.getString(0));
                cstLogs.setCustalias(cursor.getString(1));
                cstLogs.setSbmtddte(cursor.getString(2));
                getMyLogs.add(cstLogs);
            }while (cursor.moveToNext());
            cursor.close();
        }


        return  getMyLogs;

    }

    public boolean AddInvItm (MaterialModel mats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();



        String query = "Insert into TBL_INVT_ITM (CUSTCD,EXTMAT,MATDESC,QTY,EXPDTE,SBMTDBY) Values (?,?,?,?,?,?)";
        SQLiteStatement insertStmt = database.compileStatement(query);
        insertStmt.clearBindings();
        insertStmt.bindString(1,mats.getCustcode());
        insertStmt.bindString(2, mats.getExtmat());
        insertStmt.bindString(3, mats.getMatnme());
        insertStmt.bindString(4, String.valueOf(mats.getQty()));
        insertStmt.bindString(5, mats.getExpdte());
        insertStmt.bindString(6,mats.getBioId());
        insertStmt.executeInsert();


        return true;

    }

    public boolean UpdateInvItem (MaterialModel mats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        String query = "UPDATE TBL_INVT_ITM\n" +
                "SET QTY = '" + mats.getQty() + "'\n" +
                "WHERE MATDESC = '" + mats.getMatnme() + "' \n" +
                "AND CUSTCD = '" + mats.getCustcode() + "' AND \n" +
                "EXPDTE = '" + mats.getExpdte() + "'";

        database.execSQL(query);

        return  true;
    }

    public boolean DeleteInvItem (MaterialModel mats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        String query = "DELETE FROM TBL_INVT_ITM\n" +
                "WHERE MATDESC = '" + mats.getMatnme() + "' AND \n" +
                "CUSTCD = '" + mats.getCustcode() + "' AND \n" +
                "EXPDTE = '" + mats.getExpdte() + "'";
        database.execSQL(query);

        return  true;
    }

    public List<MaterialModel> getInvItmToSend (MaterialModel mats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<MaterialModel> getItmInv = new ArrayList<MaterialModel>();

        String query = "SELECT EXTMAT,QTY,EXPDTE,SBMTDBY \n" +
                "FROM TBL_INVT_ITM\n" +
                "WHERE HASSEND = 0 AND\n" +
                "CUSTCD = '" + mats.getCustcode() + "' AND \n" +
                "SBMTDBY = '" + mats.getBioId() + "'";
        Cursor cursor = database.rawQuery(query,null);

        if (cursor.moveToFirst())
        {
            do{
                MaterialModel mat = new MaterialModel();
                mat.setExtmat(cursor.getString(0));
                mat.setQty(cursor.getInt(1));
                mat.setExpdte(cursor.getString(2));
                mat.setBioId(cursor.getString(3));
                getItmInv.add(mat);
            }while (cursor.moveToNext());
        }


        return getItmInv;
    }

    public boolean UpdateInvItmSend(MaterialModel mats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        String query = "UPDATE TBL_INVT_ITM\n" +
                "SET HASSEND = 1\n" +
                "WHERE CUSTCD = '" + mats.getCustcode() + "' AND\n" +
                "EXTMAT = '" + mats.getExtmat() + "' AND\n" +
                "SBMTDBY = '" + mats.getBioId() + "'";

        database.execSQL(query);
        return true;
    }


    public boolean AddDelItm (DeliveryModel mats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();



        String query = "Insert into TBL_DEL_ITM (CUSTCD,INVNO,INVDATE,EXTMAT,MATDESC,QTY,EXPDTE,SBMTDBY) Values (?,?,?,?,?,?,?,?)";
        SQLiteStatement insertStmt = database.compileStatement(query);
        insertStmt.clearBindings();
        insertStmt.bindString(1,mats.getCustcode());
        insertStmt.bindString(2,mats.getInvoiceNum());
        insertStmt.bindString(3,mats.getInvoiceDate());
        insertStmt.bindString(4, mats.getExtmat());
        insertStmt.bindString(5, mats.getMatnme());
        insertStmt.bindString(6, String.valueOf(mats.getQty()));
        insertStmt.bindString(7, mats.getExpdte());
        insertStmt.bindString(8,mats.getBioId());
        insertStmt.executeInsert();


        return true;

    }

    public boolean UpdateDelItem (DeliveryModel mats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        String query = "UPDATE TBL_DEL_ITM\n" +
                "SET QTY = '" + mats.getQty() + "'\n" +
                "WHERE MATDESC = '" + mats.getMatnme() + "' AND \n" +
                "CUSTCD = '" + mats.getCustcode() + "' AND \n" +
                "INVNO = '" + mats.getInvoiceNum() + "' AND \n" +
                "INVDATE = '" + mats.getInvoiceDate() + "' AND\n" +
                "EXPDTE = '" + mats.getExpdte() + "'";

        database.execSQL(query);

        return  true;
    }

    public boolean DeleteDelItem (DeliveryModel mats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        String query = "DELETE FROM TBL_DEL_ITM\n" +
                "WHERE MATDESC = '" + mats.getMatnme() + "' AND \n" +
                "CUSTCD = '" + mats.getCustcode() + "' AND \n" +
                "INVNO = '" + mats.getInvoiceNum() + "' AND \n" +
                "INVDATE = '" + mats.getInvoiceDate() + "' AND\n" +
                "EXPDTE = '" + mats.getExpdte() + "'";
        database.execSQL(query);

        return  true;
    }


    public List<DeliveryModel> getDelItmToSend (DeliveryModel mats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<DeliveryModel> getItmDel = new ArrayList<DeliveryModel>();

        String query = "SELECT INVNO,INVDATE,EXTMAT,QTY,EXPDTE,SBMTDBY \n" +
                "FROM TBL_DEL_ITM\n" +
                "WHERE HASSEND = 0 AND\n" +
                "CUSTCD = '" + mats.getCustcode() + "' AND \n" +
                "SBMTDBY = '" + mats.getBioId() + "'";
        Cursor cursor = database.rawQuery(query,null);

        if (cursor.moveToFirst())
        {
            do{
                DeliveryModel mat = new DeliveryModel();
                mat.setInvoiceNum(cursor.getString(0));
                mat.setInvoiceDate(cursor.getString(1));
                mat.setExtmat(cursor.getString(2));
                mat.setQty(cursor.getInt(3));
                mat.setExpdte(cursor.getString(4));
                mat.setBioId(cursor.getString(5));
                getItmDel.add(mat);
            }while (cursor.moveToNext());
        }


        return getItmDel;
    }

    public boolean UpdateDelItmSend(DeliveryModel mats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        String query = "UPDATE TBL_DEL_ITM\n" +
                "SET HASSEND = 1\n" +
                "WHERE CUSTCD = '" + mats.getCustcode() + "' AND\n" +
                "EXTMAT = '" + mats.getExtmat() + "' AND\n" +
                "SBMTDBY = '" + mats.getBioId() + "'";

        database.execSQL(query);
        return true;
    }

    public boolean AddAbisItm (BoModel mats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();


        String query = "Insert into TBL_ABIS_ITM (CUSTCD,RTVNO,RTVDATE,EXTMAT,MATDESC,QTY,EXPDTE,SBMTDBY) Values (?,?,?,?,?,?,?,?)";
        SQLiteStatement insertStmt = database.compileStatement(query);
        insertStmt.clearBindings();
        insertStmt.bindString(1,mats.getCustcode());
        insertStmt.bindString(2,mats.getRtvnum());
        insertStmt.bindString(3,mats.getRtvdate());
        insertStmt.bindString(4, mats.getExtmat());
        insertStmt.bindString(5, mats.getMatnme());
        insertStmt.bindString(6, String.valueOf(mats.getQty()));
        insertStmt.bindString(7, mats.getExpdte());
        insertStmt.bindString(8,mats.getBioId());
        insertStmt.executeInsert();


        return true;

    }


    public boolean UpdateAbisItem (BoModel mats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        String query = "UPDATE TBL_ABIS_ITM\n" +
                "SET QTY = '" + mats.getQty() + "'\n" +
                "WHERE MATDESC = '" + mats.getMatnme() + "' AND \n" +
                "CUSTCD = '" + mats.getCustcode() + "' AND \n" +
                "RTVNO = '" + mats.getRtvnum() + "' AND \n" +
                "RTVDATE = '" + mats.getRtvdate() + "' AND\n" +
                "EXPDTE = '" + mats.getExpdte() + "'";

        database.execSQL(query);

        return  true;
    }

    public boolean DeleteAbisItem (BoModel mats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        String query = "DELETE FROM TBL_ABIS_ITM\n" +
                "WHERE MATDESC = '" + mats.getMatnme() + "' AND \n" +
                "CUSTCD = '" + mats.getCustcode() + "' AND \n" +
                "RTVNO = '" + mats.getRtvnum() + "' AND \n" +
                "RTVDATE = '" + mats.getRtvdate() + "' AND\n" +
                "EXPDTE = '" + mats.getExpdte() + "'";
        database.execSQL(query);

        return  true;
    }

    public List<BoModel> getBoItmToSend(BoModel mats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<BoModel> getItmBo = new ArrayList<BoModel>();

        String query = "SELECT RTVNO,RTVDATE,EXTMAT,QTY,EXPDTE,SBMTDBY \n" +
                "FROM TBL_ABIS_ITM\n" +
                "WHERE HASSEND = 0 AND\n" +
                "CUSTCD = '" + mats.getCustcode() + "' AND \n" +
                "SBMTDBY = '" + mats.getBioId() + "'";
        Cursor cursor = database.rawQuery(query,null);

        if (cursor.moveToFirst())
        {
            do{
                BoModel mat = new BoModel();
                mat.setRtvnum(cursor.getString(0));
                mat.setRtvdate(cursor.getString(1));
                mat.setExtmat(cursor.getString(2));
                mat.setQty(cursor.getInt(3));
                mat.setExpdte(cursor.getString(4));
                mat.setBioId(cursor.getString(5));
                getItmBo.add(mat);
            }while (cursor.moveToNext());
        }


        return getItmBo;
    }

    public boolean UpdateBoItmSend(BoModel mats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        String query = "UPDATE TBL_ABIS_ITM\n" +
                "SET HASSEND = 1\n" +
                "WHERE CUSTCD = '" + mats.getCustcode() + "' AND\n" +
                "EXTMAT = '" + mats.getExtmat() + "' AND\n" +
                "SBMTDBY = '" + mats.getBioId() + "'";

        database.execSQL(query);
        return true;
    }

    public List<MaterialModel> getMatInv (MaterialModel mats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<MaterialModel> getInvItem = new ArrayList<MaterialModel>();

        String query = "SELECT DISTINCT A.MATDESC,CASE WHEN B.UNIT = 'PAC' THEN 'PACK/S'\n" +
                "WHEN B.UNIT = 'PC' THEN 'PC/S' ELSE B.UNIT END AS UOM\n" +
                "FROM TBL_INVT_ITM A\n" +
                "INNER JOIN TBL_MATERIALS B\n" +
                "ON A.EXTMAT = B.EXTMATGRP\n" +
                "WHERE CUSTCD = '" + mats.getCustcode() + "' AND HASSEND = 0 ";
        Cursor cursor = database.rawQuery(query,null);


        if (cursor.moveToFirst())
        {
            do{
                MaterialModel myItem = new MaterialModel();
                myItem.setMatnme(cursor.getString(0));
                myItem.setUom(cursor.getString(1));
                getInvItem.add(myItem);
            }while(cursor.moveToNext());
            cursor.close();
        }
        return  getInvItem;

    }

    public List<MaterialModel> getMatInvSent (MaterialModel mats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<MaterialModel> getInvItem = new ArrayList<MaterialModel>();

        String query = "SELECT DISTINCT A.MATDESC,CASE WHEN B.UNIT = 'PAC' THEN 'PACK/S'\n" +
                "WHEN B.UNIT = 'PC' THEN 'PC/S' ELSE B.UNIT END AS UOM\n" +
                "FROM TBL_INVT_ITM A\n" +
                "INNER JOIN TBL_MATERIALS B\n" +
                "ON A.EXTMAT = B.EXTMATGRP\n" +
                "WHERE CUSTCD = '" + mats.getCustcode() + "' AND HASSEND = 1 ";
        Cursor cursor = database.rawQuery(query,null);


        if (cursor.moveToFirst())
        {
            do{
                MaterialModel myItem = new MaterialModel();
                myItem.setMatnme(cursor.getString(0));
                myItem.setUom(cursor.getString(1));
                getInvItem.add(myItem);
            }while(cursor.moveToNext());
            cursor.close();
        }
        return  getInvItem;

    }


    public List<MaterialModel> getMatDel (MaterialModel mats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<MaterialModel> getInvItem = new ArrayList<MaterialModel>();

        String query = "SELECT DISTINCT A.MATDESC,CASE WHEN B.UNIT = 'PAC' THEN 'PACK/S'\n" +
                "WHEN B.UNIT = 'PC' THEN 'PC/S' ELSE B.UNIT END AS UOM\n" +
                "FROM TBL_DEL_ITM A\n" +
                "INNER JOIN TBL_MATERIALS B\n" +
                "ON A.EXTMAT = B.EXTMATGRP\n" +
                "WHERE CUSTCD = '" + mats.getCustcode() + "' AND HASSEND = 0";
        Cursor cursor = database.rawQuery(query,null);


        if (cursor.moveToFirst())
        {
            do{
                MaterialModel myItem = new MaterialModel();
                myItem.setMatnme(cursor.getString(0));
                myItem.setUom(cursor.getString(1));
                getInvItem.add(myItem);
            }while(cursor.moveToNext());
            cursor.close();
        }
        return  getInvItem;

    }


    public List<MaterialModel> getMatDelSent (MaterialModel mats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<MaterialModel> getInvItem = new ArrayList<MaterialModel>();

        String query = "SELECT DISTINCT A.MATDESC,CASE WHEN B.UNIT = 'PAC' THEN 'PACK/S'\n" +
                "WHEN B.UNIT = 'PC' THEN 'PC/S' ELSE B.UNIT END AS UOM\n" +
                "FROM TBL_DEL_ITM A\n" +
                "INNER JOIN TBL_MATERIALS B\n" +
                "ON A.EXTMAT = B.EXTMATGRP\n" +
                "WHERE CUSTCD = '" + mats.getCustcode() + "' AND HASSEND = 1";
        Cursor cursor = database.rawQuery(query,null);


        if (cursor.moveToFirst())
        {
            do{
                MaterialModel myItem = new MaterialModel();
                myItem.setMatnme(cursor.getString(0));
                myItem.setUom(cursor.getString(1));
                getInvItem.add(myItem);
            }while(cursor.moveToNext());
            cursor.close();
        }
        return  getInvItem;

    }


    public List<MaterialModel> getMatAbis (MaterialModel mats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<MaterialModel> getInvItem = new ArrayList<MaterialModel>();

        String query = "SELECT DISTINCT A.MATDESC,CASE WHEN B.UNIT = 'PAC' THEN 'PACK/S'\n" +
                "WHEN B.UNIT = 'PC' THEN 'PC/S' ELSE B.UNIT END AS UOM\n" +
                "FROM TBL_ABIS_ITM A\n" +
                "INNER JOIN TBL_MATERIALS B\n" +
                "ON A.EXTMAT = B.EXTMATGRP\n" +
                "WHERE CUSTCD = '" + mats.getCustcode() + "' AND HASSEND = 0";
        Cursor cursor = database.rawQuery(query,null);


        if (cursor.moveToFirst())
        {
            do{
                MaterialModel myItem = new MaterialModel();
                myItem.setMatnme(cursor.getString(0));
                myItem.setUom(cursor.getString(1));
                getInvItem.add(myItem);
            }while(cursor.moveToNext());
            cursor.close();
        }
        return  getInvItem;

    }


    public List<MaterialModel> getMatAbisSent (MaterialModel mats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<MaterialModel> getInvItem = new ArrayList<MaterialModel>();

        String query = "SELECT DISTINCT A.MATDESC,CASE WHEN B.UNIT = 'PAC' THEN 'PACK/S'\n" +
                "WHEN B.UNIT = 'PC' THEN 'PC/S' ELSE B.UNIT END AS UOM\n" +
                "FROM TBL_ABIS_ITM A\n" +
                "INNER JOIN TBL_MATERIALS B\n" +
                "ON A.EXTMAT = B.EXTMATGRP\n" +
                "WHERE CUSTCD = '" + mats.getCustcode() + "' AND HASSEND = 1";
        Cursor cursor = database.rawQuery(query,null);


        if (cursor.moveToFirst())
        {
            do{
                MaterialModel myItem = new MaterialModel();
                myItem.setMatnme(cursor.getString(0));
                myItem.setUom(cursor.getString(1));
                getInvItem.add(myItem);
            }while(cursor.moveToNext());
            cursor.close();
        }
        return  getInvItem;

    }



    public List<MaterialModel> getMatInvDets (MaterialModel mats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<MaterialModel> getInvItemDets = new ArrayList<MaterialModel>();

        String query = "SELECT MATDESC,QTY,EXPDTE \n" +
                "FROM TBL_INVT_ITM\n" +
                "WHERE MATDESC = '" + mats.getMatnme() + "'";
        Cursor cursor = database.rawQuery(query,null);


        if (cursor.moveToFirst())
        {
            do{
                MaterialModel myItemDets = new MaterialModel();
                myItemDets.setMatnme(cursor.getString(0));
                myItemDets.setQty(cursor.getInt(1));
                myItemDets.setExpdte(cursor.getString(2));
                getInvItemDets.add(myItemDets);
            }while(cursor.moveToNext());
            cursor.close();
        }
        return  getInvItemDets;

    }



    public int CountItm (MaterialModel mats, int trigg)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        int Cnt = 0;


        if (trigg == 0)
        {
            String query = "SELECT COUNT(*) FROM TBL_INVT_ITM\n" +
                    "WHERE CUSTCD = '" + mats.getCustcode() + "' AND\n" +
                    "MATDESC = '" + mats.getMatnme() + "' AND\n" +
                    "EXPDTE = '" + mats.getExpdte() + "'";
            Cursor cursor = database.rawQuery(query,null);

            if(cursor.moveToFirst())
            {
                do{
                    Cnt = cursor.getInt(0);
                }while (cursor.moveToNext());
                cursor.close();
            }
        }
        else if (trigg == 1)
        {
            String query = "SELECT COUNT(*) FROM TBL_DEL_ITM\n" +
                    "WHERE CUSTCD = '" + mats.getCustcode() + "' AND\n" +
                    "MATDESC = '" + mats.getMatnme() + "' AND\n" +
                    "EXPDTE = '" + mats.getExpdte() + "'";
            Cursor cursor = database.rawQuery(query,null);

            if(cursor.moveToFirst())
            {
                do{
                    Cnt = cursor.getInt(0);
                }while (cursor.moveToNext());
                cursor.close();
            }
        }
        else
        {
            String query = "SELECT COUNT(*) FROM TBL_ABIS_ITM\n" +
                    "WHERE CUSTCD = '" + mats.getCustcode() + "' AND\n" +
                    "MATDESC = '" + mats.getMatnme() + "' AND\n" +
                    "EXPDTE = '" + mats.getExpdte() + "'";
            Cursor cursor = database.rawQuery(query,null);

            if(cursor.moveToFirst())
            {
                do{
                    Cnt = cursor.getInt(0);
                }while (cursor.moveToNext());
                cursor.close();
            }
        }

        return  Cnt;
    }

    public List<MaterialModel> getMatDelDets (MaterialModel mats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<MaterialModel> getDelItemDets = new ArrayList<MaterialModel>();

        String query = "SELECT MATDESC,QTY,EXPDTE \n" +
                "FROM TBL_DEL_ITM\n" +
                "WHERE MATDESC = '" + mats.getMatnme() + "'";
        Cursor cursor = database.rawQuery(query,null);


        if (cursor.moveToFirst())
        {
            do{
                MaterialModel myItemDets = new MaterialModel();
                myItemDets.setMatnme(cursor.getString(0));
                myItemDets.setQty(cursor.getInt(1));
                myItemDets.setExpdte(cursor.getString(2));
                getDelItemDets.add(myItemDets);
            }while(cursor.moveToNext());
            cursor.close();
        }
        return  getDelItemDets;

    }


    public List<MaterialModel> getMatAbisDets (MaterialModel mats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<MaterialModel> getAbisItemDets = new ArrayList<MaterialModel>();

        String query = "SELECT MATDESC,QTY,EXPDTE \n" +
                "FROM TBL_ABIS_ITM\n" +
                "WHERE MATDESC = '" + mats.getMatnme() + "'";
        Cursor cursor = database.rawQuery(query,null);


        if (cursor.moveToFirst())
        {
            do{
                MaterialModel myItemDets = new MaterialModel();
                myItemDets.setMatnme(cursor.getString(0));
                myItemDets.setQty(cursor.getInt(1));
                myItemDets.setExpdte(cursor.getString(2));
                getAbisItemDets.add(myItemDets);
            }while(cursor.moveToNext());
            cursor.close();
        }
        return  getAbisItemDets;

    }

    public List<PriceSurveyModel> getPrceModel(PriceSurveyModel prc)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<PriceSurveyModel> getPrceData = new ArrayList<PriceSurveyModel>();

        String query = "SELECT PRDCTCD,PRCE,SBMTDBY \n" +
                "FROM TBL_PRCE_SRVEY\n" +
                "WHERE CUSTCD = '" + prc.getCustcode() + "' AND\n" +
                "SBMTDBY = '" + prc.getBioId() + "' AND\n" +
                "HASSEND = 0\n";
        Cursor cursor =  database.rawQuery(query,null);

        if (cursor.moveToFirst())
        {
            do{
                PriceSurveyModel prceData = new PriceSurveyModel();
                prceData.setPrdct(cursor.getString(0));
                prceData.setPrce(cursor.getDouble(1));
                prceData.setBioId(cursor.getString(2));
                getPrceData.add(prceData);
            }while (cursor.moveToNext());
            cursor.close();
        }

        return  getPrceData;
    }

    public boolean AddPrceSrvey (PriceSurveyModel prce)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        String query = "Insert into TBL_PRCE_SRVEY (CUSTCD,PRDCTCD,PRCE,SBMTDBY) Values (?,?,?,?)";
        SQLiteStatement insertStmt = database.compileStatement(query);
        insertStmt.clearBindings();
        insertStmt.bindString(1,prce.getCustcode());
        insertStmt.bindString(2,prce.getPrdct());
        insertStmt.bindString(3, String.valueOf(prce.getPrce()));
        insertStmt.bindString(4, prce.getBioId());
        insertStmt.executeInsert();

        return  true;
    }

    public boolean UpdatePrceSrvry (PriceSurveyModel prce)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        String query = "UPDATE TBL_PRCE_SRVEY\n" +
                "SET PRCE = '" + prce.getPrce() + "'\n" +
                "WHERE CUSTCD = '" + prce.getCustcode() + "' AND \n" +
                "PRDCTCD = '" + prce.getPrdct() + "' AND \n" +
                "SBMTDBY = '" + prce.getBioId() + "'";
        database.execSQL(query);

        return true;
    }

    public boolean DeletePrceSrvy (PriceSurveyModel prc)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        String query = "DELETE FROM TBL_PRCE_SRVEY\n" +
                "WHERE PRDCTCD = '" + prc.getPrdct() + "' AND \n" +
                "CUSTCD = '" + prc.getCustcode() + "' AND\n" +
                "SBMTDBY = '" + prc.getBioId() + "'";
        database.execSQL(query);

        return  true;
    }


    public boolean UpdatePrceSrvrySend (PriceSurveyModel prce)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        String query = "UPDATE TBL_PRCE_SRVEY\n" +
                "SET HASSEND = 1\n" +
                "WHERE CUSTCD = '" + prce.getCustcode() + "' AND\n" +
                "SBMTDBY = '" + prce.getBioId() + "' AND \n" +
                "PRDCTCD = '" + prce.getPrdct() + "' AND \n" +
                "PRCE = '" + prce.getPrce() + "'";
        database.execSQL(query);

        return true;
    }

    public int CountPrceItm (PriceSurveyModel prc)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        int cnt = 0;


        String query = "SELECT COUNT(*) FROM TBL_PRCE_SRVEY\n" +
                "WHERE PRDCTCD = '" + prc.getPrdct() + "' AND\n" +
                "CUSTCD = '" + prc.getCustcode() + "' AND\n" +
                "SBMTDBY = '" + prc.getBioId() + "'";
        Cursor cursor = database.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do{
                cnt = cursor.getInt(0);
            }while (cursor.moveToNext());
            cursor.close();
        }

        return  cnt;
    }

    public boolean AddStatsRpt (StatusReportModel stats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        String query = "Insert into TBL_STATUS_REPORT (CUSTCD,STATS_ID,DTE,TME,SBMTDBY) Values (?,?,?,?,?)";
        SQLiteStatement insertStmt = database.compileStatement(query);
        insertStmt.clearBindings();
        insertStmt.bindString(1,stats.getCustcode());
        insertStmt.bindString(2, String.valueOf(stats.getStatsID()));
        insertStmt.bindString(3, stats.getStatsDate());
        insertStmt.bindString(4, stats.getStatsTme());
        insertStmt.bindString(5, stats.getBioId());
        insertStmt.executeInsert();

        return  true;
    }


    public List<StatusReportModel> getStatsReport(StatusReportModel stats)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<StatusReportModel> getData = new ArrayList<StatusReportModel>();

        String query = "SELECT DISTINCT C.CUSTALIAS,B.STATSDESC,A.DTE,A.TME FROM TBL_STATUS_REPORT A\n" +
                "INNER JOIN TBL_STATS_RPRT_CAT B \n" +
                "\tON A.STATS_ID = B.ID\n" +
                "INNER JOIN TBL_CUSTOMERS C\n" +
                "\tON A.CUSTCD = C.CUSTCD\n" +
                "WHERE A.HASSEND = 1 AND\n" +
                "A.CUSTCD = '" + stats.getCustcode() + "' AND\n" +
                "A.SBMTDBY = '" + stats.getBioId() + "'";
        Cursor cursor = database.rawQuery(query,null);

        if (cursor.moveToFirst())
        {
            do{
                StatusReportModel stats_ = new StatusReportModel();
                stats_.setCustalias(cursor.getString(0));
                stats_.setStatscat(cursor.getString(1));
                stats_.setStatsDate(cursor.getString(2));
                stats_.setStatsTme(cursor.getString(3));
                getData.add(stats_);
            }while (cursor.moveToNext());
            cursor.close();
        }


        return getData;

    }

    public List<CustomerModel> showCust()
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<CustomerModel> getCustomers = new ArrayList<CustomerModel>();

        String query = "SELECT CUSTCD,CUSTNM,CUSTALIAS FROM TBL_CUSTOMERS";
        Cursor cursor = database.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do{
                CustomerModel cust = new CustomerModel();
                cust.setCustcode(cursor.getString(0));
                cust.setCustnme(cursor.getString(1));
                cust.setCustalias(cursor.getString(2));
                getCustomers.add(cust);
            }while (cursor.moveToNext());
            cursor.close();
        }

        return getCustomers;
    }


    public List<MaterialModel> showMats()
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<MaterialModel> getMats = new ArrayList<MaterialModel>();

        String query = "SELECT * FROM TBL_MATERIALS";
        Cursor cursor = database.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do{
                MaterialModel mats = new MaterialModel();
                mats.setMatcd(cursor.getString(0));
                mats.setMatnme(cursor.getString(1));
                mats.setUom(cursor.getString(2));
                mats.setExtmat(cursor.getString(3));
                getMats.add(mats);
            }while (cursor.moveToNext());
            cursor.close();
        }

        return getMats;
    }

    public List<DiserModel> showDiserInfo()
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<DiserModel> getMats = new ArrayList<DiserModel>();

        String query = "SELECT BIO_ID,FNAME,MNAME,LNAME,COMPNY,ZRXNO  FROM TBL_DISER_INFO";
        Cursor cursor = database.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do{
                DiserModel mats = new DiserModel();
                mats.setBioId(cursor.getString(0));
                mats.setFname(cursor.getString(1));
                mats.setMname(cursor.getString(2));
                mats.setLname(cursor.getString(3));
                mats.setCompny(cursor.getString(4));
                mats.setMobile(cursor.getString(5));
                getMats.add(mats);
            }while (cursor.moveToNext());
            cursor.close();
        }

        return getMats;
    }

    public List<StatusReportModel> showStatsCat()
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<StatusReportModel> getMats = new ArrayList<StatusReportModel>();

        String query = "SELECT * FROM TBL_STATS_RPRT_CAT";
        Cursor cursor = database.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do{
                StatusReportModel mats = new StatusReportModel();
                mats.setStatsID(cursor.getInt(0));
                mats.setStatscat(cursor.getString(1));
                getMats.add(mats);
            }while (cursor.moveToNext());
            cursor.close();
        }

        return getMats;
    }


    public List<IncidentReportModel> showReportCat()
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<IncidentReportModel> getReportCat = new ArrayList<IncidentReportModel>();

        String query = "SELECT * FROM TBL_REPRT_CAT";
        Cursor cursor = database.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do{
                IncidentReportModel cats = new IncidentReportModel();
                cats.setID(cursor.getInt(0));
                cats.setReportcat(cursor.getString(1));
                getReportCat.add(cats);
            }while (cursor.moveToNext());
            cursor.close();
        }

        return getReportCat;
    }

    public List<FilingModel> showFilingCat()
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<FilingModel> getFilingCat = new ArrayList<FilingModel>();

        String query = "SELECT * FROM TBL_FILING_RPRT_CAT_CLSS";
        Cursor cursor = database.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do{
                FilingModel cats = new FilingModel();
                cats.setID(cursor.getInt(0));
                cats.setFilingcat(cursor.getString(1));
                getFilingCat.add(cats);
            }while (cursor.moveToNext());
            cursor.close();
        }

        return getFilingCat;
    }


    public List<DisplayModel> showDispCat()
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<DisplayModel> getDisplayCat = new ArrayList<DisplayModel>();

        String query = "SELECT * FROM TBL_DSPLY_CLSS";
        Cursor cursor = database.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do{
                DisplayModel cats = new DisplayModel();
                cats.setID(cursor.getInt(0));
                cats.setDispcat(cursor.getString(1));
                getDisplayCat.add(cats);
            }while (cursor.moveToNext());
            cursor.close();
        }

        return getDisplayCat;
    }


    public List<ExpenseModel> showExpnseCat()
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<ExpenseModel> getExpnseCat = new ArrayList<ExpenseModel>();

        String query = "SELECT * FROM TBL_XPNS_CLSS";
        Cursor cursor = database.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do{
                ExpenseModel cats = new ExpenseModel();
                cats.setId(cursor.getInt(0));
                cats.setXpnseDesc(cursor.getString(1));
                getExpnseCat.add(cats);
            }while (cursor.moveToNext());
            cursor.close();
        }

        return getExpnseCat;
    }


    public int getCheckInCnt (String bioid)
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        int cnter = 0;

        String query = "SELECT CASE WHEN CHECK_IN_CUST = '' THEN 0 ELSE 1 END AS 'CHECKINCUST' FROM TBL_RAW_PROCESS\n" +
                "WHERE BIOID = '" + bioid + "'";
        Cursor cursor = database.rawQuery(query,null);


        if(cursor.moveToFirst())
        {
            do{
                cnter = cursor.getInt(0);
            }while (cursor.moveToNext());
            cursor.close();
        }


        return  cnter;
    }


    public List<CustomerModel> showRaw()
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();

        List<CustomerModel> getData = new ArrayList<CustomerModel>();

        String query = "SELECT BIOID,CPNO,CHECK_IN_CUST FROM TBL_RAW_PROCESS";
        Cursor cursor = database.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do{
                CustomerModel rawproc = new CustomerModel();
                rawproc.setBioId(cursor.getString(0));
                rawproc.setMobile(cursor.getString(1));
                rawproc.setCustalias(cursor.getString(2));
                getData.add(rawproc);
            }while (cursor.moveToNext());
            cursor.close();
        }

        return getData;
    }

    //TBL_DISER_INFO

    public void CREATETABLES()
    {
        database = openHelper.getWritableDatabase();
        database.isOpen();


        database.execSQL("CREATE TABLE IF NOT EXISTS TBL_STATS_RPRT_CAT\n" +
                "(\n" +
                "\tID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\tSTATSDESC TEXT NOT NULL DEFAULT ''\n" +
                ")");


        database.execSQL("CREATE TABLE IF NOT EXISTS TBL_REPRT_CAT\n" +
                "(\n" +
                "\tID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\tRPORDESC TEXT NOT NULL DEFAULT ''\n" +
                ")");

        database.execSQL("CREATE TABLE IF NOT EXISTS TBL_FILING_RPRT_CAT_CLSS\n" +
                "(\n" +
                "\tID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\tFILINGCATDESC TEXT NOT NULL DEFAULT ''\n" +
                ")");


        database.execSQL("CREATE TABLE IF NOT EXISTS TBL_XPNS_CLSS\n" +
                "(\n" +
                "\tID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\tXPNSDESC TEXT NOT NULL DEFAULT ''\n" +
                ")");


        database.execSQL("CREATE TABLE IF NOT EXISTS TBL_TRNSP_XPNS_CLSS\n" +
                "(\n" +
                "\tID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\tTRNSPOXPNSDESC TEXT NOT NULL DEFAULT ''\n" +
                ")");

        database.execSQL("CREATE TABLE IF NOT EXISTS TBL_DSPLY_CLSS\n" +
                "(\n" +
                "\tID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\tDSPLYDESC TEXT NOT NULL DEFAULT ''\n" +
                ")");

        database.execSQL("CREATE TABLE IF NOT EXISTS TBL_DISER_INFO\n" +
                "(\n" +
                "\tBIO_ID  TEXT NOT NULL DEFAULT '',\n" +
                "\tFNAME TEXT NOT NULL DEFAULT '',\n" +
                "\tMNAME TEXT NOT NULL DEFAULT '',\n" +
                "\tLNAME TEXT NOT NULL DEFAULT '',\n" +
                "\tCOMPNY TEXT NOT NULL DEFAULT '',\n" +
                "\tZRXNO TEXT NOT NULL DEFAULT ''\n" +
                ")");


        database.execSQL("CREATE TABLE IF NOT EXISTS TBL_ATTNDNCE\n" +
                "(\n" +
                "\tBIO_ID TEXT NOT NULL DEFAULT '',\n" +
                "\tDT_TME_IN REAL NOT NULL DEFAULT (DATETIME('now', 'localtime')),\n" +
                "\tDTE_IN_LOC TEXT NOT NULL DEFAULT '0.0,0.0',\n" +
                "\tDT_TME_OUT DATETIME NOT NULL DEFAULT '',\n" +
                "\tDTE_OUT_LOC TEXT NOT NULL DEFAULT '0.0,0.0'\t\n" +
                ")");


        database.execSQL("CREATE TABLE IF NOT EXISTS TBL_CHCK_IN_OUT\n" +
                "(\n" +
                "\tBIO_ID TEXT NOT NULL DEFAULT '',\n" +
                "\tCUSTCD TEXT NOT NULL DEFAULT '',\n" +
                "\tCHCK_IN_TME REAL NOT NULL DEFAULT (DATETIME('now', 'localtime')),\n" +
                "\tCHCK_IN_LOC TEXT NOT NULL DEFAULT '0.0,0.0',\n" +
                "\tCHCK_OUT_TME DATETIME NOT NULL DEFAULT  '',\n" +
                "\tCHCK_OUT_LOC TEXT NOT NULL DEFAULT '0.0,0.0'\n" +
                ")");


        database.execSQL("CREATE TABLE IF NOT EXISTS TBL_CUSTOMERS\n" +
                "(\n" +
                "\tCUSTCD TEXT NOT NULL DEFAULT '',\n" +
                "\tCUSTNM TEXT NOT NULL DEFAULT '',\n" +
                "\tCUSTALIAS TEXT NOT NULL DEFAULT ''\n" +
                ")");


        database.execSQL("CREATE TABLE IF NOT EXISTS TBL_MATERIALS\n" +
                "(\n" +
                "\tMATCODE TEXT NOT NULL DEFAULT '',\n" +
                "\tUNIT TEXT NOT NULL DEFAULT '',\n" +
                "\tMATSHRTDESC TEXT NOT NULL DEFAULT '',\n" +
                "\tEXTMATGRP TEXT NOT NULL DEFAULT ''\n" +
                ")");


        database.execSQL("CREATE TABLE IF NOT EXISTS TBL_RAW_PROCESS\n" +
                "(\n" +
                "\tBIOID TEXT NOT NULL DEFAULT '',\n" +
                "\tCHECK_IN_CUST TEXT NOT NULL DEFAULT '',\n" +
                "\tCPNO TEXT NOT NULL DEFAULT '',\n" +
                "\tISLOGIN INT NOT NULL DEFAULT 0\n" +
                ")");


        database.execSQL("CREATE TABLE IF NOT EXISTS TBL_INVT_ITM\n" +
                "(\n" +
                "\tCUSTCD TEXT NOT NULL DEFAULT '',\n" +
                "\tEXTMAT TEXT NOT NULL DEFAULT '',\n" +
                "\tMATDESC TEXT NOT NULL DEFAULT '',\n" +
                "\tQTY INT NOT NULL DEFAULT 0,\n" +
                "\tEXPDTE TEXT NOT NULL DEFAULT '',\n" +
                "\tHASSEND INT NOT NULL DEFAULT 0,\n" +
                "\tSBMTDBY TEXT NOT NULL DEFAULT '',\n" +
                "\tSBMTDDT REAL NOT NULL DEFAULT (datetime('now','localtime'))\n" +
                "\t\n" +
                ")");


        database.execSQL("CREATE TABLE IF NOT EXISTS TBL_DEL_ITM\n" +
                "(\n" +
                "\tCUSTCD TEXT NOT NULL DEFAULT '',\n" +
                "\tINVNO TEXT NOT NULL DEFAULT '',\n" +
                "\tINVDATE TEXT NOT NULL DEFAULT '',\n" +
                "\tEXTMAT TEXT NOT NULL DEFAULT '',\n" +
                "\tMATDESC TEXT NOT NULL DEFAULT '',\n" +
                "\tQTY INT NOT NULL DEFAULT 0,\n" +
                "\tEXPDTE TEXT NOT NULL DEFAULT '',\n" +
                "\tHASSEND INT NOT NULL DEFAULT 0,\n" +
                "\tSBMTDBY TEXT NOT NULL DEFAULT '',\n" +
                "\tSBMTDDT REAL NOT NULL DEFAULT (datetime('now','localtime'))\n" +
                "\t\n" +
                ")");


        database.execSQL("CREATE TABLE IF NOT EXISTS TBL_ABIS_ITM\n" +
                "(\n" +
                "\tCUSTCD TEXT NOT NULL DEFAULT '',\n" +
                "\tRTVNO TEXT NOT NULL DEFAULT '',\n" +
                "\tRTVDATE TEXT NOT NULL DEFAULT '',\n" +
                "\tEXTMAT TEXT NOT NULL DEFAULT '',\n" +
                "\tMATDESC TEXT NOT NULL DEFAULT '',\n" +
                "\tQTY INT NOT NULL DEFAULT 0,\n" +
                "\tEXPDTE TEXT NOT NULL DEFAULT '',\n" +
                "\tHASSEND INT NOT NULL DEFAULT 0,\n" +
                "\tSBMTDBY TEXT NOT NULL DEFAULT '',\n" +
                "\tSBMTDDT REAL NOT NULL DEFAULT (datetime('now','localtime'))\n" +
                "\t\n" +
                ")");


        database.execSQL("CREATE TABLE IF NOT EXISTS TBL_PRCE_SRVEY\n" +
                "(\n" +
                "\tCUSTCD TEXT NOT NULL DEFAULT '',\n" +
                "\tPRDCTCD TEXT NOT NULL DEFAULT '',\n" +
                "\tPRCE DECIMAL(10,5) NOT NULL DEFAULT 0,\n" +
                "\tHASSEND INT NOT NULL DEFAULT 0,\n" +
                "\tSBMTDBY TEXT NOT NULL DEFAULT '',\n" +
                "\tSBMTDDT REAL NOT NULL DEFAULT (datetime('now','localtime'))\n" +
                ")");


        database.execSQL("CREATE TABLE IF NOT EXISTS TBL_STATUS_REPORT\n" +
                "(\n" +
                "\tCUSTCD TEXT NOT NULL DEFAULT '',\n" +
                "\tSTATS_ID TEXT NOT NULL DEFAULT '',\n" +
                "\tTME TEXT NOT NULL DEFAULT '',\n" +
                "\tDTE TEXT NOT NULL DEFAULT '',\n" +
                "\tHASSEND INT NOT NULL DEFAULT 1,\n" +
                "\tSBMTDBY TEXT NOT NULL DEFAULT '',\n" +
                "\tSBMTDDT REAL NOT NULL DEFAULT (datetime('now','localtime'))\n" +
                ")");


        database.execSQL("CREATE TABLE IF NOT EXISTS TBL_ACTVTY_LOGS\n" +
                "(\n" +
                "\tCUSTCD TEXT NOT NULL DEFAULT '',\n" +
                "\tACTVTY TEXT NOT NULL DEFAULT '',\n" +
                "\tSBMTDBY TEXT NOT NULL DEFAULT '',\n" +
                "\tSBMTDDT REAL NOT NULL DEFAULT (datetime('now','localtime'))\n" +
                ")");


        database.execSQL("CREATE TABLE IF NOT EXISTS TBL_OFF_FILING\n" +
                "(\n" +
                "\tBIOID TEXT NOT NULL DEFAULT '',\n" +
                "\tLVETPE TEXT NOT NULL DEFAULT '',\n" +
                "\tRSON TEXT NOT NULL DEFAULT '',\n" +
                "\tSBMTDDT REAL NOT NULL DEFAULT (datetime('now','localtime'))\n" +
                ")");

    }

}
