package software.amazon.amplifyuibuilder.theme;

import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.cloudformation.LambdaWrapper;

public class ClientBuilder {
  /*
  It is recommended to use static HTTP client so less memory is consumed
  e.g. https://github.com/aws-cloudformation/aws-cloudformation-resource-providers-logs/blob/master/aws-logs-loggroup/src/main/java/software/amazon/logs/loggroup/ClientBuilder.java#L9
  */
  public static AmplifyUiBuilderClient getClient() {
    return AmplifyUiBuilderClient
        .builder()
        .httpClient(LambdaWrapper.HTTP_CLIENT)
        .build();
  }
}
