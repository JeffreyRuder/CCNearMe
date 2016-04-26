package com.epicodus.ccnearme.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.util.List;

public class IntentSafetyCheck {

    //ensure that an appropriate app is available to handle an implicit intent
    public static boolean isSafe(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return activities.size() > 0;
    }
}
