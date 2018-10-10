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
    public Optional<String> getValue(Key key) throws ConfigurationException {
        return getNode(data, key).map(JsonStore::getString);
    }

    public Optional<JsonValue> getNode(JsonObject context, Key key) throws ConfigurationException {
        Optional<JsonValue> node = getContextObject(context, key).map(jo -> jo.get(key.getOptionName()));
        if (key.isIndexed()) {
            node = node.map(jv-> {
                if (jv.getValueType() != JsonValue.ValueType.ARRAY) {
                    return null;
                }
                JsonArray array = jv.asJsonArray();
                if (array.size() <= key.getIndex()) {
                    return null;
                }
                return array.get(key.getIndex());
            });
        }
        return node;
    }

    private Optional<JsonObject> getContextObject(JsonObject context, Key key) throws ConfigurationException {
        if (key.isTopLevel()) {
            return Optional.of(context);
        } else {
            Optional<JsonValue> parent = getNode(context, key.getContext());
            if (!parent.isPresent()) {
                return Optional.empty();
            }
            if (parent.get().getValueType() != JsonValue.ValueType.OBJECT) {
                throw new ConfigurationException(key.getOptionName() + " is not an object", key);
            }
            return Optional.of(parent.get().asJsonObject());
        }
    }

    private static String getString(JsonValue node) {
        switch (node.getValueType()) {
            case STRING:
                return ((JsonString) node).getString();
            case NUMBER:
            case TRUE:
            case FALSE:
                return node.toString();
            case NULL:
            case ARRAY:
            case OBJECT:
            default:
                return null;
        }
    }
}
