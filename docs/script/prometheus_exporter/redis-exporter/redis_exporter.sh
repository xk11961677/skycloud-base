#!/bin/env bash

docker run -d --name redis_exporter \
-p 9121:9121 oliver006/redis_exporter --redis.addr redis://114.215.198.87:6380 --redis.password reachlife!@1116
