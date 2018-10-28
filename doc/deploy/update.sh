#!/bin/bash
rm -rf ./BOOT-INF/classes;
unzip -q demo-0.0.1-SNAPSHOT.jar.original -d ./BOOT-INF/classes;
zip -qd demo-0.0.1-SNAPSHOT.jar BOOT-INF/classes/*;
zip -ur demo-0.0.1-SNAPSHOT.jar ./BOOT-INF/*;