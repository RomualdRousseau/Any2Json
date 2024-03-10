
# Any2Json

![Test](https://github.com/RomualdRousseau/Any2Json/actions/workflows/build-and-test.yml/badge.svg)
![Build](https://github.com/RomualdRousseau/Any2Json/actions/workflows/build-and-deploy.yml/badge.svg)
![ShuJu](https://maven-badges.herokuapp.com/maven-central/com.github.romualdrousseau/shuju/badge.svg)

This is a java API to manipulate semi structured documents and extract data from them.

## Description

In today's data-driven landscape, navigating the complexities of semi-structured documents poses a significant challenge
for organizations. These documents, characterized by diverse formats and a lack of standardization, often require
specialized skills for effective manipulation and analysis. However, we propose a novel framework to address this
challenge. By leveraging innovative algorithms and machine learning techniques, [Any2Json](https://github.com/RomualdRousseau/Any2Json)
offers a solution that transcends manual coding, providing enhanced accessibility to users across diverse skill levels.
Moreover, by automating the extraction process, it not only saves time but also minimizes errors, particularly beneficial
for industries dealing with large volumes of such documents. Crucially, this framework integrates seamlessly with machine
learning workflows, unlocking new possibilities for data enrichment and predictive modeling. Aligned with the paradigm of
data as a service, it offers a scalable and efficient means of managing semi-structured data, thereby expanding the toolkit
of data services available to organizations.

## Getting Started

### Dependencies

* The Java Developer Kit, version 17.
* Apache Maven, version 3.0 or above.

### Apache Maven Installation

For more details, see the [Installation Guide](https://maven.apache.org/install.html).

#### Update dependencies

Run the following command line:

```bash
mvn -DcreateChecksum=true versions:display-dependency-updates
```

#### Update pom.xml plugins

Run the following command line:

```bash
mvn -DcreateChecksum=true versions:display-plugin-updates
```

### Build and install locally

Run the following command line:

```bash
mvn clean install
```

### Build and deploy a snapshot to the Maven repository

Run the following command line:

```bash
mvn -P snapshot clean deploy
```

### Build and deploy a release to the Maven repository

Run the following command line:

```bash
mvn -P release clean deploy
```

### Build and deploy the javadoc documentation

Run the following command line:

```bash
mvn -P documentation clean site
```

### Documentation

* Please find the project documentation on [github.io](https://romualdrousseau.github.io/Any2Json-Documents/).
* Please find the javadoc documentation on [github.io](https://romualdrousseau.github.io/Any2Json/).

## Authors

* Romuald Rousseau, romuald.rousseau@servier.com

## Version History

* 2.37
* ...
* Initial Release
