# Configuration

Early work in progress on a Java configuration library.

This is intended to re-do some work I have done as closed source previously. The idea is to use interfaces with annotated getters to define configuration data of components. These can then be parsed from various data sources.

The way this is better than many other configuration approaches is that the configuration interfaces can be nested. For example a database configuration can be part of an application configuration. The database configuration is defined as part of a database layer, there is no need for the database layer to know anything else.

The interfaces also make testing easy: a test class can implement it in any way wanted, including just using an anonymous inner class.

Values will be parsed using handlers from Strings to the return type of the getters. A range of default handlers will be available, custom handlers can be added.

Addtionally there will be support for advanced features such as printing usage instructions, dumping actual configuration into log files, and layering configuration sources to ease customization (e.g. default in classpath, overridden by file, overridden by command line).

Target platform is Java 8. There is no roadmap or schedule, but I'm available for questions.

