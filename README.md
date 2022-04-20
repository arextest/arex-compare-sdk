# AREX-COMPARE-SDK

Arex-compare-sdk is a Java library that can be used to compare two JSON messages.

# FETURE

- Out-of-order comparsion
  Supports out-of-order comparison of arrays with limited configuration.

- Exclusion/Inclusion
  Inclusion means that you can specify nodes in the interface you want to compare.
  Exclusion means that you can specify nodes in the interface you want to ignore.

- The support for Map

  When configuring, use the symbol "*"  to replace the variable key

- Node decompression

  Dynamically load decompression classes into SDK with SPI

# DOCUMENT

https://github.com/arextest/arex-compare-sdk/wiki

# DOWNLOAD

- **COMPILE**

```
git clone https://github.com/arextest/arex-compare-sdk.git
cd arex-compare-sdk
mvn install
```

- **MAVEN**

```
<dependency>
	<groupId>io.arex</groupId>
	<artifactId>compare-sdk</artifactId>
	<version>0.0.1</version>
</dependency>
<repository>
	<id>github</id>
	<url>https://raw.github.com/arextest/arex-jar-respository/main</url>
	<snapshots>
		<enabled>true</enabled>
		<updatePolicy>always</updatePolicy>
	</snapshots>
</repository>
```



# LICESE

```
Copyright (C) 2022 ArexTest

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see https://www.gnu.org/licenses/.
```

