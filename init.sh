#!/bin/sh
JBOSS_HOME=./target/jboss-soa-p-5
SERVER_DIR=$JBOSS_HOME/jboss-as/server/default

# Create the target directory if it does not already exist
if [ ! -d target ]; then
    mkdir target
fi

# Move the old JBoss instance, if it exists, to the OLD position
if [ -x $JBOSS_HOME ]; then
    rm -rf $JBOSS_HOME.OLD
    mv $JBOSS_HOME $JBOSS_HOME.OLD
fi

echo 
# Unzip the JBoss SOA-P instance
echo Unpacking JBoss Enterprise SOA Platform 5.2...
unzip -q -d target installs/soa-p-5.2.0.GA.zip

# Unzip the jboss-brms-manager.zip from JBoss BRMS Deployable
echo Unpacking JBoss Enterprise BRMS 5.2
unzip -q installs/brms-p-5.2.0.GA-deployable.zip jboss-brms-manager.zip 
echo Deploying JBoss Enterprise BRMS Manager WAR
unzip -q -d $SERVER_DIR/deploy jboss-brms-manager.zip
rm jboss-brms-manager.zip

# Add execute permissions to the run.sh script
chmod u+x $JBOSS_HOME/jboss-as/bin/run.sh

cp support/soa-users.properties $SERVER_DIR/conf/props
cp support/bpel.properties $SERVER_DIR/deploy/riftsaw.sar
cp support/droolsfusion-eventlistener.jar $SERVER_DIR/deploy/riftsaw.sar/lib

# Make the required /tmp directories used by the demo. 
# WARNING: If you modify these locations, you will need to modify similar locations
# in the appropriate jboss-esb.xml configuration file!!!
mkdir -p /tmp/inboundLoanApplications

echo 
echo Integration 5.2 Home Loan Demo Setup Complete
echo

