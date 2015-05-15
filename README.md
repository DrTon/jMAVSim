jMAVSim
=======

[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/DrTon/jMAVSim?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Simple multirotor simulator with MAVLink protocol support

Installation
------------

### Common

Requirements:
- Java 6 or newer (JDK, http://www.oracle.com/technetwork/java/javase/downloads/index.html)
- java3d (http://www.oracle.com/technetwork/articles/javase/index-jsp-138252.html)

Clone repository and initialize submodules:
```
git clone https://github.com/DrTon/jMAVSim
git submodule init
git submodule update
```

Compile:
```
cd jMAVSim
ant
```

Run:
```
java -jar out/production/jmavsim.jar
```

### Platform-specific

#### Arch Linux

Installing java3d:
```
sudo pacman -S jdk7-openjdk

yaourt -S java3d
cp /jre/lib/ext/j3dcore.jar /home/USERNAME/src/jMAVSim/lib/j3dcore.jar
cp /jre/lib/amd64/libj3dcore-ogl.so /home/USERNAME/src/jMAVSim/
```

#### MAC OS

Mac OS app can be created by running:
```
ant jmavsim_mac_os_app
```
Result will be placed to `out/production`

Developing
----------

jMAVSim is not out-of-the-box simulator, but very flexible toolkit, new vehicle types (e.g. non standard multirotors configurations) can be added very easily. (But for fixed wing you will need some more aerodynamics knowledge). Camera can be placed on any point, including gimabal, that can be controlled by autopilot. Multiple systems simulation is possible. Sensors data can be replayed from real flight log.
