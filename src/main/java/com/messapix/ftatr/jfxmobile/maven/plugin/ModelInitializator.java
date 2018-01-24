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
package com.messapix.ftatr.jfxmobile.maven.plugin;

import com.messapix.ftatr.jfxmobile.maven.plugin.annotations.AsProperty;
import com.messapix.ftatr.jfxmobile.maven.plugin.annotations.Default;
import com.messapix.ftatr.jfxmobile.maven.plugin.annotations.Inspect;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

/**
 *
 * @author Alfio Gloria
 */
@Component( role = ModelInitializator.class )
public class ModelInitializator {
    @Requirement
    private MobileExpressionEvaluator evaluator;

    @Requirement
    private FileSystem fs;

    public void process( Object object, String propertyPrefix ) throws MojoExecutionException {
        for ( Field field : object.getClass().getDeclaredFields() ) {
            boolean isAccessible = field.isAccessible();
            field.setAccessible( true );

            if ( field.isAnnotationPresent( AsProperty.class ) ) {
                String propertyName = field.getAnnotation( AsProperty.class ).value();

                if ( propertyName == null || propertyName.trim().isEmpty() ) {
                    propertyName = field.getName();
                }

                String propertyFullName = propertyPrefix + "." + propertyName;
                String propertyValue;
                try {
                    propertyValue = evaluator.evaluate( "${" + propertyFullName + "}" );
                }
                catch ( ExpressionEvaluationException ex ) {
                    throw new MojoExecutionException( "Error", ex );
                }

                String separator = field.getAnnotation( AsProperty.class ).separator();

                if ( propertyValue != null ) {
                    try {
                        if ( field.getType().equals( List.class ) ) {
                            field.set( object, toList( propertyValue, separator ) );
                        }
                        else {
                            field.set( object, convert( propertyValue, field.getType(), propertyFullName ) );
                        }
                    }
                    catch ( IllegalArgumentException | IllegalAccessException ex ) {
                        // DO NOTHING
                    }
                }
            }

            if ( field.isAnnotationPresent( Default.class ) ) {
                String defVal = field.getAnnotation( Default.class ).value();
                String separator = field.getAnnotation( Default.class ).separator();

                try {
                    if ( nullOrEmpty( field.get( object ) ) ) {
                        if ( field.getType().equals( List.class ) ) {
                            field.set( object, toList( defVal, separator ) );
                        }
                        else {
                            field.set( object, convert( defVal, field.getType(), "default value for " + field.getName() ) );
                        }
                    }
                }
                catch ( IllegalArgumentException | IllegalAccessException ex ) {
                    // DO NOTHING
                }
            }

            if ( field.isAnnotationPresent( Inspect.class ) ) {
                String propertyName = field.getAnnotation( Inspect.class ).value();
                String propertyBase = field.getAnnotation( Inspect.class ).base();

                if ( propertyName == null || propertyName.trim().isEmpty() ) {
                    propertyName = field.getName();
                }

                String newPrefix;
                if ( propertyBase == null || propertyBase.trim().isEmpty() ) {
                    newPrefix = propertyPrefix + "." + propertyName;
                }
                else {
                    newPrefix = propertyBase;
                }

                try {
                    if ( List.class.equals( field.getType() ) ) {
                        List list = (List)field.get( object );

                        if ( list == null ) {
                            list = new ArrayList();
                            field.set( object, list );
                        }

                        for ( Object item : list ) {
                            try {
                                Method idMethod = item.getClass().getMethod( "getId" );
                                String id = (String)idMethod.invoke( item );
                                process( item, newPrefix + "." + id );
                            }
                            catch ( NoSuchMethodException | SecurityException | InvocationTargetException ex ) {
                                // DO NOTHING
                            }
                        }
                    }
                    else {
                        Object value = field.get( object );
                        if ( value == null ) {
                            value = field.getType().newInstance();
                            field.set( object, value );
                        }

                        process( value, newPrefix );
                    }
                }
                catch ( IllegalArgumentException | IllegalAccessException | InstantiationException ex ) {
                    // DO NOTHING
                }
            }

            field.setAccessible( isAccessible );
        }

    }

    private Object convert( String value, Class<?> type, String propertyName ) throws MojoExecutionException {
        if ( type.equals( String.class ) ) {
            return value;
        }
        else if ( type.equals( File.class ) ) {
            Path path = Paths.get( value );
            if ( path.isAbsolute() ) {
                return path.toFile();
            }
            else {
                return fs.root().resolve( path ).toFile();
            }
        }
        else if ( type.equals( Integer.class ) ) {
            try {
                return Integer.parseInt( value );
            }
            catch ( NumberFormatException ex ) {
                throw new MojoExecutionException( propertyName + " must be an integer" );
            }
        }
        else if ( type.equals( Boolean.class ) ) {
            if ( "true".equalsIgnoreCase( value ) || "false".equalsIgnoreCase( value ) ) {
                return Boolean.parseBoolean( value );
            }
            else {
                throw new MojoExecutionException( propertyName + " must be true or false" );
            }
        }
        else {
            throw new MojoExecutionException( "Internal error. Type convertion not supported. String -> " + type );
        }
    }

    private List<String> toList( String str, String separator ) {
        if ( nullOrEmpty( str ) ) {
            return new ArrayList<>();
        }

        List<String> list = new ArrayList<>();

        for ( String element : str.split( separator ) ) {
            list.add( element.trim() );
        }

        return list;
    }

    private boolean nullOrEmpty( Object object ) {
        if ( object == null ) {
            return true;
        }

        if ( object instanceof String ) {
            return ( (String)object ).trim().isEmpty();
        }

        return false;
    }
}
