#!/bin/bash
# jar包启动脚本
#

PID_FILE=proxy.pid
JAVA_OPTS=
JAR_NAME=proxy-application-1.0-SNAPSHOT.jar
START_CMD="java $JAVA_OPTS -jar $JAR_NAME"

pid=
status(){
    if [ -f "${PID_FILE}" ];then
        pid=$(cat ${PID_FILE})
    fi
}

status

case $1 in
"start")
    if [ -z "$pid" ];then
        if [ "$2" == "-d" ];then
            nohup ${START_CMD} &> /dev/null &
        else
            ${START_CMD}
        fi
     else
        echo "proxy already running in $pid"
     fi
     ;;
"stop")
    if [ -n "$pid" ];then
        kill $pid
    else
        echo "proxy already stop"
    fi
    ;;
"status")
    if [ -z "$pid" ];then
        echo "proxy is stopped"
    else
        echo "proxy is running in $pid"
    fi
    ;;
*)
    echo "must is [start/stop/status]"
    ;;
esac


