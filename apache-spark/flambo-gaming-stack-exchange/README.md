# flambo-gaming-stack-exchange

Example of querying a Stack Exchange data dump with Apache Spark in Clojure with Flambo

Assumes Spark 1.3.1 is installed in ~/bin/spark and the Gaming Stack Exchange Data Dump from https://archive.org/details/stackexchange has been downloaded to ~/data/gaming-stackexchange


## Usage

$ lein uberjar

ETL Users
$ ~/bin/spark/bin/spark-submit --class flambo_gaming_stack_exchange.etl_users target/flambo-gaming-stack-exchange-0.1.0-SNAPSHOT-standalone.jar

ETL Posts
$ ~/bin/spark/bin/spark-submit --class flambo_gaming_stack_exchange.etl_posts target/flambo-gaming-stack-exchange-0.1.0-SNAPSHOT-standalone.jar

Run queries
$ ~/bin/spark/bin/spark-submit --class flambo_gaming_stack_exchange.core target/flambo-gaming-stack-exchange-0.1.0-SNAPSHOT-standalone.jar

## License

Copyright © 2015 William Fleming

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
