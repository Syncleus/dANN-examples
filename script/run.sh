#!/bin/sh
java -Xmx320m -classpath ./target/dann-examples-2.0-SNAPSHOT.jar:../java_dann/target/dann-core-2.0-SNAPSHOT.jar:./lib/run/java3d/j3dcore.jar:./lib/run/java3d/vecmath.jar:./lib/run/java3d/j3dutils.jar:./lib/run/log4j/log4j-1.2.15.jar:./lib/run/jaxb/jaxb2-basics-runtime-0.5.3.jar com.syncleus.dann.examples.Main
