# hibernate-search-performance

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
