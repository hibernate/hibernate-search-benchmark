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
[p1]> docker run -it --rm=true --name es-7.10-it -p 9200:9200 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch-oss:7.10.2
[p0]> java -jar jmh-elasticsearch/target/benhmark.jar
```

Run Elasticsearch server in another process e.g.: `p1`.

### Search 5

``` bash
[p0]> mvn clean install -P search5
<< fork `p1` from `p0` >>
[p1]> docker run -it --rm=true --name es-5.6-it -p 9200:9200 -e "discovery.type=single-node" -e "xpack.security.enabled=false" docker.elastic.co/elasticsearch/elasticsearch:5.6.16
[p0]> java -jar jmh-elasticsearch/target/benchmark.jar
```

Run Elasticsearch server in another process e.g.: `p1`.