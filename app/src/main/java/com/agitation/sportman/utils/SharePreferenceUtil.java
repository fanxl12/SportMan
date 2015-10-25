package com.agitation.sportman.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Map;
import java.util.Set;

public class SharePreferenceUtil {
    private static SharedPreferences sp;
    private static final String SharePreferncesName = "MPOS_SETTING";

    public static boolean setValue(Context context, String key, Object value) {
        if (sp == null) {
            sp = context.getSharedPreferences(SharePreferncesName, Context.MODE_PRIVATE);
        }

        Editor edit = sp.edit();
        if (value instanceof String) {
            return edit.putString(key, (String) value).commit();
        } else if (value instanceof Boolean) {
            return edit.putBoolean(key, ((Boolean) value).booleanValue()).commit();
        } else if (value instanceof Float) {
            return edit.putFloat(key, ((Float) value).floatValue()).commit();
        } else if (value instanceof Integer) {
            return edit.putInt(key, ((Integer) value).intValue()).commit();
        } else if (value instanceof Long) {
            return edit.putLong(key, ((Long) value).longValue()).commit();
        } else if (value instanceof Set) {
            new IllegalArgumentException("Value can not be Set object!");
            return false;
        } else {
            return false;
        }
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        if (sp == null) {
            sp = context.getSharedPreferences(SharePreferncesName, Context.MODE_PRIVATE);
        }

        return sp.getBoolean(key, defaultValue);
    }

    public static String getString(Context context, String key, String defaultValue) {
        if (sp == null) {
            sp = context.getSharedPreferences(SharePreferncesName, Context.MODE_PRIVATE);
        }

        return sp.getString(key, defaultValue);
    }

    public static Float getFloat(Context context, String key, float defaultValue) {
        if (sp == null) {
            sp = context.getSharedPreferences(SharePreferncesName, Context.MODE_PRIVATE);
        }

        return Float.valueOf(sp.getFloat(key, defaultValue));
    }

    public static int getInt(Context context, String key, int defaultValue) {
        if (sp == null) {
            sp = context.getSharedPreferences(SharePreferncesName, Context.MODE_PRIVATE);
        }

        return sp.getInt(key, defaultValue);
    }

    public static long getLong(Context context, String key, long defaultValue) {
        if (sp == null) {
            sp = context.getSharedPreferences(SharePreferncesName, Context.MODE_PRIVATE);
        }

        return sp.getLong(key, defaultValue);
    }

    public static boolean remove(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(SharePreferncesName, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.remove(key);
        return editor.commit();
    }

    public static boolean clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SharePreferncesName, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.clear();
        return editor.commit();
    }

    public static boolean contains(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(SharePreferncesName, Context.MODE_PRIVATE);
        boolean result = sp.contains(key);
        return result;
    }

    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SharePreferncesName, Context.MODE_PRIVATE);
        return sp.getAll();
    }
}
