rm -rf ../src/*
svn co https://10.10.192.42/svn/searchdev/vertical/RT/crawler/trunk ../src --username zouyandi --password benqfp71gx@
rm -rf *
svn co https://10.10.192.42/svn/searchdev/vertical/RT/crawler/trunk/op . --username zouyandi --password benqfp71gx@
chmod 777 *.sh