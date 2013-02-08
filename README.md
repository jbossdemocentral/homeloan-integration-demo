Home Loan Integration Demo Quickstart Guide
===========================================

Demo based on JBoss SOA-P and BRMS products.

Setup and Configuration
-----------------------

See Quick Start Guide in project as ODT and PDF for details on installation. For those that can't wait:

- see README in 'installs' directory

- add products 

- run 'init.sh' & read output

- read Quick Start Guide

- setup JBDS for project import, add soa-p server (adjust server VM args in launch config to '-Xms1303m -Xmx1303m')

- import projects

- deploy esb and bpel projects to soa-p server

- start soa-p server

- login to BRM (jboss-brms)

- import repository-export from support dir

- build and deploy project

- login to Business Central (business-central)

- start process, view soa-p logs for results


Released versions
-----------------

See the tagged releases for the following versions of the product:

- v1.0 is SOA-P 5.3.0, ESB, BPEL, Rules using BRMS 5.3.

- v1.1 adds BPM from BRMS 5.3.

- v1.2 merges mavanize.sh into main init script, init script builds project and deploys local model jars.
