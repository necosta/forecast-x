FROM mesosphere/spark:latest

ARG VERSION

ENV SCALA_VERSION=2.11
ENV CLASS_PATH="pt.necosta.forecastx.ForecastX"
ENV PROJECT_NAME="forecastx"
ENV VERSION=$VERSION
ENV JAR_FILE=${PROJECT_NAME}_$SCALA_VERSION-$VERSION.jar

COPY target/scala-$SCALA_VERSION/$JAR_FILE /opt

RUN mkdir /forecastx

# Change log level to WARN
RUN sed -i -e 's/INFO/WARN/g' /opt/spark/dist/conf/log4j.properties

ENTRYPOINT ./bin/spark-submit \
       --class $CLASS_PATH \
       --master local[4] \
       /opt/$JAR_FILE
