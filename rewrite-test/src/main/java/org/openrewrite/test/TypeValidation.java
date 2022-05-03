/*
 * Copyright 2022 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.test;

import org.openrewrite.java.search.FindMissingTypes;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JavaSourceFile;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.fail;

public class TypeValidation {

    private boolean classDeclarations = true;
    private boolean identifiers = true;
    private boolean methodDeclarations = true;
    private boolean methodInvocations = true;
    public TypeValidation classDeclarations(boolean classDeclarations) {
        this.classDeclarations = classDeclarations;
        return this;
    }
    public TypeValidation methodDeclarations(boolean methodDeclarations) {
        this.methodDeclarations = methodDeclarations;
        return this;
    }
    public TypeValidation methodInvocations(boolean methodInvocations) {
        this.methodInvocations = methodInvocations;
        return this;
    }
    public TypeValidation identifiers(boolean identifiers) {
        this.identifiers = identifiers;
        return this;
    }

    public void assertValidTypes(JavaSourceFile sf) {

        List<FindMissingTypes.MissingTypeResult> missingTypeResults = FindMissingTypes.findMissingTypes(sf);
        missingTypeResults = missingTypeResults.stream()
                .filter(missingType -> {
                    if (identifiers && missingType.getJ() instanceof J.Identifier) {
                        return true;
                    } else if (classDeclarations && missingType.getJ() instanceof J.ClassDeclaration) {
                        return true;
                    } else if (methodInvocations && missingType.getJ() instanceof J.MethodInvocation) {
                        return true;
                    } else return methodDeclarations && missingType.getJ() instanceof J.MethodDeclaration;
                })
                .collect(Collectors.toList());
        if (!missingTypeResults.isEmpty()) {
            fail("AST contains missing or invalid type information\n" + missingTypeResults.stream().map(v -> v.getMessage() + "\n" + v.getPath() + "\n" + v.getPrintedTree())
                    .collect(Collectors.joining("\n\n")));
        }
    }
}