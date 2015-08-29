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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.shade.ShadeRequest;
import org.apache.maven.plugins.shade.Shader;
import org.apache.maven.plugins.shade.filter.Filter;
import org.apache.maven.plugins.shade.filter.SimpleFilter;
import org.apache.maven.plugins.shade.relocation.Relocator;
import org.apache.maven.plugins.shade.resource.ResourceTransformer;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

/**
 *
 * @author Alfio Gloria
 */
@Component( role = JarMerger.class )
public class JarMerger {
    @Requirement( hint = "default", role = org.apache.maven.plugins.shade.Shader.class )
    private Shader shader;

    public void merge( Set<File> jars, File dest, boolean onlyClasses ) throws MojoExecutionException {
        ShadeRequest req = new ShadeRequest();
        req.setJars( jars );
        req.setFilters( createFilters( jars, onlyClasses ) );
        req.setUberJar( dest );
        req.setRelocators( new ArrayList<Relocator>() );
        req.setResourceTransformers( new ArrayList<ResourceTransformer>() );
        req.setShadeSourcesContent( false );

        try {
            shader.shade( req );
        }
        catch ( IOException ex ) {
            throw new MojoExecutionException( "Error", ex );
        }
    }

    private static List<Filter> createFilters( Set<File> jars, boolean onlyClasses ) {
        List<Filter> filters = new ArrayList<>();
        filters.add( new SimpleFilter( jars, includes( onlyClasses ), new HashSet<String>() ) );
        return filters;
    }

    private static Set<String> includes( boolean onlyClasses ) {
        Set<String> set = new HashSet<>();
        set.add( onlyClasses ? "**/*.class" : "**/*.*" );
        return set;
    }
}
