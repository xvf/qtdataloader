#!/bin/bash

DATADIR='/var/lib/mysql'
tempSqlFile='/tmp/mysql-first-time.sql'


cat "$MYSQL_SCRIPT" >> "$tempSqlFile"

echo $tempSqlFile

chown -R mysql:mysql "$DATADIR"

service mysql start
mysql -uroot -proot < "$tempSqlFile"
service mysql stop

mysqld -u root