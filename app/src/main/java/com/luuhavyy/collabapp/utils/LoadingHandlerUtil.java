package com.luuhavyy.collabapp.utils;

import android.content.Context;

import com.luuhavyy.collabapp.ui.dialogs.LoadingDialog;

import java.util.concurrent.atomic.AtomicBoolean;

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
            if (loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        });
    }

    public static void executeOnceWithLoading(Context context, LoadingTask task) {
        AtomicBoolean isCalled = new AtomicBoolean(false);
        LoadingDialog loadingDialog = new LoadingDialog(context);
        loadingDialog.show();

        task.run(() -> {
            if (isCalled.compareAndSet(false, true)) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
            }
        });
    }
}
