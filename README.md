# forecast-x
Tennis data exploration and forecasting

## How to
* Format: (automatic)
* Build: `sbt compile`
* Test: `sbt test`
* Package: `sbt coverageOff package`


* Run code coverage: `sbt coverage`
* Code coverage report: `sbt coverageReport`

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
docker build --build-arg VERSION=x.y.z --build-arg OPTION=Import -t forecastx:latest .
```
**Note**: Get version from version.sbt file

* Run image:
```
docker run -v /local/folder/forecastx:/forecastx forecastx:latest
```

### How to run Jupyter notebook

* Build image:
```
docker build -t forecastx_nb:latest -f notebooks/Dockerfile .
```

* Run image:
```
docker run -p 8888:8888 -e NB_USER=forecastx -v /local/folder/forecastx:/home/forecastx/work forecastx_nb:latest
```

