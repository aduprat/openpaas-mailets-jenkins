FROM jenkins:2.60.2

RUN /usr/local/bin/install-plugins.sh blueocean:1.1.4
RUN /usr/local/bin/install-plugins.sh envinject:2.1.3

COPY basic-authentication.groovy /usr/share/jenkins/ref/init.groovy.d/basic-authentication.groovy
