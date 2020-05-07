# MapGenerator Software Architecture

## Overall architecture

MapGenerator has a simple two-layered architecture consisting of the UI and Domain layers. There is no third layer, as MapGenerator doesn't store information between uses.

![Application layers](https://github.com/otsohelos/ot_harjoitustyo/blob/master/MapGenerator/documentation/layers.png)

## Domain layer


![Application architecture](https://github.com/otsohelos/ot_harjoitustyo/blob/master/MapGenerator/documentation/architecture.jpg)


## Sequence diagram
Example case of MapUI calling the creation of a new map, with user-chosen variables:

* height 120
* width 150
* highVariability false
* coastal true

Randomized integer variables are represented as (int). Case-dependent variables are represented as their names (i, j, stopWhen, elevation etc.)

**Sequence diagram of initial tile creation:**

![Sequence diagram](https://github.com/otsohelos/ot_harjoitustyo/blob/master/MapGenerator/documentation/sequence1.png)

**Sequence diagram of river assignment:**
- This happens at the point indicated in the lower left of the above diagram.

![Sequence diagram 2](https://github.com/otsohelos/ot_harjoitustyo/blob/master/MapGenerator/documentation/sequence2.png)
