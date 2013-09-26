rosnxt
======

rosnxt is a collection of software packages for using the NXT brick and sensor in ROS.

From [ROS.org](http://www.ros.org/wiki/): ROS (Robot Operating System) provides libraries and tools to help software developers create robot applications. It provides hardware abstraction, device drivers, libraries, visualizers, message-passing, package management, and more. 

rosnxt_lejos_firmware
=====================

rosnxt_lejos_firmware is the counterpart of [rosnxt_proxy](https://github.com/rosnxt/rosnxt_proxy/), consisting of a program running on the NXT brick, on top of the leJOS NXT firmware, containing the control logic, the device drivers, and the protocol implementation for communicating with the rosnxt_proxy ROS node.

The aim of rosnxt_lejos_firmware is to support nearly every sensor available, so it has been designed to be easy to extend with new devices.

Future goals include optimization of the communication bandwidth, such as avoiding retransmitting repeated data packets, noise filtering, tresholding, and avoid wasting bits in the data packets.
