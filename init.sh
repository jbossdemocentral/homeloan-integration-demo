#!/bin/bash 
JBOSS_HOME=./target/jboss-soa-p-5
SERVER_DIR=$JBOSS_HOME/jboss-as/server/default
INBOUND_DIR=/tmp/inboundLoanApplications
SRC_DIR=./installs
PRJ_DIR=./projects/homeloan-integration-bpm
SOA_P=soa-p-5.3.1.GA.zip
BRMS=brms-p-5.3.1.GA-deployable.zip
DESIGNER=designer-patched.war
MAVENIZE_VERSION=5.3.1.BRMS
VERSION=5.3

##
# Installation mavanization functions.
##
installPom() {
    mvn -q install:install-file -Dfile=../support/$2-$MAVENIZE_VERSION.pom.xml -DgroupId=$1 -DartifactId=$2 -Dversion=$MAVENIZE_VERSION -Dpackaging=pom;
}

installBinary() {
    unzip -q $2-$MAVENIZE_VERSION.jar META-INF/maven/$1/$2/pom.xml;
    mvn -q install:install-file -DpomFile=./META-INF/maven/$1/$2/pom.xml -Dfile=$2-$MAVENIZE_VERSION.jar -DgroupId=$1 -DartifactId=$2 -Dversion=$MAVENIZE_VERSION -Dpackaging=jar;
}


echo
echo Setting up the Home Loan SOA-P + BRMS demo environment...
echo

command -v mvn -q >/dev/null 2>&1 || { echo >&2 "Maven is required but not installed yet... aborting."; exit 1; }

# make some checks first before proceeding.	
if [[ -r $SRC_DIR/$SOA_P || -L $SRC_DIR/$SOA_P ]]; then
	echo SOA-P sources are present...
	echo
else
	echo Need to download $SOA_P package from the Customer Support Portal 
	echo and place it in the $SRC_DIR directory to proceed...
	echo
	exit
fi

if [[ -x $SRC_DIR/$BRMS || -L $SRC_DIR/$BRMS ]]; then
	echo BRMS sources are present...
	echo
else
	echo Need to download $BRMS package from the Customer Support Portal 
	echo and place it in the $SRC_DIR directory to proceed...
	echo
	exit
fi

# Make the required /tmp directories used by the demo. 
# WARNING: If you modify these locations, you will need to modify similar locations
# in the appropriate jboss-esb.xml configuration file!!!
if [ -x $INBOUND_DIR ]; then
	echo "  - listener directory $INBOUND_DIR exists, no need to make it again..."
	echo
else
	echo "  - creating Listener directory $INBOUND_DIR for demo..."
	echo
	mkdir -p $INBOUND_DIR
fi

# we need this for the demo to work.
if [ ! -x $INBOUND_DIR ]; then
	echo Was not able to create the Listener directory $INBOUND_DIR pls do this by hand and run script again...
	echo
	exit
fi

# Create the target directory if it does not already exist
if [ ! -x target ]; then
	echo "  - creating the target directory..."
	echo
  mkdir target
else
	echo "  - detected target directory, moving on..."
	echo
fi

# Move the old JBoss instance, if it exists, to the OLD position
if [ -x $JBOSS_HOME ]; then
	echo "  - existing JBoss Enterprise SOA Platform $VERSION detected..."
	echo
	echo "  - moving existing JBoss Enterprise SOA Platform $VERSION aside..."
	echo
  rm -rf $JBOSS_HOME.OLD
  mv $JBOSS_HOME $JBOSS_HOME.OLD

	# Unzip the JBoss SOA-P instance
	echo Unpacking JBoss Enterprise SOA Platform $VERSION...
	echo
	unzip -q -d target $SRC_DIR/$SOA_P
else
	# Unzip the JBoss SOA-P instance
	echo Unpacking new JBoss Enterprise SOA Platform $VERSION...
	echo
	unzip -q -d target $SRC_DIR/$SOA_P
fi

# Unzip the required files from JBoss BRMS Deployable
echo Unpacking JBoss Enterprise BRMS $VERSION...
echo

unzip -q $SRC_DIR/$BRMS jboss-brms-manager.zip 
echo "  - deploying JBoss Enterprise BRMS Manager WAR..."
echo
unzip -q -d $SERVER_DIR/deploy jboss-brms-manager.zip
rm jboss-brms-manager.zip

unzip -q $SRC_DIR/$BRMS jboss-jbpm-console.zip 
echo "  - deploying jBPM Console WARs..."
echo
unzip -q -d $SERVER_DIR/deploy jboss-jbpm-console.zip
rm jboss-jbpm-console.zip

