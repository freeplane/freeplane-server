# Design
* http://www.plantuml.com/plantuml/uml/ZLDFRnC_4BtxKqnp-JNwjO4xLLKfSE2GYf8YpbvxafYiPoAFDw50lpkpThRPbWfnSJAUzzxyUUiRoJRbBhPc4pm2nbr-W8YnmIJ6a8qeE-iGF4SRQ1RjJk2d-T-rLWGMDGAOCxak9QIiLt2bnqT_qQS_upv8OBg6U0f46GdNGJAcMpmexDorGSaNFUfMQquBmlhdAHNj4xl43aKqzsNdRKPvWdGrdeAYV137HEZoCJqrZpeGPuGMLnbuLTjNx6kIHGC4oGj8jhaJY8gmQuJCK1KhnEvH86_mM1q2bKWo9-m1bZmusxO2yyAIMQ_wLCTe07rRnfH--FAoRsnPwCFDG3KcKCQqiWvF85FDg93gX-O64-RSjkXoOBggYElYHQs_x1fnAQZPIbe-aDia9kxahiCEhuQr7WSVyUITKO8CxFDOQmsB1kZeBydn_3KvSkxncKV3L2zOKmWacBBeRsPOtbtqZZCA_PV1sOHW6-xoO8bZogfofg1eIKSHi31NJspJuflxJKBhFp6tiE841qxRG6ijlb9X0tf0jbkKAgmzSVAO579s1YFtXlrFtavwowvXGfDZ-sFRwarnIOuB3p_Gb_4MTT7dnuMNB_Pn_0gjjpiLaja16s6tnU9tzVyhDijojSIqnrGgR2pv5k_nUnwa-_r_isuhJ2hzagDjZr_wRA67EQ5gxuhwzHmsJFcYaT6eXrCXaS3qZzE_FdnaMhDlL7ZH_tlVVDXhdV8O_knjU2QgTQD5kjZ-1W00

# Installing / configuring mongodb
* Install mongodb server ("mongod") and mongodb client ("mongo", but there are also GUI clients)
  (these are usually in one package)
* if necessary: change spring.data.mongodb.* in application.properties
* use (databases are automatically created :-))

# Getting started
- checkout https://github.com/freeplane/freeplane-events
- gradle build publishToMavenLocal
- checkout https://github.com/freeplane/freeplane-server
- gradle build cleanEclipse eclipse
- checkout https://github.com/freeplane/freeplane
- gradle checkout client
- gradle build cleanEclipse eclipse
* run a basic use case:
  * run org.freeplane.server.Application (as Java Application) from server repo
  * run freeplane-osgi launcher from freeplane repo
    * select the websockets check box
	* click button -> message interchange is triggered
    
# TODO:
* One client
    * Connect to server (web socket session) *check*
    * Create a new map on server and get its uuid *check*
    * Send updates to server and receive them back
    * Subscribes to updates for a given map id
    * Receives all updates from server for a given map id
        * From the beginning
        * From given map revision
        * Optimize: server skips overwritten updates of the same element (later)
* Many clients
    * Servers orders all updates from all clients, server changes map revisions for later updates if necessary
    * Server checks for conflicts in updates having the same map revision in the input events and notifies clients (later)
* OAuth2 authentication 
    * Check authentication (full access for any clients with valid credentials, may be JWT tokens)
    * Manage write and read permissions for a map (API or browser app ?)

** Other issues
    * Extract events and serialization in a separate project to be used from both client and server *check*
