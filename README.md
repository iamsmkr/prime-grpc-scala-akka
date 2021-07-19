# prime-grpc-scala-akka

## Install Dependencies
1. Install docker
2. Install minikube
```sh
$ ./deploy/scripts/setup-minikube-for-linux.sh
```

## Build/Publish Docker Images
```sh
$ sbt prime-generator/docker:publishLocal
$ sbt prime-proxy/docker:publishLocal
```

## Deploy
```sh
$ kubectl apply -f deploy/k8s/prime-generator.yml
$ kubectl apply -f deploy/k8s/prime-proxy.yml
```

## Usage
```sh
$ curl --header 'Host: primeservice.com' $(sudo -E minikube ip)/prime/23
```
