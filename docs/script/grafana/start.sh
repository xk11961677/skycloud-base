#!/bin/env bash

docker run -d --name grafana -p 3000:3000 -e "GF_SECURITY_ADMIN_PASSWORD=sky123" -v /data/application/grafana/data:/var/lib/grafana grafana/grafana
