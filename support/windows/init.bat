@ECHO OFF
setlocal

SET JBOSS_HOME=.\target\jboss-soa-p-5
SET SERVER_DIR=%JBOSS_HOME%\jboss-as\server\default\
SET INBOUND_DIR=\tmp\inboundLoanApplications\
SET SRC_DIR=.\installs\
SET PRJ_DIR=.\projects\homeloan-integration-bpm\
SET SOA_P=soa-p-5.3.1.GA.zip
SET BRMS=brms-p-5.3.1.GA-deployable.zip
SET MAVENIZE_VERSION=5.3.1.BRMS
SET VERSION=5.3


echo.
echo Setting up the Home Loan SOA-P + BRMS demo environment...
echo.

REM Verify Maven and Jar Commands Exist
call where mvn >nul 2>&1
if  %ERRORLEVEL% NEQ 0 (
	echo Maven Not Installed. Setup Cannot Continue
	GOTO :EOF
)

call where jar >nul 2>&1
if  %ERRORLEVEL% NEQ 0 (
	echo Jar Not Installed. Setup Cannot Continue
	GOTO :EOF
)

REM Make some checks before proceeding.
if exist %SRC_DIR%\%SOA_P% (
	echo SOA-P sources are present...
	echo.
) else (
	echo Need to download %SOA_P% package from the Customer Support Portal
	echo and place it in the %SRC_DIR% directory to proceed...
	echo.
	GOTO :EOF
)

if exist %SRC_DIR%\%BRMS% (
	echo BRMS sources are present...
	echo.
) else (
	echo Need to download %BRMS% package from the Customer Support Portal
	echo and place it in the %SRC_DIR% directory to proceed...
	echo.
	GOTO :EOF
)

REM Make the required /tmp directories used by the demo. 
REM WARNING: If you modify these locations, you will need to modify similar locations
REM in the appropriate jboss-esb.xml configuration file!!!
if exist %INBOUND_DIR% (
	echo - listener directory %INBOUND_DIR% exists, no need to make it again...
	echo.
) else (
	echo - creating Listener directory %INBOUND_DIR% for demo...
	echo.
	mkdir %INBOUND_DIR%
)

REM we need this for the demo to work.
if not exist %INBOUND_DIR% (
	echo Was not able to create the Listener directory %INBOUND_DIR% pls do this by hand and run script again...
	echo.
	GOTO :EOF
)

REM Create the target directory if it does not already exist
if not exist target (
	echo  - creating the target directory...
	echo.
	mkdir target
) else (
	echo  - detected target directory, moving on...
	echo.
)

REM Move the old JBoss instance, if it exists, to the OLD position.
 if exist %JBOSS_HOME% (
  	echo  - existing JBoss Enterprise SOA Platform %VERSION% detected...
  	echo.
  	echo  - moving existing JBoss Enterprise SOA Platform %VERSION% aside...
  	echo.
	
 	if exist "%JBOSS_HOME%.OLD" (
 		rmdir /s /q "%JBOSS_HOME%.OLD"
 	)
	
  	move "%JBOSS_HOME%" "%JBOSS_HOME%.OLD"
	
	REM Unzip the JBoss SOA-P instance.
 	echo.
  	echo Unpacking JBoss Enterprise SOA Platform %VERSION%...
  	echo.
  	cscript /nologo unzip.vbs %SRC_DIR%\%SOA_P% target
	
    ) else (
		
	REM Unzip the JBoss SOA-P instance.
  	echo Unpacking JBoss Enterprise SOA Platform %VERSION%...
  	echo.
  	cscript /nologo unzip.vbs %SRC_DIR%\%SOA_P% target
  )
 
REM Unzip the required files from JBoss BRMS Deployable
echo Unpacking JBoss Enterprise BRMS %VERSION%...
echo.
cscript /nologo unzip.vbs "%SRC_DIR%\%BRMS%" "%SRC_DIR%"

REM cscript /nologo ../unzip.vbs jboss-brms-manager.zip "%SRC_DIR%/%BRMS%"

echo - deploying JBoss Enterprise BRMS Manager WAR...
echo.
cscript /nologo unzip.vbs "%SRC_DIR%\jboss-brms-manager.zip" "%SERVER_DIR%\deploy"
del %SRC_DIR%\jboss-brms-manager.zip

REM cscript /nologo ../unzip.vbs jboss-jbpm-console.zip "%SRC_DIR%/%BRMS%"

echo - deploying jBPM Console WARs...
echo.
cscript /nologo unzip.vbs "%SRC_DIR%\jboss-jbpm-console.zip" "%SERVER_DIR%\deploy"
del %SRC_DIR%\jboss-jbpm-console.zip

mkdir "%SRC_DIR%\binaries"
mkdir "%SRC_DIR%\jboss-jbpm-engine"

cscript /nologo unzip.vbs "%SRC_DIR%\jboss-jbpm-engine.zip" "%SRC_DIR%\jboss-jbpm-engine"
xcopy /Y /Q "%SRC_DIR%\jboss-jbpm-engine\*.jar" "%SRC_DIR%\binaries"
xcopy /Y /Q "%SRC_DIR%\jboss-jbpm-engine\lib\*.jar" "%SRC_DIR%\binaries"

echo.
echo - copying jBPM client JARs...
echo.

