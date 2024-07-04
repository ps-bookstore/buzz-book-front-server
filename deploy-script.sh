#!/bin/bash

APP_NAME=front-1
DOCKER_REPO=heldenar
VERSION=latest

# 새로운 컨테이너 실행
docker run -d --name ${APP_NAME}-${VERSION} ${DOCKER_REPO}/${APP_NAME}:${VERSION}

# 새로운 컨테이너의 상태 확인
NEW_CONTAINER_STATUS=$(docker inspect -f '{{.State.Status}}' ${APP_NAME}-${VERSION})

if [ "$NEW_CONTAINER_STATUS" == "running" ]; then
  # 새로운 컨테이너가 실행 중이면 기존 컨테이너 중지 및 제거
  docker stop ${APP_NAME}-old
  docker rm ${APP_NAME}-old

  # 현재 컨테이너를 'old'로 이름 변경
  docker rename ${APP_NAME} ${APP_NAME}-old

  # 새로운 컨테이너를 현재 컨테이너 이름으로 변경
  docker rename ${APP_NAME}-${VERSION} ${APP_NAME}

  # Eureka 서버에서 서비스 등록 및 제거를 통해 무중단 배포 처리
  # Eureka 서버와 통신하여 상태 업데이트
  curl -X PUT http://eureka-server:8761/eureka/apps/${APP_NAME}/${INSTANCE_ID}/status?value=UP
else
  echo "New container failed to start, rolling back."
  docker rm ${APP_NAME}-${VERSION}
fi
