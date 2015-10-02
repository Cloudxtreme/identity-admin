#!/bin/bash

source set-env.sh

adduser --home /$apptag --disabled-password --gecos \"\" $apptag

aws s3 cp s3://gu-$apptag-dist/$apptag-upstart.conf /etc/init/$apptag.conf
aws s3 cp s3://gu-$apptag-dist/$stacktag/$stagetag/$apptag/$apptag-1.0.0-SNAPSHOT.tgz /$apptag/$apptag.tar.gz
aws s3 cp s3://gu-$apptag-private/$stagetag/$apptag.conf /etc/gu/$apptag.conf

tar -xvzf /$apptag/$apptag.tar.gz -C /$apptag

chown -R $apptag /$apptag
sed -i "s/<APP>/$apptag/g" /etc/init/$apptag.conf
sed -i "s/<Stage>/$stagetag/g" /etc/init/$apptag.conf
