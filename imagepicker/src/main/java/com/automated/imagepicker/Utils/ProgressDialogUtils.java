package com.automated.imagepicker.Utils;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogUtils
{
    public static ProgressDialog getProgressDialog(Context mContext) {
        ProgressDialog pd = new ProgressDialog(mContext);
        pd.setIndeterminate(false);
        pd.setTitle(null);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        return pd;
    }
}
