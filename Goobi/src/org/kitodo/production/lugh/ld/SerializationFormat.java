/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General private License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * GPL3-License.txt file that was distributed with this source code.
 */

package org.kitodo.production.lugh.ld;

import java.io.*;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * Convenience class to serialize a node.
 *
 * @author Matthias Ronge
 */
public enum SerializationFormat {
    /**
     * The N-Triples serialisation format ({@code application/n-triples}).
     *
     * @see "https://en.wikipedia.org/wiki/N-Triples"
     */
    N_TRIPLES {
        @Override
        public void write(Node node, Map<String, String> map, File file) throws FileNotFoundException {
            write(node, map, file, "N-TRIPLE");
        }
    },
    /**
     * The N3 serialisation format ({@code text/n3;charset=utf-8}).
     *
     * @see "https://en.wikipedia.org/wiki/Notation3"
     */
    N3 {
        @Override
        public void write(Node node, Map<String, String> map, File file) throws FileNotFoundException {
            write(node, map, file, "N3");
        }
    },
    /**
     * The RDF/XML serialisation format ({@code application/rdf+xml}), linear
     * style. This serialiser creates identifiers for all nodes and serialises
     * them in a linear way. It produces an output which reminds you of
     * N-Triples and is hardly readable. However, it is comparably faster for
     * larger models than the {@link #RDF_XML_ABBREV} serializer.
     *
     * @see "https://en.wikipedia.org/wiki/RDF/XML"
     * @see "https://jena.apache.org/documentation/io/rdfxml_howto.html#rdfxml-rdfxml-abbrev"
     */
    RDF_XML {
        @Override
        public void write(Node node, Map<String, String> map, File file) throws FileNotFoundException {
            write(node, map, file, "RDF/XML");
        }
    },
    /**
     * The RDF/XML serialisation format ({@code application/rdf+xml}), pretty
     * printed. This serialiser creates serialises the nodes in a way which
     * reminds you of Turtle and improves readability. However, its runtime may
     * become unacceptable for large models.
     *
     * @see "https://en.wikipedia.org/wiki/RDF/XML"
     * @see "https://jena.apache.org/documentation/io/rdfxml_howto.html#rdfxml-rdfxml-abbrev"
     */
    RDF_XML_ABBREV {
        @Override
        public void write(Node node, Map<String, String> map, File file) throws FileNotFoundException {
            write(node, map, file, "RDF/XML-ABBREV");
        }
    },
    /**
     * The Turtle serialisation format ({@code text/turtle}).
     *
     * @see "https://en.wikipedia.org/wiki/Turtle_(syntax)"
     */
    TURTLE {
        @Override
        public void write(Node node, Map<String, String> map, File file) throws FileNotFoundException {
            write(node, map, file, "TURTLE");
        }
    };

    /**
     * Write a serialised representation of this model in a specified language.
     *
     * @param file
     *            a file to write to
     * @param map
     *            user defined namespace prefixes, mapped from prefix to
     *            namespace, the namespace must end either in {@code #} or
     *            {@code /}
     * @param lang
     *            the language in which to write the model, predefined values
     *            are {@code RDF/XML}, {@code RDF/XML-ABBREV}, {@code N-TRIPLE},
     *            {@code TURTLE} and {@code N3}.
     */
    static void write(Node node, Map<String, String> map, File file, String lang) throws FileNotFoundException {
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            write(node, map, bos, lang);
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    /* close silently */ }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    /* close silently */ }
            }
        }
    }

    /**
     * Write a serialised representation of this model in a specified language.
     *
     * @param out
     *            an output stream to write to
     * @param map
     *            user defined namespace prefixes, mapped from prefix to
     *            namespace, the namespace must end either in {@code #} or
     *            {@code /}
     * @param lang
     *            the language in which to write the model, predefined values
     *            are {@code RDF/XML}, {@code RDF/XML-ABBREV}, {@code N-TRIPLE},
     *            {@code TURTLE} and {@code N3}.
     */
    private static void write(Node node, Map<String, String> map, OutputStream out, String lang) {
        Model model = node.toModel();
        if (map != null) {
            model.setNsPrefixes(map);
        }
        model.write(out, lang);
    }

    /**
     * Write the node to a file.
     *
     * @param node
     *            node to print
     * @param map
     *            map of prefixes to resolve. For XML, mapping from namespaces,
     *            without {@code #}, to abbreviations; for all other formats
     *            mapping from abbreviations to namespaces, with {@code #}.
     * @param file
     *            path to the file to write to
     * @throws FileNotFoundException
     *             if the file exists but is a directory rather than a regular
     *             file, does not exist but cannot be created, or cannot be
     *             opened for any other reason
     */
    public abstract void write(Node node, Map<String, String> map, File file) throws FileNotFoundException;
}