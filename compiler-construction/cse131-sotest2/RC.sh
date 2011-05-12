#!/bin/bash

# We use this shell script "RC.sh" to run the main Java class.  This 
# shell script is copied to a file called "RC" which is then named the
# same as the C++ executables.  The "RC.sh" file is used in case a grader
# deleted the "RC" file thinking it was an executable.

dir=`dirname $0`

if [[ -n `uname | grep NT` ]]
then
    dirsep="\\"
    pathsep=";"
else
    dirsep="/"
    pathsep=":"
fi

cp="$CLASSPATH${pathsep}${dir}${dirsep}java-cup-v11a.jar${pathsep}${dir}${pathsep}${dir}${dirsep}bin${pathsep}${dir}${dirsep}src"

java -cp "$cp" RC $*
