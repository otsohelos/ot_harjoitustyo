# MapGenerator

Demo for [Software Development Methods class](https://github.com/mluukkai/ohjelmistotekniikka-kevat-2020), University of Helsinki, spring 2020

By [Otso Helos](https://github.com/otsohelos)


## About the app

MapGenerator is a desktop app that generates random maps. It's intended for RPG players, creative writers, and anyone who enjoys nice maps of imaginary places.

MapGenerator is done with Java 11 and JavaFX. A working version will be finished during April 2020.


## Releases

[Week 5](https://github.com/otsohelos/ot_harjoitustyo/releases/tag/v1.0)


## Documentation

[Software Requirements Specification](https://github.com/otsohelos/ot_harjoitustyo/blob/master/MapGenerator/documentation/Software%20Requirements%20Specification.md)

[Software Architecture](https://github.com/otsohelos/ot_harjoitustyo/blob/master/MapGenerator/documentation/architecture.md)

[Log of working hours](https://github.com/otsohelos/ot_harjoitustyo/blob/master/MapGenerator/documentation/WorkHoursLog.md)

## Command Line Commands

### Testing

Perform tests:

```
mvn test
```

Create test coverage report:

```
mvn jacoco:report
```

Coverage report can be found at _target/site/jacoco/index.html_

### Generate runnable jar file

Generate runnable jar fine:

```
mvn package
```

Runnable jar file _MapGenerator-1.0-SNAPSHOT.jar_ can be found in _target_

Command to run jar file:

```
 java -jar MapGenerator-1.0-SNAPSHOT.jar
```

### JavaDoc

Generate JavaDoc:

```
mvn javadoc:javadoc
```

JavaDoc can be found at _target/site/apidocs/index.html_

### Checkstyle

Perform checks defined in [checkstyle.xml](https://github.com/otsohelos/ot_harjoitustyo/blob/master/MapGenerator/checkstyle.xml):

```
 mvn jxr:jxr checkstyle:checkstyle
```

Possible errors can be found at _target/site/checkstyle.html_

