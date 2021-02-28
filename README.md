# Service Oriented Architecture Assignment
## Develop Java REST API without any Framework
Few reasons why we are designing REST API without any Framework (SpringBoot)
- Cleaner and more predictable code, no annotation mess.
- Control of your code, no framework magic.
- Faster startup (less dependencies).
- Smaller docker images (which is important for our second assignment).
- Foster your Java skills. 

Using JaxRS Specification. Jersey RESTful Web Services framework is an open source, production quality framework for developing RESTful Web Services in Java that provides support for JAX-RS APIs and serves as a JAX-RS (JSR 311 & JSR 339) Reference Implementation


## Dockerizing REST API  
Our beverage store needs now a portable and scalable solution to host our server 
since a single monolithic application running on one of our laptops is not sufficient any more. This 
requires some changes in our code and the solution design.

## Orchestrating Docker Containers
If we want to run multiple containers, we need some solution to make it easy to manage and handle multiple 
docker containers because it is not feasible to build and run each docker container or even using docker compose 
how could we scale. Here comes the Kubernettes which is basically a great 
orchestration tool for docker containers.