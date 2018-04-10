# installing under tomcat and apache

## build

Build using maven 3.5 or better

```
mvn clean install -DskipTests=true
```

Maven will drop the war file in the target directory

## install

1. Copy the war file to your tomcat, naming it aim.war
2. It seems that maven doesn't copy the CumulusJC.jar to the war file because <scope>system</scope>. It somehow takes for granted that it will be available through classpath upon deployment. 
3. Edit aim.yml and set the parameters needed for communicating with Cumulus and for storing and retrieving images in the jpeg_folder parameter.
4. 

## configuration

