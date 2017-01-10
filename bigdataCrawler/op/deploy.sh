#!/bin/bash
echo -e 'deploy start...\n'

chmod 777 *.sh

sh complie.sh

deployPath="/application/search/ps-web-crawler"
echo -e "deployPath=$deployPath\n"

for host in `cat deploylist`;
do    
  echo deploying host: $host
  ssh $host "mkdir -p $deployPath" &
  deployList=" target/ps-web-crawler-1.0.0.jar target/lib ../op/deployHost/"$host"/*"
  cd ../src
  rsync -a -e ssh --delete --exclude=.svn $deployList $host:$deployPath
  ssh $host "chmod 777 $deployPath/*.sh" &
  echo -e  "deploy host: $host ok.\n";
done

echo deploy all ok.
