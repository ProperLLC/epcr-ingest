epcr-ingest
===========

The ePCR ingest RESTful web service.

This service will accept an HTTP POST on one of two endpoints: 
* /incident - submit a single incident JSON document for storage
* /incidents - submit multiple incident JSON documents for storage (atomic operation)

It will reject any request to POST to the endpoints that do not pass the HMAC authenitciation scheme.
