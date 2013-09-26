#!/bin/sh
set -e -x
nxjc -sourcepath src -d bin src/org/ros/ROSResponder.java
nxjlink -v -cp bin -o ROSResponder.nxj org.ros.ROSResponder | tee ROSResponder.sym
