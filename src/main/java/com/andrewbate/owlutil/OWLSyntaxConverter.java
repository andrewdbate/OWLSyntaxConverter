/*
 * Copyright (c) 2018, Andrew D Bate
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.andrewbate.owlutil;

import java.io.File;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.*;
import org.semanticweb.owlapi.io.SystemOutDocumentTarget;
import org.semanticweb.owlapi.model.*;

/**
 * A utility to convert the ontologies into a new syntax using the OWL API.
 *
 * @author Andrew Bate <code@andrewbate.com>
 */
public class OWLSyntaxConverter {

    private final static String EXECUTABLE_NAME = "owlconvert";
    private final static String USAGE = "Usage: " + EXECUTABLE_NAME + " FORMAT FILE";

    public static void main(String[] args) {
        // Help pages.
        if (args.length == 1 && args[0].equals("--help")) {
            System.err.println(USAGE);
            System.err.println("Converts an OWL ontology document into a new document with the specified syntax.");
            System.err.println("Example: " + EXECUTABLE_NAME + " functional kb.owl");
            System.err.println();
            System.err.println("Supported target ontology document formats:");
            System.err.println("   functional    Convert to functional-style syntax");
            System.err.println("   manchester    Convert to Manchester syntax");
            System.err.println("   owlxml        Convert to OWL/XML syntax");
            System.err.println("   rdfxml        Convert to RDF/XML syntax");
            System.err.println("   turtle        Convert to Turtle syntax");
            System.exit(1);
        } else if (args.length != 2) {
            System.err.println(USAGE);
            System.err.println("Try '" + EXECUTABLE_NAME + " --help' for more information.");
            System.exit(1);
        }
        // Determine the target syntax.
        PrefixDocumentFormat outputFormat = null;
        switch (args[0].toLowerCase()) {
            case "functional":
                outputFormat = new FunctionalSyntaxDocumentFormat();
                break;
            case "manchester":
                outputFormat = new ManchesterSyntaxDocumentFormat();
                break;
            case "owlxml":
                outputFormat = new OWLXMLDocumentFormat();
                break;
            case "rdfxml":
                outputFormat = new RDFXMLDocumentFormat();
                break;
            case "turtle":
                outputFormat = new TurtleDocumentFormat();
                break;
            default:
                System.err.println(EXECUTABLE_NAME + ": Invalid target format: " + args[0]);
                System.exit(1);
        }
        // Check the specified file exists.
        final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        final File file = new File(args[1]);
        if (!file.exists()) {
            System.err.println(EXECUTABLE_NAME + ": " + args[1] + ": No such file.");
            System.exit(1);
        }
        // Do conversion and print result to standard out.
        try {
            OWLOntology myOWL = manager.loadOntologyFromOntologyDocument(file);
            OWLDocumentFormat inputFormat = manager.getOntologyFormat(myOWL);
            if (inputFormat.isPrefixOWLDocumentFormat()) {
                outputFormat.copyPrefixesFrom(inputFormat.asPrefixOWLDocumentFormat());
            }
            manager.saveOntology(myOWL, outputFormat, new SystemOutDocumentTarget());
        } catch (OWLOntologyCreationException e) {
            System.err.println(EXECUTABLE_NAME + ": Could not load ontology: " + e.getMessage());
        } catch (OWLOntologyStorageException e) {
            System.err.println(EXECUTABLE_NAME + ": Could not save ontology: " + e.getMessage());
        }
    }

}
