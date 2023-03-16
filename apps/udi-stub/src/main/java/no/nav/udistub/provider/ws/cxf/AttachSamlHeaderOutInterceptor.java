package no.nav.udistub.provider.ws.cxf;

import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.function.Supplier;

// See https://github.com/navikt/samordning/blob/master/samordningsoapsupport/src/main/java/no/nav/samordning/saml/interceptor/AttachSamlHeaderOutInterceptor.java
public class AttachSamlHeaderOutInterceptor extends AbstractSoapInterceptor {
    private static final QName securityNamespace = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security");
    private final Supplier<String> samlXmlSupplier;

    private static final Logger LOG = LoggerFactory.getLogger(AttachSamlHeaderOutInterceptor.class);

    public AttachSamlHeaderOutInterceptor(Supplier<String> samlXmlSupplier) {
        super(Phase.PRE_STREAM);
        this.samlXmlSupplier = samlXmlSupplier;
    }

    @Override
    public void handleMessage(SoapMessage soapMessage) throws Fault {

        var samlXml = samlXmlSupplier.get();
        var soapHeader = createSoapHeader(samlXml);

        if (soapHeader != null) {
            soapMessage.getHeaders().add(soapHeader);
        }
    }

    public static SoapHeader createSoapHeader(String samlXml) throws Fault {
        try {
            var factory = DocumentBuilderFactory.newInstance();
            var builder = factory.newDocumentBuilder();
            var doc = builder.parse(new InputSource(new StringReader(samlXml)));

            var wsse = doc.createElementNS(securityNamespace.getNamespaceURI(), "wsse:Security");
            wsse.appendChild(doc.getFirstChild());

            return new SoapHeader(securityNamespace, wsse);
        } catch (Exception e) {
            throw new Fault(e);
        }
    }
}
