package software.amazon.amplifyuibuilder.form;

import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.cloudformation.LambdaWrapper;

import java.net.URI;

import static software.amazon.amplifyuibuilder.common.ClientWrapper.getUIBuilderEndpoint;

public class ClientBuilder {

  public static AmplifyUiBuilderClient getClient() {
    String region = System.getenv("AWS_REGION");

    return AmplifyUiBuilderClient
        .builder()
        .httpClient(LambdaWrapper.HTTP_CLIENT)
        .endpointOverride(URI.create(getUIBuilderEndpoint(region)))
        .build();
  }
}
