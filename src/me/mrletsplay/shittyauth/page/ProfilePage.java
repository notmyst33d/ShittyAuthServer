package me.mrletsplay.shittyauth.page;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import me.mrletsplay.mrcore.json.JSONArray;
import me.mrletsplay.mrcore.json.JSONObject;
import me.mrletsplay.shittyauth.ShittyAuth;
import me.mrletsplay.shittyauth.UUIDHelper;
import me.mrletsplay.shittyauth.textures.TexturesHelper;
import me.mrletsplay.shittyauth.user.UserData;
import me.mrletsplay.webinterfaceapi.http.HttpStatusCodes;
import me.mrletsplay.webinterfaceapi.http.document.HttpDocument;
import me.mrletsplay.webinterfaceapi.http.request.HttpRequestContext;
import me.mrletsplay.webinterfaceapi.webinterface.Webinterface;
import me.mrletsplay.webinterfaceapi.webinterface.auth.WebinterfaceAccount;
import me.mrletsplay.webinterfaceapi.webinterface.auth.impl.PasswordAuth;

public class ProfilePage implements HttpDocument {
    // Backwards compatibility with authlib-injector
    public static final String PATH_PREFIX = "/session/minecraft/profile/";
    public static final ProfilePage INSTANCE = new ProfilePage();

    @Override
    public void createContent() {
        // TODO: ?unsigned=(1|0|true|false)
        HttpRequestContext ctx = HttpRequestContext.getCurrentContext();

        // Backwards compatibility with authlib-injector
        boolean isAuthlibInjector = ctx.getClientHeader().getPath().toString().startsWith("/sessionserver");
        String uuid = ctx.getClientHeader().getPath().getDocumentPath().substring(isAuthlibInjector ? ("/sessionserver" + PATH_PREFIX).length() : PATH_PREFIX.length());
        if (!uuid.contains("-")) {
            uuid = UUIDHelper.parseShortUUID(uuid).toString();
        }

        WebinterfaceAccount acc = Webinterface.getAccountStorage().getAccountByID(uuid);
        if (acc == null || acc.getConnection(PasswordAuth.ID) == null) {
            ctx.getServerHeader().setStatusCode(HttpStatusCodes.NOT_FOUND_404);
            return;
        }

        JSONObject obj = new JSONObject();
        obj.put("id", UUIDHelper.toShortUUID(UUID.fromString(acc.getID())));
        obj.put("name", acc.getConnection(PasswordAuth.ID).getUserName());

        JSONArray a = new JSONArray();
        JSONObject b = new JSONObject();
        b.put("name", "textures");

        JSONObject textures = new JSONObject();
        textures.put("timestamp", System.currentTimeMillis());
        textures.put("profileId", UUIDHelper.toShortUUID(UUID.fromString(acc.getID())));
        textures.put("profileName", acc.getConnection(PasswordAuth.ID).getUserName());
        // TODO: signatureRequired (present with true if ?unsigned=false)

        UserData d = ShittyAuth.dataStorage.getUserData(acc.getID());
        textures.put("textures", TexturesHelper.getTexturesObject(d));
        b.put("value", Base64.getEncoder().encodeToString(textures.toString().getBytes(StandardCharsets.UTF_8)));
        a.add(b);
        obj.put("properties", a);

        ctx.getServerHeader().setContent("application/json", obj.toString().getBytes(StandardCharsets.UTF_8));
    }
}
