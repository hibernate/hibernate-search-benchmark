# hibernate-search-benchmark

How to run the performance tests.

## Lucene

### Search 6

``` bash
> mvn clean install
> java -jar jmh-lucene/target/benhmark.jar
```

### Search 5

``` bash
> mvn clean install -P search5
> java -jar jmh-lucene/target/benhmark.jar
```

## Elasticsearch

### Search 6

``` bash
[p0]> mvn clean install
<< fork `p1` from `p0` >>
[p1]> ./jmh-elasticsearch/target/elasticsearch0/bin/elasticsearch
[p0]> java -jar jmh-elasticsearch/target/benhmark.jar
```

Run Elasticsearch server in another process e.g.: `p1`.

### Search 5

``` bash
[p0]> mvn clean install -P search5
<< fork `p1` from `p0` >>
[p1]> ./jmh-elasticsearch/target/elasticsearch0/bin/elasticsearch
[p0]> java -jar jmh-elasticsearch/target/benchmark.jar
```

Run Elasticsearch server in another process e.g.: `p1`.