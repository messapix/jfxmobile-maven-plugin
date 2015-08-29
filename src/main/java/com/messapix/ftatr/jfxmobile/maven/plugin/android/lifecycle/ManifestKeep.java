/*
 * Copyright 2015 Messapix.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.messapix.ftatr.jfxmobile.maven.plugin.android.lifecycle;

import com.google.common.collect.ImmutableMap;
import com.messapix.ftatr.jfxmobile.maven.plugin.FileSystem;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.AndroidTarget;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Alfio Gloria
 */
@Mojo( name = "android-manifestkeep" )
public class ManifestKeep extends AbstractMojo{
    @Component
    private FileSystem fs;

    private final SAXParser parser;

    public ManifestKeep() {
        try {
            parser = SAXParserFactory.newInstance().newSAXParser();
        }
        catch ( ParserConfigurationException | SAXException ex ) {
            throw new RuntimeException( "Error", ex );
        }
    }

    @Override
    public void execute() throws MojoFailureException, MojoExecutionException {
        try {
            try (Writer writer = new FileWriter( fs.file( AndroidTarget.MANIFESTKEEP ) )) {
                parser.parse( fs.file( AndroidTarget.MANIFEST ), new ManifestHandler( writer ) );

                writer.write( "-keep public class * extends android.app.backup.BackupAgent {\n"
                        + "<init>();\n"
                        + "}\n"
                        + "-keep public class * extends java.lang.annotation.Annotation {\n"
                        + "*;\n"
                        + "}"
                );
            }
        }
        catch ( SAXException | IOException ex ) {
            throw new MojoExecutionException( "Error", ex );
        }

    }

    private static class ManifestHandler extends DefaultHandler {
        private static final String DEFAULT_KEEP_SPEC = "{ <init>(); }";

        private final Writer writer;

        ManifestHandler( Writer writer ) {
            this.writer = writer;
        }

        @Override
        public void startElement( String uri, String localName, String qName, Attributes attr ) {
            Map<String, String> KEEP_SPECS = ImmutableMap.<String, String>builder()
                    .put( "application", "{\n <init>();\n void attachBaseContext(android.content.Context);\n}" )
                    .put( "activity", DEFAULT_KEEP_SPEC )
                    .put( "service", DEFAULT_KEEP_SPEC )
                    .put( "receiver", DEFAULT_KEEP_SPEC )
                    .put( "provider", DEFAULT_KEEP_SPEC )
                    .put( "instrumentation", DEFAULT_KEEP_SPEC )
                    .build();

            String keepSpec = (String)KEEP_SPECS.get( qName );
            if ( keepSpec != null ) {
                try {
                    writer.write( (String)"-keep class " + attr.getValue( "android:name" ) + " " + keepSpec + "\n" );
                }
                catch ( IOException ex ) {
                    throw new RuntimeException( ex );
                }
            }
        }
    }
}
