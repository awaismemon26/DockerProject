# Docker Containers and Orchestration - SOA Assignment 02 Summer Term 2020

In assignment 2, we package your application into containers and run these containers.

In the first task, we make use of docker-compose with runs containers on a single machine.
In the second task, we use kubernetes, a container orchestrator, which can be used to deploy docker container across multiple machines.

## 1. Using Docker Compose

To start the project using docker-compose, go the `Assignment_2` directory and run `docker-compose up`.
This command shall build all containers automatically.

Once the container are running, following services are accessible
- beverage-service: `localhost:9998`
- management-service: `localhost:9997`

The database-handler is hidden and not exposed using a port on purpose.

We can verify the functionality of the `beverage-service` and `mangement-service` using `curl.`

Sidenote: `jq` is a useful little program which can filter and format json input.

```
$ curl http://localhost:9998/v1/customer/bottles/15 | jq
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   185  100   185    0     0    260      0 --:--:-- --:--:-- --:--:--   260
{
  "id": 15,
  "name": "Orange",
  "volume": 0.5,
  "isAlcoholic": false,
  "volumePercent": 0,
  "price": 1.99,
  "supplier": "Juice Factory",
  "inStock": 55,
  "href": "http://localhost:9998/v1/customer/bottles/15"
}
```

and

```
$ curl http://localhost:9997/v1/employee/bottles/15 | jq
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   185  100   185    0     0    436      0 --:--:-- --:--:-- --:--:--   436
{
  "id": 15,
  "name": "Orange",
  "volume": 0.5,
  "isAlcoholic": false,
  "volumePercent": 0,
  "price": 1.99,
  "supplier": "Juice Factory",
  "inStock": 55,
  "href": "http://localhost:9997/v1/employee/bottles/15"
}
```

If one would want to expose the database-handler, then adjust the docker-compose:

Replace

```
  db:
    build:
      context: database_handler
    container_name: database_handler
    volumes:
      - ./volumes:/app/files
    stdin_open: true
```

with

```
  db:
    build:
      context: database_handler
    container_name: database_handler
    ports:
      - 9999:9999
    volumes:
      - ./volumes:/app/files
    stdin_open: true
```

then, the database-handler shall be accessible at `localhost:9999`.

Inside the docker network, the `database-handler` is reachable under `db:9999` because docker-compose's DNS server allows to use the service name as URL.

That way, by calling the the database-handler service `db`, see

```
  db:
    build:
      context: database_handler
    container_name: database_handler
    [...]
```

we can re-use the name `db` as URL in the other services in the environment variables, see

```
  beverage_service:
    [...]
    environment:
      - DB_HOST=db
      - DB_PORT=9999
    [...]
  management_service:
    [...]
    environment:
      - DB_HOST=db
      - DB_PORT=9999
    [...]
```

For persistent storage in docker, we make usage of volumes in the `database-handler`.

```
  db:
    [...]
    volumes:
      - ./volumes:/app/files
    [...]
```

This configuration tells docker to map the `./volumes` directory on the host to the `/app/files` directory in the container.
So once the container stops, the files are still persistently stored in the host system in the `./volumes` directory.
When the `database-handler` docker container restarts, it can re-use the files in this directory and regain its old state.

## 2. Using Kubernetes

In task 2, we ran into issues regarding the DNS resolver of minikube, which will be further explained in section 2.2.

So we have two different solutions, one which deploys all containers to the same pod which doesn't make use of DNS, described in section 2.3.
The second version is able to distribute the services across multiple machines, however requires a little bit of manual effort which is described in section 2.4.

### 2.1 Building the docker images

First of all, we need to build the docker images of each service and tag them accordingly.

Assuming, you are in the `Assignment_2` directory, run following commands to build and tag each of the three containers.

