#!/bin/sh
for i in $@; do
    grep "^Method $i: " ROSResponder.sym
done
