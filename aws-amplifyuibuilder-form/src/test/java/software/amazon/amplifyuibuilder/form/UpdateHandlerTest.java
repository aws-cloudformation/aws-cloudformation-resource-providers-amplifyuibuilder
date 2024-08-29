package software.amazon.amplifyuibuilder.form;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import software.amazon.amplifyuibuilder.common.TaggingHelpers;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.*;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UpdateHandlerTest extends AbstractTestBase {

  @Mock
  AmplifyUiBuilderClient sdkClient;
  @Mock
  private AmazonWebServicesClientProxy proxy;
  @Mock
  private ProxyClient<AmplifyUiBuilderClient> proxyClient;

  @BeforeEach
  public void setup() {
    proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
    sdkClient = mock(AmplifyUiBuilderClient.class);
    proxyClient = MOCK_PROXY(proxy, sdkClient);
  }

  @Test
  public void handleRequest_SimpleSuccess() {
    final UpdateHandler handler = new UpdateHandler();

    final GetFormResponse getResponse = GetFormResponse.builder()
        .form(Form.builder()
            .id(ID)
            .name(NAME)
            .environmentName(ENV_NAME)
            .appId(APP_ID)
            .build())
        .build();

    when(proxyClient.client().getForm(any(GetFormRequest.class)))
        .thenReturn(getResponse);

    final UpdateFormResponse updateResponse = UpdateFormResponse.builder()
        .entity(Form.builder().tags(new HashMap<String, String>()).build())
        .build();

    when(proxyClient.client().updateForm(any(UpdateFormRequest.class)))
        .thenReturn(updateResponse);

    final TagResourceResponse tagResourceResponse = TagResourceResponse.builder().build();
    when(proxyClient.client().tagResource(any(TagResourceRequest.class)))
        .thenReturn(tagResourceResponse);

    final UntagResourceResponse untagResourceResponse = UntagResourceResponse.builder().build();
    when(proxyClient.client().untagResource(any(UntagResourceRequest.class)))
        .thenReturn(untagResourceResponse);

    final ResourceModel model = ResourceModel.builder()
        .name(NAME)
        .environmentName(ENV_NAME)
        .appId(APP_ID)
        .id(ID)
        .cta(CTA)
        .build();

    final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
        .desiredResourceState(model)
        .build();

    final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request,
        new CallbackContext(), proxyClient, logger);

    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
    assertThat(response.getCallbackContext()).isNull();
    assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
    assertThat(response.getResourceModel().getAppId()).isEqualTo(request.getDesiredResourceState().getAppId());
    assertThat(response.getResourceModels()).isNull();
    assertThat(response.getMessage()).isNull();
    assertThat(response.getErrorCode()).isNull();
  }

  @Test
  public void handleRequest_UpdateWithoutCreate() {
    final UpdateHandler handler = new UpdateHandler();

    final ResourceModel modelWithNullID = ResourceModel.builder()
        .name(NAME)
        .environmentName(ENV_NAME)
        .appId(APP_ID)
        .build();

    final ResourceHandlerRequest<ResourceModel> requestWithNullID = ResourceHandlerRequest.<ResourceModel>builder()
        .desiredResourceState(modelWithNullID)
        .build();

    final ResourceModel modelWithEmptyStringID = ResourceModel.builder()
        .name(NAME)
        .environmentName(ENV_NAME)
        .appId(APP_ID)
        .id("")
        .build();

    final ResourceHandlerRequest<ResourceModel> requestWithEmptyStringID = ResourceHandlerRequest
        .<ResourceModel>builder()
        .desiredResourceState(modelWithEmptyStringID)
        .build();

    Assertions.assertThrows(CfnNotFoundException.class,
        () -> handler.handleRequest(proxy, requestWithNullID, new CallbackContext(), proxyClient, logger));

    Assertions.assertThrows(CfnNotFoundException.class, () -> handler.handleRequest(proxy, requestWithEmptyStringID,
        new CallbackContext(), proxyClient, logger));
  }

  @Test
  public void handleRequest_taggingError_failure() {
    final GetFormResponse getResponse = GetFormResponse.builder()
        .form(Form.builder()
            .id(ID)
            .name(NAME)
            .environmentName(ENV_NAME)
            .appId(APP_ID)
            .build())
        .build();

    when(proxyClient.client().getForm(any(GetFormRequest.class)))
        .thenReturn(getResponse);

        final ResourceModel model = ResourceModel.builder()
        .name(NAME)
        .environmentName(ENV_NAME)
        .appId(APP_ID)
        .id(ID)
        .cta(CTA)
        .build();

    AwsServiceException e = AwsServiceException.builder()
        .awsErrorDetails(AwsErrorDetails.builder().errorCode("AccessDeniedException").build())
        .message(TaggingHelpers.SAMPLE_TAGGING_ACCESS_DENIED_MESSAGE)
        .build();

    when(proxyClient.client().updateForm(any(UpdateFormRequest.class)))
        .thenThrow(e);

    ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
        .desiredResourceState(model)
        .build();

    ProgressEvent<ResourceModel, CallbackContext> response = new UpdateHandler().handleRequest(proxy, request,
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
