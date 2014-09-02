rosnxt
======

rosnxt is a collection of software packages for using the NXT brick and sensor in ROS.

From [ROS.org](http://www.ros.org/wiki/): ROS (Robot Operating System) provides libraries and tools to help software developers create robot applications. It provides hardware abstraction, device drivers, libraries, visualizers, message-passing, package management, and more. 

rosnxt_lejos_firmware
=====================

rosnxt_lejos_firmware is the counterpart of [rosnxt_proxy](https://github.com/rosnxt/rosnxt_proxy/), consisting of a program running on the NXT brick, on top of the leJOS NXT firmware, containing the control logic, the device drivers, and the protocol implementation for communicating with the rosnxt_proxy ROS node.

The aim of rosnxt_lejos_firmware is to support nearly every sensor available, so it has been designed to be easy to extend with new devices.

Future goals include optimization of the communication bandwidth, such as avoiding retransmitting repeated data packets, noise filtering, tresholding, and avoid wasting bits in the data packets.

communication protocol
======================


Sensor protocol
---------------

| Data type | Data size | Description     |
|-----------|-----------|-----------------|
| byte      |         1 | device          |
| byte      |         1 | port            |
| byte      |         1 | chunk type      |
| byte      |         1 | payload length  |
| type1     |         ? | value1          |
| ...       |           | ...             |
| type1     |         ? | value1          |

Note: payload starts after field *payload length*


Command protocol
----------------

| Data type | Data size | Description     |
|-----------|-----------|-----------------|
| byte      |         1 | device          |
| byte      |         1 | port            |
| byte      |         1 | command type    |
| byte      |         1 | payload length  |
| type1     |         ? | value1          |
| ...       |           | ...             |
| type1     |         ? | value1          |

Note: payload starts after field *payload length*


Device numbers
--------------

Device number indicates the *software* device handling commands and sending data.

| Number | Device                  |
|--------|-------------------------|
|      0 | Dummy device            |
|      1 | Diagnostics             |
|      2 | Motor                   |
|      3 | Touch                   |
|      4 | Sound                   |
|      5 | Light                   |
|      6 | Color                   |
|      7 | UltrasonicRanger        |
|      8 | TouchMux                |
|      9 | IRLink                  |
|     10 | dIMU                    |
|     11 | dCompass                |


Port numbers
------------

| Number | Port                     |
|--------|--------------------------|
|      0 | Diagnostics/Internal use |
|      1 | Sensor 1 port            |
|      2 | Sensor 1 port            |
|      3 | Sensor 1 port            |
|      4 | Sensor 1 port            |
|      5 | Motor A port             |
|      6 | Motor B port             |
|      7 | Motor C port             |


