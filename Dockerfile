FROM jenkins:2.60.2

# Install docker
USER root

RUN apt-get update \
    && apt-get install -y apt-transport-https ca-certificates \
    && apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys 58118E89F3A912897C070ADBF76221572C52609D \
    && echo deb https://apt.dockerproject.org/repo debian-stretch main >> /etc/apt/sources.list.d/docker.list \
    && apt-get update \
    && apt-get install -y docker-engine \
    && rm -rf /var/lib/apt/lists/*

# Install Jenkins plugins
USER jenkins

RUN /usr/local/bin/install-plugins.sh blueocean:1.1.4
RUN /usr/local/bin/install-plugins.sh envinject:2.1.3
RUN /usr/local/bin/install-plugins.sh ghprb:1.39.0
RUN /usr/local/bin/install-plugins.sh job-dsl:1.64

# Copy init Jenkins scripts
COPY basic-authentication.groovy /usr/share/jenkins/ref/init.groovy.d/basic-authentication.groovy
COPY create-dsl-job.groovy /usr/share/jenkins/ref/init.groovy.d/create-dsl-job.groovy

# Skip initial setup
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false
