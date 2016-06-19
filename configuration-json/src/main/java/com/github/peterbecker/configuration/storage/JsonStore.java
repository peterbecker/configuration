package com.github.peterbecker.configuration.storage;

import com.github.peterbecker.configuration.ConfigurationException;

import javax.json.*;
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
        JsonObject node = data;
        for (String nodeName : key.getContext()) {
            if (!node.containsKey(nodeName)) {
                return Optional.empty();
            }
            node = node.getJsonObject(nodeName);
        }
        JsonValue jsonValue = node.get(key.getOptionName());
        if (jsonValue == null) {
            return Optional.empty();
        }
        switch (jsonValue.getValueType()) {
            case STRING:
                return Optional.of(node.getString(key.getOptionName()));
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
}
