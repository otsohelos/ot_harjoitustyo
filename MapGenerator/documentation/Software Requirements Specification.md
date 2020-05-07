# Software Requirements Specification

## Purpose

The purpose of this application is to let users generate random procedurally-generated maps that can be used to aid creative writing, role-playing games and other creative pursuits. The user can specify the size and other variables to create maps to their liking.

### Maps Generated

A map consists of Tiles. A given Tile is either land or water. Both land and water Tiles have further properties.

Land Tiles have the property of altitude. Altitude will change gradually from one Tile to another. Land Tiles also have the property of a terrain type, which is one of the following: Desert, Grassland, Forest, or Wetland. Terrain depends on both the altitude of a Tile. Additionally, a land Tile can be a River tile, forming part of a river.

Water Tiles have the property of depth. Depth will also change gradually from one Tile to the next.

## Users

MapGenerator has only one user.

## User Interface

MapGenerator has three views: a Settings view and two Map views, Elevation and Terrain.

### Settings View

In the Settings View, the user can determine the width and length of the map in Tiles. Variability of altitude/depth and whether the are on the map is Coastal or Inland is also determined. In the Settings view, the user puts in their preferred settings and clicks Generate, after which a random map is generated.

### Map Views: Elevation And Terrain

After a map is generated, the user will be taken to the Elevation view of the generated map. The user can switch between the Terrain and Elevation views of the same map. In the Elevation view, rivers can be toggled to be visible or not visible.

The user can choose to save the map to a png file. If the user is not satisfied with the map, they can choose to generate a new random map with the same settings, or go back to the Settings view. It's also possible to keep the elevations but re-randomize the rivers or the terrain.

### UI Sketch

![UI sketch of two views](https://github.com/otsohelos/ot_harjoitustyo/blob/master/MapGenerator/documentation/uisketch.jpg)
Map view has elevation indicated by color darkness.

## Functionality In Basic Version

### Settings View

- DONE: User can set the size of the map within certain constraints.
- DONE: User can set the amount of variability in elevation of both water and land Tiles.
- DONE: User can set the map as Coastal or Inland.
- DONE: Question mark labels will show tips on hover

### Map Views

- DONE: Initially, the Elevation view will be generated.
- DONE: Save button: User can choose to save the map.
- DONE: Redo button: User can generate a new map with the same settings.
- DONE: Back button: User can go back to the Settings view to change settings.
- DONE: Addition of Terrain: Terrain types will be generated depending on settings and the altitude or a given terrain Tile.
- DONE: Switch buttons: Possibility to switch between Elevation and Terrain views.
- DONE: Addition of Legend.
- DONE: Option to re-randomize terrain.
- DONE: Rainy / dry setting.
- DONE: Addition of rivers.
- DONE: Possibility to re-randomize rivers.


## Further Development Ideas

- Legend inclusion in save files
- Customisable map name
- Addition of cities
- Addition of roads
- Possibility to go back a couple of maps if user clicked Redo by accident
- Possibility to save a map in a format readable by MapGenerator