# personium-plugin-sample

## About
Sample of [personium](https://personium.io) authentication plugin.

## Installation
Please see [Setup Authentication Plugins](https://personium.io/docs/en/server-operator/setup_authentication_plugins.html).

## Details
Account type:"auth:sample"  
grant_type:"urn:x-personium:auth:sample"  

Behavior  
- "personium" is specified for sample_password
  - Authentication successful with the account specified by sample_account
- sample_password other than "personium" is specified
  - Authentication failure

## Example of calling this plugin
1. Create target account
```
curl "https://{UnitFQDN}/{CellName}/__ctl/Account" -X POST -i -H 'Authorization: Bearer {AccessToken}' -H 'Accept: application/json' -d '{"Name":"sample","Type":"auth:sample"}'
```
2. Authentication
```
curl "https://{UnitFQDN}/{CellName}/__token" -X POST -i -d 'grant_type=urn:x-personium:auth:sample&sample_account=sample&sample_password=personium'
```

## License

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Copyright 2018 FUJITSU LIMITED
```
