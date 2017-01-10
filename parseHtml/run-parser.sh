#/bin/sh
BASE_PATH=/data0/bigdata/parser

#JVM基础参数
JAVA_OPTS="$JAVA_OPTS -server -Xms1500m -Xmx1500m -Xmn600m -XX:SurvivorRatio=1 -XX:PermSize=128m -XX:MaxPermSize=128m -Xss128k -XX:+HeapDumpOnOutOfMemoryError "


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

nohup java $JAVA_OPTS -Dhome.dir=${BASE_PATH} -classpath "$CLASSPATH" com.weibo.datasys.parser.main.Main > ${BASE_PATH}/nohup.out 2>&1 &
