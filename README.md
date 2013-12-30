Static blog generator
============

Short intro
-----------

This is yet another tool to build static blogs. The basic idea - you write your posts in ```asciidoc``` format and blog-builder generates from these posts a bunch of ```html/css/js``` files. The generated code can be uploaded to any web server and you get a [beautiful blog](http://blog.sinica.me) without a pain. 

Prerequisites
-------------

* JRE 7;
* Maven;
* Skills to use tools listed above.

Features
----------

* Sidebar with info about the author;
* index.html page with list of posts;
* Post page;
* "Follow me on Twitter" button;
* Share post in Twitter and G+;
* Google Analytics integration.

Roadmap
---------

* RSS;
* Command line interface;
* Distribution as ZIP archive;
* Use images in posts.

How to generate
---------------

The tool is available only as source code, and there is no a friendly interface to manage a blog. But still it should be very easy to start if you have some programming experience.

* Clone the repository: ```git clone git@github.com:ssinica/blog-builder.git```;
* Build the project: ```mvn clean install```;
* Create somethere (dropbox is a goog choise) a directory which will contain the posts in asciidoc format;
* Copy somethere example properties file ```blog-builder.properties``` and edit with correct values;
* Write a post;
* Generate a blog (this step should be performed after any change in ```blog-builder.properties``` or in post) by starting builder ```com.synitex.blogbuilder.BlogBuilder```. As a propgram argument pass the path to ```blog-builder.properties``` file. If you are lucky then the blog will be generated in directory you have specified in ```blog-builder.properties``` file.
* Start a server ```com.synitex.blogbuilder.server.BlogServer```. As a program argument pass the path to ```blog-builder.properties``` file. Now you should be able to access your blog by url ```http://localhost:8080```;

How to publish
--------------

Blog builder respects ```.git``` directory and ```CNAME``` file in output directory. These files will not be changed during blog re-generation. So the easiest way to publish your blog is to use [github pages](http://pages.github.com/).

Post format
-----------

```
= Post caption
:date: 22-12-2013
:permalink: the_first_post
:intro: Hello, this is an intro for post.

== This is a title for test post

Hello folks!

This is a sample post
```

