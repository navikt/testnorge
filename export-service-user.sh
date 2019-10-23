#!/usr/bin/env sh

if test -f /secrets/serviceuser/srvtestnorge-aareg/username;
then
    export testnorge-aareg_srvuser=$(cat /secrets/serviceuser/srvtestnorge-aareg/username)
fi

if test -f /secrets/serviceuser/srvtestnorge-aareg/password;
then
    export testnorge-aareg_srvpwd=$(cat /secrets/serviceuser/srvtestnorge-aareg/password)
fi

if [ -z "$testnorge-aareg_srvuser" ] ;
then
    printf "testnorge-aareg_srvuser er ikke satt \n";
else
    printf "testnorge-aareg_srvuser er ${testnorge-aareg_srvuser} \n" ;
fi

if [ -z "$testnorge-aareg_srvpwd" ] ;
then
    printf "testnorge-aareg_srvpwd er ikke satt \n";
else
    printf "testnorge-aareg_srvpwd er satt \n" ;
fi