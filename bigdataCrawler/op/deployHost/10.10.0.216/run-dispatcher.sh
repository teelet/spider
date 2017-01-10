#/bin/sh
IP=10.10.0.216
BASE_PATH=/application/search/ps-web-crawler/homedispatcher

#JVM基础参数
JAVA_OPTS="$JAVA_OPTS -server -Xms15000m -Xmx15000m -Xmn9000m -XX:SurvivorRatio=1 -XX:PermSize=128m -XX:MaxPermSize=128m -Xss128k -XX:+HeapDumpOnOutOfMemoryError "


#GC行为参数
JAVA_OPTS="$JAVA_OPTS -XX:+UseParNewGC -XX:ParallelGCThreads=8 -XX:+UseConcMarkSweepGC -XX:ParallelCMSThreads=8 -XX:+CMSParallelRemarkEnabled -XX:+CMSClassUnloadingEnabled -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=0 -XX:CMSMaxAbortablePrecleanTime=500 -XX:GCTimeRatio=19 -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:SoftRefLRUPolicyMSPerMB=0 -XX:MaxTenuringThreshold=20 -XX:+DisableExplicitGC"

#GC调试时打开
JAVA_OPTS="$JAVA_OPTS -Xloggc:gc.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintTenuringDistribution -XX:+PrintCMSInitiationStatistics -XX:+PrintGCApplicationStoppedTime -XX:+PrintHeapAtGC"


rm -rf ${BASE_PATH}/nohup.out ${BASE_PATH}/logs

CLASSPATH=.;
# add libs to CLASSPATH
for f in ./*.jar; do
  CLASSPATH=${CLASSPATH}:$f;
done
for f in lib/*.jar; do
  CLASSPATH=${CLASSPATH}:$f;
done

nohup java $JAVA_OPTS -Dhome.dir=${BASE_PATH} -classpath "$CLASSPATH" com.panguso.ps.crawler.appMain.Main dispatcher ${IP} > ${BASE_PATH}/nohup.out 2>&1 &
