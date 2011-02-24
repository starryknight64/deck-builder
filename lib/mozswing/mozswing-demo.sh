#!/usr/bin/env bash

OSNAME=`uname`

if [ "${OSNAME}" == "Linux" ]; then
  NATIVE_LIBS=linux
  if [ ! -d "${JAVA_HOME}" -o ! -f "${JAVA_HOME}/bin/java" ]; then
    echo "the JAVA_HOME variable is not set or invalid"
    exit 1
  fi
  _JAVACMD=$JAVA_HOME/bin/java
else if [ "${OSNAME}" == "SunOS" ]; then
  NATIVE_LIBS=solaris-x86
  _JAVACMD=java
else
  NATIVE_LIBS=macosx
  _JAVACMD=java
fi
fi

CURRDIR=$(cd $(dirname "$0") 2>/dev/null ; pwd)

if [ -d "${CURRDIR}/src" ]; then
  #running from source code
  CP=${CURRDIR}/bin:${CURRDIR}/build/classes:${CURRDIR}/build/dist/mozswing.jar
fi

CP=${CP}:${CURRDIR}/lib/commons-logging-1.1.jar:${CURRDIR}/lib/mozdom4java.jar:${CURRDIR}/lib/MozillaInterfaces-1.9.jar:${CURRDIR}/lib/MozillaGlue-1.9.jar:${CURRDIR}/lib/jna.jar:${CURRDIR}/lib/mozswing.jar
#echo ${CP}

exec "${_JAVACMD}" -cp ${CP} -Djava.library.path=${CURRDIR}/native/${NATIVE_LIBS} org.mozilla.browser.MozillaWindow

