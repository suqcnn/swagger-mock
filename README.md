# swagger-mock

Runs an HTTP server which mocks all requests specified in a Swagger 2.0 definition.

## Installation

    $ lein uberjar

## Usage

    $ java -jar swagger-mock.jar <swagger-yaml-file>

The server will use the lowest non-default response code of your response definition (mostly 200) and look for the
example in your object schema definition. The server assumes that responses are application/json; if it discovers a
string in the example, it will parse the string as json, else it will take the given data structure as the response.

## Options

You can pass in all variables that the Jetty Ring adapter supports. The most important variable is 'port'
which can be passed as an environment variable:

    $ PORT=9191 java -jar swagger-mock.jar myapp.yaml

## License

Copyright © 2015 Zalando SE

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
