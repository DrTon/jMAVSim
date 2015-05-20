jMAVSim
=======

[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/DrTon/jMAVSim?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Simple multirotor simulator with MAVLink protocol support

Installation
------------

### Common

Requirements:
- Java 6 or newer (JDK, http://www.oracle.com/technetwork/java/javase/downloads/index.html)

Java3D and JOGL/JOAL jars, including native libs for Linux (i586/64bit), Windows (i586/64bit) and Mac OS (universal) already included in this repository, no need to install it.

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
java -cp lib/*:out/production/jmavsim.jar me.drton.jmavsim.Simulator
```

Some shells (e.g. tcsh) will try to expand `*`, so use `\*` instead:
```
java -cp lib/\*:out/production/jmavsim.jar me.drton.jmavsim.Simulator
```

On Windows use `;` instead of `:` in -cp:
```
java -cp lib/*;out/production/jmavsim.jar me.drton.jmavsim.Simulator
```

### Platform-specific

jMAVSim uses java3d library for visualization. It was discontinued for long time, but now maintained again and works with JOGL backend. All necessary jars with java classes and native binaries (Linux/Mac OS/Windows) included in this repo, no need ti install java3d manually. And need to make sure that java doesn't use any other deprecated version of java3d. For more info related to java3d see this article: https://gouessej.wordpress.com/2012/08/01/java-3d-est-de-retour-java-3d-is-back/

#### Mac OS

On Mac OS java may use deprecated version of java3d as extension, if you get following error:
```
JavaVM WARNING: JAWT_GetAWT must be called after loading a JVM
AWT not found
Exception in thread "main" java.lang.NoClassDefFoundError: apple/awt/CGraphicsDevice
	at javax.media.j3d.GraphicsConfigTemplate3D.<clinit>(GraphicsConfigTemplate3D.java:55)
...
```

Then just add `-Djava.ext.dirs=` option to command line when starting:
```
java -Djava.ext.dirs= -cp lib/*:out/production/jmavsim.jar me.drton.jmavsim.Simulator
```

Developing
----------

jMAVSim is not out-of-the-box simulator, but very flexible toolkit, new vehicle types (e.g. non standard multirotors configurations) can be added very easily. (But for fixed wing you will need some more aerodynamics knowledge). Camera can be placed on any point, including gimabal, that can be controlled by autopilot. Multiple systems simulation is possible. Sensors data can be replayed from real flight log.

All simulator configuration hardcoded in file `src/me/drton/jmavsim/Simulator.java`, this file _should_ be edited before running simulator.

Custom vehicle visual models in .obj format can be used.

Custom MAVLink protocols can be used, no any recompilation needed, just specify XML file instead of `custom.xml`.

It's convinient to start simulator from IDE. Free and powerful IntelliJ IDEA IDE recommended, project files for it are already included, just open project file `jMAVSim.ipr` and right-click -> Run `Simulator`.
