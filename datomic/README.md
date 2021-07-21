# datomic

Examples of working with Datomic

## Usage

Download Datomic dev-local following the instructions here https://docs.datomic.com/cloud/dev-local.html

You will get an email with credentials, you will need to make them available to lein like so:

```
export LEIN_DATOMIC_USERNAME=<username>
export LEIN_DATOMIC_PASSWORD=<password>
```

To verify it is configured correctly run

```
$ lein repl

datomic.core=> (System/getenv "LEIN_PASSWORD")
"<your password">
```


## License

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
