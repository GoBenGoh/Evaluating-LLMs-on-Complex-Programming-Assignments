#!/bin/bash

# Check if directory parameter is provided
if [ -z "$1" ]; then
  echo "Please provide a directory parameter."
  exit 1
fi

# Change directory
cd "$1" || exit 1

# Check the action parameter
case "$2" in
  "COMPILE")
    ./mvnw clean compile
    ;;
  "TEST")
    ./mvnw clean compile
    sync
    ./mvnw test
    ;;
  "CLEAR_TESTS")
    cd "src/test/java/nz/ac/auckland/se281"
    rm -rf *
    ;;
  "ADD_PROVIDED_TESTS")
    cp "../../Tests/CliTest.java" "src/test/java/nz/ac/auckland/se281"
    cp "../../Tests/MainTest.java" "src/test/java/nz/ac/auckland/se281/MainTest.java"
    ;;
  "ADD_HIDDEN_TESTS")
    cp "../../Tests/CliTest.java" "src/test/java/nz/ac/auckland/se281"
    cp "../../Tests/MainTestHidden.java" "src/test/java/nz/ac/auckland/se281/MainTest.java"
    ;;
  *)
    echo "Invalid action parameter."
    exit 1
    ;;
esac
