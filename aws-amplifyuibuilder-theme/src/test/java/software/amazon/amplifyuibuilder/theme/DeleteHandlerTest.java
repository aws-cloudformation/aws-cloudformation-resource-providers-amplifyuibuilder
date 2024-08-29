package software.amazon.amplifyuibuilder.theme;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import software.amazon.amplifyuibuilder.common.TaggingHelpers;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.DeleteThemeRequest;
import software.amazon.awssdk.services.amplifyuibuilder.model.DeleteThemeResponse;
import software.amazon.cloudformation.proxy.*;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteHandlerTest extends AbstractTestBase {

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
    final DeleteHandler handler = new DeleteHandler();

    final DeleteThemeResponse deleteResponse = DeleteThemeResponse.builder().build();

    when(proxyClient.client().deleteTheme(any(DeleteThemeRequest.class)))
        .thenReturn(deleteResponse);

    final ResourceModel model = ResourceModel.builder()
        .appId(APP_ID)
        .environmentName(ENV_NAME)
        .id(ID)
        .build();

    final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
        .desiredResourceState(model)
        .build();

    final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request,
        new CallbackContext(), proxyClient, logger);

    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
    assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
    assertThat(response.getResourceModel()).isNull();
    assertThat(response.getResourceModels()).isNull();
    assertThat(response.getMessage()).isNull();
    assertThat(response.getErrorCode()).isNull();
  }

  @Test
  public void handleRequest_taggingError_failure() {
    ResourceModel model = ResourceModel.builder()
        .appId(APP_ID)
        .environmentName(ENV_NAME)
        .overrides(null)
        .values(null)
        .tags(null)
        .name(null)
        .build();

    AwsServiceException e = AwsServiceException.builder()
        .awsErrorDetails(AwsErrorDetails.builder().errorCode("AccessDeniedException").build())
        .message(TaggingHelpers.SAMPLE_TAGGING_ACCESS_DENIED_MESSAGE)
        .build();

    when(proxyClient.client().deleteTheme(any(DeleteThemeRequest.class)))
        .thenThrow(e);

    ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
        .desiredResourceState(model)
        .build();

    ProgressEvent<ResourceModel, CallbackContext> response = new DeleteHandler().handleRequest(proxy, request,
        new CallbackContext(), proxyClient, logger);

    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
    assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
    assertThat(response.getResourceModels()).isNull();
    assertThat(response.getMessage()).isNotNull();
    assertThat(response.getErrorCode()).isNotNull();
    assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.UnauthorizedTaggingOperation);
  }
}
