package com.github.peterbecker.configuration.storage;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class JsonStore implements Store {
    private final JsonObject data;

    public JsonStore(Path resource) throws IOException {
        JsonReader reader = Json.createReader(Files.newBufferedReader(resource));
        data = reader.readObject();
    }

    @Override
    public Optional<String> getValue(Key key) {
        Optional<JsonObject> node = getNode(data, key);
        if(!node.isPresent()) {
            return Optional.empty();
        }
        JsonValue jsonValue = node.get().get(key.getOptionName());
        if (jsonValue == null) {
            return Optional.empty();
        }
        switch (jsonValue.getValueType()) {
            case STRING:
                return Optional.of(node.get().getString(key.getOptionName()));
            case NUMBER:
            case TRUE:
            case FALSE:
                return Optional.of(jsonValue.toString());
            case NULL:
            case ARRAY:
            case OBJECT:
            default:
                return Optional.empty();
        }
    }

    private Optional<JsonObject> getNode(JsonObject context, Key key) {
        if(key == Key.ROOT) {
            return Optional.of(context);
        } else {
            if (!context.containsKey(key.getOptionName())) {
                return Optional.empty();
            }
            return Optional.of(context.getJsonObject(key.getOptionName()));
        }
    }
}
