package order.stubs;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class InventoryClientStubs {

    public static void stubInventoryCall(String skuCode) {
        stubFor(
                get( urlEqualTo("/api/inventory?skuCode=" + skuCode))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "Application/json")
                                .withBody("true")
                        )
        );
    }
}
