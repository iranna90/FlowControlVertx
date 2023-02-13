# FlowControlVertx
Demo repo to show how to do flow control and prioritising the requests using Http vertx famewrok

# Demo 
it has 2 end point 
- http://localhost:8080/license
- http://localhost:8080/health/ready

# Flow control for http://localhost:8080/license
We have added flow control to the end point http://localhost:8080/license, for demo purpose we have allowed only 1 request of it at any given moment of time.
So if the number of requests for "license" goes above 1, it will start responding 503 after 1 active request.

But http://localhost:8080/health/ready which is a priority request, can still be handled by the health verticle, even when "license" requests reached max allowed value.



