package me.mrletsplay.shittyauth.textures;

import me.mrletsplay.mrcore.json.JSONObject;
import me.mrletsplay.shittyauth.ShittyAuth;
import me.mrletsplay.shittyauth.config.ShittyAuthSettings;
import me.mrletsplay.shittyauth.page.api.UserCapeDocument;
import me.mrletsplay.shittyauth.page.api.UserSkinDocument;
import me.mrletsplay.shittyauth.user.UserData;
import me.mrletsplay.webinterfaceapi.webinterface.Webinterface;
import me.mrletsplay.webinterfaceapi.webinterface.config.DefaultSettings;

public class TexturesHelper {
    public static final String FALLBACK_HOST = "http://"
            + Webinterface.getConfig().getSetting(DefaultSettings.HTTP_HOST) + ":"
            + Webinterface.getConfig().getSetting(DefaultSettings.HTTP_PORT),
            SKIN_PATH = UserSkinDocument.PATH_PREFIX + "%s",
            CAPE_PATH = UserCapeDocument.PATH_PREFIX + "%s";

    private static String getHost() {
        String configHost = ShittyAuth.config.getSetting(ShittyAuthSettings.SKIN_BASE_URL);
        return configHost != null ? configHost : FALLBACK_HOST;
    }

    public static JSONObject getTexturesObject(UserData user) {
        JSONObject textures2 = new JSONObject();
        JSONObject skin = new JSONObject();
        JSONObject cape = new JSONObject();

        skin.put("url", String.format(getHost() + SKIN_PATH, user.getSkinId()));
        if (user.getSkinType() == SkinType.ALEX) {
            JSONObject meta = new JSONObject();
            meta.put("model", "slim");
            skin.put("metadata", meta);
        }
        textures2.put("SKIN", skin);

        if (user.hasCape()) {
            cape.put("url", String.format(getHost() + CAPE_PATH, user.getCapeId()));
            textures2.put("CAPE", cape);
        }

        return textures2;
    }
}
