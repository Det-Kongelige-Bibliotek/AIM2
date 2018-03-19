# HOWTO

If you have a new installation make sure that your server has an admin with UID postgres and a password corresponding to 

 src/main/resources/application.properties

You can do that using the 

 psql

tool. You can use the same tool to create a database called

 aim 

then select that database 

 \c aim

Then you can cut and paste the content of 

 create_words.sql

at the 

 aim=#

prompt
