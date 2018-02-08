import org.testng.Assert;
import org.testng.annotations.Test;
import processors.SessionProcessor;

import java.util.Arrays;

/**
 * Created by wojciech on 20.01.18.
 */
public class SessionProcessorTest {

    @Test
    public void parseSession() {
        String log = "Session:\n" +
                "Session(TUM,5,Map(gatling.http.cache.dns -> io.gatling.http.resolver.ShuffleJdkNameResolver@742c215d, e2481e72-7047-4cb9-ab74-55fcd895f58f -> 0, userId -> 204383),1515679172492,0,OK,List(TryMaxBlock(e2481e72-7047-4cb9-ab74-55fcd895f58f,io.gatling.core.action.InnerTryMax@7fd90b80,OK)),io.gatling.core.protocol.ProtocolComponentsRegistry$$Lambda$463/440472115@62a30f14)\n";


        SessionProcessor sessionProcessor = new SessionProcessor(Arrays.asList(log.split("\n")));
        sessionProcessor.parse();
        Assert.assertTrue(sessionProcessor.getAttributesMap().containsKey("userId"));
        Assert.assertEquals(sessionProcessor.getAttributesMap().get("userId"), "204383");
        Assert.assertEquals(sessionProcessor.getScenarioName(), "TUM");
        Assert.assertEquals(sessionProcessor.getUserId(), "5");


    }

    @Test
    public void multilineSessionTest() {

        String log = "Session:\n" +
                "Session(OM,7,Map(productorder_CREDIT_CHECK_INFORMATION -> check additional information, productorder_REQUESTOR_INFO -> some addictional information, customerAccountId -> 17, productorder_CREDIT_CHECK_RESULT -> Credit check result, productsIds -> List(), gatling.http.cache.dns -> io.gatling.http.resolver.ShuffleJdkNameResolver@31a80006, orderContactPersonId -> 187, productorder_AGREEMENT_SIGNATURE_ALT -> AGREEMENT.SIGNATURE_ALTERNATIVE.SIGNED_BY_BOTH_PARTIES, productorder_AGREEMENT_SIGNATURE_LOC -> asdf, productorder_ORDER_DEALER_CODE -> 5345345, productorder_AGREEMENT_START_DATE -> 2018-01-11T14:59:32.506Z, orderId -> e-IfPXOkSdOiuGtUemPJXQ, 840fdeeb-b506-4d9f-bb2a-d3094306ec84 -> 0, addedProductCnt -> 0, requestActionEndDate -> 2018-01-11T14:59:32.506Z, productorder_ORDER_SALESMAN_NUMBER -> Dealer information for, salesChannelId -> CRM, productorder_AGREEMENT_DELIVERY_METHOD -> AGREEMENT.DELIVERY_METHOD_PRINTOUT.DOWNLOAD_THE_PDF_PRINTOUT, productItemComments -> List(), defaultInstallationAddress -> 167, requestActionStartDate -> 2018-01-11T14:59:32.506Z, productorder_ORDER_IDENTIFICATION_METHOD -> DriversLicense, productorder_AGREEMENT_SIGNING_DATE -> 2018-01-11T14:59:32.506Z, stringBody -> {\n" +
                "  \"salesChannelId\": \"CRM\",\n" +
                "  \"customerAccountId\": \"17\",\n" +
                "  \"defaultInstallationAddress\": \"167\",\n" +
                "  \"addresses\": [\n" +
                "    \"167\"\n" +
                "  ],\n" +
                "  \"requestActionStartDate\": \"2018-01-11T14:59:32.506Z\",\n" +
                "  \"requestActionEndDate\": \"2018-01-11T14:59:32.506Z\",\n" +
                "  \"attributes\" : [\n" +
                "    {\n" +
                "      \"id\": \"productorder_AGREEMENT_DELIVERY_METHOD\",\n" +
                "      \"value\" : \"AGREEMENT.DELIVERY_METHOD_PRINTOUT.DOWNLOAD_THE_PDF_PRINTOUT\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"productorder_AGREEMENT_SIGNATURE_ALT\",\n" +
                "      \"value\" : \"AGREEMENT.SIGNATURE_ALTERNATIVE.SIGNED_BY_BOTH_PARTIES\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"productorder_AGREEMENT_SIGNATURE_LOC\",\n" +
                "      \"value\" : \"asdf\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"productorder_AGREEMENT_SIGNING_DATE\",\n" +
                "      \"value\" : \"2018-01-11T14:59:32.506Z\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"productorder_AGREEMENT_START_DATE\",\n" +
                "      \"value\" : \"2018-01-11T14:59:32.506Z\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"productorder_ORDER_DEALER_CODE\",\n" +
                "      \"value\" : \"5345345\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"productorder_ORDER_SALESMAN_NUMBER\",\n" +
                "      \"value\" : \"Dealer information for\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"productorder_ORDER_IDENTIFICATION_METHOD\",\n" +
                "      \"value\" : \"DriversLicense\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"productorder_CREDIT_CHECK_RESULT\",\n" +
                "      \"value\" : \"Credit check result\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"productorder_CREDIT_CHECK_INFORMATION\",\n" +
                "      \"value\" : \"check additional information\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"productorder_REQUESTOR_INFO\",\n" +
                "      \"value\" : \"some addictional information\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"orderContactPersonId\": \"187\",\n" +
                "  \"orderConfirmation\": [\n" +
                "    {\n" +
                "      \"channel\": \"Email\",\n" +
                "      \"notification\": \"SaveOnly\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"activationConfirmation\": [\n" +
                "    {\n" +
                "      \"channel\": \"SMS\",\n" +
                "      \"notification\": \"SaveOnly\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"orderCompletionConfirmation\": [\n" +
                "    {\n" +
                "      \"channel\": \"Email\",\n" +
                "      \"notification\": \"SaveOnly\"\n" +
                "    }\n" +
                "  ]\n" +
                "}, addresses -> 167),1515679172500,32,OK,List(TryMaxBlock(840fdeeb-b506-4d9f-bb2a-d3094306ec84,io.gatling.core.action.InnerTryMax@79a57640,OK)),io.gatling.core.protocol.ProtocolComponentsRegistry$$Lambda$463/440472115@3dbc5f84)\n";

        SessionProcessor sessionProcessor = new SessionProcessor(Arrays.asList(log.split("\n")));
        sessionProcessor.parse();
        //System.out.println("Attributes map keys:");
        sessionProcessor.getAttributesMap().keySet().forEach(System.out::println);
        Assert.assertTrue(sessionProcessor.getAttributesMap().containsKey("productorder_REQUESTOR_INFO"));
        Assert.assertEquals(sessionProcessor.getAttributesMap().get("addresses"), "167");
        Assert.assertTrue(sessionProcessor.getAttributesMap().get("stringBody").contains("activationConfirmation"));
        Assert.assertEquals(sessionProcessor.getScenarioName(), "OM");
        Assert.assertEquals(sessionProcessor.getUserId(), "7");

    }
}
