#!/bin/bash

TOOL_CONF_FILE=./init.properties
package_name=hcop-otn

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
PIDS=`ps -aef | grep "$package_name" | grep -v grep | gawk '{ print $2 }'`

if [ "x$PIDS" == "x" ] 
then
	printf "$package_name is [ \033[31mStopped\033[0m ].\n"
	if [ -f "$pid_file" ]; then
		rm -rf $pid_file
	fi
	exit 1
fi

for PID in ${PIDS[*]}
do
	printf "$package_name is [ \033[32mRunning\033[0m ], PID:$PID\n"
done

exit 0
