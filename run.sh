#!/bin/bash
JAVA_OPTS="-Xmx5G -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=5G -Xss2M -Duser.timezone=GMT"
./target/universal/stage/bin/corp_act_adj_ratio_calcr /home/qy/Dropbox/dataENF/blmg/d1_adj_hkstk.csv /home/qy/Dropbox/dataENF/blmg/d1_unadj_hkstk.csv /home/qy/Dropbox/nirvana/sbtProj/corpActAdjRatioCalcr/corpActRatio.csv

