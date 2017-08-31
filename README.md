# openpaas-mailets-jenkins

DOCKER_HOST=tcp://127.0.0.1:2376 docker rm jks
DOCKER_HOST=tcp://127.0.0.1:2376 docker build -t jks .
DOCKER_HOST=tcp://127.0.0.1:2376 docker run --env-file=./env.file -p 8080:8080 --name=jks jks
