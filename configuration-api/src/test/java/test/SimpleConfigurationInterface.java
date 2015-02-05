package test;

import com.github.peterbecker.configuration.v1.Configuration;
import com.github.peterbecker.configuration.v1.Option;

/**
 * Test interface to see if the annotations work in code (technically and stylistically).
 */
@SuppressWarnings("ALL")
@Configuration
public interface SimpleConfigurationInterface {
    @Option
    String someSimpleValue();

    @Option(
            defaultValue = "This is the default",
            description = "Set this to change stuff"
    )
    String someValueThatNeedsExplanation();
}
