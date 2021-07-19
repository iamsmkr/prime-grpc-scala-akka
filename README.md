# prime-grpc-scala-akka

## Install Dependencies
1. Install sbt
2. Install docker
3. Install minikube/kubectl
```sh
$ ./deploy/scripts/setup-minikube-for-linux.sh
```

## Build/Publish Docker Images
```sh
$ sbt prime-generator/docker:publishLocal
$ sbt prime-proxy/docker:publishLocal
```

**Note**: Make sure to point local docker daemon to minikube internal docker registry to make the docker images available inside minikube cluster. Use following command.
```sh
$ eval $(minikube -p minikube docker-env)
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
