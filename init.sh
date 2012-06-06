#!/bin/sh 
JBOSS_HOME=./target/jboss-soa-p-5
SERVER_DIR=$JBOSS_HOME/jboss-as/server/default
INBOUND_DIR=/tmp/inboundLoanApplications

echo
echo Setting up the Home Loan SOA-P + BRMS demo environment...
echo

# Make the required /tmp directories used by the demo. 
# WARNING: If you modify these locations, you will need to modify similar locations
# in the appropriate jboss-esb.xml configuration file!!!
if [ -x $INBOUND_DIR ]; then
	echo Listener directory $INBOUND_DIR exists, no need to make it again...
	echo
else
	echo Creating Listener directory $INBOUND_DIR for demo...
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
	echo Creating the target directory...
	echo
  mkdir target
else
	echo Detected target directory, moving on...
	echo
fi

# Move the old JBoss instance, if it exists, to the OLD position
if [ -x $JBOSS_HOME ]; then
	echo Existing JBoss Enterprise SOA Platform 5.2 detected...
	echo
	echo Moving existing JBoss Enterprise SOA Platform 5.2 aside...
	echo
  rm -rf $JBOSS_HOME.OLD
  mv $JBOSS_HOME $JBOSS_HOME.OLD

	# Unzip the JBoss SOA-P instance
	echo Unpacking JBoss Enterprise SOA Platform 5.2...
	echo
	unzip -q -d target installs/soa-p-5.2.0.GA.zip
else
	# Unzip the JBoss SOA-P instance
	echo Unpacking new JBoss Enterprise SOA Platform 5.2...
	echo
	unzip -q -d target installs/soa-p-5.2.0.GA.zip
fi


# Unzip the jboss-brms-manager.zip from JBoss BRMS Deployable
echo Unpacking JBoss Enterprise BRMS 5.2...
echo
unzip -q installs/brms-p-5.2.0.GA-deployable.zip jboss-brms-manager.zip 
echo Deploying JBoss Enterprise BRMS Manager WAR...
echo
unzip -q -d $SERVER_DIR/deploy jboss-brms-manager.zip
rm jboss-brms-manager.zip

# Add execute permissions to the run.sh script
chmod u+x $JBOSS_HOME/jboss-as/bin/run.sh

cp support/soa-users.properties $SERVER_DIR/conf/props
cp support/bpel.properties $SERVER_DIR/deploy/riftsaw.sar
cp support/droolsfusion-eventlistener.jar $SERVER_DIR/deploy/riftsaw.sar/lib

echo 
echo Integration 5.2 Home Loan Demo Setup Complete.
echo

