# simple-load-balancer
A simple load balancer using the round-robbin algorithm

There are three main source files in the program: LoadBalancer.java, WorkerNode.java and Client.java

## LoadBalancer
The load balancer acts as the central server for the worker nodes and clients to connect to. It uses a scheduler to manage worker nodes and jobs. Jobs are assigned using the round-robin algorithm. Jobs are queued until a worker node is available to handle the job. Each job contains information about the amount of time (in seconds) it takes to complete.

## WorkerNode
A worker node connects to the load balancer and waits for jobs to be assigned to it. Once a job comes in, the node processes it and notifies the load balancer when complete. There can be many instances of WorkerNode running and each can resolve tasks independently.

## Client
A client serves as the producer of tasks. As the clients schedule jobs, they are sent to the load balancer for processing. Usually, one instance of the client is needed to produce jobs



# Overview of Source Files
**BaseConnectionHandler**: Serves as an abstract base class for handling connections to a node in a network. The class accepts the connection socket and initialises streams for reading and sending data. It also sets up an infinite loop (in a separate thread) that continuously transmits data to and from the socket.

**Client**: The class connects to the load balancer and sets up a loop to continuously request for user inputs until the application is exited.

**ClientNodeRegistrationMessage**: This is a simple serializable class that signals a connection request from a client node. The load balancer uses this to differentiate a client node from other nodes.

**Job**: A serializable class that represents a job to be distributed by the load balancer. Each job has a duration (in seconds) that it takes to complete.

**JobCompletedMessage**: Used to signal job completion from a worker node to the load balancer.

**LoadBalancer**: The load balancer opens a server socket and waits for nodes to connect. Valid nodes are client and worker nodes. It uses a scheduler to distribute jobs as they arrive. It keeps records of connected worker nodes inside the scheduler.

**RegistrationSuccessfulMessage**: Represents a message sent from the load balancer to connection nodes to signal a successful connection. Connecting nodes must wait for this message before they can confirm acceptance by the load balancer.

**Scheduler**: The scheduler is used by the load balancer to store worker nodes and distribute jobs using the round-robin algorithm. Jobs and worker nodes are stored inside a blocking queue. The scheduler waits for jobs and worker nodes; when a job is available, it is assigned to the next free worker node using the round-robin algorithm.

**WorkerNode**: Represents a single worker node that can connect to the load balancer. It accepts the host and port of the load balancer as command line arguments and also the name of the node. It transmits its name during the initial connection for identification. After a successful connection, it waits for jobs from the worker load. Once a job is received, it sleeps for the specified amount of time in the job and then notifies the load balancer when complete.

**WorkerNodeInfo**: Stores information about a connected node. This is used by the scheduler to figure out how to transmit data to the worker node and also its name.

**WorkerNodeRegistrationMessage**: An instance of this class is sent by the worker node when connecting to the load balancer. The object contains the name of the node for the load balancer to use for identification.
