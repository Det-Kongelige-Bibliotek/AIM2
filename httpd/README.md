# installing under tomcat and apache

## build

Build using maven 3.5 or better

```
mvn clean install -DskipTests=true
```

Maven will drop the war file in the target directory

## install

1. Copy the war file to your tomcat, naming it aim.war
2. It seems that maven doesn't copy the CumulusJC.jar to the war file because <scope>system</scope>. It somehow takes for granted that it will be available through classpath upon deployment. Do it yourself. Destination is webapps/aim/WEB-INF/lib
3. setenv.sh need to be edited in order to set GOOGLE_APPLICATION_CREDENTIALS, AIM_CONF, LD_LIBRARY_PATH and CATALINA_OPTS for the application
4. Edit aim.yml and set the parameters needed for communicating with Cumulus and for storing and retrieving images in the jpeg_folder parameter. Its home should be given under setenv.sh under 3.


## configuration

Edit aim.conf to reflect jpeg_folder parameter in aim.yml

```
ProxyPassMatch /aim/(images|word|img|css|js)(.*$) "ajp://localhost:8009/aim/$1$2"
Alias /aim/image_store "/home/tomcat/aim/"

<Location /aim/image_store>
Require all granted
</Location>
```





