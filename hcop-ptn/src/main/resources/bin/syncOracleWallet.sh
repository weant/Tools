#!/bin/bash

TOOL_CONF_FILE=./init.properties
package_name=hcop-ptn
java_path=/usr/java/jdk1.8.0_192-amd64
sync_class=com.hcop.ptn.common.omsinfo.SyncOmsInfo
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

lib_path=`get_param "lib_path" "$lib_path"`
sync_class=`get_param "sync_class" "$sync_class"`
package_name=`get_param "package_name" "$package_name"`
java_path=`get_param "java_path" "$java_path"`

export JAVA_HOME=$java_path
export PATH=$JAVA_HOME/bin:$PATH
export export CLASSPATH=.:$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar
export CONF_PATH=`get_param "CONF_PATH" "$CONF_PATH"`
java -cp $lib_path/$package_name.jar $sync_class false true