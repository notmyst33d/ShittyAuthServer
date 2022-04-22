package me.mrletsplay.shittyauth.page.api.minecraftservices;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import me.mrletsplay.mrcore.json.JSONObject;
import me.mrletsplay.shittyauth.ShittyAuth;
import me.mrletsplay.shittyauth.UUIDHelper;
import me.mrletsplay.shittyauth.auth.StoredAccessToken;
import me.mrletsplay.shittyauth.textures.MinecraftServicesTexturesHelper;
import me.mrletsplay.shittyauth.user.UserData;
import me.mrletsplay.webinterfaceapi.http.HttpStatusCodes;
import me.mrletsplay.webinterfaceapi.http.document.HttpDocument;
import me.mrletsplay.webinterfaceapi.http.request.HttpRequestContext;
import me.mrletsplay.webinterfaceapi.webinterface.Webinterface;
import me.mrletsplay.webinterfaceapi.webinterface.auth.WebinterfaceAccount;
import me.mrletsplay.webinterfaceapi.webinterface.auth.impl.PasswordAuth;

public class MinecraftServicesProfilePage implements HttpDocument {
    @Override
    public void createContent() {
        HttpRequestContext ctx = HttpRequestContext.getCurrentContext();
        String accessToken = ctx.getClientHeader().getFields().getFieldValue("Authorization").replace("Bearer ", "");

        StoredAccessToken storedToken = ShittyAuth.tokenStorage.getStoredToken(accessToken);
        if (storedToken == null) {
            ctx.getServerHeader().setStatusCode(HttpStatusCodes.UNAUTHORIZED_401);
            return;
        }

        WebinterfaceAccount account = Webinterface.getAccountStorage().getAccountByID(storedToken.getAccountID());
        if (account == null || account.getConnection(PasswordAuth.ID) == null) {
            ctx.getServerHeader().setStatusCode(HttpStatusCodes.NOT_FOUND_404);
            return;
        }

        UserData user = ShittyAuth.dataStorage.getUserData(account.getID());

        JSONObject response = new JSONObject();

        response.put("id", UUIDHelper.toShortUUID(UUID.fromString(account.getID())));
        response.put("name", account.getName());
        response.put("skins", MinecraftServicesTexturesHelper.getSkinsObject(user));
        response.put("capes", MinecraftServicesTexturesHelper.getCapesObject(user));

        ctx.getServerHeader().setContent("application/json", response.toString().getBytes(StandardCharsets.UTF_8));
    }
}
