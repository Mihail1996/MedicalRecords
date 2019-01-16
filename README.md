# SIRS

Our project was tested in Ubuntu 18.10(64-bit) with Java 8 but should be working in 
any distribution of Linux(64-bit). We use Maven to build the project.

---------------------------------------------------------------------------------------

To be able to run this project there are three things that have to be done beforehand:

#### 1. 

* Create a file named jaxp.properties (if it doesn't already exist) under 
'{path to jdk1.8}/jre/lib' and add the following line to it : 
    
        javax.xml.accessExternalSchema = all

    
#### 2. 

* Export our KMS certificate, changing to directory 'SirsProj/kms-server/main/resources'
and using the following command: 
       
       keytool -exportcert -alias tomcat -keystore keystore.jks -file keystore.cer
 
* Install the certificate into Java cacerts Keystore:
        
        keytool -import -trustcacerts -alias tomcat -file keystore.cer -storepass changeit -keystore {path to jdk1.8}/jre/lib/security/cacerts


#### 3.
* Start MySQL server. 
* Connect to MySQL Server. Setup your username and password in both modules, changing lines 14
and 15 in files 'SirsProj/{each module}/src/main/resources/application.properties'.
* Create two new databases in your MySQL server with names 'sirs' and 'kms':

        create database sirs;
        create database kms;
       
       
---------------------------------------------------------------------------------------

After that, you can run the project in your IDE or in the command line. We tested and it's
working in Intellij IDEA: just run KmsApplication first and ServerApplication second. 


To run our project in the command line just run the two following commands:

1. Under 'SirsProj/kms-server':

        mvn spring-boot:run
    
2. Under 'SirsProj/server-app':

        mvn spring-boot:run

---------------------------------------------------------------------------------------

Everything should be running now. The server starts with four users created, one for each role
(admin, medic, nurse, patient), which credentials are: 

   Username | Password
   ---------|----------
     0001   | admin
     0002   | admin
     0003   | admin
     0004   | admin

