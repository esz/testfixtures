When applying the `java-test-fixtures` plugin to a project, classes from `sourceSets.main` are put onto the test classpath twice, which imho might be a bug in gradle's handling of test-fixtures.

`gradlew test --tests at.myorg.testfixtures.MyTest.classesFromMainSourceSetArePresentMultipleTimes` yields

* `file:/.../testfixtures/build/classes/java/main/at/myorg/testfixtures/App.class`
* `jar:file:/.../testfixtures/build/libs/testfixtures.jar!/at/myorg/testfixtures/App.class`

while only

`file:/.../testfixtures/build/classes/java/main/at/myorg/testfixtures/App.class`

is expected.

Notice, that the project itself is present on the dependency tree twice:

<pre>
gradlew dependencies --configuration testRuntimeClasspath

> Task :dependencies

------------------------------------------------------------
Root project
------------------------------------------------------------

testRuntimeClasspath - Runtime classpath of source set 'test'.
+--- <b>project : (*)</b>
+--- org.junit.jupiter:junit-jupiter-api:5.5.2
|    +--- org.apiguardian:apiguardian-api:1.1.0
|    +--- org.opentest4j:opentest4j:1.2.0
|    \--- org.junit.platform:junit-platform-commons:1.5.2
|         \--- org.apiguardian:apiguardian-api:1.1.0
+--- org.junit.jupiter:junit-jupiter-engine:5.5.2
|    +--- org.apiguardian:apiguardian-api:1.1.0
|    +--- org.junit.platform:junit-platform-engine:1.5.2
|    |    +--- org.apiguardian:apiguardian-api:1.1.0
|    |    +--- org.opentest4j:opentest4j:1.2.0
|    |    \--- org.junit.platform:junit-platform-commons:1.5.2 (*)
|    \--- org.junit.jupiter:junit-jupiter-api:5.5.2 (*)
\--- <b>project : (*)</b>

(*) - dependencies omitted (listed previously)

A web-based, searchable dependency report is available by adding the --scan option.
</pre>

The result of `dependencyInsight` are as follows:

<pre>
gradlew dependencyInsight --dependency testfixtures --configuration testRuntimeClasspath

> Task :dependencyInsight
project :
   variant "testRuntimeClasspath" [
      org.gradle.usage               = java-runtime
      org.gradle.libraryelements     = jar
      org.gradle.dependency.bundling = external
      org.gradle.jvm.version         = 8
   ]
   variant "testFixturesRuntimeElements" [
      org.gradle.usage               = java-runtime
      org.gradle.libraryelements     = jar
      org.gradle.dependency.bundling = external
      org.gradle.category            = library (not requested)
      org.gradle.jvm.version         = 8
   ]
   variant "runtimeElements" [
      org.gradle.usage               = java-runtime
      org.gradle.libraryelements     = jar
      org.gradle.dependency.bundling = external
      org.gradle.category            = library (not requested)
      org.gradle.jvm.version         = 8
   ]

project :
\--- project : (*)

project :
\--- project : (*)

(*) - dependencies omitted (listed previously)

A web-based, searchable dependency report is available by adding the --scan option.
</pre>

Removing the `java-test-fixtures` plugin from the project (and moving all sources from `src/testFixtures` into `src/test` to
avoid compile errors) makes the `jar:file:...`
classpath resource disappear. Also, the dependency tree looks more familiar:

<pre>
gradlew dependencies --configuration testRuntimeClasspath

> Task :dependencies

------------------------------------------------------------
Root project
------------------------------------------------------------

testRuntimeClasspath - Runtime classpath of source set 'test'.
+--- org.junit.jupiter:junit-jupiter-api:5.5.2
|    +--- org.apiguardian:apiguardian-api:1.1.0
|    +--- org.opentest4j:opentest4j:1.2.0
|    \--- org.junit.platform:junit-platform-commons:1.5.2
|         \--- org.apiguardian:apiguardian-api:1.1.0
\--- org.junit.jupiter:junit-jupiter-engine:5.5.2
     +--- org.apiguardian:apiguardian-api:1.1.0
     +--- org.junit.platform:junit-platform-engine:1.5.2
     |    +--- org.apiguardian:apiguardian-api:1.1.0
     |    +--- org.opentest4j:opentest4j:1.2.0
     |    \--- org.junit.platform:junit-platform-commons:1.5.2 (*)
     \--- org.junit.jupiter:junit-jupiter-api:5.5.2 (*)
</pre>
