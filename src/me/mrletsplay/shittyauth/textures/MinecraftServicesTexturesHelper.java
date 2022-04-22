package me.mrletsplay.shittyauth.textures;

import me.mrletsplay.mrcore.json.JSONObject;
import me.mrletsplay.shittyauth.ShittyAuth;
import me.mrletsplay.shittyauth.config.ShittyAuthSettings;
import me.mrletsplay.shittyauth.page.api.UserCapeDocument;
import me.mrletsplay.shittyauth.page.api.UserSkinDocument;
import me.mrletsplay.shittyauth.user.UserData;
import me.mrletsplay.webinterfaceapi.webinterface.Webinterface;
import me.mrletsplay.webinterfaceapi.webinterface.config.DefaultSettings;

public class MinecraftServicesTexturesHelper {
    public static final String FALLBACK_HOST = "http://"
            + Webinterface.getConfig().getSetting(DefaultSettings.HTTP_HOST) + ":"
            + Webinterface.getConfig().getSetting(DefaultSettings.HTTP_PORT),
            SKIN_PATH = UserSkinDocument.PATH_PREFIX + "%s",
            CAPE_PATH = UserCapeDocument.PATH_PREFIX + "%s";

    private static String getHost() {
        String configHost = ShittyAuth.config.getSetting(ShittyAuthSettings.SKIN_BASE_URL);
        return configHost != null ? configHost : FALLBACK_HOST;
    }

    public static JSONObject getSkinsObject(UserData user) {
        JSONObject skin = new JSONObject();

        skin.put("id", user.getSkinId());
        skin.put("url", String.format(getHost() + SKIN_PATH, user.getSkinId()));
        skin.put("state", "ACTIVE");

        if (user.getSkinType() == SkinType.ALEX) {
            skin.put("variant", "SLIM");
            skin.put("alias", "ALEX");
        } else {
            skin.put("variant", "CLASSIC");
            skin.put("alias", "STEVE");
        }

        return skin;
    }

    public static JSONObject getCapesObject(UserData user) {
        JSONObject cape = new JSONObject();

        if (user.hasCape()) {
            cape.put("id", user.getCapeId());
            cape.put("url", String.format(getHost() + CAPE_PATH, user.getCapeId()));
            cape.put("state", "ACTIVE");
            cape.put("alias", "Cape");
        }

        return cape;
    }
}
