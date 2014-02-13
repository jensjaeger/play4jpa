Play Framework 2.2.x module for JPA with Hibernate
==================================================

Main code by [Jens Jaeger](https://github.com/jensjaeger/play4jpa).

Why do we need this?
--------------------
See a blog entry by Jens Jaeger [here](http://www.jensjaeger.com/2013/11/play-framework-2-ebean-vs-jpa/).

Goal
----
This module should help you work with JPA instead of Ebean as your ORM layer. It provides a Finder-like implementation to facilitate working with the database layer.

How to use
----------
Currently, you have to compile the code yourself and put it in your local dependency repository.

Follow the steps below to use it in your own application:

1. Start up the play console by running `play` on the command line.
2. Make sure you have the _play4jpa_ project enabled by executing the command `projects` (there should be a _*_ in front of _play4jpa_).
3. Compile the project by running `compile`
4. Publish the project to your local repository: `publishLocal`
5. In your *own* project, edit the `Build.scala` file and add `"play4jpa" %% "play4jpa" % "0.1-SNAPSHOT"` to your _appDependencies_ (note the first *double %*)
6. In your Models, extend the `com.play4jpa.jpa.models.Model` and add a `com.play4jpa.jpa.models.Finder` static field (exactly like for Ebean)
7. For samples see the test cases in the module or the sample application in the repository (the latter one is not yet finished)!
