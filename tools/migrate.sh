#!/bin/bash

if [ "$1" != "" ]; then
    echo "Migrating $1"
    git remote add -f $1 https://github.com/navikt/$1.git
    git merge -s ours --no-commit $1/master --allow-unrelated-histories
    git read-tree --prefix=apps/$1/ -u $1/master
    git commit -m "Migrering av $1 inn i testnorge"
else
    echo "Missing reposistory name as first argument argument."
fi