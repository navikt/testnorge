# Testnorge Ereg-mapper

The application provides an interface for creating flatmapped files which simulates the records which is fetched from 
BREG. It also provides an interface for submitting these files to the batch executor for EREG. 

## About the automatic generation of data

The name and næringskode attributes are generated through a csv file containing the records. This file is
created from the the values in _Kodeverk_ and the definition of NACE næringskoder. _Kodeverk_ takes 
presidence and contains the actual values, but not a good enough description of the name and purpose.

A simple script is used to generate this file and the copied in to the repository. This was a simpler 
solution than including auto-generated classes from a WSDL to parse the request to _Kodeverk_. 