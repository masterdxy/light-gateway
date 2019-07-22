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

JAVA_DEBUG_OPTS=""
if [ "$1" = "debug" ]; then
    JAVA_DEBUG_OPTS=" -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n "
fi

JAVA_JMX_OPTS=""
if [ "$1" = "jmx" ]; then
    JAVA_JMX_OPTS=" -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false "
fi

JAVA_MEM_OPTS=""
BITS=`java -version 2>&1 | grep -i 64-bit`
if [ -n "$BITS" ]; then
    JAVA_MEM_OPTS=" -server -Xmx2g -Xms2g -Xmn1g -XX:MaxMetaspaceSize=256m -Xss1m -XX:+DisableExplicitGC
    -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70"
else
    JAVA_MEM_OPTS=" -server -Xms512m -Xmx512m -XX:PermSize=128m -XX:SurvivorRatio=2 -XX:+UseParallelGC "
fi

echo "Service is starting..."
nohup java $JAVA_OPTS $JAVA_MEM_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS \
-classpath \
$CONF_DIR:$LIB_JARS com.github.masterdxy.gateway.Bootstrap > $STDOUT_FILE 2>&1 &

echo "$!" > ${PID_FILE};

echo "PID: $(cat $PID_FILE)"
echo "Service is started..."

