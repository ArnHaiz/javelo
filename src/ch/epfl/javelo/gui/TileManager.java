package ch.epfl.javelo.gui;

import javafx.scene.image.Image;;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;

public final class TileManager {
    private final Path path;
    private final URL url;
    public TileManager(Path path, URL url) {
        this.path = path;
        this.url = url;
    }
    private LinkedHashMap<TileId, Image> cacheMemory = new LinkedHashMap<>(100);
    public Image imageForTileAt(TileId tileId) throws IOException {
        return getCache(tileId);
    }
    private Image getCache(TileId tileId) throws IOException {
        Image image = cacheMemory.get(tileId);
        if(image==null) {
            setCache(getFromDisk(tileId), tileId);
            return getCache(tileId);
        }else {
            return image;
        }
    }
    private void setCache(Image tileImage, TileId tileId) {
        cacheMemory.put(tileId, tileImage);
    }
    private Image getFromDisk(TileId tileId) throws IOException {
        Path finalPath = path;
        finalPath.resolve(String.valueOf(tileId.tileZoomLevel()));
        finalPath.resolve(String.valueOf(tileId.tileXIndex()));
        finalPath.resolve(tileId.tileYIndex+".png");
        String newFile = finalPath.toString();
        if(!Files.exists(finalPath)){
            setToDisk(getFromServer(tileId), tileId);
            return getFromDisk(tileId);
        }else {
            FileInputStream stream;
            Image image = null;
            try {
                stream = new FileInputStream(newFile);
                image = new Image(stream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return image;
        }
    }
    private void setToDisk(InputStream stream, TileId tileId) throws IOException {
        Path finalPath = path;
        finalPath.resolve(String.valueOf(tileId.tileZoomLevel()));
        finalPath.resolve(String.valueOf(tileId.tileXIndex()));
        finalPath.resolve(tileId.tileYIndex+".png");
        String newFile = finalPath.toString();
        Files.createDirectories(finalPath.getParent());
        try(FileOutputStream output = new FileOutputStream(newFile)) {
            stream.transferTo(output);
            stream.close();
        }
    }
    private InputStream getFromServer(TileId tileId) throws IOException {
        URL u = new URL(
                "https://"+url+"/"+tileId.tileZoomLevel()+"/"+tileId.tileXIndex()+"/"+tileId.tileYIndex()+".png");
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "JaVelo");
        return c.getInputStream();
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
