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
	printf "[ \033[31mError\033[0m ] $package_name is not running.\n"
	exit 0
fi

for PID in ${PIDS[*]}
do
	if [ "x$PID" == "x" ]
	then
		printf "[ \033[31mError\033[0m ] $package_name is not running.\n"
		exit 0
	else
		echo "Stopping $package_name, PID:$PID"
		kill -9 $PID 2> /dev/null
		sleep 1
	fi
done

rm -rf $pid_file

exit 0
