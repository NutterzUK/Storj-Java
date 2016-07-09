# Java client for the storj project.

This is a java client for the storj-bridge project. It has two maven modules.
The storj-client is the higher level project. The storj-client handles sharding and splitting files.
It relies on the storj-bridge-rest-client which handles HTTP requests to the storj API.

If you are looking to include a storj client in your project, you'll want the storj-client project. 
If you are ONLY looking for handling HTTP requests and wish to implement file sharding and encryption yourself, you may use the storj-bridge-rest-client.
```
++ Parent project
++++ Storj-client
+++++++ Storj-bridge-rest-client
```
