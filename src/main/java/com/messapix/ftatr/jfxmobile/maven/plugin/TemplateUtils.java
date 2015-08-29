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

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

/**
 *
 * @author Alfio Gloria
 */
@Component( role = TemplateUtils.class )
public class TemplateUtils {
    @Requirement
    private FileSystem fs;

    public void process( String templateName, Target target, Map<String, ?> map ) throws MojoExecutionException {
        try {
            Configuration cfg = new Configuration( Configuration.VERSION_2_3_22 );
            cfg.setClassForTemplateLoading( getClass(), "/" );
            cfg.setDefaultEncoding( "UTF-8" );
            cfg.setTemplateExceptionHandler( TemplateExceptionHandler.RETHROW_HANDLER );

            Template template = cfg.getTemplate( templateName );
            fs.path( target).getParent().toFile().mkdirs();
            Writer out = new OutputStreamWriter( new FileOutputStream( fs.file( target ) ) );
            template.process( map, out );
        }
        catch ( IOException | TemplateException ex ) {
            throw new MojoExecutionException( "Error", ex );
        }
    }
}
