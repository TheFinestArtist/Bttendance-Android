package com.bttendance.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.bttendance.event.update.UpdateUserEvent;
import com.bttendance.model.json.SchoolJsonArray;
import com.bttendance.model.json.UserJson;
import com.google.gson.Gson;
import com.squareup.otto.BTEventBus;

/**
 * Preference Helper
 *
 * @author The Finest Artist
 */
public class BTPreference {

    private static SharedPreferences mPref = null;
    private static Object mSingletonLock = new Object();

    private BTPreference() {
    }

    private static SharedPreferences getInstance(Context ctx) {
        synchronized (mSingletonLock) {
            if (mPref != null)
                return mPref;

            if (ctx != null) {
                mPref = ctx.getSharedPreferences("BTRef", Context.MODE_PRIVATE);
            }
            return mPref;
        }
    }

    // on Log out
    public static void clearUser(Context ctx) {
        Editor edit = getInstance(ctx).edit();
        edit.remove("users");
        edit.remove("user");
        edit.commit();
    }

    public static UserJson getUser(Context ctx) {
        String jsonStr = getInstance(ctx).getString("users", null);
        if (jsonStr == null)
            return null;

        Gson gson = new Gson();
        try {
            UserJson user = gson.fromJson(jsonStr, UserJson.class);
            return user;
        } catch (Exception e) {
            clearUser(ctx);
            return null;
        }
    }

    public static int getUserId(Context ctx) {
        UserJson user = getUser(ctx);
        if (user == null)
            return -1;
        return user.id;
    }

    public static void setUser(Context ctx, UserJson user) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(user);

        Editor edit = getInstance(ctx).edit();
        edit.putString("users", jsonStr);
        edit.commit();

        BTEventBus.getInstance().post(new UpdateUserEvent());
    }

    public static SchoolJsonArray getAllSchools(Context ctx) {
        String jsonStr = getInstance(ctx).getString("all_schools", null);
        if (jsonStr == null)
            return null;

        Gson gson = new Gson();
        try {
            SchoolJsonArray schoolJsonArray = gson.fromJson(jsonStr, SchoolJsonArray.class);
            return schoolJsonArray;
        } catch (Exception e) {
            return null;
        }
    }

    public static void setAllSchools(Context ctx, SchoolJsonArray schoolJsonArray) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(schoolJsonArray);

        Editor edit = getInstance(ctx).edit();
        edit.putString("all_schools", jsonStr);
        edit.commit();
    }

    // courses, schools, posts

    public static String getUUID(Context ctx) {
        return getInstance(ctx).getString("uuid", null);
    }

    public static void setUUID(Context ctx, String uuid) {
        Editor edit = getInstance(ctx).edit();
        edit.putString("uuid", uuid);
        edit.commit();
    }

}// end of class
