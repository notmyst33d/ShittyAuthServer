package me.mrletsplay.shittyauth.page.api.yggdrasil;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import me.mrletsplay.mrcore.json.JSONObject;
import me.mrletsplay.shittyauth.ShittyAuth;
import me.mrletsplay.shittyauth.UUIDHelper;
import me.mrletsplay.shittyauth.auth.AccessToken;
import me.mrletsplay.shittyauth.auth.StoredAccessToken;
import me.mrletsplay.webinterfaceapi.http.HttpStatusCodes;
import me.mrletsplay.webinterfaceapi.http.document.HttpDocument;
import me.mrletsplay.webinterfaceapi.http.header.HttpClientContentTypes;
import me.mrletsplay.webinterfaceapi.http.request.HttpRequestContext;
import me.mrletsplay.webinterfaceapi.webinterface.Webinterface;
import me.mrletsplay.webinterfaceapi.webinterface.auth.WebinterfaceAccount;

public class RefreshPage implements HttpDocument {
    // https://wiki.vg/Authentication#Refresh

    @Override
    public void createContent() {
        HttpRequestContext ctx = HttpRequestContext.getCurrentContext();
        JSONObject obj = (JSONObject) ctx.getClientHeader().getPostData().getParsedAs(HttpClientContentTypes.JSON);
        String accessToken = obj.getString("accessToken"),
                clientToken = obj.optString("clientToken").orElse(null);

        StoredAccessToken sTok = ShittyAuth.tokenStorage.getStoredToken(accessToken);
        if (sTok == null) {
            ctx.getServerHeader().setStatusCode(HttpStatusCodes.ACCESS_DENIED_403);
            return;
        }

        WebinterfaceAccount account = Webinterface.getAccountStorage().getAccountByID(sTok.getAccountID());
        if (account == null) { // I dont know how would that even happen, but lets check it anyway
            ctx.getServerHeader().setStatusCode(HttpStatusCodes.ACCESS_DENIED_403);
            return;
        }

        JSONObject response = new JSONObject();
        JSONObject profile = new JSONObject();
        profile.put("name", account.getName());
        profile.put("id", UUIDHelper.toShortUUID(UUID.fromString(account.getID())));

        response.put("selectedProfile", profile);

        AccessToken tok = ShittyAuth.tokenStorage.generateToken(sTok.getAccountID(), clientToken);
        response.put("accessToken", tok.getAccessToken());
        response.put("clientToken", tok.getClientToken());

        ctx.getServerHeader().setContent("application/json", response.toString().getBytes(StandardCharsets.UTF_8));
    }
}
