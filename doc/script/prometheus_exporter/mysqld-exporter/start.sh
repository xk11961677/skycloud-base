#!/bin/env bash

docker run -d -p 9104:9104 --name mysqld-exporter -e DATA_SOURCE_NAME="dev:dev123@(127.0.0.1:3306)/" prom/mysqld-exporter
