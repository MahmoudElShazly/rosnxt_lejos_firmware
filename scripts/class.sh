#!/bin/sh
for i in $@; do
    grep "^Class $i: " ROSResponder.sym
done
