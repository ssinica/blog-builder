REGISTRY = ssinica
IMAGE_NAME = blog-builder
VERSION = dev

build:
		docker build -t $(REGISTRY)/$(IMAGE_NAME):$(VERSION) .

run:
		docker run -ti --rm -p 8888:8080 -v /home/ssinca/Dropbox/sinica-blog/source:/source -v /tmp/blog/result:/result -v /home/ssinca/Dropbox/sinica-blog/config:/config $(REGISTRY)/$(IMAGE_NAME):$(VERSION)

run_ssh:
		docker run -ti --rm $(REGISTRY)/$(IMAGE_NAME):$(VERSION) /bin/bash

rmi:
		docker rmi -f $(REGISTRY)/$(IMAGE_NAME):$(VERSION)

push:
		docker push $(REGISTRY)/$(IMAGE_NAME):$(VERSION)

logs:
		docker logs -f $(IMAGE_NAME)

.PHONY: build run_ssh rmi push run
