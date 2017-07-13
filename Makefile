.PHONY: all test clean

IMAGE=myapp
VERSION=2

prepare:
	export PATH=/usr/local/apache-maven-3.5.0/bin:$PATH
	mvn install

start-dev:
	mvn exec:java

image:
	docker build -t $(IMAGE) .

tag:
	docker tag $(IMAGE):latest $(IMAGE):$(VERSION)

start:
	cp haproxy/haproxy_pristine.cfg haproxy/haproxy.cfg
	docker run -dt --name haproxy1 --net mynet123 -p 80:80 -v `pwd`/haproxy:/usr/local/etc/haproxy:ro haproxy:1.7
	docker run -dt --name mysql1 --hostname mysql1 --net mynet123 -v `pwd`/host_volume/mysql1:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=root mysql
	make add-server number=1
	make add-server number=2

add-server:
	docker run -dt --net mynet123 --name server$(number) --hostname server$(number) $(IMAGE):$(VERSION) 
	echo "    server server$(number) server$(number):8080 check" >> haproxy/haproxy.cfg 
	docker kill -s HUP haproxy1

remove-server:
	docker stop server$(number)
	docker rm server$(number)
	sed -i.bak "/server server$(number) server$(number):8080 check/d" ./haproxy/haproxy.cfg 
	docker kill -s HUP haproxy1

stop:
	docker stop `docker ps --no-trunc -aq`
	docker rm `docker ps --no-trunc -aq`

clean-db:
	rm -rf host_volume/mysql1/*
