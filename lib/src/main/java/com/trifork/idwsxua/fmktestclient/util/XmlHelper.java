/*
 * The MIT License
 *
 * Original work sponsored and donated by National Board of e-Health (NSI), Denmark (http://www.nsi.dk)
 *
 * Copyright (C) 2011 National Board of e-Health (NSI), Denmark (http://www.nsi.dk)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.trifork.idwsxua.fmktestclient.util;

import org.w3c.dom.Node;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public class XmlHelper {
    public static final String XML_ENCODING = "UTF-8";

    private static final int DEFAULT_INDENT = 2;

    /**
     * Serialize the supplied DOM node to string form. This result is configurable through parameterettings.
     * <p/>
     * <b>WARNING: </b>If the DOM document contains digital signatures (more precisely <code><SignedInfo></code> elements the signatures
     * will be broken if the XML document is formatted through pretty-printing!
     *
     * @param node             The node to serialize
     * @param pretty           If true, will indent and generally pretty-print XML. Note:
     *                         This may affect validity of  contained digital signatures!
     * @param includeXMLHeader If true, add the standard XML header to the output
     * @return DOM as XML String
     */
    public static String node2String(Node node, boolean pretty, boolean includeXMLHeader) {
        final DOMSource source = new DOMSource(node);
        return source2String(source, pretty, includeXMLHeader);
    }

    public static String source2String(Source source, boolean pretty, boolean includeXMLHeader) {
        final ByteArrayOutputStream bas = new ByteArrayOutputStream();
        final StreamResult result = new StreamResult(bas);
        transform(source, result, pretty);
        try {
            String str = bas.toString(XML_ENCODING);
            if (includeXMLHeader) {
                str = "<?xml version=\"1.0\" encoding=\"" + XML_ENCODING + "\" ?>" + ((pretty) ? "\n" + str : str);
            }
            return str;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported XML encoding", e);
        }
    }

    public static void transform(Source source, Result result, boolean pretty) {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();

            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, (pretty) ? "yes" : "no");
            transformer.setOutputProperty(OutputKeys.ENCODING, XML_ENCODING);
            transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", Integer.toString(DEFAULT_INDENT));

            transformer.transform(source, result);

        } catch (TransformerConfigurationException e) {
            throw new RuntimeException("TransformerConfigurationException during prettyPrint", e);
        } catch (TransformerException e) {
            throw new RuntimeException("TransformerException during prettyPrint", e);
        }
    }

}
