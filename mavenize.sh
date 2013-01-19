#!/bin/bash
SRC_DIR=./installs
BRMS=brms-p-5.3.0.GA-deployable.zip
VERSION=5.3.0.BRMS

command -v mvn -q >/dev/null 2>&1 || { echo >&2 "Maven is required but not installed yet... aborting."; exit 1; }

echo
echo Installing the BRMS binaries into the Maven repository...
echo

unzip -q $SRC_DIR/$BRMS jboss-brms-engine.zip
unzip -q jboss-brms-engine.zip binaries/*
cd binaries

echo Installing Drools binaries...
echo
mvn -q install:install-file -Dfile=drools-ant-$VERSION.jar -DgroupId=org.drools -DartifactId=drools-ant -Dversion=$VERSION -Dpackaging=jar
mvn -q install:install-file -Dfile=drools-camel-$VERSION.jar -DgroupId=org.drools -DartifactId=drools-camel -Dversion=$VERSION -Dpackaging=jar
mvn -q install:install-file -Dfile=drools-compiler-$VERSION.jar -DgroupId=org.drools -DartifactId=drools-compiler -Dversion=$VERSION -Dpackaging=jar
mvn -q install:install-file -Dfile=drools-core-$VERSION.jar -DgroupId=org.drools -DartifactId=drools-core -Dversion=$VERSION -Dpackaging=jar
mvn -q install:install-file -Dfile=drools-decisiontables-$VERSION.jar -DgroupId=org.drools -DartifactId=drools-decisiontables -Dversion=$VERSION -Dpackaging=jar
mvn -q install:install-file -Dfile=droolsjbpm-ide-$VERSION.jar -DgroupId=org.drools -DartifactId=droolsjbpm-ide -Dversion=$VERSION -Dpackaging=jar
mvn -q install:install-file -Dfile=drools-jsr94-$VERSION.jar -DgroupId=org.drools -DartifactId=drools-jsr94 -Dversion=$VERSION -Dpackaging=jar
mvn -q install:install-file -Dfile=drools-persistence-jpa-$VERSION.jar -DgroupId=org.drools -DartifactId=drools-persistence-jpa -Dversion=$VERSION -Dpackaging=jar
mvn -q install:install-file -Dfile=drools-templates-$VERSION.jar -DgroupId=org.drools -DartifactId=drools-templates -Dversion=$VERSION -Dpackaging=jar
mvn -q install:install-file -Dfile=drools-verifier-$VERSION.jar -DgroupId=org.drools -DartifactId=drools-verifier -Dversion=$VERSION -Dpackaging=jar
mvn -q install:install-file -Dfile=knowledge-api-$VERSION.jar -DgroupId=org.drools -DartifactId=knowledge-api -Dversion=$VERSION -Dpackaging=jar

echo Installing jBPM binaries...
echo
mvn -q install:install-file -Dfile=jbpm-bam-$VERSION.jar -DgroupId=org.jbpm -DartifactId=jbpm-bam -Dversion=$VERSION -Dpackaging=jar
mvn -q install:install-file -Dfile=jbpm-bpmn2-$VERSION.jar -DgroupId=org.jbpm -DartifactId=jbpm-bpmn2 -Dversion=$VERSION -Dpackaging=jar
mvn -q install:install-file -Dfile=jbpm-flow-$VERSION.jar -DgroupId=org.jbpm -DartifactId=jbpm-flow -Dversion=$VERSION -Dpackaging=jar
mvn -q install:install-file -Dfile=jbpm-flow-builder-$VERSION.jar -DgroupId=org.jbpm -DartifactId=jbpm-flow-builder -Dversion=$VERSION -Dpackaging=jar
mvn -q install:install-file -Dfile=jbpm-human-task-$VERSION.jar -DgroupId=org.jbpm -DartifactId=jbpm-human-task -Dversion=$VERSION -Dpackaging=jar
mvn -q install:install-file -Dfile=jbpm-persistence-jpa-$VERSION.jar -DgroupId=org.jbpm -DartifactId=jbpm-persistence-jpa -Dversion=$VERSION -Dpackaging=jar
mvn -q install:install-file -Dfile=jbpm-test-$VERSION.jar -DgroupId=org.jbpm -DartifactId=jbpm-test -Dversion=$VERSION -Dpackaging=jar
mvn -q install:install-file -Dfile=jbpm-workitems-$VERSION.jar -DgroupId=org.jbpm -DartifactId=jbpm-workitems -Dversion=$VERSION -Dpackaging=jar

cd ..
rm -rf binaries
rm jboss-brms-engine.zip

echo Installation of binaries "for" BRMS $VERSION complete.
echo

