#!/bin/bash

echo -e "start all crawlers...\n"

deployPath="/application/search/searcher/ps-web-crawler"

echo -e "deployPath=$deployPath\n"

for host in `ls -l deployHost | awk '{print $9}'`;
do

echo -e "starting host: $host...\n"

ssh $host "cd $deployPath; sh run-crawler.sh ;" &
sleep 2

echo -e "start host: $host ok.\n"

done

echo -e "start all crawlers ok.\n"
