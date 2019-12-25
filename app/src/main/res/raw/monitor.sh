#!/bin/sh

# author: wooddeep
# hope for health!!!!

if [ ! -d /sdcard/crane ]; then
    mkdir -p /sdcard/crane
fi

while true; do
  #echo "hello world"

  exist=`ps | grep 'com.wooddeep.crane'`
  if [ -z "$exist" ]; then
    am start com.wooddeep.crane/com.wooddeep.crane.MainActivity
  fi

  sleep 5
done
