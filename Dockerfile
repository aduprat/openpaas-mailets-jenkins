FROM jenkins:2.60.2

RUN /usr/local/bin/install-plugins.sh blueocean:1.1.4
