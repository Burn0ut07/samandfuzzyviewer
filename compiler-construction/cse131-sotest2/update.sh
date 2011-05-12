#!/bin/bash                                                                     
cd `dirname $0`

echo "Attempting to update SOtest2"

which hg &> /dev/null
if [[ `echo $?` == 0 ]]; then
    echo "Using Mercurial"
    hg pull
    hg update
else
    echo "Downloading Tarball"
    wget http://bitbucket.org/elliottslaughter/cse131-sotest2/get/tip.tar.gz
    tar xfz tip.tar.gz
    cp -r cse131-sotest2/* .
    rm -rf cse131-sotest2 tip.tar.gz
fi
