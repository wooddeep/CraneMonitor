#!/bin/sh

# author: wooddeep
# hope for health!!!!


# fromusb /data/data/com.wooddeep.crane/databases crane.db

# /mnt/media_rw，
# /mnt/usb_storage

usb_root=(/mnt/media_rw  /mnt/usb_storage /sdcard/test)

detect_usb_root() {
    for root in ${usb_root[@]}; do
        if [ -d $root ]; then
            disk_dir=`cd $root; ls`
            array=(${disk_dir// / })
            length=${#array[@]}
            if [ $length -gt 0 ]; then
               echo $root/${array[0]}
               return
            fi
        fi
    done
    echo "none"
}

file_size_compare() {
    # ls -l init | awk '{for (i = 1; i < NF; i++) { if ($i ~ /^[0-9]+$/) printf("%s\n", $i)};}'
    #string=`ls -l $1`
    #array=(${string//,/ })
    #src_size=${array[3]}
    src_size=`ls -l $1 | awk '{for (i = 1; i < NF; i++) { if ($i ~ /^[0-9]+$/) printf("%s\n", $i)};}'`

    #string=`ls -l $2`
    #array=(${string//,/ })
    #dst_size=${array[3]}
    src_size=`ls -l $2 | awk '{for (i = 1; i < NF; i++) { if ($i ~ /^[0-9]+$/) printf("%s\n", $i)};}'`

    if [ $src_size -eq $dst_size ]; then
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
            mv -f $2/$3.tmp $2/$3
            echo "ok"
        else
            echo "err"
        fi
    fi
fi

if [ "$1" == "tousb" ]; then
    dst_root=`detect_usb_root`
    if [ "$dst_root" == "none" ]; then
        echo "#err, usb disk not found!"
    else
        echo "usb root: $dst_root"

        cp -f $2/$3 $dst_root/$3.tmp

        #file_size_compare $2/$3 $dst_root/$3.tmp

        copy_ret=`file_size_compare $2/$3 $dst_root/$3.tmp` # compare size
        echo "## compare result: $copy_ret"
        if [ "$copy_ret" == "equ" ]; then
            mv -f $dst_root/$3.tmp $dst_root/$3
            echo "ok"
        else
            echo "err"
        fi
    fi
fi

if [ "$1" == "usbdetect" ]; then
    detect_usb_root
fi
