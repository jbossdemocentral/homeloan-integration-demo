Home Loan Integration Demo Quickstart Guide
===========================================

Demo based on JBoss SOA-P and BRMS products.


Supporting Articles
-------------------

[Ready to Rubmble with JBoss Integration with video] (http://www.schabell.org/2013/02/home-loan-demo-with-bpm-integration.html)

[How to setup SOA Tools in BRMS Example for JBoss Dev Studio 7] (http://www.schabell.org/2013/04/jboss-developer-studio-7-how-to-setup.html)

[How to setup SOA Tools in BRMS Example for JBoss Dev Studio 6] (http://www.schabell.org/2013/04/jboss-developer-studio-6-how-to-setup.html)

[How to setup SOA Tools in BRMS Example for JBoss Dev Studio 5] (http://www.schabell.org/2012/05/jboss-developer-studio-5-how-to-setup.html)

[How to add Eclipse BPMN2 Modeller project to JBoss Dev Studio 5] (http://www.schabell.org/2013/01/jbds-bpmn2-modeler-howto-install.html)

[Demo now available with Windows installation scripts] (http://www.schabell.org/2013/04/jboss-brms-demos-available-windows.html)


Setup and Configuration
-----------------------

See Quick Start Guide in project as ODT and PDF for details on installation. For those that can't wait:

- see README in 'installs' directory

- add products 

- run 'init.sh' & read output

- read Quick Start Guide

- setup JBDS for project import, add soa-p server (adjust server VM args in launch config to '-Xms1303m -Xmx1303m')
    (note: need to create server instance seperately due to https://issues.jboss.org/browse/JBDS-2481)

- import projects

- deploy esb and bpel projects to soa-p server

- start soa-p server

- login to BRM (jboss-brms)

- import repository-export from support dir

- build and deploy project

- login to Business Central (business-central)

- start process, view soa-p logs for results

Windows users see support/windows/README for installation.


Released versions
-----------------

See the tagged releases for the following versions of the product:

- v1.6 added patched web process designer, code editor no longer losing EOL's.

- v1.5 added Windows installation scripts.

- v1.4 updated to add SOA-P 5.3.1 release.

- v1.3 is SOA-P 5.3.0, ESB, BPEL, Rules using BRMS 5.3.1. 

- v1.2 merges mavanize.sh into main init script, init script builds project and deploys local model jars.

- v1.1 adds BPM from BRMS 5.3.0

- v1.0 is SOA-P 5.3.0, ESB, BPEL, Rules using BRMS 5.22.0

