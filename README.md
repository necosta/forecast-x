# forecast-x
Tennis data exploration and forecasting

## How to
* Format: (automatic)
* Build: `sbt compile`
* Test: `sbt test`
* Package: `sbt package`

### Pre-requisites
* Install [SBT](https://www.scala-sbt.org/download.html)
* Install [Docker](https://docs.docker.com/install/linux/docker-ce/ubuntu/#install-using-the-repository)

### Source data
Available here: https://raw.githubusercontent.com/JeffSackmann/tennis_atp/master/atp_matches_2018.csv

Based on work at https://github.com/JeffSackmann/

[Source code](https://github.com/JeffSackmann/tennis_atp)

[License](https://github.com/JeffSackmann/tennis_atp#license)


### How to run Spark job
* Build image:
```
docker build --build-arg VERSION=x.y.z -t forecastx:latest .
```
**Note**: Get version from version.sbt file

* Run image:
```
docker run forecastx:latest
```
