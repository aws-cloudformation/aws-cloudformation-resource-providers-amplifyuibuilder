package software.amazon.amplifyuibuilder.theme;

import com.google.common.collect.ImmutableList;
import software.amazon.awssdk.awscore.AwsRequest;
import software.amazon.awssdk.awscore.AwsResponse;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Credentials;
import software.amazon.cloudformation.proxy.LoggerProxy;
import software.amazon.cloudformation.proxy.ProxyClient;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class AbstractTestBase {
  protected static final Credentials MOCK_CREDENTIALS;
  protected static final LoggerProxy logger;
  protected static String ID = "t-1234XYZ";
  protected static String APP_ID = "d1234";
  protected static String ENV_NAME = "dev";
  protected static Instant NOW = Instant.now();
  protected static String NAME = "TestThemeName";
  protected static Map<String, String> TAGS = Collections.singletonMap("Stage", "Dev");
  protected static List<ThemeValues> THEME_VALUES_LIST = ImmutableList.of(ThemeValues.builder()
          .key("colorMode")
          .value(ThemeValue.builder().value("dark").build())
          .build(),
      ThemeValues.builder()
          .key("tokens")
          .value(ThemeValue.builder()
              .children(ImmutableList.of(ThemeValues.builder()
                  .key("colors")
                  .value(ThemeValue.builder()
                      .children(ImmutableList.of(ThemeValues.builder()
                          .key("black")
                          .value(ThemeValue.builder()
                              .children(ImmutableList.of(ThemeValues.builder()
                                  .value(ThemeValue.builder()
                                      .value("#fff")
                                      .build())
                                  .build()))
                              .build())
                          .build()))
                      .build())
                  .build()))
              .build())
          .build());

  static {
    MOCK_CREDENTIALS = new Credentials("accessKey", "secretKey", "token");
    logger = new LoggerProxy();
  }

  static ProxyClient<AmplifyUiBuilderClient> MOCK_PROXY(
      final AmazonWebServicesClientProxy proxy,
      final AmplifyUiBuilderClient sdkClient) {
    return new ProxyClient<AmplifyUiBuilderClient>() {
      @Override
      public <RequestT extends AwsRequest, ResponseT extends AwsResponse> ResponseT
      injectCredentialsAndInvokeV2(RequestT request, Function<RequestT, ResponseT> requestFunction) {
        return proxy.injectCredentialsAndInvokeV2(request, requestFunction);
      }

      @Override
      public <RequestT extends AwsRequest, ResponseT extends AwsResponse>
      CompletableFuture<ResponseT>
      injectCredentialsAndInvokeV2Async(RequestT request, Function<RequestT, CompletableFuture<ResponseT>> requestFunction) {
        throw new UnsupportedOperationException();
      }

      @Override
      public <RequestT extends AwsRequest, ResponseT extends AwsResponse, IterableT extends SdkIterable<ResponseT>>
      IterableT
      injectCredentialsAndInvokeIterableV2(RequestT request, Function<RequestT, IterableT> requestFunction) {
        return proxy.injectCredentialsAndInvokeIterableV2(request, requestFunction);
      }

      @Override
      public <RequestT extends AwsRequest, ResponseT extends AwsResponse> ResponseInputStream<ResponseT>
      injectCredentialsAndInvokeV2InputStream(RequestT requestT, Function<RequestT, ResponseInputStream<ResponseT>> function) {
        throw new UnsupportedOperationException();
      }

      @Override
      public <RequestT extends AwsRequest, ResponseT extends AwsResponse> ResponseBytes<ResponseT>
      injectCredentialsAndInvokeV2Bytes(RequestT requestT, Function<RequestT, ResponseBytes<ResponseT>> function) {
        throw new UnsupportedOperationException();
      }

      @Override
      public AmplifyUiBuilderClient client() {
        return sdkClient;
      }
    };
  }
}
