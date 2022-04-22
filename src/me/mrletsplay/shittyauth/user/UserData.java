package me.mrletsplay.shittyauth.user;

import java.util.UUID;

import me.mrletsplay.mrcore.json.converter.JSONConstructor;
import me.mrletsplay.mrcore.json.converter.JSONConvertible;
import me.mrletsplay.mrcore.json.converter.JSONValue;
import me.mrletsplay.shittyauth.textures.SkinType;

public class UserData implements JSONConvertible {
    @JSONValue
    private boolean hasCape;

    @JSONValue
    private long skinLastChanged; // To update skin cache in client after change

    @JSONValue
    private String skinId;

    @JSONValue
    private long capeLastChanged; // To update cape cache in client after change

    @JSONValue
    private String capeId;

    @JSONValue
    private SkinType skinType;

    @JSONConstructor
    public UserData() {
        this.skinId = "00000000-0000-0000-0000-000000000000";
        this.capeId = "00000000-0000-0000-0000-000000000000";

        this.skinType = SkinType.STEVE;
        this.hasCape = false;
    }

    public boolean hasCape() {
        return hasCape;
    }

    public void setHasCape(boolean hasCape) {
        this.hasCape = hasCape;
    }

    public void setSkinType(SkinType skinType) {
        this.skinType = skinType;
    }

    public SkinType getSkinType() {
        return skinType;
    }

    public void setSkin(UUID skinId) {
        this.skinLastChanged = System.currentTimeMillis();
        this.skinId = skinId.toString();
    }

    public long getSkinLastChanged() {
        return skinLastChanged;
    }

    public String getSkinId() {
        return skinId;
    }

    public void setCape(UUID capeId) {
        this.capeLastChanged = System.currentTimeMillis();
        this.capeId = capeId.toString();
    }

    public long getCapeLastChanged() {
        return capeLastChanged;
    }

    public String getCapeId() {
        return capeId;
    }
}
