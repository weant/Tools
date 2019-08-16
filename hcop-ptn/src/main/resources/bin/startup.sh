#!/bin/bash

TOOL_CONF_FILE=./init.properties
port=8112
package_name=hcop-ptn
java_path=/usr/java/jdk1.8.0_192-amd64
CONF_PATH=../conf
lib_path=../libs

get_param()
{
	if [ -e $TOOL_CONF_FILE ]
	then
		VALUE=`cat $TOOL_CONF_FILE | dos2unix | grep $1= | sed "s#$1=##"`
		if [ "$VALUE" = "" ] || [ -z "$VALUE" ]; then
			echo $2
		else
			echo $VALUE
		fi
	else
		echo $2
	fi
}

package_name=`get_param "package_name" "$package_name"`
pid_file=./$package_name.pid

if [ -f "$pid_file" ]; then
	printf "[ \033[31mError\033[0m ] $package_name is running already.\n"
	exit 0
fi

PID=$(ps -aef | grep "$package_name" | grep -v grep | gawk '{ print $2 }')
if [ "x$PID" != "x" ] 
then
	printf "[ \033[31mError\033[0m ] $package_name is running already.\n"
	exit 0
fi

lib_path=`get_param "lib_path" "$lib_path"`
port=`get_param "port" "$port"`
java_path=`get_param "java_path" "$java_path"`
export JAVA_HOME=$java_path
export PATH=$JAVA_HOME/bin:$PATH
export export CLASSPATH=.:$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar
export CONF_PATH=`get_param "CONF_PATH" "$CONF_PATH"`
nohup java -jar $lib_path/$package_name.jar -server_port $port >> /dev/null 2>&1 &
#pid=$!
PID=$(ps -aef | grep "$package_name" | grep -v grep | gawk '{ print $2 }')
if [ "x$PID" = "x" ] 
then
	printf "[ \033[31mFailed\033[0m ] start $package_name\n"
	exit 1
else
	echo $PID > $pid_file
	printf "[ \033[32mSuccess\033[0m ] start $package_name, PID:$PID\n"
	exit 0
fi
