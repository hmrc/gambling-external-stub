# Business Details Stub

All the endpoints related to gambling business are listed below

## Controller

uk.gov.hmrc.gamblingexternalstub.controllers.rdsDataCacheProxy.GamblingBusinessController

## Available Endpoints

### 1. Get Business Address Details

The business-address endpoint returns gambling business address details based on the provided mgdRegNumber. 
Different mgdRegNumber values yield different responses to simulate retriving successful response with value
and successful response with empty data.


| mgdRegNumber    | HTTP Status               | Description                                              |
|:----------------|---------------------------|----------------------------------------------------------|
| XGM00000001761  | 200 OK                    | Success response with full address details               |
| XGM00000001762  | 200 OK                    | Success response with partial address details            |
| XGM00000000400  | 400 BAD REQUEST           | Bad request                                              |
| XGM00000000401  | 401 UNAUTHORIZED          | Unauthorized                                             |
| XGM00000000500  | 500 INTERNAL SERVER ERROR | Internal server error                                    |
| Any other value | 200 OK                    | Success response with no address details (Empty Respone) |