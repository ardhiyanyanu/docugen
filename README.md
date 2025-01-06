# Docugen

docugen is service api service to turn html, rtf, or odt file to PDF with input data.

## Architecture

This service use spring boot webflux and use reactive based programming. To generate PDF from odt or rtf file then this service will use LibreOffice API.

## Feature

Right now, this service only have endpoint to upload template and generate single pdf from template registered. This service will be maintained and more feature will be available like endpoint security, more feature on generating pdf like custom image and font, getting data from external source, etc. 