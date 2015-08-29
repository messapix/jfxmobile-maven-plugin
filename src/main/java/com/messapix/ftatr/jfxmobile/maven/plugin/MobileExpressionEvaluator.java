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

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.PluginParameterExpressionEvaluator;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;

/**
 *
 * @author Alfio Gloria
 */
@Component( role = MobileExpressionEvaluator.class )
public class MobileExpressionEvaluator {
    private ExpressionEvaluator evaluator;

    @Requirement
    private MavenSession session;

    @Requirement
    private MojoExecution execution;

    public <T> T evaluate( String string, Class<T> clazz ) throws ExpressionEvaluationException {
        return clazz.cast( getEvaluator().evaluate( string ) );
    }

    public String evaluate( String string ) throws ExpressionEvaluationException {
        return (String)getEvaluator().evaluate( string );
    }

    private ExpressionEvaluator getEvaluator() {
        if ( evaluator == null ) {
            evaluator = new PluginParameterExpressionEvaluator( session, execution );
        }

        return evaluator;
    }
}
