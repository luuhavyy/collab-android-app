package com.luuhavyy.collabapp.utils;

import android.content.Context;

import com.luuhavyy.collabapp.ui.dialogs.LoadingDialog;

public class LoadingHandlerUtil {

    public interface LoadingTask {
        void run(TaskCallback callback);
    }

    public interface TaskCallback {
        void onComplete();
    }

    public static void executeWithLoading(Context context, LoadingTask task) {
        LoadingDialog loadingDialog = new LoadingDialog(context);
        loadingDialog.show();

        task.run(() -> {
            // gọi khi task hoàn tất
            loadingDialog.dismiss();
        });
    }
}
