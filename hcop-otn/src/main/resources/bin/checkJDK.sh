#!/bin/bash

installedArray=($(rpm -qa | grep "^jdk1.8"))

if (( ${#installedArray[*]} > 0 ))
then
	echo -e "JDK-1.8 is [ \033[32mInstalled\033[0m ]"
	exit 0
else 
	echo -e "JDK-1.8 is [ \033[31mnot installed\033[0m ], begin to install jdk-1.8."
	jdkpath=../jdk/jdk-8u192-linux-x64.rpm
	chmod +x $jdkpath
	if rpm -ivh --force $jdkpath
	then
		echo "jdk-1.8 is installed successfully."
		exit 0
	else
		echo "Failed to install jdk-1.8"
		exit 1
	fi
fi
