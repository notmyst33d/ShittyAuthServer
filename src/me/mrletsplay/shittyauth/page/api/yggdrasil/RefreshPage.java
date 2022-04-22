package me.mrletsplay.shittyauth.page.api.yggdrasil;

import java.nio.charset.StandardCharsets;

import me.mrletsplay.mrcore.json.JSONObject;
import me.mrletsplay.shittyauth.ShittyAuth;
import me.mrletsplay.shittyauth.auth.AccessToken;
import me.mrletsplay.shittyauth.auth.StoredAccessToken;
import me.mrletsplay.webinterfaceapi.http.HttpStatusCodes;
import me.mrletsplay.webinterfaceapi.http.document.HttpDocument;
import me.mrletsplay.webinterfaceapi.http.header.HttpClientContentTypes;
import me.mrletsplay.webinterfaceapi.http.request.HttpRequestContext;
import me.mrletsplay.webinterfaceapi.webinterface.Webinterface;

public class RefreshPage implements HttpDocument {
    // https://wiki.vg/Authentication#Refresh

    @Override
    public void createContent() {
        HttpRequestContext ctx = HttpRequestContext.getCurrentContext();
        JSONObject obj = (JSONObject) ctx.getClientHeader().getPostData().getParsedAs(HttpClientContentTypes.JSON);
        String accessToken = obj.getString("accessToken"),
                clientToken = obj.optString("clientToken").orElse(null);

        Webinterface.getLogger().info("Somebody requested /refresh, please note that this is an experimental feature.");

        StoredAccessToken sTok = ShittyAuth.tokenStorage.getStoredToken(accessToken);
        if (sTok == null || (clientToken != null && clientToken.equals(sTok.getClientToken()))) {
            ctx.getServerHeader().setStatusCode(HttpStatusCodes.ACCESS_DENIED_403);
            return;
        }

        JSONObject response = new JSONObject();

        AccessToken tok = ShittyAuth.tokenStorage.generateToken(sTok.getAccountID(), clientToken);
        response.put("accessToken", tok.getAccessToken());
        response.put("clientToken", tok.getClientToken());

        ctx.getServerHeader().setContent("application/json", response.toString().getBytes(StandardCharsets.UTF_8));
    }
}
