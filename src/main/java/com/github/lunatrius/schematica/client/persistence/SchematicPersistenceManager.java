package com.github.lunatrius.schematica.client.persistence;

import com.github.lunatrius.schematica.Schematica;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manages per-dimension last-schematic persistence for singleplayer worlds.
 */
public final class SchematicPersistenceManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static boolean attemptedAutoLoad = false;

    private SchematicPersistenceManager() {}

    public static void resetState() {
        attemptedAutoLoad = false;
    }

    public static void recordLoadedSchematic(final File file) {
        final File worldSaveDir = getCurrentWorldSaveDir();
        if (worldSaveDir == null || file == null) {
            return;
        }

        final File schematicaDir = new File(worldSaveDir, "schematica");
        if (schematicaDir.exists()) {
            if (!schematicaDir.isDirectory()) {
                return;
            }
        } else if (!schematicaDir.mkdirs()) {
            return;
        }

        final File target = new File(schematicaDir, getDimensionFileName());
        final JsonObject obj = new JsonObject();
        obj.addProperty("path", file.getAbsolutePath());

        try (FileWriter writer = new FileWriter(target)) {
            GSON.toJson(obj, writer);
        } catch (Exception ignored) {
        }
    }

    public static void maybeAutoLoad() {
        if (attemptedAutoLoad) {
            return;
        }

        attemptedAutoLoad = true;

        final File worldSaveDir = getCurrentWorldSaveDir();
        if (worldSaveDir == null) {
            return;
        }

        final File target = new File(new File(worldSaveDir, "schematica"), getDimensionFileName());
        if (!target.isFile()) {
            return;
        }

        try (FileReader reader = new FileReader(target)) {
            final JsonObject obj = GSON.fromJson(reader, JsonObject.class);
            if (obj == null || !obj.has("path")) {
                return;
            }

            final File schematicFile = Paths.get(obj.get("path").getAsString()).toFile();
            if (!schematicFile.isFile()) {
                return;
            }

            final File dir = schematicFile.getParentFile();
            final String name = schematicFile.getName();
            Schematica.proxy.loadSchematic(null, dir, name);
        } catch (Exception ignored) {
        }
    }

    private static File getCurrentWorldSaveDir() {
        final Minecraft mc = Minecraft.getMinecraft();
        if (!mc.isIntegratedServerRunning() || mc.getIntegratedServer() == null) {
            return null;
        }

        final String worldFolder = mc.getIntegratedServer().getFolderName();
        if (worldFolder == null || worldFolder.isEmpty()) {
            return null;
        }

        final File mcDir = mc.mcDataDir;
        final Path path = Paths.get(mcDir.getAbsolutePath(), "saves", worldFolder);
        final File dir = path.toFile();
        if (!dir.isDirectory()) {
            return null;
        }
        return dir;
    }

    private static String getDimensionFileName() {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.theWorld.provider == null) {
            return "overworld.json";
        }

        final int dim = mc.theWorld.provider.getDimensionId();
        switch (dim) {
            case -1:
                return "nether.json";
            case 0:
                return "overworld.json";
            case 1:
                return "end.json";
            default:
                return "dim" + dim + ".json";
        }
    }
}
