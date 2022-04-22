package me.mrletsplay.shittyauth.page.api;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import me.mrletsplay.webinterfaceapi.http.HttpStatusCodes;
import me.mrletsplay.webinterfaceapi.http.document.HttpDocument;
import me.mrletsplay.webinterfaceapi.http.request.HttpRequestContext;

public class UserSkinDocument implements HttpDocument {
    public static final UserSkinDocument INSTANCE = new UserSkinDocument();

    public static final String PATH_PREFIX = "/skin/s";

    @Override
    public void createContent() {
        HttpRequestContext ctx = HttpRequestContext.getCurrentContext();
        String skinId = ctx.getClientHeader().getPath().getDocumentPath().substring(PATH_PREFIX.length());

        File skin = new File("shittyauth/skins/", skinId + ".png");
        if (!skin.exists())
            skin = new File("shittyauth/default_skin.png");

        try {
            byte[] bytes = Files.readAllBytes(skin.toPath());
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
