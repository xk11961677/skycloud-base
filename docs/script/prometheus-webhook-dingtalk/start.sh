#!/bin/env bash

docker run -d -p 8060:8060 --name dingtalk timonwong/prometheus-webhook-dingtalk:v0.3.0 --ding.profile="webhook1=https://oapi.dingtalk.com/robot/send?access_token=xxxxx"
