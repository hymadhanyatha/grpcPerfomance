# grpcPerfomance
Performance Comparison on Spring Rest vs GRPC Unary and Stream Processing
This story talks about performance of Spring Rest API vs gRPC service implementation. We will use the Apache benchmark to perform a performance test on exhaustive load.

gRPC is an RPC implementation/framework from Google for inter-microservices communication. Google has been using this for more than 15 years (the internal name is Stubby). It is battle tested for more than a decade. Google site shows they have been processing 10 BILLIONS requests / second using gRPC.

We will see why gRPC is faster than REST with a simple. Before that Let see the basic difference between Rest and RPC


GRPC Uses Http 2
GRPC brings in many advantages in using HTTP2 protocol. Please see for the details.

Binary Framing:
Transport of message is lighter and safer to decode.
It works well with Protocol Buffer.
More performant and robust.
2. Header Compression:

GRPC uses HPACK for header compression.
It reduces overhead and improve performance.
3. Multiplexing:

We can send multiple requests and responses in parallel over a TCP connection.
It will improve network utilization and reduce latency.
One client request, multiple responses.
HTTP/1.1 Vs HTTP/2

There are many websites available to test the performance of HTTP/1.1 over HTTP/2. Please visit this site http://www.http2demo.io/

If you observe HTTP/1.1 took 1.82 seconds to download 200 images where as HTTP/2 took only 0.58 seconds which is almost three times lesser. This is because, it sends multiple requests in parallel over a single TCP connection.


Difference between GRPC and Rest


We could tell that GRPC has many advantages to rest. The only limited support on browser side, GRPC needs gRPC-Web as proxy layer to convert between HTTP/2 to HTTP/1.1.

Where can be gRPC used?
Micro Services
Low latency and hight throughput communication.
Strong API Contract.
2. Polyglot Environments

Code generation is out of the box for many programming languages.
3. Point to Point real time communication

With the excellent support for Bi directional streaming support, gRPC suits well for real time data streaming.
4. Low Network or Mobile Networks

Light weight message format, the data transfer is faster.
There are four types of service definitions in gRPC

Unary : This is regular blocking service for request and response. Just like our rest stateless calls.


Server Streaming: Server sends multiple messages to the client via single TCP connection. An example could be server pushing updates to clients periodically.


Client Streaming: Client keeps on sending a request to the server by using a single TCP connection. The server might accept all the messages and sends a single response back. An example could be file upload from client side.


Bi-Directional Streaming: Client and Server can keep on sharing messages via single TCP connection. An example could be client and server sharing messages like a chat application.


Performance testing on Rest API vs gRPC Services


The above is the use case we are going to implement. Basically it is generation of userdetails randomly. Just call a method will generate user details. This is same for both gRPC and Rest implementations. The role of enricher / aggregator is, it routes to gRPC implementation or Spring rest based on URI. To perform load test, added the path variable “range” in aggregator service, this is for looping, for each request made to aggregator, it loops from 1 to range values and gives us the response in Json Map.

Let us jump in to implementation

gRPC server

To implement gRPC, first we need to define ProtoBuffer and then we should generate the code base.

The whole application is on gradle build. But this proto buffer code is in maven. Some how I couldn’t generate jar using gradle plugin, I could do it in maven and installed the dependency in my local repo. This is added as dependency to other services.



curl localhost:8080/api/rest/unary/user/1000
curl localhost:8080/api/grpc/unary/user/1000
curl localhost:8080/api/grpc/stream/user/1000

install apache bench tool and run stress test
ab -n 100 localhost:8080/api/rest/unary/user/1000
ab -n 100 localhost:8080/api/grpc/unary/user/1000
ab -n 100 localhost:8080/api/grpc/stream/user/1000

check performace yourself
