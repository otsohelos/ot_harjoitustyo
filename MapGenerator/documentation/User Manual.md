# Instructions

Download [MapGenerator](https://github.com/otsohelos/ot_harjoitustyo/releases/tag/v1.1).

## Startup

MapGenerator requires Java 1.8.

The downloaded MapGenerator package can be started by navigating to the relevant folder and entering the command 

```
java -jar MapGenerator-1.1.jar
```

## Settings

First you will be taken to a Settings view.

### Dimensions

Height and Width are the dimensions of your map. These should be between 40 and 260. On smaller screens, a height of less than 150 is recommended.

### Variability of elevation

High variability means the elevation is likely to change more from square to square.

### Land type

The Coastal setting gives you an archipelago region, with large amounts of water. A Coastal map is likely to be surrounded by water on all edges. The Inland setting gives you more land and less water.

When you've chosen your settings, press Generate.

## Elevation view

Now you have a map. The first view you see is the Elevation view. A legend can be seen on the right.

### Back

This button takes you back to the Settings. Your map is lost forever.

### Redo

Makes a new map with the same settings. Your previous map is lost forever.

### Show rivers / Hide rivers

Shows or hides rivers.

### Redo rivers

(This button is visible only if the rivers are visible.) Re-randomizes rivers.

### Show terrain

Takes you to the Terrain view.

### Save...

You can choose to save the current view as a png by pressing Save.


## Terrain view

This is the view of what kind or terrain your map has. The legend can be seen on the right.

### Altitude

This button takes you back to the Altitude view. The Terrain is not lost, and you can switch back and forth.

### Redo

Re-randomizes the rainfall and terrain.

### Rainy / Dry

You can choose to re-randomize the terrain with a choice of whether the area is likely to be rainy or dry.

### Save...

You can choose to save the current view as a png by pressing Save.