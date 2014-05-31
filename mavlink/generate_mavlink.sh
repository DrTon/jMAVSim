#!/bin/sh

rm -rf ../src/org/mavlink
java -cp ../lib/org.mavlink.generator-1.0.0.jar:../lib/org.mavlink.util-1.0.0.jar org.mavlink.generator.MAVLinkGenerator message_definitions/common.xml ../src/ true true true true
java -cp ../lib/org.mavlink.generator-1.0.0.jar:../lib/org.mavlink.util-1.0.0.jar org.mavlink.generator.MAVLinkGenerator message_definitions/px4.xml ../src/ true true true true
