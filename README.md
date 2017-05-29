# spark-presentation

This repository contains a presentation (in Czech) which was given on May 27, 2017 at jobsdev.cz

## Presentation

The presentation explains:
* what Spark is (very briefly), 
* what RDD is and how it is realized, with an example of transformations and actions,
* what happens when an action is called (logical & physical plan, tasks),
* how shuffle is implemented.

The presentation takes at least 45 minutes, and can be extended by:
* Telling more details about 
* Showing relevant Spark source code
* Live coding examples of transformations and actions is various settings (number of partitions, partitioners)

## Code Example

The code example uses data from:
* https://catalog.data.gov/dataset/crimes-2001-to-present-398a4
* https://data.cityofchicago.org/Public-Safety/Chicago-Police-Department-Illinois-Uniform-Crime-R/c7ck-438e/data

The task is to compute distribution/density of crime depending on its distance from the crime centroid.
The data must be acquired separately, and distributed to HDFS if needed.

The source code is complete and commented; incrementally uncomment parts as adviced.
It can be started either locally:
```
gradle run file:///home/adam/spark/
```
or it can run on a cluster (configuration is in `cluster.sh`):
```
gradle runCluster
```

Contact for more information: adam.juraszek@ataccama.com
