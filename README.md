# Java PD-Code Library

This Java library supports to handling 3D PD-Code (e.g., encoding and decoding.)

Note that, PD-Code is one of type of Morton code, especially DGGS Morton for the point cloud.
It is introduced from "Utilizing a discrete global grid system for handling point clouds with varying locations, times, and levels of detail."

From the 0.5.0 version, this project includes the [Geogrid project](https://github.com/mocnik-science/geogrid) code for using ISEA projection. It is a modified fit for this project.

## Required
- Maven: 3.5+
- Java: 1.8+

## How to use
- Using Maven: Put below code in your POM file
```xml
<repositories>
    <repository>
        <id>github</id>
        <name>GitHub OWNER Apache Maven Packages</name>
        <url>https://maven.pkg.github.com/TaehoonK/dggs_code</url>
    </repository>
</repositories>
...
<dependencies>
    <dependency>
        <groupId>aist.dprt</groupId>
        <artifactId>dggs-code</artifactId>
        <version>0.5.1</version>
    </dependency>
</dependencies>
```
- Other cases: Using jar file directly
    -  https://github.com/TaehoonK/dggs_code/packages/273033

You can browse the [javadoc](https://taehoonk.github.io/dggs_code/apidocs/index.html) for more information.

## Reference
- Kim, Taehoon, et al. "Utilizing extended geocodes for handling massive three-dimensional point cloud data." World Wide Web (2020): 1-24.
- Mocnik, Franz-Benjamin. "A novel identifier scheme for the isea aperture 3 hexagon discrete global grid system." Cartography and geographic information science 46.3 (2019): 277-291.
- Kim, Taehoon, et al. "Efficient Encoding and Decoding Extended Geocodes for Massive Point Cloud Data." 2019 IEEE International Conference on Big Data and Smart Computing (BigComp). IEEE, 2019.
- Sirdeshmukh, Neeraj, et al. "Utilizing a discrete global grid system for handling point clouds with varying locations, times, and levels of detail." Cartographica: The International Journal for Geographic Information and Geovisualization 54.1 (2019): 4-15.  

## Developer
- Taehoon Kim, kim.taehoon@aist.go.jp
- Akiyoshi Matono, a.matono@aist.go.jp

## License
This project is under the MIT License - see the [License](https://github.com/TaehoonK/dggs_code/blob/master/LICENSE)
