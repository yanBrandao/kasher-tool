
![kasher-icon](https://user-images.githubusercontent.com/5366951/120748039-7b90cf80-c4d8-11eb-9fbe-27b30f2fe379.png)
# kasher-tool
![dockerhub](https://img.shields.io/docker/cloud/build/yanbrandao/kasher-tool) ![docker pull](https://img.shields.io/docker/pulls/yanbrandao/kasher-tool) [![Coverage Status](https://coveralls.io/repos/github/yanBrandao/kasher-tool/badge.svg?branch=main)](https://coveralls.io/github/yanBrandao/kasher-tool?branch=main)

Kasher is a handy kafka tool 

## Requirements

 - Docker Engine: **1.10.0+**

## Execution

In root folder, start kafka [Confluent docker-compose](https://docs.confluent.io/platform/current/quickstart/ce-docker-quickstart.html#step-1-download-and-start-cp-using-docker) with command below:

```
docker-compose up
```

Although kafka enviroment, there's a service to create sample topic named: `kasher-sample-topic`

When Control-Center with Kafka finish his running, It's possible to build kasher image, for this just do one of two commands below to get our image:

 - Pull image from docker hub:
```shell
docker pull yanbrandao/kasher-tool
```
 - Build with source files:
```shell
docker build -t kasher-tool .
```

Now it's possible to send message using kasher-tool image. The message can be sent by command line or Rest API.

Using command line below
```docker
docker run --rm --network host --name kasher kasher-tool -b localhost:9092 -c uahe -t kasher-sample-topic -d "just a message test"
```

For use Rest API needed to set mode REST by environment variables
```docker
docker run --env MODE=REST --expose 8888 --rm --network host --name kasher kasher-tool"
```
Using Rest API by url http://localhost:8888/api/{topic-name}/producer 
```shell
curl --location --request POST 'http://localhost:8888/api/kasher-sample-topic/producer' --header 'Content-Type: application/json' --data-raw '{
    "broker": "localhost:9092",
    "data" : "{\"message\" : \"data_one\"}",
    "headers": [{"name": "value", "value": "value"}]
}'
```
For more details about API check the API.yml file

> ⚠️ Remember that if you running Kafka local and kasher-tool isn't in compose, you need to use network host to connect to Kafka Broker.

## Authors

Yan Tapajós - [@yanBrandao](https://github.com/yanBrandao)

William Cesar - [@WilliamCesar](https://github.com/WilliamCesarSantos)