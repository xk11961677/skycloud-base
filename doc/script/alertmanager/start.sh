#!/bin/env bash

docker run -d -p 9093:9093 --name alertmanager -v /data/application/alertmanager/conf/config.yml:/etc/alertmanager/alertmanager.yml  docker.io/prom/alertmanager:latest
-net host --name grafana -e "GF_SERVER_ROOT_URL=http://127.0.0.1:3000" -e "GF_SECURITY_ADMIN_PASSWORD=sky123" grafana/grafana
