# POC RESTful API in Java with SpringBoot and MongoDB running in Docker

## Requirements

Java JDK 1.8

Maven

Docker

## Environment preparation

Clone this repository:

```
git clone https://github.com/sergioaraki/POC-RESTful-API-Java-SpringBoot-MongoDB
```

Go to the repository folder:

```
cd POC-RESTful-API-Java-SpringBoot-MongoDB/ShoppingCart
```

Build project with maven:

```
mvn clean install
```

Build docker image:

```
docker build -t springboot-mongo:latest .
```

Run a docker image with mongo:

```
docker run -d -p 27000:27017 --name mongo mongo
```

Run this project docker image linked with mongo:

```
docker run -p 8080:8080 --name springboot-mongo --link=mongo  springboot-mongo
```

## All the API endpoints available

(GET/POST) http://localhost:8080/products

(GET) http://localhost:8080/products/{id}

(POST) http://localhost:8080/carts

(POST) http://localhost:8080//carts/{id}/products

(DELETE) http://localhost:8080/carts/{cartId}/products/{productId}

(GET) http://localhost:8080/carts/{id}/products

(GET) http://localhost:8080/carts/{id}/ 

(POST) http://localhost:8080/carts/{id}/checkout

There is an example for each endpoint in the postman collection (ShoppingCart.postman_collection.json)

There is also a service running in background every minute processing carts in status "READY" and setting "PROCESSED" in case there is stock available of all products in the cart.