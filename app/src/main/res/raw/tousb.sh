#!/bin/sh

# author: wooddeep
# hope for health!!!!

string=`ls -lt %s`
array=(${string//,/ })
src_size=${array[4]}
string=`ls -lt %s`
array=(${string//,/ })
dst_size=${array[4]}
if [ $src_size == $dst_size ] then
  echo equ
else
  echo neq
fi