unzip -q $SRC_DIR/$BRMS jboss-jbpm-engine.zip 
echo "  - copying jBPM client JARs..."
echo
unzip -q -d $JBOSS_HOME/jboss-as/common jboss-jbpm-engine.zip lib/netty.jar
rm jboss-jbpm-engine.zip

echo Updating to the newest web designer...
echo
rm -rf $SERVER_DIR/deploy/designer.war/*
unzip -q support/$DESIGNER -d $SERVER_DIR/deploy/designer.war

echo "  - set designer to jboss-brms in profile..."
echo
cp support/designer-jbpm.xml $SERVER_DIR/deploy/designer.war/profiles/jbpm.xml

# Add execute permissions to the run.sh script
echo "  - making sure run.sh for server is executable..."
echo
chmod u+x $JBOSS_HOME/jboss-as/bin/run.sh

echo "  - adding BRMS policy in login-config.xml file..."
echo
cp support/login-config.xml $SERVER_DIR/conf

echo "  - limiting verbose JackRabbit logging in jboss-log4j.xml file..."
echo
cp support/jboss-log4j.xml $SERVER_DIR/conf

echo "  - enabling admin account in soa-users.properties file..."
echo
cp support/soa-users.properties $SERVER_DIR/conf/props

echo "  - registering an additional RiftSaw event listener in bpel.properties file..."
echo
cp support/bpel.properties $SERVER_DIR/deploy/riftsaw.sar

echo "  - copying custom RiftSaw event listener implementation jar to project..."
echo 
cp support/droolsfusion-eventlistener.jar $SERVER_DIR/deploy/riftsaw.sar/lib

echo "  - mavenizing your repo with BRMS components."
echo
echo
echo Installing the BRMS binaries into the Maven repository...
echo
unzip -q $SRC_DIR/$BRMS jboss-brms-engine.zip
unzip -q jboss-brms-engine.zip binaries/*
unzip -q $SRC_DIR/$BRMS jboss-jbpm-engine.zip
unzip -q -o -d ./binaries jboss-jbpm-engine.zip
cd binaries

echo Installing parent POMs...
echo
installPom org.drools droolsjbpm-parent
installPom org.drools droolsjbpm-knowledge
installPom org.drools drools-multiproject
installPom org.drools droolsjbpm-tools
installPom org.drools droolsjbpm-integration
installPom org.drools guvnor
installPom org.jbpm jbpm

echo Installing Rules dependencies into your Maven repository...
echo
#
# droolsjbpm-knowledge
installBinary org.drools knowledge-api

#
# drools-multiproject
installBinary org.drools drools-core
installBinary org.drools drools-compiler
installBinary org.drools drools-jsr94
installBinary org.drools drools-verifier
installBinary org.drools drools-persistence-jpa
installBinary org.drools drools-templates
installBinary org.drools drools-decisiontables

#
# droolsjbpm-tools
installBinary org.drools drools-ant

#
# droolsjbpm-integration
installBinary org.drools drools-camel

#
# guvnor
installBinary org.drools droolsjbpm-ide-common

echo Installing BPM dependencies into your Maven repository...
echo
installBinary org.jbpm jbpm-flow
installBinary org.jbpm jbpm-flow-builder
installBinary org.jbpm jbpm-persistence-jpa
installBinary org.jbpm jbpm-bam
installBinary org.jbpm jbpm-bpmn2
installBinary org.jbpm jbpm-workitems
installBinary org.jbpm jbpm-human-task
installBinary org.jbpm jbpm-test

cd ..
rm -rf binaries jboss-jbpm-engine.zip jboss-brms-engine.zip 

echo Installation of binaries for BRMS $MAVENIZE_VERSION complete.
echo

echo Now going to build the model jars by generating classes in your project.
echo
cd $PRJ_DIR
mvn clean install -Dmaven.test.skip=true
cd ../..

echo "  - copying model jars and configuration to Business Central server..."
echo 
cp $PRJ_DIR/target/homeloan-integration-bpm-model.jar $SERVER_DIR/deploy/business-central-server.war/WEB-INF/lib
cp support/mortgages-model.jar $SERVER_DIR/deploy/business-central-server.war/WEB-INF/lib
cp support/drools.session.conf $SERVER_DIR/deploy/business-central-server.war/WEB-INF/classes/META-INF
cp support/CustomWorkItemHandlers.conf $SERVER_DIR/deploy/business-central-server.war/WEB-INF/classes/META-INF

echo Integration $VERSION Home Loan Demo Setup Complete.
echo

