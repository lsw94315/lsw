package cn.jiguang.cordova.plugin.badge;

import android.content.Context;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static org.apache.cordova.PluginResult.Status.OK;


public class Badge extends CordovaPlugin{
    // Implementation of the badge interface methods
    private BadgeImpl impl;

    /**
     * Called after plugin construction and fields have been initialized.
     */
    protected void pluginInitialize() {
        impl = new BadgeImpl(getContext());
    }

    /**
     * Executes the request.
     *
     * @param action   The action to execute.
     * @param args     The exec() arguments.
     * @param callback The callback context used when
     *                 calling back into JavaScript.
     *
     * @return Returning false results in a "MethodNotFound" error.
     */
    @Override
    public boolean execute (String action, JSONArray args, CallbackContext callback)
            throws JSONException {

        if (action.equalsIgnoreCase("load")) {
            loadConfig(callback);
            return true;
        }

        if (action.equalsIgnoreCase("save")) {
            saveConfig(args.getJSONObject(0));
            return true;
        }

        if (action.equalsIgnoreCase("clear")) {
            clearBadge(callback);
            return true;
        }

        if (action.equalsIgnoreCase("get")) {
            getBadge(callback);
            return true;
        }

        if (action.equalsIgnoreCase("set")) {
            setBadge(args, callback);
            return true;
        }

        return false;
    }

    /**
     * Load the persisted plugin config.
     *
     * @param callback The function to be exec as the callback.
     */
    private void loadConfig(final CallbackContext callback) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                JSONObject cfg = impl.loadConfig();
                sendPluginResult(callback, cfg);
            }
        });
    }

    /**
     * Persist the plugin config.
     *
     * @param config The config map to persist.
     */
    private void saveConfig(final JSONObject config) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                impl.saveConfig(config);
            }
        });
    }

    /**
     * Clear the badge number.
     *
     * @param callback The function to be exec as the callback.
     */
    private void clearBadge (final CallbackContext callback) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                impl.clearBadge();
                int badge = impl.getBadge();
                sendPluginResult(callback, badge);
            }
        });
    }

    /**
     * Get the badge number.
     *
     * @param callback The function to be exec as the callback.
     */
    private void getBadge (final CallbackContext callback) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                int badge = impl.getBadge();
                sendPluginResult(callback, badge);
            }
        });
    }

    /**
     * Set the badge number.
     *
     * @param args     The number to set as the badge number.
     * @param callback The function to be exec as the callback.
     */
    private void setBadge (final JSONArray args,
                           final CallbackContext callback) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                impl.clearBadge();
                impl.setBadge(args.optInt(0));
                int badge = impl.getBadge();
                sendPluginResult(callback, badge);
            }
        });
    }

    /**
     * Send badge number has the plugin result back to the JS caller.
     *
     * @param callback The callback to invoke.
     * @param badge    The badge number to pass with.
     */
    private void sendPluginResult(CallbackContext callback, int badge) {
        PluginResult result = new PluginResult(OK, badge);
        callback.sendPluginResult(result);
    }

    /**
     * Send badge number has the plugin result back to the JS caller.
     *
     * @param callback The callback to invoke.
     * @param obj      The object to pass with.
     */
    private void sendPluginResult(CallbackContext callback, JSONObject obj) {
        PluginResult result = new PluginResult(OK, obj);
        callback.sendPluginResult(result);
    }

    /**
     * Returns the context of the activity.
     */
    private Context getContext () {
        return cordova.getActivity();
    }
}