```
cd beverage_service
docker build -t grpsvntn/bs -f Dockerfile .
cd ..

cd management_service
docker build -t grpsvntn/ms -f Dockerfile .
cd ..

cd database_handler
docker build -t grpsvntn/dh -f Dockerfile .
cd ..
```

### 2.2 The DNS problem

According to the tutorial from https://github.com/johannes-manner/k8s-soa-example, minikube shall have a running DNS server which is able to resolve DNS names like `minikube`.

Taken from the example:

```
$ http://minikube:31916/cats          DNS resolution, minikube is the node name.
$ http://MACHINE-IP:31916/cats        Via the $ service --url command.
$ minikube ssh
  $ curl 172.17.0.5:9999/cats         Port on the pod.
  $ curl 10.99.220.194:8989/cats      Service IP and mapped port.
  $ curl minikube:31916/cats          DNS resolution, minikube is the node name.
```

in our case, `minikube` could not get resolved and the curl requests `http://minikube:31916/cats` and `curl minikube:31916/cats` did not work, however the other calls worked.

### 2.3 The One-Pod-For-All Solution

The first solution bypasses the DNS problem by deploying everything in one deployment, as it can be seen in the `k8s` directory, assuming you are in the `Assignment_2` directory.
This implies that all services are run on the same machine and cannot be distributed across multiple machines.

In the `k8s` directory, we have three files. The first file is called `deployment.yaml` which is used to deploy all three containers on the same pod.
The second file `services.yaml` is used to provide a public endpoint to the `beverage-service` and `mangement-service`. It does not expose the `database-handler` on purpose, but we will explain how to expose it as well.
The third file `storage.yaml` is used for providing persistent storage for the database files.

To run this solution, simply start the kubernetes cluster using `kubectl apply -f k8s`.

This solution starts all three built containers on the same pod, hence these services can reach each other using `localhost`, which does not make use of DNS.

This solution only exposes the `beverage-service` and `management-service` and hides the `database-handler`.

If one wants to access the `database-handler` as well, adapt the `k8s/services.yaml` and append following code at the bottom of the file:

```
---
apiVersion: v1
kind: Service
metadata:
  name: database-handler-service
spec:
  ports:
    - port: 2600
      targetPort: 9999
      protocol: TCP
  type: NodePort
  selector:
    app: beerz
```

One can get the URL of the services using following commands
```
$ minikube service beverage-service --url
http://172.17.0.2:30403
$ minikube service management-service --url
http://172.17.0.2:31715
```

then, using `curl` one can simply verify if the services are running successfull.

```
$ curl http://172.17.0.2:30403/v1/customer/bottles/15 | jq
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   185  100   185    0     0  12333      0 --:--:-- --:--:-- --:--:-- 12333
{
  "id": 15,
  "name": "Orange",
  "volume": 0.5,
  "isAlcoholic": false,
  "volumePercent": 0,
  "price": 1.99,
  "supplier": "Juice Factory",
  "inStock": 55,
  "href": "http://172.17.0.2:30403/v1/customer/bottles/15"
}
```

Sidenote: Using a bash terminal, one can make use of command subsitution and verify the services more easily:

```
curl $(minikube service beverage-service --url)/v1/customer/bottles/15 | jq
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   185  100   185    0     0  16818      0 --:--:-- --:--:-- --:--:-- 16818
{
  "id": 15,
  "name": "Orange",
  "volume": 0.5,
  "isAlcoholic": false,
  "volumePercent": 0,
  "price": 1.99,
  "supplier": "Juice Factory",
  "inStock": 55,
  "href": "http://172.17.0.2:30403/v1/customer/bottles/15"
}
```

and

```
$ curl $(minikube service management-service --url)/v1/employee/bottles/15 | jq
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   187  100   187    0     0    433      0 --:--:-- --:--:-- --:--:--   433
{
  "id": 15,
  "name": "Orange",
  "volume": 0.5,
  "isAlcoholic": false,
  "volumePercent": 0,
  "price": 1.99,
  "supplier": "Juice Factory",
  "inStock": 55,
  "href": "http://172.17.0.2:31715/v1/employee/bottles/15"
}
```

