# notify

This is a web service that will send out notification emails according to the schedule set in a google spreadsheet formated to this https://docs.google.com/spreadsheets/d/1qqk07dvIbQpy7wPSIyJWkwvfgxj-uvc4PVb5pNoh6RM/edit?usp=sharing

The idea is that people who are coordinating can use a familiar UI - google spreadsheet to do the scheduling. And this web service will be activated either by a visit to the exposed web url or by a cron job that is supported by Google App Engine.

# Setup:

1. Data source: Google spreadsheet
2. Mailing service: Google mail service
3. Hosting: Google App Engine and its cron job

I try to refactor the code in a way to allow plugging in different data source other than google spreadsheet and different mailing service e.g. AWS SNS.  I create this with my own use cases in mind and try to make it as generic. As this is just a pet project which I built using time squeezed out of my day time job and kids/family time, it is not at the level of code quality and feature richness I would like it to be.
