#!/bin/bash

cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
PID_FILE=${DEPLOY_DIR}/pid;
if [ ! -f $PID_FILE ];then
	echo " echo $PID_FILE can not find , please check service is running !...";
	exit 0;
fi
PID=$(cat $PID_FILE);
cat $PID_FILE | xargs kill -9 > /dev/null 2>&1;
rm -rf $PID_FILE;