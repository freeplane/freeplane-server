# Installing / configuring mongodb
* Install mongodb server ("mongod") and mongodb client ("mongo", but there are also GUI clients)
  (these are usually in one package)
* if necessary: change spring.data.mongodb.* in application.properties
* use (databases are automatically created :-))
* TODO: add notes about how to start mongodb (cd dir; starting exe)

# Server Side Notes

## Getting the basic server code structure in place (using websockets)
* Create code for a 'post' of an entire xml file
* Create code for a 'get' of an entire xml file

* We are not doing anything with STOMP.  Using Spring websockets for the server.

* TODO:
** define the application level protocol (above websockets)
*** outbound subscription to client of updated maps
*** update to client of changed maps
*** unsubscribe from client
** Disconnect (timeout) service (client dies or whatever)
** Configuration and Admin tool  
** Add subscription service [Joe]
** Add authentication service (use OAuth2)
** Sufficient "unit test" code and stand-alone build code
** Deployment of server
** Conflict resolution
** chat functionality between clients (that are modifying the same map?)

# Client Notes
* Using JSR-356 websockets for the client
* TODO:
** Create a "powerful" test client
** Create multiple clients
*** create freeplane map via freeplane and use that as input to test client
*** how do we test with an updated map (use a 'real' client)?

