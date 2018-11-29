Also there will be NIFI workflow, which takes the configuration file and submit the spark jobs. The configuration file will have type of the transformations that we want to perform, the format of the file , the input folder /output folder and schema regustry folder.

We can use avro schema as a sample for now.
The code we can try with both java and scala if possible.
The spaek job is batch and can run every 15 minutes and system should be highly scalable



Hi Stanislav, the initial requirement is there will be two different files coming at different times of the day, these files will be in the either csv, telda delimited or fixedlength format. Separate Spark job will convert these files based on the schema that is provided to parquet format. Then we will have a spark job will create a reconciliationfile from the first two files based on the column and will be sorted. In the third job dataframe will be converted to the required format and stored in output folder.

Also there will be NIFI workflow, which takes the configuration file and submit the spark jobs. The configuration file will have the input /output file folder locations and the type of the transformations that we want to perform, the format of the file , and schema regustry folder.

We can use avro schema as a sample schema for now.

The code we can try with both java and scala if possible.

The spaek job is batch and can run every 15 minutes and system should be highly scalable


We are planning to use NIFi just to move the files before the spark job. We can park the NIFI for noe but take a look how beanio files can be translated to avro and they can be used to convert the fixedlength files to parquet..

section 2 of http://beanio.org/2.0/docs/reference/index.html provides an example of fixedlength

is there a way to convert beanio to avro and apply on fixedlength files and convert them to parquet in spark?.


https://ramgsuri.wordpress.com/2014/07/02/java-code-to-read-from-fixed-length-flat-file-dta-file-and-create-canonical-xml/


1. 




# Quick Start

- Install Apache nifi (from https://nifi.apache.org/)
- Install Apache Spark (from http://spark.apache.org/)
- Clone this repository and build

```
$ git clone git@github.com:emethk/spark-streaming-wordcount-on-nifi.git
$ cd spark-streaming-wordcount-on-nifi
$ sbt assembly
```
- Edit `/path/to/nifi/conf/nifi.properties` as

```
...
nifi.remote.input.socket.host=
nifi.remote.input.socket.port=8090
nifi.remote.input.secure=false
...
```
- Start nifi

```
$ /path/to/nifi/bin/nifi.sh start
```

- Add `Port` at `http://localhost:8080/nifi` named `Data For Spark` which is the same as in the Scala code.

- Start the spark streaming job

```
$ spark-submit target/scala-2.10/spark-streaming-wordcount-on-nifi-assembly-1.0.jar
```

# references
- https://blogs.apache.org/nifi/entry/stream_processing_nifi_and_spark
- http://spark.apache.org/docs/latest/streaming-programming-guide.html
