#!/bin/sh 
JBOSS_HOME=./target/jboss-soa-p-5
SERVER_DIR=$JBOSS_HOME/jboss-as/server/default
INBOUND_DIR=/tmp/inboundLoanApplications
SRC_DIR=./installs
SOA_P=soa-p-5.2.0.GA.zip
BRMS=brms-p-5.2.0.GA-deployable.zip


echo
echo Setting up the Home Loan SOA-P + BRMS demo environment...
echo

# make some checks first before proceeding.	
if [[ -x $SRC_DIR/$SOA_P || -L $SRC_DIR/$SOA_P ]]; then
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
	echo "  - existing JBoss Enterprise SOA Platform 5.2 detected..."
	echo
	echo "  - moving existing JBoss Enterprise SOA Platform 5.2 aside..."
	echo
  rm -rf $JBOSS_HOME.OLD
  mv $JBOSS_HOME $JBOSS_HOME.OLD

	# Unzip the JBoss SOA-P instance
	echo Unpacking JBoss Enterprise SOA Platform 5.2...
	echo
	unzip -q -d target $SRC_DIR/$SOA_P
else
	# Unzip the JBoss SOA-P instance
	echo Unpacking new JBoss Enterprise SOA Platform 5.2...
	echo
	unzip -q -d target $SRC_DIR/$SOA_P
fi

# Unzip the jboss-brms-manager.zip from JBoss BRMS Deployable
echo Unpacking JBoss Enterprise BRMS 5.2...
echo
unzip -q $SRC_DIR/$BRMS jboss-brms-manager.zip 

echo Deploying JBoss Enterprise BRMS Manager WAR...
echo
unzip -q -d $SERVER_DIR/deploy jboss-brms-manager.zip
rm jboss-brms-manager.zip

# Add execute permissions to the run.sh script
echo "  - making sure run.sh for server is executable..."
echo
chmod u+x $JBOSS_HOME/jboss-as/bin/run.sh

echo "  - enabling admin account in soa-users.properties file..."
echo
cp support/soa-users.properties $SERVER_DIR/conf/props

echo "  - registering an additional RiftSaw event listner in bpel.properties file..."
echo
cp support/bpel.properties $SERVER_DIR/deploy/riftsaw.sar

echo "  - copying custom RiftSaw event listner implementation jar to project..."
echo 
cp support/droolsfusion-eventlistener.jar $SERVER_DIR/deploy/riftsaw.sar/lib

echo Integration 5.2 Home Loan Demo Setup Complete.
echo
