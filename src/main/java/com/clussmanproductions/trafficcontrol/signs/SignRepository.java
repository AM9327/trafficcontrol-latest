package com.clussmanproductions.trafficcontrol.signs;

import com.clussmanproductions.trafficcontrol.ModTrafficControl;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class SignRepository {

    private final HashMap<UUID, Sign> signsByID = new HashMap<>();
    private final ArrayList<Sign> allSigns = new ArrayList<>();
    private final HashMap<String, String> friendlyTypesByName = new HashMap<>();
    private boolean initialized = false;

    public void init(ResourceManager resourceManager) {
        if (initialized) return;

        try {
            Identifier signsJsonRL = Identifier.fromNamespaceAndPath("trafficcontrol", "misc/signs.json");
            Optional<Resource> resource = resourceManager.getResource(signsJsonRL);
            if (resource.isEmpty()) {
                ModTrafficControl.LOGGER.error("Could not find signs.json");
                initialized = true;
                return;
            }

            InputStream jsonStream = resource.get().open();
            InputStreamReader reader = new InputStreamReader(jsonStream);
            JsonObject signsFile = JsonParser.parseReader(reader).getAsJsonObject();
            jsonStream.close();

            processSignFile(signsFile);
        } catch (Exception ex) {
            ModTrafficControl.LOGGER.error("Could not process signs.json", ex);
        }

        initialized = true;
    }

    private void processSignFile(JsonObject signsFile) {
        UUID packID;
        try {
            packID = UUID.fromString(signsFile.get("pack_id").getAsString());
        } catch (Exception ex) {
            ModTrafficControl.LOGGER.error("Could not parse pack_id", ex);
            return;
        }

        if (signsFile.has("types") && signsFile.get("types").isJsonObject()) {
            for (var entry : signsFile.get("types").getAsJsonObject().entrySet()) {
                friendlyTypesByName.put(entry.getKey(), entry.getValue().getAsString());
            }
        }

        JsonElement signsArrayElement = signsFile.get("signs");
        if (signsArrayElement == null || !signsArrayElement.isJsonArray()) return;

        for (JsonElement signElement : signsArrayElement.getAsJsonArray()) {
            if (!signElement.isJsonObject()) continue;
            try {
                Sign sign = parseSign(signElement.getAsJsonObject(), packID);
                signsByID.put(sign.getID(), sign);
                allSigns.add(sign);
            } catch (Exception ex) {
                ModTrafficControl.LOGGER.error("A sign failed to load", ex);
            }
        }

        ModTrafficControl.LOGGER.info("Loaded {} signs from pack {}", allSigns.size(), packID);
    }

    private Sign parseSign(JsonObject obj, UUID packID) {
        UUID id = UUID.fromString(obj.get("id").getAsString());
        String name = obj.get("name").getAsString();
        String type = obj.get("type").getAsString();
        String frontFile = obj.get("front").getAsString();

        // Textures use blocks/ path (1.12.2 style) — the actual files are at textures/blocks/signs/
        Identifier frontRL = Identifier.fromNamespaceAndPath("trafficcontrol",
                "textures/blocks/signs/" + packID + "/" + type + "/" + frontFile);

        String backFile = obj.has("back") ? obj.get("back").getAsString() : "back.png";
        Identifier backRL = Identifier.fromNamespaceAndPath("trafficcontrol",
                "textures/blocks/signs/" + packID + "/" + type + "/" + backFile);

        int variant = obj.has("variant") ? Integer.parseInt(obj.get("variant").getAsString()) : -1;
        String tooltip = obj.has("tooltip") ? obj.get("tooltip").getAsString() : null;
        String note = obj.has("note") ? obj.get("note").getAsString() : null;
        boolean halfHeight = obj.has("halfheight") && obj.get("halfheight").getAsBoolean();

        ArrayList<Sign.TextLine> textLines = new ArrayList<>();
        if (obj.has("textlines")) {
            for (JsonElement el : obj.get("textlines").getAsJsonArray()) {
                textLines.add(parseTextLine(el.getAsJsonObject()));
            }
        }

        return new Sign(id, frontRL, backRL, name, variant, type, tooltip, note, halfHeight, textLines);
    }

    private Sign.TextLine parseTextLine(JsonObject obj) {
        String label = obj.get("label").getAsString();
        double x = obj.get("x").getAsDouble();
        double y = obj.get("y").getAsDouble();
        double width = obj.get("width").getAsDouble();
        int color = obj.get("color").getAsInt();
        int maxLength = obj.has("maxlength") ? obj.get("maxlength").getAsInt() : -1;
        double xScale = obj.has("xscale") ? obj.get("xscale").getAsDouble() : 1;
        double yScale = obj.has("yscale") ? obj.get("yscale").getAsDouble() : 1;

        SignHorizontalAlignment hAlign = SignHorizontalAlignment.Left;
        if (obj.has("halign")) {
            String h = obj.get("halign").getAsString().toLowerCase();
            for (SignHorizontalAlignment a : SignHorizontalAlignment.values()) {
                if (a.toString().toLowerCase().equals(h)) { hAlign = a; break; }
            }
        }

        SignVerticalAlignment vAlign = SignVerticalAlignment.Top;
        if (obj.has("valign")) {
            String v = obj.get("valign").getAsString().toLowerCase();
            for (SignVerticalAlignment a : SignVerticalAlignment.values()) {
                if (a.toString().toLowerCase().equals(v)) { vAlign = a; break; }
            }
        }

        return new Sign.TextLine(label, x, y, width, xScale, yScale, maxLength, color, hAlign, vAlign);
    }

    public Sign getSignByID(UUID id) {
        return signsByID.get(id);
    }

    public String getFriendlyTypeName(String type) {
        return friendlyTypesByName.get(type);
    }

    public ImmutableList<Sign> getAllSigns() {
        return ImmutableList.copyOf(allSigns);
    }

    public List<Sign> getSignsByType(String type) {
        return allSigns.stream().filter(s -> s.getType().equals(type)).toList();
    }

    public ImmutableMap<String, String> getTypes() {
        return ImmutableMap.copyOf(friendlyTypesByName);
    }

    public boolean isInitialized() {
        return initialized;
    }
}
