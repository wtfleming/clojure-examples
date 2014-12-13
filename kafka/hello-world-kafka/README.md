# hello-world-kafka

Example of creating a Kafka producer and high level consumer in Clojure. The producer will periodically send a random number to a topic on Kafka.

Assumes both Kafka 0.8.1.1 and ZooKeeper are running on localhost.

## Usage

```
lein trampoline run <topic> <num consumer threads>
```

Using a topic called random_numbers and 2 consumer threads.

```
lein trampoline run random_numbers 2
```

You should see output something like this

```
Sending to Kafka topic random_numbers: 753
Received on thread 1: 753
Sending to Kafka topic random_numbers: 971
Received on thread 1: 971
Sending to Kafka topic random_numbers: 56
Received on thread 1: 56
Sending to Kafka topic random_numbers: 536
Received on thread 1: 536
Sending to Kafka topic random_numbers: 589
Received on thread 1: 589
```


## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
