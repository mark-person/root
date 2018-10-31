#!/bin/bash
rm -rf ./BOOT-INF/classes;
unzip -q root-demo-0.0.1-SNAPSHOT.jar.original -d ./BOOT-INF/classes;
zip -qd root-demo-0.0.1-SNAPSHOT.jar BOOT-INF/classes/*;
zip -ur root-demo-0.0.1-SNAPSHOT.jar ./BOOT-INF/*;