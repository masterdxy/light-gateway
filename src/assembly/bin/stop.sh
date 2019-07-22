#!/bin/bash
set -e;
cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
PID_FILE=${DEPLOY_DIR}/pid;
if [ ! -f $PID_FILE ];then
        echo "echo $PID_FILE can not find , please check service is running."
        exit 0;
fi
CONF_DIR=$DEPLOY_DIR/conf

echo "Service is stopping..."

PID=$(cat $PID_FILE 2>/dev/null)
kill $PID 2>/dev/null
rm -f ${PID_FILE}
echo "Stopped!"