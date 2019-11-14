#!/bin/sh

# author: wooddeep
# hope for health!!!!

while true; do
  #echo "hello world"

  exist=`ps | grep 'com.wooddeep.crane'`
  if [ -z "$exist" ]; then
    am start com.wooddeep.crane/com.wooddeep.crane.MainActivity
  fi

  sleep 2
done
