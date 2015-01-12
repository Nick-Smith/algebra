#!/usr/bin/env bash

LOGSTASH_VERSION=1.4.0
GOR_VERSION=0.8

SBT_OPTIONS="-Xmx1G \
    -XX:MaxPermSize=250m \
    -XX:+UseCompressedOops \
    -Dsbt.log.noformat=true \
    -Dbuild.number=$BUILD_NUMBER \
    -Dbuild.vcs.number=$BUILD_VCS_NUMBER"

[ -d target ] && rm -rf target
mkdir target
cd $(dirname $0)/target

mkdir downloads
mkdir -p packages/concierge

# concierge
if [ -z "$JDK_HOME" ]; then
    JAVA=java
else
    JAVA=$JDK_HOME/bin/java 
fi

if cd .. && $JAVA $SBT_OPTIONS -jar sbt-launch.jar assembly && cd target
then
    cp scala-*/*.jar downloads/concierge.jar
    cp ../concierge.conf downloads
else
    echo 'Failed to build Concierge'
    exit 1
fi

# logstash
if wget -O downloads/logstash.tar.gz https://download.elasticsearch.org/logstash/logstash/logstash-$LOGSTASH_VERSION.tar.gz
then
    tar xfv downloads/logstash.tar.gz -C downloads
    mv downloads/logstash-* downloads/logstash
    cp ../logstash-shipper.conf downloads
    cp ../logstash.conf downloads
else
    echo 'Failed to download Logstash'
    exit 1
fi

# gor
if wget -O downloads/gor.tar.gz  https://github.com/buger/gor/releases/download/$GOR_VERSION/gor_x64.tar.gz
then
    tar xzf downloads/gor.tar.gz -C downloads
    cp ../gor downloads
    cp ../gor.conf downloads
else
    echo 'Failed to download Gor'
    exit 1
fi

tar czfv packages/concierge/concierge.tar.gz -C downloads concierge.jar concierge.conf logstash logstash-shipper.conf logstash.conf gor gor.conf
cp ../src/main/deploy/deploy.json .
zip -rv artifacts.zip packages/ deploy.json

echo "##teamcity[publishArtifacts '$(pwd)/artifacts.zip => .']"
