package com.pedestriamc.strings.external;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.resources.ResourcePack;
import com.pedestriamc.strings.api.settings.Option;
import org.apache.commons.codec.binary.Hex;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class ModrinthService {

    private static final String HEADER_VALUE = "wiicart/strings/" + Strings.VERSION + " (wiicart.net)";
    private static final String MODRINTH_URL = "https://api.modrinth.com/v2/";

    private final Strings strings;

    private final HttpClient client = HttpClient
            .newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final Gson gson = new Gson();

    private final String textureId;
    private ResourcePack cache;

    public ModrinthService(@NotNull Strings strings) {
        this.strings = strings;
        textureId = strings.getConfiguration().get(Option.Text.TEXTURES_MODRINTH_ID);
        try {
            cache = getLatestPack();
        } catch(Exception e) {
            strings.getLogger().info(e.getMessage());
        }
    }

    @NotNull
    public CompletableFuture<ResourcePack> getPack() {
        CompletableFuture<ResourcePack> future = new CompletableFuture<>();
        strings.async(() -> {
            if (isValid(cache)) {
                future.complete(cache);
                return;
            }

            try {
                ResourcePack pack = getLatestPack();
                cache = pack;
                future.complete(pack);
            } catch(Exception e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    @Nullable
    private ResourcePack getLatestPack() {
        HttpRequest request = HttpRequest.newBuilder()
                .header("User-Agent", HEADER_VALUE)
                .uri(URI.create(MODRINTH_URL + "project/" + textureId + "/version"))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonArray array = gson.fromJson(response.body(), JsonArray.class);
            JsonObject version = findLatest(array);
            if (version != null) {
                return packFromJsonObject(version);
            }

        } catch(IOException | InterruptedException | UnsupportedOperationException e) {
            strings.getLogger().warning("Failed to fetch latest texture version");
            strings.getLogger().warning(e.getMessage());
            throw new RuntimeException(e);
        }

        return null;
    }

    @NotNull
    private ResourcePack packFromJsonObject(@NotNull JsonObject object) {
        JsonArray files = object.getAsJsonArray("files");
        if (files.isEmpty()) {
            throw new IllegalStateException("Files array is empty");
        }

        JsonObject file = files.get(0).getAsJsonObject();

        String url = file.get("url").getAsString();
        byte[] hash = readHash(file);
        return ResourcePack.of(url, hash);
    }

    // Expects the "File" JsonObject
    // https://stackoverflow.com/questions/4895523/java-string-to-sha1
    private byte[] readHash(@NotNull JsonObject object) {
        try {
            JsonObject hashes = object.getAsJsonObject("hashes");
            String hash = hashes.get("sha1").getAsString();

            return Hex.decodeHex(hash.toCharArray());
        } catch(Exception e) {
            strings.getLogger().warning("Failed to find SHA-1 hash");
            strings.getLogger().warning(e.getMessage());
            return null;
        }
    }

    @Nullable
    private JsonObject findLatest(@NotNull JsonArray array) {
        JsonObject latest = null;
        for (JsonElement jsonElement : array) {
            JsonObject current = jsonElement.getAsJsonObject();
            if(latest == null) {
                latest = current;
                continue;
            }

            if(current.get("featured").getAsBoolean()) {
                latest = current;
                break;
            }

            String currentVersion = current.get("version_number").getAsString();
            String latestVersion = latest.get("version_number").getAsString();
            if(isNewer(currentVersion, latestVersion)) {
                latest = current;
            }
        }

        return latest;
    }

    // Checks if version2 is newer than version1
    // https://stackoverflow.com/questions/198431/how-do-you-compare-two-version-strings-in-java
    private boolean isNewer(@NotNull String version1, @NotNull String version2) {
        try {
            DefaultArtifactVersion one = new DefaultArtifactVersion(version1);
            DefaultArtifactVersion two = new DefaultArtifactVersion(version2);
            return one.compareTo(two) < 0;
        } catch(Exception e) {
            return false;
        }
    }

    private boolean isValid(@Nullable ResourcePack pack) {
        if (pack == null) {
            return false;
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .header("User-Agent", HEADER_VALUE)
                    .uri(URI.create(pack.url()))
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            int status = response.statusCode();
            return status >= 200 && status < 300;
        } catch(Exception e) {
            return false;
        }
    }

}
