package com.github.peterbecker.configuration.storage;

import com.github.peterbecker.configuration.ConfigurationException;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class YamlStore implements Store {
    private final Map<String, Object> data;

    @SuppressWarnings("unchecked")
    public YamlStore(Path resource) throws IOException {
        Yaml yaml = new Yaml(
                new Constructor(), // default
                new Representer(), // default
                new DumperOptions(), // default
                new CustomResolver());
        data = yaml.load(Files.newBufferedReader(resource));
    }

    @Override
    public Optional<String> getValue(Key key) throws ConfigurationException {
        return getNode(data,key).map(Object::toString);
    }

    private Optional<Object> getNode(Map<String, Object> context, Key key) throws ConfigurationException {
        Optional<Object> node = getContextObject(context, key).map(o -> o.get(key.getOptionName()));
        if(key.isIndexed()) {
            node = node.map(object -> {
                if(!(object instanceof List)) {
                    return null;
                }
                List<Map<String,Object>> listNode = (List<Map<String, Object>>) object;
                if(listNode.size() <= key.getIndex()) {
                    return null;
                }
                return listNode.get(key.getIndex());
            });
        }
        return node;
    }

    private Optional<Map<String, Object>> getContextObject(Map<String, Object> context, Key key) throws ConfigurationException {
        if(key.isTopLevel()) {
            return Optional.of(context);
        } else {
            Optional<Object> parent = getNode(context, key.getContext());
            if (!parent.isPresent()) {
                return Optional.empty();
            }
            if (!(parent.get() instanceof Map)) {
                throw new ConfigurationException(key.getOptionName() + " is not an object");
            }
            return Optional.of((Map)parent.get());

        }
    }

    private class CustomResolver extends Resolver {
        protected void addImplicitResolvers() {
            // no implicit resolving, that is up to the Configuration Parser
        }
    }
}
