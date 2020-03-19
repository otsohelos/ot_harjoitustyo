# Software Requirements Specification

## Purpose

The purpose of this application is to let users generate random procedurally-generated maps that can be used to aid creative writing, role-playing games and other creative pursuits. The user can specify the size and other variables to create maps to their liking.

### Maps Generated

A map will consist of squares. A given square is either land or water. Both land and water squares can have further properties.

Land squares have the property of altitude. Altitude will change gradually from one square to another. Time permitting, land squares will also have the property of a terrain type, which is one of the following: Desert, Grassland, Forest, Cultivated or City. Forest and Cultivated terrains are not possible in the highest altitudes.

Water squares have the property of depth. Depth will also change gradually from one square to the next.

## Users

MapGenerator will have only one user.

## User Interface

MapGenerator will have two views: a Settings view and a Map view.

### Settings View

In the Settings View, the user can determine the width and length of the map in squares. Variability of altitude/depth, presence of cities and jaggedness of coastlines can also be controlled. In this view, the user puts in their preferred settings and clicks Generate, after which a random map is generated.

### Map Views: Elevation And Terrain

After a map is generated, the user will be taken to the Elevation view of the generated map. The user can switch between the Terrain and Elevation views of the same map.

The user can choose to save the map to a picture document. If the user is not satisfied with the map, they can choose to generate a new random map with the same settings, or go back to the Settings view.

### UI Sketch




## Functionality In Basic Version

### Settings View

- User can set the size of the map within certain constraints.
- User can set the amount of variability in elevation of both water and land squares.

### Map Views

- Initially, only a topographical view will be generated.
- Save button: User can choose to save the map.
- Redo button: User can generate a new map with the same settings.
- Back button: User can go back to the Settings view to change settings.


## Further Development Ideas

After the basic version is functional, it will be complemented by the following functionalities, time permitting:

- Addition of Terrain: Terrain types will be generated depending on settings and the altitude or a given terrain square
- Switch buttons: Possibility to switch between Elevation and Terrain views
- Contiguity of terrain types setting: how likely adjacent squares are to have the same type of terrain
- Addition of Legend and customisable map name
- Addition of roads and rivers