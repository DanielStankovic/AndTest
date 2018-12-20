package com.androidb2c.microbs.androidb2c.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.androidb2c.microbs.androidb2c.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Utility {


    public static boolean isValidEmail(CharSequence text){
        return !TextUtils.isEmpty(text) && android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches();
    }


    public static void deleteApplicationData(Context context)
    {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists())
        {
            String[] children = appDir.list();
            for (String s : children)
            {
                if (!s.equals("lib"))
                {
                    deleteDir(new File(appDir, s));
                }
            }
        }
    }

    public static boolean deleteDir(File dir)
    {
        if (dir != null && dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success)
                {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static String generateOrderID(String customerID){
        StringBuilder sb = new StringBuilder();
        String[] shortCustID = customerID.split("-");
        String fullDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String date = fullDate.split(" ")[0];
        String time = fullDate.split(" ")[1];

        sb.append(shortCustID[0]);
        sb.append(shortCustID[1]);
        sb.append("_");
        sb.append(date.split("-")[0]);
        sb.append(date.split("-")[1]);
        sb.append(date.split("-")[2]);
        sb.append(time.split(":")[0]);
        sb.append(time.split(":")[1]);
        sb.append("_");
        sb.append(String.valueOf(new Random().nextInt(9999)));
        return sb.toString();
    }

    public static void showNoInternetDialog(Activity activity){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.myDialog);

        builder.setTitle("Woops!");
        builder.setMessage(activity.getResources().getString(R.string.no_internet_label));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
