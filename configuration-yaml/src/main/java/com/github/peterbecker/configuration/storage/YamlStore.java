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
import java.util.Map;
import java.util.Optional;

public class YamlStore implements Store {
    private final Map<String, Object> data;

    @SuppressWarnings("unchecked")
    public YamlStore(Path resource) throws IOException {
        Yaml yaml = new Yaml(
                new Constructor(), // default
                new Representer(), // default
                new DumperOptions(), // default
                new CustomResolver());
        data = (Map<String, Object>) yaml.load(Files.newBufferedReader(resource));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<String> getValue(Key key) {
        Map<String, Object> node = data;
        for (String nodeName : key.getContext()) {
            node = (Map<String, Object>) node.get(nodeName);
        }
        return Optional.ofNullable(node.get(key.getOptionName())).map(Object::toString);
    }

    private class CustomResolver extends Resolver {
        protected void addImplicitResolvers() {
            // no implicit resolving, that is up to the Configuration Parser
        }
    }
}
