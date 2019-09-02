#!/bin/bash
## Author LinkinStar

# solve the space by IFS
IFS=`echo -en "\n\b"`
echo -en $IFS

SHELL_FOLDER=$(cd "$(dirname "$0")";pwd)

echo ${SHELL_FOLDER}

dir=${SHELL_FOLDER}'/../../../'

if [ -f "${dir}CHANGELOG.md" ];then
    rm -f ${dir}CHANGELOG.md
    touch ${dir}CHANGELOG.md
else
    touch ${dir}CHANGELOG.md
fi

function printFeat(){
    for i in ${feat[@]}
    do
        echo "- "$i >> ${dir}CHANGELOG.md
    done
    echo >> ${dir}CHANGELOG.md
}

function printFix(){
    for i in ${fix[@]}
    do
        echo "- "$i >> ${dir}CHANGELOG.md
    done
    echo >> ${dir}CHANGELOG.md
}

function printOther(){
    for i in ${other[@]}
    do
        echo "- "$i >> ${dir}CHANGELOG.md
    done
    echo >> ${dir}CHANGELOG.md
}

function printDocs(){
  echo "document formats are not added to changelog"
}

function printStyle(){
  echo "style formats are not added to changelog"
}

function printRefactor(){
  for i in ${refactor[@]}
    do
        echo "- "$i >> ${dir}CHANGELOG.md
    done
    echo >> ${dir}CHANGELOG.md
}

function printTest(){
  echo "test formats are not added to changelog"
}

function printChore(){
  echo "chore formats are not added to changelog"
}

function checkLog(){
    if [[ $1 == "feat"* ]]
    then
        feat[featIndex]=$1
        let featIndex++
    elif [[ $1 == "fix"* ]]
    then
        fix[fixIndex]=$1
        let fixIndex++
    elif [[ $1 == "docs"* ]]
    then
        docs[docsIndex]=$1
        let docsIndex++
    elif [[ $1 == "style"* ]]
    then
        style[styleIndex]=$1
        let styleIndex++
    elif [[ $1 == "refactor"* ]]
    then
        refactor[refactorIndex]=$1
        let refactorIndex++
    elif [[ $1 == "test"* ]]
    then
        test[testIndex]=$1
        let testIndex++
    elif [[ $1 == "chore"* ]]
    then
        test[choreIndex]=$1
        let choreIndex++
    else
        other[otherIndex]=$1
        let otherIndex++
    fi
}

function printLog(){
    if [[ $featIndex -ne 0 ]]; then
        echo "### Features" >> ${dir}CHANGELOG.md
        printFeat
    fi

    if [[ $fixIndex -ne 0 ]]; then
        echo "### Bug Fixes" >> ${dir}CHANGELOG.md
        printFix
    fi

    if [[ docsIndex -ne 0 ]]; then
        printDocs
    fi

    if [[ styleIndex -ne 0 ]]; then
        printDocs
    fi

    if [[ refactorIndex -ne 0 ]]; then
        printDocs
    fi

    if [[ testIndex -ne 0 ]]; then
        printDocs
    fi

    if [[ choreIndex -ne 0 ]]; then
        printDocs
    fi

    if [[ $otherIndex -ne 0 ]]; then
        echo "### Other Changes" >> ${dir}CHANGELOG.md
        printOther
    fi

    feat=()
    featIndex=0

    fix=()
    fixIndex=0

    docs=()
    docsIndex=0

    style=()
    styleIndex=0

    refactor=()
    refactorIndex=0

    test=()
    testIndex=0

    chore=()
    choreIndex=0

    other=()
    otherIndex=0
}

curDate=""
function checkDate()
{
    if [[ $curDate = $1 ]]; then
        return
    fi
    curDate=$1

    printLog

    echo >> ${dir}CHANGELOG.md
    echo "## "$curDate >> ${dir}CHANGELOG.md
}

commitMessageList=`git log --after="2019-09-01" --date=format:'%Y-%m-%d' --pretty=format:'%cd%n%s'`

index=0

for i in ${commitMessageList[@]}
do
    if [[ $index%2 -eq 0 ]]
    then
        checkDate $i
    else
        #echo "- "$i >> CHANGELOG.md
        checkLog $i
    fi

    let index++
done

printLog

