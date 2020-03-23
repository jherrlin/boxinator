FROM node:13.3.0

COPY . /app
WORKDIR /app

RUN apt-get update && apt-get -q -y install \
    openjdk-8-jdk chromedriver curl gnupg curl \
    && curl -s https://download.clojure.org/install/linux-install-1.10.1.492.sh | bash \
    && rm -rf /var/lib/apt/lists/* \
    && npm install -g shadow-cljs \
    && shadow-cljs release app
