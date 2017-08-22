.PHONY: all test clean

IMAGE=vivekbhandari/myapp
VERSION=10

prepare:
	export PATH=/usr/local/apache-maven-3.5.0/bin:$PATH
	mvn install

create-host-volume:
	mkdir -p host_volume/haproxy1
	mkdir -p host_volume/msql1

start-dev:
	sudo launchctl load -F /Library/LaunchDaemons/com.oracle.oss.mysql.mysqld.plist
	mvn package
	mvn exec:java

image:
	docker build -t $(IMAGE) .

tag:
	docker tag $(IMAGE):latest $(IMAGE):$(VERSION)

start-old:
	cp haproxy/haproxy_pristine.cfg host_volume/haproxy1/haproxy.cfg
	docker run -dt --name haproxy1 --net mynet123 -p 80:80 -v `pwd`/host_volume/haproxy1/:/usr/local/etc/haproxy:ro haproxy:1.7
	docker run -dt --name mysql1 --hostname mysql1 --net mynet123 -v `pwd`/host_volume/mysql1:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=root mysql
	make add-server number=1
	make add-server number=2

start:
	cp haproxy/haproxy_pristine.cfg host_volume/haproxy1/haproxy.cfg
	echo "    server server1 server1:8080 check" >> ./host_volume/haproxy1/haproxy.cfg
	echo "    server server2 server2:8080 check" >> ./host_volume/haproxy1/haproxy.cfg
	docker-compose -f docker-compose.yml up -d

add-server:
	docker run -dt --net mynet123 --name server$(number) --hostname server$(number) $(IMAGE):$(VERSION) 
	echo "    server server$(number) server$(number):8080 check" >> ./host_volume/haproxy1/haproxy.cfg 
	docker kill -s HUP haproxy1

remove-server:
	docker stop server$(number)
	docker rm server$(number)
	sed -i.bak "/server server$(number) server$(number):8080 check/d" ./host_volume/haproxy1/haproxy.cfg 
	docker kill -s HUP haproxy1

stop:
	docker stop `docker ps --no-trunc -aq`
	docker rm `docker ps --no-trunc -aq`

clean-db:
	rm -rf host_volume/mysql1/*
