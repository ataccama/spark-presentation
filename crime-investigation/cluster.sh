#!/bin/bash

# define in your .ssh/config
SERVER=king1 

jar=build/libs/*.jar

case $SERVER in
a1)
	ENV=
	PREFIX="hdfs:///user/centos/"
	;;
king1)
	ENV="JAVA_HOME=/usr/java/jdk1.8.0_65/"
	PREFIX="hdfs:///user/adam.juraszek/"
	;;
esac

if [ $1 = 'deploy' ]; then
	scp $jar $SERVER:.
elif [ $1 = 'run' ]; then
	ssh $SERVER $ENV spark-submit --master yarn-client `basename "$jar"` "$PREFIX"
fi
