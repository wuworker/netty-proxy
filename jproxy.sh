#!/bin/bash
# jar包启动脚本
#

PID_FILE=proxy.pid
JAVA_OPTS=

# 进入脚本所在绝对路径
basedir=$(cd $(dirname $0); pwd -P)
cd ${basedir}

# 获取包名
jar_name=$(ls|grep "proxy-application-*.*.jar")

strat_cmd="java $JAVA_OPTS -jar $jar_name"


# 获取pid
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
            nohup ${strat_cmd} &> /dev/null &
        else
            ${strat_cmd}
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


