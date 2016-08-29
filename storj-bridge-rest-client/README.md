# storj.io.client.DefaultStorjClient-bridge-rest-client

This is a REST client for communicating the the storj-bridge, implemented in Java.
Above this project sits a storj-client, which depends on this project for making HTTP requests.
If you are wanting to use this project to upload/download data to the storj network, it is recommended
that you use the storj-client project, which uses this project for it's communication.

This project is purely for HTTP requests, so does not include logic for sharding files or encrypting them.

```
++ Parent project
++++ Storj Browser: A UI for the storj client.
++++++ storj.io.client.DefaultStorjClient-client: Storj Client.
+++++++++ storj.io.client.DefaultStorjClient-bridge-rest-client: Low level rest services for connecting to the Storj Bridge.
```
