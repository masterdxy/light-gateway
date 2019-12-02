#!/bin/bash
MODE=$1
BIN_DIR=`pwd`
DEPLOY_DIR=`pwd`
PID_FILE=${DEPLOY_DIR}/pid;
if [ ! -f $PID_FILE ];then
	touch ${PID_FILE};
else
	PID=$(cat ${PID_FILE});
	PID_EXIST=$(ps aux | grep $PID | grep -v 'grep');
	if [ ! -z "$PID_EXIST" ];then
	    echo "Service is running,no need to start again!"
		exit 1;
	fi	
fi
CONF_DIR=$DEPLOY_DIR/conf

LOGS_DIR=$DEPLOY_DIR/logs

if [ ! -d $LOGS_DIR ]; then
    mkdir $LOGS_DIR
fi

STDOUT_FILE=$LOGS_DIR/stdout.log

LIB_DIR=$DEPLOY_DIR/lib

LIB_JARS=`ls $LIB_DIR|grep .jar|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`

JAVA_OPTS=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true"

JAVA_MEM_OPTS=" -server -Xms2g -Xmx2g -Xmn1g -XX:MaxDirectMemorySize=1g -XX:+UseG1GC "

JAVA_MEM_OPTS="${JAVA_MEM_OPTS} -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=320m"
JAVA_MEM_OPTS="${JAVA_MEM_OPTS} -XX:-OmitStackTraceInFastThrow -XX:+HeapDumpOnOutOfMemoryError
                    -XX:HeapDumpPath=${LOGS_DIR}/java_heap_dump.hprof"
JAVA_MEM_OPTS="${JAVA_MEM_OPTS} -XX:-UseLargePages"

echo "Service is starting..."
nohup java $JAVA_OPTS $JAVA_MEM_OPTS \
-classpath \
$CONF_DIR:$LIB_JARS com.github.masterdxy.gateway.Bootstrap > $STDOUT_FILE 2>&1 &

echo "$!" > ${PID_FILE};

echo "PID: $(cat $PID_FILE)"
echo "Service is started..."

