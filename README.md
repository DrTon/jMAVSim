jMAVSim
=======

[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/DrTon/jMAVSim?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Simple multirotor simulator with MAVLink protocol support

Installation
------------

### Linux

#### Arch Linux
```
git clone https://github.com/DrTon/jMAVSim
git submodule init
git submodule update

sudo pacman -S jdk7-openjdk

yaourt -S java3d
cp /jre/lib/ext/j3dcore.jar /home/USERNAME/src/jMAVSim/lib/j3dcore.jar
cp /jre/lib/amd64/libj3dcore-ogl.so /home/USERNAME/src/jMAVSim/
```
