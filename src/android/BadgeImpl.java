package cn.jiguang.cordova.plugin.badge;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import me.leolin.shortcutbadger.ShortcutBadger;

public final class BadgeImpl {
    // The name for the shared preferences key
    private static final String BADGE_KEY = "badge";

    // The name for the shared preferences key
    private static final String CONFIG_KEY = "badge.config";

    // The application context
    private final Context ctx;

    /**
     * Initializes the impl with the context of the app.
     *
     * @param context The app context.
     */
    public BadgeImpl(Context context) {
        this.ctx = context;
    }

    /**
     * Clear the badge number.
     */
    public void clearBadge() {
        saveBadge(0);
        ShortcutBadger.removeCount(ctx);
    }

    /**
     * Get the badge number.
     *
     * @return The badge number
     */
    public int getBadge() {
        return getPrefs().getInt(BADGE_KEY, 0);
    }

    /**
     * Set the badge number.
     *
     * @param badge The number to set as the badge number.
     */
    public void setBadge(int badge) {
        saveBadge(badge);
        ShortcutBadger.applyCount(ctx, badge);
    }

    /**
     * The badge auto add 1
     */
    public void autoBadge() {
        setBadge(getBadge() + 1);
    }

    /**
     * Get the persisted config map.
     */
    public JSONObject loadConfig() {
        String json = getPrefs().getString(CONFIG_KEY, "{}");

        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    /**
     * Persist the config map so that `autoClear` has same value after restart.
     *
     * @param config The config map to persist.
     */
    public void saveConfig(JSONObject config) {
        SharedPreferences.Editor editor = getPrefs().edit();

        editor.putString(CONFIG_KEY, config.toString());
        editor.apply();
    }

    /**
     * Persist the badge of the app icon so that `getBadge` is able to return
     * the badge number back to the client.
     *
     * @param badge The badge number to persist.
     */
    private void saveBadge(int badge) {
        SharedPreferences.Editor editor = getPrefs().edit();

        editor.putInt(BADGE_KEY, badge);
        editor.apply();
    }

    /**
     * The Local storage for the application.
     */
    private SharedPreferences getPrefs() {
        return ctx.getSharedPreferences(BADGE_KEY, Context.MODE_PRIVATE);
    }


}
