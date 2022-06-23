package com.lemonsquare.diserapps;

import android.app.Dialog;
import android.content.Context;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeNoticeDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeWarningDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.lemonsquare.diserapps.Controls.InventoryActivity;

public class DialogControl extends Dialog {

    private Context context;
    private int type,resource;
    private String  Message;


    public DialogControl(Context context,int type,int resource, String Message) {
        super(context);

        this.context = context;
        this.type = type;
        this.Message = Message;
        this.resource = resource;


        if (type == 1)
        {
            AwesomeErrorDialog awesomeErrorDialog = new AwesomeErrorDialog(context);
            awesomeErrorDialog.setTitle("WARNING");
            awesomeErrorDialog.setMessage(Message);
            awesomeErrorDialog.setDialogIconAndColor(resource, R.color.white);
            awesomeErrorDialog.setColoredCircle(R.color.dialogErrorBackgroundColor);
            awesomeErrorDialog.setCancelable(true);
            awesomeErrorDialog.setButtonText("Ok");
            awesomeErrorDialog.setButtonBackgroundColor(R.color.dialogErrorBackgroundColor);
            awesomeErrorDialog.setButtonTextColor(R.color.white);
            awesomeErrorDialog.setErrorButtonClick(new Closure() {
                @Override
                public void exec() {
                    awesomeErrorDialog.setCancelable(true);
                }
            });
            awesomeErrorDialog.show();
        }
        else if (type == 2)
        {
            AwesomeSuccessDialog awesomeSuccessDialog = new AwesomeSuccessDialog(context);
            awesomeSuccessDialog.setTitle("SUCCESS");
            awesomeSuccessDialog.setMessage(Message);
            awesomeSuccessDialog.setDialogIconAndColor(resource, R.color.white);
            awesomeSuccessDialog.setColoredCircle(R.color.dialogSuccessBackgroundColor);
            awesomeSuccessDialog.setCancelable(true);
            awesomeSuccessDialog.setPositiveButtonText("Done");
            awesomeSuccessDialog.setPositiveButtonTextColor(R.color.white);
            awesomeSuccessDialog.setPositiveButtonbackgroundColor(R.color.dialogSuccessBackgroundColor);
            awesomeSuccessDialog.setPositiveButtonClick(new Closure() {
                @Override
                public void exec() {
                    awesomeSuccessDialog.setCancelable(true);
                }
            });

            awesomeSuccessDialog.show();
        }
        else if (type == 3)
        {
            AwesomeNoticeDialog awesomeNoticeDialog = new AwesomeNoticeDialog(context);
            awesomeNoticeDialog.setTitle("DELETED");
            awesomeNoticeDialog.setMessage(Message);
            awesomeNoticeDialog.setDialogIconAndColor(resource,R.color.white);
            awesomeNoticeDialog.setColoredCircle(R.color.dialogNoticeBackgroundColor);
            awesomeNoticeDialog.setCancelable(true);
            awesomeNoticeDialog.setButtonText("Ok");
            awesomeNoticeDialog.setButtonBackgroundColor(R.color.dialogNoticeBackgroundColor);
            awesomeNoticeDialog.setButtonTextColor(R.color.white);
            awesomeNoticeDialog.setNoticeButtonClick(new Closure() {
                @Override
                public void exec() {
                    awesomeNoticeDialog.setCancelable(true);
                }
            });
            awesomeNoticeDialog.show();

        }
        else if  (type == 4)
        {
            AwesomeInfoDialog awesomeInfoDialog = new AwesomeInfoDialog(context);
            awesomeInfoDialog.setTitle("INFORMATION");
            awesomeInfoDialog.setMessage(Message);
            awesomeInfoDialog.setDialogIconAndColor(resource,R.color.white);
            awesomeInfoDialog.setColoredCircle(R.color.dialogInfoBackgroundColor);
            awesomeInfoDialog.setCancelable(true);
            awesomeInfoDialog.setNeutralButtonText("Ok");
            awesomeInfoDialog.setNeutralButtonbackgroundColor(R.color.dialogInfoBackgroundColor);
            awesomeInfoDialog.setNegativeButtonTextColor(R.color.white);
            awesomeInfoDialog.setNeutralButtonClick(new Closure() {
                @Override
                public void exec() {
                    awesomeInfoDialog.setCancelable(true);
                }
            });

            awesomeInfoDialog.show();

        }

        else if  (type == 5)
        {
            AwesomeWarningDialog awesomeWarningDialog = new AwesomeWarningDialog(context);
            awesomeWarningDialog.setTitle("DATA CONNECTION");
            awesomeWarningDialog.setMessage(Message);
            awesomeWarningDialog.setColoredCircle(R.color.dialogWarningBackgroundColor);
            awesomeWarningDialog.setDialogIconAndColor(resource,R.color.white);
            awesomeWarningDialog.setCancelable(true);
            awesomeWarningDialog.setButtonText("Ok");
            awesomeWarningDialog.setButtonBackgroundColor(R.color.dialogWarningBackgroundColor);
            awesomeWarningDialog.setButtonTextColor(R.color.white);
            awesomeWarningDialog.setWarningButtonClick(new Closure() {
                @Override
                public void exec() {
                    awesomeWarningDialog.setCancelable(true);
                }
            });
            awesomeWarningDialog.show();

        }
    }
}
