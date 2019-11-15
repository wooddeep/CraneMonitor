#!/bin/sh

# author: wooddeep
# hope for health!!!!


# fromusb /data/data/com.wooddeep.crane/databases crane.db

# /mnt/media_rw，
# /mnt/usb_storage

usb_root=(/mnt/media_rw  /mnt/usb_storage /sdcard)

detect_usb_root() {
    for root in ${usb_root[@]}; do
        if [ -d $root ]; then
            disk_dir=`cd $root; ls`
            array=(${disk_dir// / })
            length=${#array[@]}
            if [ $length -eq 0 ]; then
                echo "none"
                return
            else
               echo $root/${array[0]}
               return
            fi
        fi
    done
    echo "none"
}

file_size_compare() {
    string=`ls -l $1`
    array=(${string//,/ })
    src_size=${array[3]}

    string=`ls -l $2`
    array=(${string//,/ })
    dst_size=${array[3]}

    if [ $src_size == $dst_size ]; then
        echo "equ"
    else
        echo "neq"
    fi
}


if [ "$1" == "fromusb" ]; then
    src_root=`detect_usb_root`
    if [ "$src_root" == "none" ]; then
        echo "#err, usb disk not found!"
    else
        echo "usb root: $src_root"
        if [ ! -f "$src_root/$3" ]; then
            echo "#err, $src_root/$3 not found in usb disk!"
        fi

        cp -f $src_root/$3 $2/$3.tmp
        copy_ret=`file_size_compare $src_root/$3 $2/$3.tmp` # compare size
        if [ "$copy_ret" == "equ" ]; then
            cp -f $2/$3.tmp $2/$3
            echo "ok"
        else
            echo "err"
        fi
    fi
fi
