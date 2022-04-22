package me.mrletsplay.shittyauth.webinterface;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;

import me.mrletsplay.mrcore.io.IOUtils;
import me.mrletsplay.shittyauth.ShittyAuth;
import me.mrletsplay.shittyauth.textures.SkinType;
import me.mrletsplay.shittyauth.user.UserData;
import me.mrletsplay.webinterfaceapi.webinterface.DefaultPermissions;
import me.mrletsplay.webinterfaceapi.webinterface.auth.WebinterfaceAccount;
import me.mrletsplay.webinterfaceapi.webinterface.auth.WebinterfaceAccountConnection;
import me.mrletsplay.webinterfaceapi.webinterface.auth.impl.PasswordAuth;
import me.mrletsplay.webinterfaceapi.webinterface.page.WebinterfaceSettingsPage;
import me.mrletsplay.webinterfaceapi.webinterface.page.action.WebinterfaceActionHandler;
import me.mrletsplay.webinterfaceapi.webinterface.page.action.WebinterfaceHandler;
import me.mrletsplay.webinterfaceapi.webinterface.page.action.WebinterfaceRequestEvent;
import me.mrletsplay.webinterfaceapi.webinterface.page.action.WebinterfaceResponse;
import me.mrletsplay.webinterfaceapi.webinterface.page.element.WebinterfaceFileUpload;

public class ShittyAuthWIHandler implements WebinterfaceActionHandler {
    @WebinterfaceHandler(requestTarget = "shittyauth", requestTypes = "uploadSkin")
    public WebinterfaceResponse uploadSkin(WebinterfaceRequestEvent event) {
        WebinterfaceAccount account = event.getAccount();
        WebinterfaceAccountConnection connection = account.getConnection(PasswordAuth.ID);

        if (connection == null)
            return WebinterfaceResponse.error("No MC account");

        byte[] skinBytes = WebinterfaceFileUpload.getUploadedFileBytes(event);

        try {
            BufferedImage skin = ImageIO.read(new ByteArrayInputStream(skinBytes));
            UUID skinId = UUID.randomUUID();

            // Check skin dimensions
            if (skin.getWidth() != 64 || (skin.getHeight() != 64 && skin.getHeight() != 32))
                return WebinterfaceResponse.error("Skin must be 64x64 64x32 pixels");

            // Write skin file
            BufferedImage copy = new BufferedImage(64, skin.getHeight(), BufferedImage.TYPE_INT_ARGB);
            copy.createGraphics().drawImage(skin, 0, 0, null);
            File outFile = new File("shittyauth/skins/" + skinId.toString() + ".png");
            IOUtils.createFile(outFile);
            ImageIO.write(copy, "PNG", outFile);

            // Update user skin
            UserData user = ShittyAuth.dataStorage.getUserData(account.getID());
            user.setSkin(skinId);

            // Update user data
            ShittyAuth.dataStorage.updateUserData(account.getID(), user);

            return WebinterfaceResponse.success();
        } catch (IOException e) {
            return WebinterfaceResponse.error("Invalid skin file");
        }
    }

    @WebinterfaceHandler(requestTarget = "shittyauth", requestTypes = "uploadCape")
    public WebinterfaceResponse uploadCape(WebinterfaceRequestEvent event) {
        WebinterfaceAccount account = event.getAccount();
        WebinterfaceAccountConnection connection = account.getConnection(PasswordAuth.ID);

        if (connection == null)
            return WebinterfaceResponse.error("No MC account");

        byte[] capeBytes = WebinterfaceFileUpload.getUploadedFileBytes(event);

        try {
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(capeBytes));
            UUID capeId = UUID.randomUUID();

            // Check cape dimensions
            if (img.getWidth() != 64 || img.getHeight() != 32)
                return WebinterfaceResponse.error("Cape must be 64x32");

            // Write cape file
            BufferedImage copy = new BufferedImage(64, 32, BufferedImage.TYPE_INT_ARGB);
            copy.createGraphics().drawImage(img, 0, 0, null);
            File outFile = new File("shittyauth/capes/" + capeId.toString() + ".png");
            IOUtils.createFile(outFile);
            ImageIO.write(copy, "PNG", outFile);

            // Update user cape
            UserData user = ShittyAuth.dataStorage.getUserData(account.getID());
            user.setCape(capeId);

            // Update user data
            ShittyAuth.dataStorage.updateUserData(account.getID(), user);

            return WebinterfaceResponse.success();
        } catch (IOException e) {
            return WebinterfaceResponse.error("Invalid cape file");
        }
    }

    @WebinterfaceHandler(requestTarget = "shittyauth", requestTypes = "setSkinType")
    public WebinterfaceResponse setSkinType(WebinterfaceRequestEvent event) {
        WebinterfaceAccount account = event.getAccount();
        WebinterfaceAccountConnection connection = account.getConnection(PasswordAuth.ID);

        if (connection == null)
            return WebinterfaceResponse.error("No MC account");

        SkinType type = SkinType.valueOf(event.getRequestData().getString("value"));

        UserData d = ShittyAuth.dataStorage.getUserData(account.getID());
        d.setSkinType(type);

        ShittyAuth.dataStorage.updateUserData(account.getID(), d);

        return WebinterfaceResponse.success();
    }

    @WebinterfaceHandler(requestTarget = "shittyauth", requestTypes = "setEnableCape")
    public WebinterfaceResponse setEnableCape(WebinterfaceRequestEvent event) {
        WebinterfaceAccount account = event.getAccount();
        WebinterfaceAccountConnection connection = account.getConnection(PasswordAuth.ID);

        if (connection == null)
            return WebinterfaceResponse.error("No MC account");

        boolean enable = event.getRequestData().getBoolean("value");

        UserData d = ShittyAuth.dataStorage.getUserData(account.getID());
        d.setHasCape(enable);

        ShittyAuth.dataStorage.updateUserData(account.getID(), d);

        return WebinterfaceResponse.success();
    }

    @WebinterfaceHandler(requestTarget = "shittyauth", requestTypes = "setSetting")
    public WebinterfaceResponse setSetting(WebinterfaceRequestEvent event) {
        if (!event.getAccount().hasPermission(DefaultPermissions.SETTINGS))
            return WebinterfaceResponse.error("No permission");

        return WebinterfaceSettingsPage.handleSetSettingRequest(ShittyAuth.config, event);
    }
}
