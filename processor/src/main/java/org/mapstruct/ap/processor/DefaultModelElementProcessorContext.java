/**
 *  Copyright 2012-2015 Gunnar Morling (http://www.gunnarmorling.de/)
 *  and/or other contributors as indicated by the @authors tag. See the
 *  copyright.txt file in the distribution for a full listing of all
 *  contributors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.mapstruct.ap.processor;

import org.mapstruct.ap.util.FormattingMessager;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

import org.mapstruct.ap.model.common.TypeFactory;
import org.mapstruct.ap.option.Options;
import org.mapstruct.ap.processor.ModelElementProcessor.ProcessorContext;
import org.mapstruct.ap.util.Message;
import org.mapstruct.ap.version.VersionInformation;

/**
 * Default implementation of the processor context.
 *
 * @author Gunnar Morling
 */
public class DefaultModelElementProcessorContext implements ProcessorContext {

    private final ProcessingEnvironment processingEnvironment;
    private final DelegatingMessager messager;
    private final Options options;
    private final TypeFactory typeFactory;
    private final VersionInformation versionInformation;

    public DefaultModelElementProcessorContext(ProcessingEnvironment processingEnvironment, Options options) {
        this.processingEnvironment = processingEnvironment;
        this.messager = new DelegatingMessager( processingEnvironment.getMessager() );
        this.typeFactory = new TypeFactory(
            processingEnvironment.getElementUtils(),
            processingEnvironment.getTypeUtils()
        );
        this.options = options;
        this.versionInformation = DefaultVersionInformation.fromProcessingEnvironment( processingEnvironment );
    }

    @Override
    public Filer getFiler() {
        return processingEnvironment.getFiler();
    }

    @Override
    public Types getTypeUtils() {
        return processingEnvironment.getTypeUtils();
    }

    @Override
    public Elements getElementUtils() {
        return processingEnvironment.getElementUtils();
    }

    @Override
    public TypeFactory getTypeFactory() {
        return typeFactory;
    }

    @Override
    public FormattingMessager getMessager() {
        return messager;
    }

    @Override
    public Options getOptions() {
        return options;
    }

    @Override
    public VersionInformation getVersionInformation() {
        return versionInformation;
    }

    @Override
    public boolean isErroneous() {
        return messager.isErroneous();
    }

    private static class DelegatingMessager implements FormattingMessager {

        private final Messager delegate;
        private boolean isErroneous = false;

        public DelegatingMessager(Messager delegate) {
            this.delegate = delegate;
        }

        @Override
        public void printMessage( Kind kind, Message msg, Object... args) {
            String message = String.format( msg.getDescription(), args );
            delegate.printMessage( kind, message);
            if ( kind == Kind.ERROR ) {
                isErroneous = true;
            }
        }

        @Override
        public void printMessage( Kind kind, Element e, Message msg, Object... args) {
            String message = String.format( msg.getDescription(), args );
            delegate.printMessage( kind, message, e );
            if ( kind == Kind.ERROR ) {
                isErroneous = true;
            }
        }

        @Override
        public void printMessage( Kind kind, Element e, AnnotationMirror a, Message msg, Object... args) {
            String message = String.format( msg.getDescription(), args );
            delegate.printMessage( kind, message, e, a );
            if ( kind == Kind.ERROR ) {
                isErroneous = true;
            }
        }

        @Override
        public void printMessage( Kind kind, Element e, AnnotationMirror a, AnnotationValue v, Message msg,
                                 Object... args) {
            String message = String.format( msg.getDescription(), args );
            delegate.printMessage( kind, message, e, a, v );
            if ( kind == Kind.ERROR ) {
                isErroneous = true;
            }
        }

        public boolean isErroneous() {
            return isErroneous;
        }

    }
}
