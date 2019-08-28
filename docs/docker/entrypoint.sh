#!/bin/bash
echo "CONFIG_ENV:{} $CONFIG_ENV"
echo "JAVA_OPTS:{} $JAVA_OPTS"

case $CONFIG_ENV in
"dev")
echo "dev environment"
java -jar $JAVA_OPTS -Denv=$CONFIG_ENV app.jar
;;

"fat")
echo "fat environment"
java -jar $JAVA_OPTS -Denv=$CONFIG_ENV app.jar
;;

"uat")
echo "uat environment"
java -jar $JAVA_OPTS -Denv=$CONFIG_ENV app.jar
;;

"pro")
echo "pro environment"
java -jar $JAVA_OPTS -Denv=$CONFIG_ENV app.jar
;;
esac
