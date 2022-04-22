package me.mrletsplay.shittyauth.page.api;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import me.mrletsplay.webinterfaceapi.http.HttpStatusCodes;
import me.mrletsplay.webinterfaceapi.http.document.HttpDocument;
import me.mrletsplay.webinterfaceapi.http.request.HttpRequestContext;

public class UserCapeDocument implements HttpDocument {
    public static final UserCapeDocument INSTANCE = new UserCapeDocument();

    public static final String PATH_PREFIX = "/cape/c";

    @Override
    public void createContent() {
        HttpRequestContext ctx = HttpRequestContext.getCurrentContext();
        String capeId = ctx.getClientHeader().getPath().getDocumentPath().substring(PATH_PREFIX.length());

        File cape = new File("shittyauth/capes/", capeId + ".png");
        if (!cape.exists())
            cape = new File("shittyauth/default_cape.png");

        try {
            byte[] bytes = Files.readAllBytes(cape.toPath());
            ctx.getServerHeader().setContent("image/png", bytes);
        } catch (IOException e) {
            e.printStackTrace();
            ctx.getServerHeader().setStatusCode(HttpStatusCodes.INTERNAL_SERVER_ERROR_500);
            ctx.getServerHeader().setContent("text/plain",
                    "500 Internal Server error".getBytes(StandardCharsets.UTF_8));
            return;
        }
    }
}
