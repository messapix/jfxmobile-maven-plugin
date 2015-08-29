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

import java.lang.reflect.Field;
import org.apache.maven.plugin.AbstractMojo;

/**
 *
 * @author Alfio Gloria
 */
public class MojoUtils {
    private final Object instance;

    public MojoUtils( Object instance ) {
        this.instance = instance;
    }

    public void setParam( String name, Object value ) {
        setParam( instance.getClass().getSuperclass(), name, value );
    }

    public <T> T getComponent( Class<T> clazz, String name ) {
        try {
            Field field = instance.getClass().getSuperclass().getDeclaredField( name );
            field.setAccessible( true );
            return clazz.cast( field.get( instance ) );
        }
        catch ( NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex ) {
            throw new RuntimeException( ex );
        }
    }

    private void setParam( Class<?> clazz, String name, Object value ) {
        try {
            Field field = clazz.getDeclaredField( name );
            field.setAccessible( true );
            field.set( instance, value );
        }
        catch ( SecurityException | IllegalArgumentException | IllegalAccessException ex ) {
            throw new RuntimeException( ex );
        }
        catch ( NoSuchFieldException ex ) {
            Class superClazz = clazz.getSuperclass();

            if ( superClazz != null && !superClazz.equals( Object.class ) && !superClazz.equals( AbstractMojo.class ) ) {
                setParam( superClazz, name, value );
            }
            else {
                throw new RuntimeException( ex );
            }
        }
    }
}
