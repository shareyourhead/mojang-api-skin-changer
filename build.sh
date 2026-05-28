#!/bin/bash
mkdir -p out dist
javac -d out src/*.java && jar cfe dist/mc-skin-changer.jar Main -C out .
