# apache-commons-daemon

Simple example of using the Apache Commons Daemon with Clojure
http://commons.apache.org/proper/commons-daemon/

You will need to install jsvc as well
http://commons.apache.org/proper/commons-daemon/jsvc.html

On Ubuntu 14.04 it is as simple as:

```bash
$ sudo apt-get install jsvc
```

To build:

```bash
$ lein uberjar
```

Start/stop like this (change your java home to what is appropriate for you):

```bash
$ sudo /usr/bin/jsvc -java-home /usr/lib/jvm/java-8-oracle/jre/ \
  -cp "$(pwd)/target/apache-commons-daemon-0.1.0-SNAPSHOT-standalone.jar" \
  -outfile "$(pwd)/out.txt" \
  apache_commons_daemon.core

$ sudo /usr/bin/jsvc -java-home /usr/lib/jvm/java-8-oracle/jre/ \
  -cp "$(pwd)/target/apache-commons-daemon-0.1.0-SNAPSHOT-standalone.jar" \
  -stop \
  apache_commons_daemon.core

$ sudo cat out.txt
```

You should see output that looks something like this:

```
;; Starting MetricsComponent
;; Starting TickComponent
tick
tick
tick
tick
tick
;; Stopping TickComponent
;; Stopping MetricsComponent
;; Metric num ticks: 5
```

## License

Copyright © 2015 Will Fleming

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
