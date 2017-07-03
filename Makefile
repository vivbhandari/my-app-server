.PHONY: all test clean

IMAGE=myapp
VERSION=1

image:
	docker build -t $(IMAGE) .

tag:
	docker tag $(IMAGE):latest $(IMAGE):$(VERSION)

start:
	docker run -dt --name haproxy1 --net mynet123 -p 80:80 -v `pwd`/haproxy:/usr/local/etc/haproxy:ro haproxy:1.7
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
	cp haproxy/haproxy_pristine.cfg haproxy/haproxy.cfg

