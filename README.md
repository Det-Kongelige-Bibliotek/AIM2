# AIM2
New version of AIM (java)

Internal kb.dk project, where the installation guide can be found her: https://itwiki.kb.dk/wiki/AIM2


Requirements:
- Java 8
- Cumulus SDK
- Maven
- Tomcat


To build:

* Install CumulusJC.jar into maven by: 
 * `mvn install:install-file -Dfile=/usr/local/Cumulus_Java_SDK/CumulusJC.jar -DgroupId=com.canto -DartifactId=cumulus -Dversion=10 -Dpackaging=jar`
 * Then make sure, that the pom.xml only refers to the maven-installed Cumulus jar (remove scope="system" and systemPath, also exclude Cumulus from KB-Cumulus-API dependency)
* Retrieve the Google credentials (AIMapis-XXXX.json)
* Make maven installation of AIM2:
 * `GOOGLE_APPLICATION_CREDENTIALS=/path/to/AIMapis-XXXX.json mvn -Dcom.canto.cumulus.path=/usr/local/Cumulus_Java_SDK clean test install`
* Deploy the aim.war to tomcat
 * `cp target/aim-*.war /path/to/tomcat/webaps/.`


