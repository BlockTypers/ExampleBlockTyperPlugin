#!/bin/bash
mvn clean package
java -cp target/example-1.0.0-jar-with-dependencies.jar com.blocktyper.example.Builder -n ${1:-test} -g ${2:-com.example}