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
    ./mvnw clean compile test
    ;;
  *)
    echo "Invalid action parameter."
    exit 1
    ;;
esac
