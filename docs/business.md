# Business Details Stub

All the endpoints related to gambling business are listed below

## Controller

uk.gov.hmrc.gamblingexternalstub.controllers.rdsDataCacheProxy.GamblingBusinessController

## Available Endpoints

### 1. Get Business Address Details

The business-address endpoint returns gambling business address details based on the provided mgdRegNumber. 
Different mgdRegNumber values yield different responses to simulate retriving successful response with value
and successful response with empty data.


| mgdRegNumber           | HTTP Status               | Description                                              |
|:-----------------------|---------------------------|----------------------------------------------------------|
| XGM00000001761         | 200 OK                    | Success response with address details                    |
| Any other mgdRegNumber | 200 OK                    | Success response with no address details (Empty Respone) |
| invalid                | 400 BAD REQUEST           | Bad request                                              |
| error                  | 500 INTERNAL SERVER ERROR | Internal server error                                    |
