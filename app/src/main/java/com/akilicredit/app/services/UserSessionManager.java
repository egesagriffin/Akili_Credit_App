package com.akilicredit.app.services;

import android.content.Context;
import android.content.SharedPreferences;
import com.akilicredit.app.models.Applicant;
import com.google.gson.Gson;

/**
 * Manages user session and persistence of applicant data.
 */
public class UserSessionManager {
    private static final String PREF_NAME = "AkiliCreditPrefs";
    private static final String KEY_APPLICANT = "current_applicant";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Gson gson;

    public UserSessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        gson = new Gson();
    }

    public void saveApplicant(Applicant applicant) {
        String json = gson.toJson(applicant);
        editor.putString(KEY_APPLICANT, json);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public Applicant getApplicant() {
        String json = pref.getString(KEY_APPLICANT, null);
        if (json == null) return null;
        return gson.fromJson(json, Applicant.class);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}