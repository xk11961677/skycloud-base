#!/bin/env bash

docker run --name prometheus -p 9090:9090 -d -e TZ="Asia/Shanghai" -v /data/application/prometheus/data:/prometheus/data -v /etc/localtime:/etc/localtime -v /data/application/prometheus/conf:/prometheus/conf --privileged=true prom/prometheus --config.file=/prometheus/conf/prometheus.yml

#docker run --name prometheus -p 9090:9090 -d --net host -e TZ="Asia/Shanghai" -v /etc/localtime:/etc/localtime  -v /data/application/prometheus/data:/prometheus-data prom/prometheus --config.file=/prometheus-data/prometheus.yml
