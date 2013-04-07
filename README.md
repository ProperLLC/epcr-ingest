epcr-ingest
===========

The ePCR ingest RESTful web service.

This service will accept an HTTP POST on one of two endpoints: 
* /incident - submit a single incident JSON document for storage
* /incidents - submit multiple incident JSON documents for storage (atomic operation)

It will reject any request to POST to the endpoints that do not pass the HMAC authenitciation scheme.

Setup:
 * PlayFramework v 2.1.1 (required)
 * MongoDB 2.4.1 (Required)
 * IntelliJ IDEA 12.1 (with Scala, SBT, PlayFramework and Grep Console plugins) - (Optional)
 * Scala 2.10.0
 * JDK 1.7.0_17

If working on a Mac, Homebrew is recommended (install Play, Mongo and Scala from there)
