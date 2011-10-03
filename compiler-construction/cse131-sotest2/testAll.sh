#!/bin/bash

# Variables meant to be overridden by the user:
if [[ -z $RCC ]]; then RCC=../RC.sh; fi
if [[ -z $CC ]]; then CC=cc; fi
if [[ -z $CFLAGS ]]; then CFLAGS=; fi

# Internals:
red="\033[1;31m"
green="\033[1;32m"
blue="\033[1;34m"
white="\033[1m"
clear="\033[0m"

new="[ ${blue}NEW ${clear} ]"
pass="[ ${green}PASS${clear} ]"
fail="[ ${red}FAIL${clear} ]"

cd `dirname $0`

if [[ -x /software/common/gnu/bin/gdiff ]]; then
    differ=/software/common/gnu/bin/gdiff
else
    differ=diff
fi

if [[ -n $1 ]]; then
    tests=`find "$@" -name '*.rc' | sort`
else
    tests=`find */ -name '*.rc' | sort`
fi

dirs=`echo "$tests" | cut -d/ -f1 | sort | uniq`

# The assembly file which we expect RC.sh to generate:
asm=rc.s

for d in $dirs; do
    rm -f "$d/pass.lst" "$d/fail.lst"
    touch "$d/pass.lst" "$d/fail.lst"
done

# Iterate over all files and run each:
for f in $tests; do
    c="`dirname $f`/`basename $f .rc`.c"
    cflags="`dirname $f`/`basename $f .rc`.cflags"
    in="`dirname $f`/`basename $f .rc`.in"
    my_linkerr="`dirname $f`/`basename $f .rc`.my.linkerr"
    ans_linkerr="`dirname $f`/`basename $f .rc`.ans.linkerr"
    my_out="`dirname $f`/`basename $f .rc`.my.out"
    ans_out="`dirname $f`/`basename $f .rc`.ans.out"
    my_asm="`dirname $f`/`basename $f .rc`.s"
    my_exe="`dirname $f`/`basename $f .rc`.exe.out"
    pass_lst="`dirname $f`/pass.lst"
    fail_lst="`dirname $f`/fail.lst"

    rm -f $asm
    err=$($RCC $f 2>&1)
    match=`grep 'Compile: success.' <<EOF
$err
EOF`
    if [[ -z $match ]]; then
	echo -en $fail
        echo " $f"
        echo "RC.sh failed to compile the source file."
        echo "$err"
        echo "$f" >> $fail_lst
        continue
    elif [[ ! -e $asm ]]; then
        echo -en $fail
        echo " $f"
        echo "RC.sh did not produce an assembly file."
        echo "$err"
        echo "$f" >> $fail_lst
        continue
    fi
    cp $asm $my_asm

    my_cflags=$CFLAGS
    if [[ -e $cflags ]]; then
        my_cflags="$my_cflags `cat $cflags`"
    fi
    if [[ -e $c ]]; then
        my_cflags="$my_cflags $c"
    fi

    $CC $my_cflags $asm ../input.c ../output.s -o $my_exe &>$my_linkerr
    if [[ ! -x $my_exe && -e $ans_linkerr ]]; then
        diff=$($differ -u $ans_linkerr $my_linkerr)
        if [[ -z $diff ]]; then
            msg=$pass
            echo "$f" >> $pass_lst
	else
	    msg=$fail
            echo "$f" >> $fail_lst
	fi
        echo -en $msg
        echo " $f"
        if [[ -n $diff ]]; then echo "$diff"; fi
        continue
    elif [[ -e $ans_linkerr ]]; then
        echo -en $fail
        echo " $f"
        echo "CC ran successfully, but a linker error was expected."
        echo "$f" >> $fail_lst
        continue
    elif [[ ! -x $my_exe ]]; then
        echo -en $fail
        echo " $f"
        echo "CC did not produce an executable file."
        cat $my_linkerr
        echo "$f" >> $fail_lst
        continue
    fi

    if [[ -e $in ]]; then
	err=$($my_exe < $in 3>&1 1>$my_out 2>&3)
    else
	err=$($my_exe 3>&1 1>$my_out 2>&3)
    fi
    dos2unix $my_out &> /dev/null
    if [[ -e $ans_out ]]; then
        diff=$($differ -u $ans_out $my_out)
        if [[ -z $diff ]]; then
            msg=$pass
            echo "$f" >> $pass_lst
	else
	    msg=$fail
            echo "$f" >> $fail_lst
	fi
    else
	mv $my_out $ans_out
	diff=$(<$ans_out)
	msg=$new
    fi
    echo -en $msg
    echo " $f"
    if [[ -n $err ]]; then echo "$err"; fi
    if [[ -n $diff ]]; then echo "$diff"; fi
done

total_all=0
total_passed=0

echo
echo "SOtest2 Results Summary:"
echo

for d in $dirs; do
    passed=`cat "$d/pass.lst" | wc -l | tr -d '[:space:]'`
    failed=`cat "$d/fail.lst" | wc -l | tr -d '[:space:]'`

    let all=passed+failed
    let total_all=total_all+all
    let total_passed=total_passed+passed
    percent=`printf "%.2f" \`echo 100*$passed/$all | bc -l\``

    echo -e "    $d:\t${white}$passed${clear}/${white}$all${clear}\t(${white}$percent%${clear})"
done

percent=`printf "%.2f" \`echo 100*$total_passed/$total_all | bc -l\``
echo
echo -e "Passed ${white}$total_passed${clear} out of ${white}$total_all${clear} total tests (${white}$percent%${clear})."

echo
echo "To view any missed tests, either scroll up to view the output, or check the"
echo "fail.lst file in each directory for a list of which tests failed."

echo
echo "To rerun a test or set of tests, tell testAll.sh which tests to run:"
echo "    ./testAll.sh phaseN"
echo "    ./testAll.sh phaseN/testName.rc"

echo
echo "Disclaimers:"
echo -e "    Please ${white}DO NOT${clear} assume that these results have any bearing on your score."
echo -e "    Please ${white}DO NOT${clear} assume that this test suite is complete, or correct."
echo -e "    Please ${white}DO${clear} write your own tests."
echo
echo "Thanks for using SOtest2. If you liked it, please submit your testcases"
echo "for inclusion in the SOtest2 test suite."