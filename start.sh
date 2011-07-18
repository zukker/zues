#!/bin/bash
#CLASSPATH=./lib:lib/simple-log.jar:lib/lsdsoft.jar:lib/comm.jar:lib/maclaf.jar:lib/xercesImpl.jar:lib/xmlParserAPIs.jar:lib/zeus_res.jar:lib/jbcl.jar
#CMD="java -cp $CLASSPATH lsdsoft.zeus.Zeus"
CMD="java -jar lib/lsdsoft.jar"
echo ==== $CMD
$CMD
