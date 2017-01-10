#!/bin/bash

echo -e "stop all dispatchers...\n"

deployPath="/application/search/searcher/ps-web-crawler"

echo -e "deployPath=$deployPath\n"

for host in `ls -l deployHost | awk '{print $9}'`;
do

echo -e "stopping host: $host...\n"

ssh $host "cd $deployPath; sh stop-dispatcher.sh ;" &
sleep 2

echo -e "stop host: $host ok.\n"

done

echo -e "stop all dispatchers ok.\n"
