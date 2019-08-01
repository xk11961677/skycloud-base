#!/bin/env bash

docker run -d -p 8060:8060 --name dingtalk timonwong/prometheus-webhook-dingtalk:v0.3.0 --ding.profile="webhook1=https://oapi.dingtalk.com/robot/send?access_token=1fce6f30df64b818bdbe3bd2a11d2b8cc556652c1c004c03540d6268be5a1a8b"
127.0.0.1:3000" -e "GF_SECURITY_ADMIN_PASSWORD=sky123" grafana/grafana
