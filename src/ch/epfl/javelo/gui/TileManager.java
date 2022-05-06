package ch.epfl.javelo.gui;

import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;

public final class TileManager {
    private final Path path;
    private final URL url;
    private static final String MAP_URL = "https://tile.openstreetmap.org";

    public TileManager(Path path, URL url) {
        this.path = path;
        this.url = url;
    }

    private LinkedHashMap<TileId, Image> cacheMemory = new LinkedHashMap<>(100);

    public Image imageForTileAt(TileId tileId) throws IOException {
        return getCache(tileId);
    }

    private Image getCache(TileId tileId) throws IOException {
        if (!cacheMemory.containsKey(tileId)) {
            setCache(tileId, getFromDisk(tileId));
        }

        return cacheMemory.get(tileId);
    }

    private void setCache(TileId tileId, Image tileImage) {
        cacheMemory.put(tileId, tileImage);
    }

    private Image getFromDisk(TileId tileId) throws IOException {
        File file = new File(computeTilePath(path.toString(), tileId));

        if (!file.exists()) {
            setToDisk(tileId);
        }

        FileInputStream stream;
        Image image = null;

        try {
            stream = new FileInputStream(file);
            image = new Image(stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return image;
    }

    private void setToDisk(TileId tileId) throws IOException {
        URL u = new URL(computeTilePath(MAP_URL, tileId));
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "JaVelo");

        File file = new File(computeTilePath(path.toString(), tileId));
        Files.createDirectories(Path.of(file.getParent()));

        try (FileOutputStream output = new FileOutputStream(file); InputStream input = c.getInputStream()) {
            input.transferTo(output);
        }
    }

    private String computeTilePath(String basePath, TileId tileId) {
        return String.format("%s/%d/%d/%d.png", basePath, tileId.tileZoomLevel, tileId.tileXIndex, tileId.tileYIndex);
    }

    record TileId(int tileZoomLevel, int tileXIndex, int tileYIndex) {
        public static boolean isValid(int tileZoomLevel, int tileXIndex, int tileYIndex) {
            return (Math.pow(2, tileZoomLevel) > tileXIndex)
                    && (Math.pow(2, tileZoomLevel) > tileYIndex)
                    && (tileXIndex >= 0)
                    && (tileYIndex >= 0)
                    && (tileZoomLevel >= 0)
                    && (tileZoomLevel <= 19);
        }
    }
}
