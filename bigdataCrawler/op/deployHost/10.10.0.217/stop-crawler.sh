pid=`ps -ef | grep "ps-web-crawler.jar crawler" | gawk '$0 !~/grep/ {print $2}' | tr -s '\n' ' '`
echo $pid
if [ "$pid" = "" ]
then
        echo "crawler not running"
else
        for id in $pid
        do
                kill -9 $id
                if [ "$?" = "0" ]
                then
                        echo "kill crawler $id success"
                fi
        done
fi