If one decided to expose the database-handler as well, you can acess it using

```
$ curl $(minikube service database-handler-service --url)/v1/employee/bottles/15 | jq
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   185  100   185    0     0    527      0 --:--:-- --:--:-- --:--:--   528
{
  "id": 15,
  "name": "Orange",
  "volume": 0.5,
  "isAlcoholic": false,
  "volumePercent": 0,
  "price": 1.99,
  "supplier": "Juice Factory",
  "inStock": 55,
  "href": "http://localhost:9999/v1/employee/bottles/15"
}
```

### 2.4 The Multi-Pod Solution

The solution files can be found in the `k8s_if_DNS_would_work` directory.

In there, we have three directories, one for each service.
Each of these directories contains a `deployment-*.yaml` file which is used to deploy the service.
Furthermore, there is also a `service-*.yaml` file which provides a public endpoint for this serivce.
The `database-handler` also has a `storage-database-handler.yaml` file which is used for providing persistent storage for the database files, similar to docker's volumes.

Because the DNS server does not work, we need to solve the DNS issue manually.
We do this by starting the `database-handler` first, get its IP address and port and put this information into the environment variables of the `beverage-service` and the `mangement-serivce`.

This solution truly allows the services to be distributed across multiple machines with different IP addresses.

So let's start the deployment process:

First, make sure we're in the `Assignment_2/k8s_if_DNS_would_work` directory.
Then, start only the `database-handler` using `kubectl apply -f database_handler`.

After it has started, we get the IP address and port of it.

```
$ minikube service database-handler --url
http://172.17.0.2:31179
```

Then, we adapt the `beverage-service` and `management-service` with this IP and port by changing the environment variables in their deployment files.

Replace

```
env:
    - name: DB_HOST
      value: minikube
    - name: DB_PORT
      value: "9999"
```

with

```
env:
    - name: DB_HOST
      value: "172.17.0.2"
    - name: DB_PORT
      value: "31179"
```

After this, we can start the `beverage-service` and `management-service` using `kubectl apply -f beverage_service` and `kubectl apply -f management_service`.

After the services are started and deployed, one can verify that they are working correctly again using `curl`.

```
curl $(minikube service beverage-service --url)/v1/customer/bottles/15 | jq
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   185  100   185    0     0    522      0 --:--:-- --:--:-- --:--:--   522^[[A
{
  "id": 15,
  "name": "Orange",
  "volume": 0.5,
  "isAlcoholic": false,
  "volumePercent": 0,
  "price": 1.99,
  "supplier": "Juice Factory",
  "inStock": 55,
  "href": "http://172.17.0.2:30737/v1/customer/bottles/15"
}
```

and

```
curl $(minikube service management-service --url)/v1/employee/bottles/15 | jq
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   187  100   187    0     0    372      0 --:--:-- --:--:-- --:--:--   371^[[A
{
  "id": 15,
  "name": "Orange",
  "volume": 0.5,
  "isAlcoholic": false,
  "volumePercent": 0,
  "price": 1.99,
  "supplier": "Juice Factory",
  "inStock": 55,
  "href": "http://172.17.0.2:30230/v1/employee/bottles/15"
}
```

The `database-handler` in this example is exposed by default and can be tested using

```
$ curl $(minikube service database-handler --url)/v1/employee/bottles/15 | jq
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   185  100   185    0     0  37000      0 --:--:-- --:--:-- --:--:-- 37000
{
  "id": 15,
  "name": "Orange",
  "volume": 0.5,
  "isAlcoholic": false,
  "volumePercent": 0,
  "price": 1.99,
  "supplier": "Juice Factory",
  "inStock": 55,
  "href": "http://172.17.0.2:31179/v1/employee/bottles/15"
}
```