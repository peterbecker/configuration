# Configuration Parsing

[![Build Status](https://travis-ci.org/peterbecker/configuration.svg?branch=master)](https://travis-ci.org/peterbecker/configuration)

A Java configuration library, allowing loading configuration options for command-line tools in a very lightweight manner.

The core concept of this library is to use interfaces with
annotated getters to define configuration data of components. These can then be parsed from various data sources.
At the moment only property files are supported, but other formats and sources are planned (XML, YAML, JSON, CLI, JDBC).

The parser in this library requires Java 8. The code using the interfaces can be in older Java versions.

To use the configuration parser the following Maven dependency is needed:

```
<dependency>
    <groupId>com.github.peterbecker</groupId>
    <artifactId>configuration-parser</artifactId>
    <version>1.0</version>
</dependency>
```

If you want to use annotations for your configuration interfaces you need this dependency:

```
<dependency>
    <groupId>com.github.peterbecker</groupId>
    <artifactId>configuration-api</artifactId>
    <version>1.0</version>
</dependency>
```

The parser is required only for the modules using it, the annotation should sit where annotations
are needed.

For example: your database access layer might depend on the annotation API to allow documenting its configuration
interface, whereas the scheduled job that uses the access layer will depend on the parser.

Usage of the API is optional, which means library code can stay completely independent of this library.

# Basic Usage

To define a configuration, just define an interface using the option names as method names, and the option types as
return types. For example this is a valid configuration interface:

```java
public interface MyFirstConfiguration {
    int anIntegerValue();
    String aTextValue();
    Optional<LocalDate> anOptionalDate();
}
```

A corresponding properties file would look like this:

```
anIntegerValue=23
aTextValue=some text
anOptionalDate=2015-02-19
```

In this file the first two items are mandatory, if either of them is missing loading the configuration will fail. The date
can be omitted, in which case the return value in the interface will be an empty `Optional`.

To parse this configuration, the following code can be used:

```java
Path configFile = Paths.get("config.properties");
MyFirstConfiguration config =
                Configuration.
                        loadInterface(MyFirstConfiguration.class).
                        fromPropertiesFile(configFile).
                        done();
```

The resulting `config` object will have the values set that are given in the properties file.

One important thing to notice here is that the configuration interface does not use anything beyond the JDK provided
capabilities. This means that the configured components do not have any additional dependencies.

The interfaces also make testing easy: a test class can implement it in any way wanted, including just using an
inner class.

A number of value types are supported in this. This includes: `String`, all primitive types and their wrappers; all value
types from `java.time.*`; `BigInteger`, `BigDecimal` and `Color` from both AWT and JavaFX. Most of these use standard
JDK methods to map them from Strings (e.g. `Integer::parseInt`). The exception is AWT's `Color` which follows the JavaFX
pattern of using CSS-style encodings.

Any type but the primitives can be wrapped into an `Optional` to make it optional.

# Advanced Setup With Annotations

For advanced
configuration options annotations are used, in which case these become a dependency. The annotations are in a separate
module without runtime dependencies, which means that even in this scenario the footprint is very small.

Two annotations are available: `@Configuration` and `@Option`. The former is a tagging interface, i.e. it serves no
functionality apart from making it clear to a reader of the code that the interface is intended as configuration. The
`@Option` has additional functions.

The first one is that `@Option` offers a `defaultValue` attribute that can be used to
provide a string that is used in case no string is provided in the configuration input. The string provided as default
is processed in the same way a string in an input file would: it will be parsed into the return type specified in the
interface.

This is different to using the
`Optional` wrapper in that defaulting is invisible to the component using the configuration. If there is a default
configured, the interface will always provide some value. `Optional` allows communicating the absence of a value
explicitly, but the code using it will need to handle it.

`@Option` also has a `description` attribute, which can be used to document the function of the option. This is intended
for command line usage, but that part is not yet implemented. At the moment it serves a role similar to JavaDoc.

Using the annotations an interface looks like this:

```java
@Configuration
public interface ConfigInterface {
     @Option(
        description = "This value has to be set"
     )
     int mandatoryValue();

     @Option(
        description = "This value will default to 53"
        defaultValue = "53"
     )
     int defaultingValue();
}
```

# Nesting Configurations

One advantage of this library over many other configuration approaches is that the configuration interfaces can be nested.
For example a database configuration can be part of an application configuration. The database configuration is defined
as part of a database layer, there is no need for the database layer to know anything else.

If a property file is used, nesting is mapped to a dot notation. For example if we have:

```java
public interface SocketConfiguration {
    String hostName();
    int port();
}
```

and

```java
public interface ServerConfiguration {
    SocketConfiguration serverSocket();
    int numberOfWorkerThreads();
}
```

then a matching configuration file could look like this:

```
serverSocket.hostName=localhost
serverSocket.port=1223
numberOfWorkerThreads=8
```

In input formats that support nesting (XML, YAML, JSON) configurations will appear nested in the files.

Code using this configuration might look like this:

```java
public Server {
    public Server(ServerConfiguration config) {
        Socket socket = new Socket(config.serverSocket());
        createWorkerPool(socket, config.numberOfThreads());
    }
    ...
}
```

Here the `Socket` object is unaware of the the `ServerConfiguration`, all it knowns is the `SocketConfiguration`. Hence
the components are cleanly separated.

# Custom Value Parsing

Values are parsed using handlers from Strings to the return type of the getters. Custom parsers can be registered as
functions from `String` to the target type:

```java
ConfigInterface config =
        Configuration.
                loadInterface(ConfigInterface.class).
                fromPropertiesFile(configFile).
                withValueParser(MyType.class, MyType::parse).
                done();
```

The example uses a static method handle and assumes a static method on `MyType` like this:

```java
public static MyType parse(String input) {
    // parse and return value
}
```

Explicit `Function` instances or lambda expressions can be used as alternative to the static method handle above.

Once a parse function is registered, the value can be used in a configuration interface. This is true for both the
direct use, as well as indirect use such as `Optional<MyType>`.

# State of the Project

The project is under active development as of the time of writing (2015-02-19). All functionality described above works
and is covered by unit tests.

It is planned to have support for advanced features such as printing usage instructions, dumping actual
configuration into log files, and layering configuration sources to ease customization (e.g. default in classpath,
overridden by file, overridden by command line).

Target platform is Java 8, at least for the parser. The configuration interfaces do not require anything Java 8 specific,
the use of `Optional` could be replaced with a default value that is recognizable as absence. In theory this means we
could support anything back to Java 5 with full functionality, even older if defaults are not required. None of this
is planned unless someone asks, though.

There is no roadmap or schedule, but I'm available for questions.

Check out the unit tests to see what works. See the issue list for more detail on what is intended.
