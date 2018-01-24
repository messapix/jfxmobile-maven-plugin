<#macro table table>
<#list table>
| parameter | description | property | default |
| --------- | ----------- | ------------ | ------- |
<#items as row>
| ${row.parameter} | (${row.type}) ${row.description} | ${row.property!''} | ${row.defaultValue} |
</#items>
</#list>
</#macro>

# jfxmobile-maven-plugin
Maven version of jfxmobile plugin

Waiting for a better documentation take a look at the [JavafxPorts](http://docs.gluonhq.com/javafxports) project documentation.

### Gettin started
The following is an example of configuration:

```
...
<plugin>
  <groupId>com.messapix.ftatr.jfxmobile</groupId>
  <artifactId>jfxmobile-maven-plugin</artifactId>
  <version>1.0.0-b1</version>
  <extensions>true</extensions>
  <configuration>
      <mainClass>test.something.App</mainClass>
      <android>
          <manifest>lib/android/AndroidManifest.xml</manifest>
          <dependencies>
              <dependency>
                  <groupId>com.gluonhq</groupId>
                  <artifactId>charm-down-android</artifactId>
                  <version>0.0.3-SNAPSHOT</version>
                  <scope>runtime</scope>
              </dependency>
          </dependencies>
      </android>
      
      <desktop>
          <dependencies>
              <dependency>
                  <groupId>com.gluonhq</groupId>
                  <artifactId>charm-down-desktop</artifactId>
                  <version>0.0.3-SNAPSHOT</version>
                  <scope>runtime</scope>
              </dependency>
          </dependencies>
      </desktop>
      
      <ios>
          <dependencies>
              <dependency>
                  <groupId>com.gluonhq</groupId>
                  <artifactId>charm-down-ios</artifactId>
                  <version>0.0.3-SNAPSHOT</version>
                  <scope>runtime</scope>
              </dependency>
          </dependencies>
      </ios>
  </configuration>
</plugin>
...
```

Note that you must set ```<extensions>true</extensions>``` in order to work with the plugin.

To compile android apk run:
```mvn clean package android```

### General

<@table generalTable></@table>

### Android
Android parameters are wrapped in ```<android>``` element.

<@table androidTable></@table>

### Desktop
Desktop parameters are wrapped in ```<desktop>``` element.

<@table desktopTable></@table>

### iOS
iOS parameters are wrapped in ```<ios>``` element.

<@table iosTable></@table>

#### iOS Simulator
The following parameters are used mostly as properties passed to maven at runtime. Anyway they can be set in pom.xml as well.

<@table iosSimTable></@table>

### Embedded
Embedded parameters are wrapped in ```<embedded>``` element.

<@table embeddedTable></@table>

#### Embedded platforms
<@table embeddedPlatformTable></@table>
