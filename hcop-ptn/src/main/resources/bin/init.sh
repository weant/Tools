#!/bin/bash

TOOL_CONF_FILE=./init.properties

update_param()
{
	if [ "$2" != "" ] && [ ! -z "$2" ]; then
		if cat $TOOL_CONF_FILE | grep "$1=" > /dev/null 2>&1
		then
			echo "$2"
			OLD_VALUE=`cat $TOOL_CONF_FILE | grep "$1=" | sed 's#.*=##'`
			if [ "$OLD_VALUE" != "$2" ]
			then
				NEW_VALUE=$(echo $2 | sed 's#\\#\\\\#g' | sed 's#/#\\/#g')
				sed -i "s/$1=.*$/$1=$NEW_VALUE/" $TOOL_CONF_FILE
			fi
		else
			echo "$1=$2" >> $TOOL_CONF_FILE
		fi
	fi
}

dos2unix checkJDK.sh
dos2unix startup.sh
dos2unix status.sh
dos2unix stop.sh
dos2unix syncOmsInfo.sh
dos2unix syncOmsInfoAndOracleWallet.sh
dos2unix syncOracleWallet.sh

chmod +x checkJDK.sh
chmod +x startup.sh
chmod +x status.sh
chmod +x stop.sh
chmod +x syncOmsInfo.sh
chmod +x syncOmsInfoAndOracleWallet.sh
chmod +x syncOracleWallet.sh

./checkJDK.sh

installedArray=($(rpm -qa | grep "^jdk1.8"))
java_path=$(rpm -ql ${installedArray[0]} | grep '/jre$' | sed 's@/jre@@')

update_param "java_path" "$java_path"