xcopy /Y /Q "%SRC_DIR%\binaries\netty.jar" "%JBOSS_HOME%\jboss-as\common\"
del %SRC_DIR%\jboss-jbpm-engine.zip

echo.
echo - adding BRMS policy in login-config.xml file...
echo.
xcopy /Y /Q "support\login-config.xml" "%SERVER_DIR%\conf\"

echo.
echo - limiting verbose JackRabbit logging in jboss-log4j.xml file...
echo.
xcopy /Y /Q "support\jboss-log4j.xml" "%SERVER_DIR%\conf\"

echo.
echo - enabling admin account in soa-users.properties file...
echo.
xcopy /Y /Q "support\soa-users.properties" "%SERVER_DIR%\conf\props\"

echo.
echo - registering an additional RiftSaw event listener in bpel.properties file...
echo.
xcopy /Y /Q "support\bpel.properties" "%SERVER_DIR%\deploy\riftsaw.sar"

echo.
echo - copying custom RiftSaw event listener implementation jar to project...
echo. 
xcopy /Y /Q "support\droolsfusion-eventlistener.jar" "%SERVER_DIR%\deploy\riftsaw.sar\lib\"
echo.

echo - mavenizing your repo with BRMS components.
echo.
echo.
echo Installing the BRMS binaries into the Maven repository...
echo.

mkdir "%SRC_DIR%\jboss-brms-engine"
cscript /nologo unzip.vbs "%SRC_DIR%\jboss-brms-engine.zip" "%SRC_DIR%\jboss-brms-engine"
xcopy /Y /Q "%SRC_DIR%\jboss-brms-engine\binaries\*.jar" "%SRC_DIR%\binaries"

 
cd "%SRC_DIR%\binaries"

echo.
echo Installing parent POMs...
echo.

call :InstallPom org.drools droolsjbpm-parent

call :InstallPom org.drools droolsjbpm-knowledge
call :InstallPom org.drools drools-multiproject
call :InstallPom org.drools droolsjbpm-tools
call :InstallPom org.drools droolsjbpm-integration
call :InstallPom org.drools guvnor
call :InstallPom org.jbpm jbpm

REM droolsjbpm-knowledge
call :InstallBinary org.drools knowledge-api

REM drools-multiproject
call :InstallBinary org.drools drools-core
call :InstallBinary org.drools drools-compiler
call :InstallBinary org.drools drools-jsr94
call :InstallBinary org.drools drools-verifier
call :InstallBinary org.drools drools-persistence-jpa
call :InstallBinary org.drools drools-templates
call :InstallBinary org.drools drools-decisiontables

REM droolsjbpm-tools
call :InstallBinary org.drools drools-ant


REM droolsjbpm-integration
call :InstallBinary org.drools drools-camel


REM guvnor
call :InstallBinary org.drools droolsjbpm-ide-common

echo.
echo Installing BPM dependencies into your Maven repository...
echo.
call :InstallBinary org.jbpm jbpm-flow
call :InstallBinary org.jbpm jbpm-flow-builder
call :InstallBinary org.jbpm jbpm-persistence-jpa
call :InstallBinary org.jbpm jbpm-bam
call :InstallBinary org.jbpm jbpm-bpmn2
call :InstallBinary org.jbpm jbpm-workitems
call :InstallBinary org.jbpm jbpm-human-task
call :InstallBinary org.jbpm jbpm-test

cd ..
rmdir /q /s binaries
rmdir /q /s jboss-brms-engine
rmdir /q /s jboss-jbpm-engine
del /F *.RSA 
del /F modeshape.zip
del /F jboss-brms-engine.zip

echo.
echo Installation of binaries for BRMS %MAVENIZE_VERSION% complete.
echo.

echo Now going to build the model jars by generating classes in your project.
echo.
cd ..
cd %PRJ_DIR%
call mvn clean install -Dmaven.test.skip=true
cd ../..

xcopy /Y /Q  "%PRJ_DIR%\target\homeloan-integration-bpm-model.jar" "%SERVER_DIR%\deploy\business-central-server.war\WEB-INF\lib"
xcopy /Y /Q  "support\mortgages-model.jar" "%SERVER_DIR%\deploy\business-central-server.war\WEB-INF\lib"
xcopy /Y /Q  "support\drools.session.conf" "%SERVER_DIR%\deploy\business-central-server.war\WEB-INF\classes\META-INF"
xcopy /Y /Q  "support\CustomWorkItemHandlers.conf" "%SERVER_DIR%\deploy\business-central-server.war\WEB-INF\classes/META-INF"

echo.
echo Integration %VERSION% Home Loan Demo Setup Complete.
echo.

GOTO :EOF


:InstallPOM
    call mvn -q install:install-file -Dfile=../../support/%2-%MAVENIZE_VERSION%.pom.xml -DgroupId=%1 -DartifactId=%2 -Dversion=%MAVENIZE_VERSION% -Dpackaging=pom
	GOTO :EOF

:InstallBinary
	call jar xf %2-%MAVENIZE_VERSION%.jar
	call mvn -q install:install-file -DpomFile=./META-INF/maven/%1/%2/pom.xml -Dfile=%2-%MAVENIZE_VERSION%.jar -DgroupId=%1 -DartifactId=%2 -Dversion=%MAVENIZE_VERSION% -Dpackaging=jar
GOTO :EOF