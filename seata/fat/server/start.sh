#!/bin/env bash

sh /data/application/seata-server/bin/seata-server.sh -p 8092 -m db >/dev/null 2>&1 &
