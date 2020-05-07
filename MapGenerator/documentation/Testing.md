# Testing document

MapGenerator has been tested with JUnit automatized unit tests as well as manually.

## Unit Testing

Unit testing has been done on the [Domain](https://github.com/otsohelos/ot_harjoitustyo/tree/master/MapGenerator/src/main/java/com/github/otsohelos/mapgenerator/domain) level only, since MapGenerator doesn't store information in databases. This level is tested with four test classes: [MapTest](https://github.com/otsohelos/ot_harjoitustyo/blob/master/MapGenerator/src/test/java/com/github/otsohelos/mapgenerator/domain/MapTest.java), [MapCreatorTest](https://github.com/otsohelos/ot_harjoitustyo/blob/master/MapGenerator/src/test/java/com/github/otsohelos/mapgenerator/domain/MapCreatorTest.java), [TileTest](https://github.com/otsohelos/ot_harjoitustyo/blob/master/MapGenerator/src/test/java/com/github/otsohelos/mapgenerator/domain/TileTest.java) and [RiverMakerTest](https://github.com/otsohelos/ot_harjoitustyo/blob/master/MapGenerator/src/test/java/com/github/otsohelos/mapgenerator/domain/RiverMakerTest.java).

Of these, MapTest tests the basic functions of map elevation assignment and the math tools in [Map](https://github.com/otsohelos/ot_harjoitustyo/blob/master/MapGenerator/src/main/java/com/github/otsohelos/mapgenerator/domain/Map.java). TileTest tests color strings returned by [Tile](https://github.com/otsohelos/ot_harjoitustyo/blob/master/MapGenerator/src/main/java/com/github/otsohelos/mapgenerator/domain/Tile.java) and terrain and border assignments. MapCreatorTest tests that [MapCreator](https://github.com/otsohelos/ot_harjoitustyo/blob/master/MapGenerator/src/main/java/com/github/otsohelos/mapgenerator/domain/MapCreator.java) assigns terrain, returns good values for rainfall info string, and checks given map dimensions correctly. RiverMakerTest tests the mathematical methods in [RiverMaker](https://github.com/otsohelos/ot_harjoitustyo/blob/master/MapGenerator/src/main/java/com/github/otsohelos/mapgenerator/domain/RiverMaker.java).


### Test Coverage

Excluding the user interface, unit testing line coverage is 90 % and branch coverage is 82 %.

![Test coverage](https://github.com/otsohelos/ot_harjoitustyo/blob/master/MapGenerator/documentation/testcoverage.png)


## Functional testing

Functional testing has done manually.

### Subjective map quality evaluation

Since MapGenerator uses a lot of random generation and its main function is to provide interesting and aesthetically pleasing maps, manual testing has concentrated on the following points, evaluated subjectively:
1. User settings visibly affect the end result:
   * Coastal maps differ from inland maps.
   * High variability maps differ from low variability maps.
   * Rainy terrain maps from dry terrain maps.
1. There is variety within a map:
   * Large areas of a map are not likely to be the same altitude color.
   * Large areas of a map are not likely to be the same terrain.
1. "Disappointing" maps turn up rarely or not at all
   * A map is considered disappointing if it contains mostly water and very little land
   * or if it has the same elevation or terrain color on most of the map.
1. Maps close to the upper size limit and maps close to the lower size limit both provide pleasing results.
1. River-making succeeds most of the time and river courses seem natural.

Of these requirements, #1 is met practically all of the time. #2 and #3 are met most of the time. #4 is mostly met; large and small maps differ somewhat, but this can be considered a feature and not a bug. Small maps are more likely to be "disappointing", but this cannot be helped without major changes to MapGenerator's logic. #5 is the weakest point, as rivers sometimes seem unnatural, and river generation fails often, especially in high-variability maps.

### Save file testing

Saving of both Terrain and Altitude maps has been tested on both Mac OSX and Cubbli Linux. Saving works with no problems.

### Functionality

MapGenerator offers all functionalities defined in the [Software Requirements Specification](https://github.com/otsohelos/ot_harjoitustyo/blob/master/MapGenerator/documentation/Software%20Requirements%20Specification.md#functionality-in-basic-version). Usage has been tested with instructions provided in the [User Manual](https://github.com/otsohelos/ot_harjoitustyo/blob/master/MapGenerator/documentation/User%20Manual.md).

The only erroneous input that the user is able to give in the Settings is providing non-integer dimensions for the map, or providing dimensions that are outside the boundaries of acceptable map dimensions. These inputs generate a relevant error popup, which the user can close and then try again.

If the user deselects the Variability choice or Coastal/Inland choice, so that neither choice is selected, a map is generated with the previously selected setting.

Erroneous save file names (e.g. trying to rename the .png file with another file type suffix) or erroneous locations for the save file (e.g. folders where the user doesn't have permission to write) are handled by the operating system.

## Remaining problems in MapGenerator

River generation doesn't always work as desired, and rivers don't seem very natural. The RiverMaker class is, in general, unnecessarily long and complicated, and should be remade. The author of MapGenerator ran out of time to make the river algorithm better.

The original plan was to include the map legend in save files. This was not managed.

Maps are not saved and no confirmation is required when the user discards or redoes a map or part of a map (e.g. rivers). This means that if the user changes or leaves the map by accident, the map data is lost. It is also not possible to save a map in a format that would allow the user to view or modify it in MapGenerator later.