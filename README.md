# prime-grpc-scala-akka

Head to [Wiki](https://github.com/iamsmkr/prime-grpc-scala-akka/wiki) for detailed design docs.

## Install Dependencies
1. Install SBT 

    On linux/windows based systems, you can use [SDKMAN](https://sdkman.io/) software development kit manager for convenient installation and usage of multiple versions.

2. Install Docker

    Follow docker installation guide [here](https://docs.docker.com/engine/install/).

3. Install Minikube/Kubectl

    Use following convenience script to install both minikube and kubectl.
      ```sh
      $ ./deploy/scripts/setup-minikube-for-linux.sh
      ```

## Test
#### 1. Unit Tests
```sh
$ sbt prime-generator/test
$ sbt prime-proxy/test
$ sbt test
```

<br/>

#### 2. Integration Tests
The end-to-end integration tests are available under `prime-proxy/src/it`.
```sh
$ sbt it:test
```

**Note**: While running end to end integration tests make sure to export minikube ip address as environment variable `PRIME_PROXY_INTERFACE` as shown below:
```
$ export PRIME_PROXY_INTERFACE=$(minikube ip)
```

## Build
#### 1. Publish Protobuf Sources
Protobuf files are maintained in a separate sbt project to avoid maintaining them in both client and server.
```sh
$ sbt prime-protobuf/+publishLocal
```

**Note**: The project can be cross-compiled to multiple Scala versions. Should you choose to compile it into a Scala version of your choice, don't forget to update `build.sbt` with desired Scala version.

</br>

#### 2. Publish Docker Images
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
The prime number generator service is available over following REST APIs.

#### 1. Comma-Separated Stream
The following API returns a stream of comma-separated prime numbers up until a given number.
```sh
$ curl $(minikube ip)/prime/23
```

**Note**: This is achieved by making use of source streaming support in akka http. See [docs](https://docs.huihoo.com/akka/akka-http/10.0.7/scala/http/routing-dsl/source-streaming-support.html#simple-csv-streaming-example). The implementation makes use of custom `CsvEntityStreamingSupport`.

<br/>

#### 2. Comma-Separated Seq
The following API returns a string of comma-separated list of prime numbers up until a given number.
```sh
$ curl $(minikube ip)/prime/23/seq
```

**Note**: There is a hard-limit of `10000` prime numbers is set to avoid OOM.

</br>

#### 3. Newline-Separated Stream 
The following API returns a stream of prime numbers up until a given number separated by new line.
```sh
$ curl $(minikube ip)/prime/23/csv-stream
```

**Note**: This is achieved by making use of source streaming support in akka http. See [docs](https://docs.huihoo.com/akka/akka-http/10.0.7/scala/http/routing-dsl/source-streaming-support.html#simple-csv-streaming-example). The implementation makes use of default `CsvEntityStreamingSupport`.

<br/>

#### 4. SeverSentEvents
An alternative API returns prime numbers as SSE events. There is no hard limit set for this API.
```sh
$ curl $(minikube ip)/prime/23/sse
```
