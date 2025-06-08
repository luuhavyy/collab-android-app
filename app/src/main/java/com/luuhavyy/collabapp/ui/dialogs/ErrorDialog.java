package com.luuhavyy.collabapp.ui.dialogs;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;

public class ErrorDialog {

    public static void show(Context context, String message, Runnable onRetry) {
        new AlertDialog.Builder(context)
                .setTitle("Oops!")
                .setMessage(message != null ? message : "There is something wrong")
                .setCancelable(false)
                .setPositiveButton("Try Again", (dialog, which) -> {
                    if (onRetry != null) onRetry.run();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}