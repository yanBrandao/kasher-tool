![kasher-icon](https://user-images.githubusercontent.com/5366951/120748039-7b90cf80-c4d8-11eb-9fbe-27b30f2fe379.png)
# kasher-tool
![dockerhub](https://img.shields.io/docker/cloud/build/yanbrandao/kasher-tool) ![docker pull](https://img.shields.io/docker/pulls/yanbrandao/kasher-tool)

Kasher is a handy kafka tool 

## Requirements

 - Docker Engine: **1.10.0+**

## Execution

In root folder, start kafka [Confluent docker-compose](https://docs.confluent.io/platform/current/quickstart/ce-docker-quickstart.html#step-1-download-and-start-cp-using-docker) with command below:

```
docker-compose up
```

Although kafka enviroment, there's a service to create sample topic named: `kasher-sample-topic`

Now it's possible to send message using kasher-tool image, using command below

```shell
docker run --network host --name kasher yanbrandao/kasher-tool
```

## Authors

Yan Tapajós - [@yanBrandao](https://github.com/yanBrandao)

William Cesar - [@WilliamCesar](https://github.com/WilliamCesarSantos)