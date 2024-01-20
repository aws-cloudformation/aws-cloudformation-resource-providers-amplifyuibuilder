package software.amazon.amplifyuibuilder.theme;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.GetThemeRequest;
import software.amazon.awssdk.services.amplifyuibuilder.model.GetThemeResponse;
import software.amazon.awssdk.services.amplifyuibuilder.model.Theme;
import software.amazon.cloudformation.proxy.*;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static software.amazon.amplifyuibuilder.common.Transformer.transformList;

@ExtendWith(MockitoExtension.class)
public class ReadHandlerTest extends AbstractTestBase {

  @Mock
  private AmazonWebServicesClientProxy proxy;

  @Mock
  private ProxyClient<AmplifyUiBuilderClient> proxyClient;

  @Mock
  AmplifyUiBuilderClient sdkClient;

  @BeforeEach
  public void setup() {
    proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
    sdkClient = mock(AmplifyUiBuilderClient.class);
    proxyClient = MOCK_PROXY(proxy, sdkClient);
  }

  @AfterEach
  public void tear_down() {
    verify(sdkClient, atLeastOnce()).serviceName();
    verifyNoMoreInteractions(sdkClient);
  }

  @Test
  public void handleRequest_SimpleSuccess() {
    final ReadHandler handler = new ReadHandler();

    final GetThemeResponse getResponse = GetThemeResponse
        .builder()
        .theme(Theme.builder()
            .appId(APP_ID)
            .environmentName(ENV_NAME)
            .id(ID)
            .name(NAME)
            .tags(TAGS)
            .createdAt(Instant.now())
            .modifiedAt(Instant.now())
            .values(transformList(THEME_VALUES_LIST, Translator::translateThemeValuesFromCFNToSDK))
            .build())
        .build();

    when(proxyClient.client().getTheme(any(GetThemeRequest.class)))
        .thenReturn(getResponse);

    final ResourceModel model = ResourceModel.builder()
        .id(ID)
        .appId(APP_ID)
        .environmentName(ENV_NAME)
        .name(NAME)
        .values(THEME_VALUES_LIST)
        .build();

    final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
        .desiredResourceState(model)
        .build();

    final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request,
        new CallbackContext(), proxyClient, logger);
    ResourceModel responseResourceModel = response.getResourceModel();

    ResourceModel requestDesiredResourceState = request.getDesiredResourceState();

    logger.log(String.format("Response: %s", response));

    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
    assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
    assertThat(response.getMessage()).isNull();
    assertThat(response.getErrorCode()).isNull();
    assertThat(responseResourceModel.getAppId()).isEqualTo(requestDesiredResourceState.getAppId());
    assertThat(responseResourceModel.getId()).isEqualTo(requestDesiredResourceState.getId());
    assertThat(responseResourceModel.getName()).isEqualTo(requestDesiredResourceState.getName());
    assertThat(responseResourceModel.getEnvironmentName()).isEqualTo(requestDesiredResourceState.getEnvironmentName());
    assertThat(responseResourceModel.getValues().size()).isEqualTo(requestDesiredResourceState.getValues().size());
    assertThat(model.getOverrides()).isNull();
    assertThat(responseResourceModel.getOverrides().size()).isEqualTo(0);

  }
}
