<div align="center">
# XML Manager Library
<i>Library for reading and writing of specific openBIS xml properties </i>

[![Build Maven Package](https://github.com/qbicsoftware/xml-manager-lib/actions/workflows/build_package.yml/badge.svg)](https://github.com/qbicsoftware/xml-manager-lib/actions/workflows/build_package.yml)
[![Run Maven Tests](https://github.com/qbicsoftware/xml-manager-lib/actions/workflows/run_tests.yml/badge.svg)](https://github.com/qbicsoftware/xml-manager-lib/actions/workflows/run_tests.yml)
[![CodeQL](https://github.com/qbicsoftware/xml-manager-lib/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/qbicsoftware/xml-manager-lib/actions/workflows/codeql-analysis.yml)
[![release](https://img.shields.io/github/v/release/qbicsoftware/xml-manager-lib?include_prereleases)](https://github.com/qbicsoftware/xml-manager-lib/releases)

[![license](https://img.shields.io/github/license/qbicsoftware/xml-manager-lib)](https://github.com/qbicsoftware/xml-manager-lib/blob/main/LICENSE)
![language](https://img.shields.io/badge/language-java-blue.svg)

</div>

## How to run

Compile the project with Maven and Java 8 and build an executable java archive:

```
mvn clean package
```

The JAR file will be created in the ``/target`` folder

## How to use

This library is not hosted on maven central. To use it, you have to include our artifact repository to your pom.

```xml
<repositories>
    <repository>
        <releases>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
            <checksumPolicy>fail</checksumPolicy>
        </releases>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
        <id>nexus-releases</id>
        <name>QBiC Releases</name>
        <url>https://qbic-repo.qbic.uni-tuebingen.de/repository/maven-releases</url>
    </repository>
</repositories>
```

Then include this library as an artifact.
```xml
<dependency>
    <groupId>life.qbic</groupId>
    <artifactId>xml-manager-lib</artifactId>
    <version>[version]</version>
</dependency>
```
