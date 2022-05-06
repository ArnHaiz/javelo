package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.function.DoubleUnaryOperator;

public class GpxGenerator {
    private GpxGenerator() {
    }

    public static Document createGpx(Route route, DoubleUnaryOperator routeProfil) {
        Document doc = newDocument();

        Element root = doc
                .createElementNS("http://www.topografix.com/GPX/1/1",
                        "gpx");
        doc.appendChild(root);

        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 "
                        + "http://www.topografix.com/GPX/1/1/gpx.xsd");
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "javelo");

        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route javelo");

        Element rte = doc.createElement("rte");
        root.appendChild(rte);

        for (PointCh p : route.points()) {
            Element rtept = doc.createElement("rtept");
            rte.appendChild(rtept);

            Element ele = doc.createElement("ele");
            rtept.appendChild(ele);

            rtept.setAttribute("lat", String.valueOf(p.lat()));
            rtept.setAttribute("lon", String.valueOf(p.lon()));
            ele.setTextContent(String.valueOf(routeProfil.applyAsDouble(route.pointClosestTo(p).position())));
        }

        return doc;
    }

    public static void writeGpx(String fileName, Route route, DoubleUnaryOperator routeProfile) throws IOException {
        Document doc = createGpx(route, routeProfile);

        try (Writer w = new BufferedWriter(new FileWriter(fileName))) {
            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(w));
        } catch (TransformerException t) {
            throw new Error(t);
        }
    }

    private static Document newDocument() {
        try {
            return DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e); // Should never happen
        }
    }
}
