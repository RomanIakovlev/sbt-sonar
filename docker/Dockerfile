FROM sonarqube:lts

ENV PLUGINS_DIR /opt/sonarqube/extensions/plugins

WORKDIR $PLUGINS_DIR

ENV JAVA_VERSION 4.2.1.6971
RUN wget https://sonarsource.bintray.com/Distribution/sonar-java-plugin/sonar-java-plugin-$JAVA_VERSION.jar

# Sonar scala plugin must be provided separately
COPY sonar-scala-plugin-0.0.4-SNAPSHOT.jar .

WORKDIR /opt/sonarqube
