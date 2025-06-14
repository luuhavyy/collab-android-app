package com.luuhavyy.collabapp.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.luuhavyy.collabapp.LoginActivity;
import com.luuhavyy.collabapp.R;

public class AuthUtil {

    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static boolean isLoggedIn() {
        return getCurrentUser() != null;
    }

    public static void checkLoginAndRedirect(Context context, Runnable onLoggedIn) {
        if (isLoggedIn()) {
            onLoggedIn.run();
        } else {
            showLoginDialog(context);
        }
    }

    public static void showLoginDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_login_prompt, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        view.findViewById(R.id.btn_close).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.btn_login).setOnClickListener(v -> {
            dialog.dismiss();
            context.startActivity(new Intent(context, LoginActivity.class));
        });

        dialog.show();
    }
}