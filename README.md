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
#### Comma-separated list
The following API returns a string of comma-separated list of prime numbers up until a given number.
```sh
$ curl --header 'Host: primeservice.com' $(minikube ip)/prime/23
```

**Note**: There is a hard-limit of `10000` prime numbers is set to avoid stackoverflow.

#### SeverSentEvents
An alternative API returns prime numbers as SSE events. There is no hard limit set for this API.
```sh
$ curl --header 'Host: primeservice.com' $(minikube ip)/prime/23/sse
```
