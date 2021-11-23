package software.amazon.amplifyuibuilder.component;

import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.cloudformation.LambdaWrapper;

public class ClientBuilder {

  public static AmplifyUiBuilderClient getClient() {
    return AmplifyUiBuilderClient
      .builder()
      .httpClient(LambdaWrapper.HTTP_CLIENT)
      .build();
  }
}
