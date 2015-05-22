/**
 * 
 */
package org.kore.kolab.notes.v3;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.kore.kolab.notes.KolabParser;
import org.kore.kolab.notes.Note;
import org.w3c.dom.Document;

/**
 * Parser for Kolab Notes V3 Format
 * 
 * @author Konrad Renner
 * 
 */
public class KolabNotesParserV3
        implements KolabParser, Serializable {

    /* (non-Javadoc)
     * @see org.kore.kolabnotes.KolabParser#parse(java.io.InputStream)
     */
    @Override
    public Note parse(InputStream stream) {
        try {
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();

            KolabNotesHandler handler = new KolabNotesHandler();
            saxParser.parse(stream, handler);

            return handler.getNote();
        } catch (Exception e) {
            throw new KolabParseException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.kore.kolabnotes.KolabParser#write(org.kore.kolabnotes.Note, java.io.OutputStream)
     */
    @Override
    public void write(Object object, OutputStream stream) {
        try {
            Note note = (Note) object;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            //order of the builder methods is important for validation against schema
            Document document = KolabNotesXMLBuilder.createInstance(docBuilder)
                    .withIdentification(note.getIdentification())
                    .withAuditInformation(note.getAuditInformation())
                    .withClassification(note.getClassification())
                    .withSummary(note.getSummary())
                    .withDescription(note.getDescription())
                    .withColor(note.getColor())
                    .build();

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(stream);

            transformer.transform(source, result);
        } catch (Exception e) {
            throw new KolabParseException(e);
        }
    }

}
