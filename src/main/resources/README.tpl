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

### iOS Simulator
The following parameters are used mostly as properties passed to maven at runtime. Anyway they can be set in pom.xml as well.

<@table iosSimTable></@table>