Static blog generator
============

Short intro
-----------

This is yet another tool to build static blogs. The basic idea - you write your posts in ```asciidoc```
format (see [Asciidoctor Writers Guide](http://asciidoctor.org/docs/asciidoc-writers-guide/)) and blog-builder generates from these posts a bunch of ```html/css/js``` files.

The generated code can be uploaded to any web server and you get a [beautiful blog](http://blog.sinica.me) without a pain.

Features
----------

* Information about author;
* Code highlighting;
* Include images and youtube videos;
* "Follow me on Twitter" button;
* Sharing post on Twitter and G+;
* Google Analytics integration.

How to use
-------------

Blog builder is distributed as docker image. You can launch it with the following command:

```
docker run -d --name=blog-builder \ 
	-p 8888:8080 \ 
	-v /home/sergey/blog-source:/source \ 
	-v /home/sergey/blog-result:/result \ 
	-v /home/sergey/blog-config:/config \ 
	ssinica/blog-builder
```

You should mount 3 directories:
* ``/source``, with files in asciidoc format and ``.asc`` extension.
* ``/result``, where to put generated html.
* ``/config``, with configuration file named ``blog.properties``

To re-generate blog visit: http://localhost:8888/rebuild.

To see your blog visit: http://localhost:8888.

Configuration
-------------

Example of ``blog.properties`` file:

```
blog.root.url=http://blog.sinica.me

blog.ga.tracking.id=
blog.ga.domain=

author.name=Sergey Sinica
author.image=https://pbs.twimg.com/profile_images/3762256738/195f3ac721125b7d63e4bfeea53be9d2.jpeg
author.twitter=ssinica
author.github=ssinica
author.google=+SergeySinica
```


How to publish
--------------

Blog builder respects ```.git``` directory and ```CNAME``` file in result directory. These files will not be changed during blog re-generation. So the easiest way to publish your blog is to use [github pages](http://pages.github.com/).

Post format
-----------

```
= Post caption
:date: 22-12-2013
:permalink: the_first_post
:tags: java, docker

== This is a title for test post

Hello folks!

This is a sample post
```

