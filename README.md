# Java client for the storj project.

This is a java client for the storj-bridge project. It has three maven modules.
Storj browser is a JavaFX based storj client, with a UI, for an end user.
The storj-client is the higher level project. The storj-client handles sharding and splitting files.
It relies on the storj-bridge-rest-client which handles HTTP requests to the storj API.

If you are wanting the browser, you'll want the Storj Browser project.
If you are looking to include a storj client in your project, you'll want the storj-client project. 
If you are ONLY looking for handling HTTP requests and wish to implement file sharding and encryption yourself, you may use the storj-bridge-rest-client.

```
++ Parent project
++++ Storj Browser: A UI for the storj client.
++++++ storj.io.client.DefaultStorjClient-client: Storj Client.
+++++++++ storj.io.client.DefaultStorjClient-bridge-rest-client: Low level rest services for connecting to the Storj Bridge.
```
