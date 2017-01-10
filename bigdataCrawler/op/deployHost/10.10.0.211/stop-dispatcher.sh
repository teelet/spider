pid=`ps -ef | grep "com.panguso.ps.crawler.appMain.Main dispatcher" | gawk '$0 !~/grep/ {print $2}' | tr -s '\n' ' '`
echo $pid
if [ "$pid" = "" ]
then
        echo "dispatcher not running"
else
        for id in $pid
        do
                kill -9 $id
                if [ "$?" = "0" ]
                then
                        echo "kill dispatcher $id success"
                fi
        done
fi
