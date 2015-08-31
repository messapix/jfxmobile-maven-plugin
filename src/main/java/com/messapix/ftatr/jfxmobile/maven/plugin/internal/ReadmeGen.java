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
package com.messapix.ftatr.jfxmobile.maven.plugin.internal;

import com.messapix.ftatr.jfxmobile.maven.plugin.MobileConf;
import com.messapix.ftatr.jfxmobile.maven.plugin.TemplateUtils;
import com.messapix.ftatr.jfxmobile.maven.plugin.android.model.AndroidData;
import com.messapix.ftatr.jfxmobile.maven.plugin.annotations.AsProperty;
import com.messapix.ftatr.jfxmobile.maven.plugin.annotations.Descriptor;
import com.messapix.ftatr.jfxmobile.maven.plugin.desktop.model.DesktopData;
import com.messapix.ftatr.jfxmobile.maven.plugin.ios.model.IosData;
import com.messapix.ftatr.jfxmobile.maven.plugin.ios.model.Simulator;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Used to generate README.md from template
 *
 * @author Alfio Gloria
 */
public class ReadmeGen {
    public static void main( String[] args ) throws MojoExecutionException {
        System.out.println( Paths.get( "." ).toAbsolutePath().toString() );

        Map<String, List<ParameterRow>> root = new HashMap<>();
        root.put( "generalTable", processDescriptorsInClass( MobileConf.class, "jfxmobile" ) );
        root.put( "androidTable", processDescriptorsInClass( AndroidData.class, "jfxmobile.android" ) );
        root.put( "desktopTable", processDescriptorsInClass( DesktopData.class, "jfxmobile.desktop" ) );
        root.put( "iosTable", processDescriptorsInClass( IosData.class, "jfxmobile.ios" ) );
        root.put( "iosSimTable", processDescriptorsInClass( Simulator.class, "jfxmobile.ios.simulator" ) );

        TemplateUtils.process( "README.tpl", Paths.get( "README.md" ).toAbsolutePath(), root );
    }

    private static List<ParameterRow> processDescriptorsInClass( Class clazz, String propertyPrefix ) {
        List<ParameterRow> parameterRows = new ArrayList<>();

        for ( Field field : clazz.getDeclaredFields() ) {
            if ( field.isAnnotationPresent( Descriptor.class ) ) {
                Descriptor descriptor = field.getAnnotation( Descriptor.class );

                ParameterRow parameterRow = new ParameterRow();
                parameterRow.setParameter( field.getName() );
                parameterRow.setType( field.getType().getSimpleName() );
                parameterRow.setDescription( descriptor.desc() );
                parameterRow.setDefaultValue( descriptor.defaultValue() );

                if ( field.isAnnotationPresent( AsProperty.class ) ) {
                    parameterRow.setProperty( propertyPrefix + "." + field.getName() );
                }

                parameterRows.add( parameterRow );
            }
        }

        return parameterRows;
    }
}
