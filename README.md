Play Framework 2.2.x module for JPA with Hibernate
==================================================

Why do we need this?
--------------------
See this blog entry [here](http://www.jensjaeger.com/2013/11/play-framework-2-ebean-vs-jpa/).

Goal
----
This module should help you work with JPA instead of Ebean as your ORM layer. It provides a Finder-like implementation to facilitate working with the database layer.

Whats included
--------------
* Better @Transactional annotations (http://www.jensjaeger.com/2013/11/replace-play-transactional-with-something-better/)
* A generic helper for database related tests with play 2 and JPA (http://www.jensjaeger.com/2013/12/a-ebean-like-finder-for-jpa/)
* A Ebean like finder for JPA (http://www.jensjaeger.com/2013/12/a-ebean-like-finder-for-jpa/)

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

License
----------

This software is licensed under the Apache 2 license, quoted below.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this project except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
