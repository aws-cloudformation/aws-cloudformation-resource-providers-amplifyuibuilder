package software.amazon.amplifyuibuilder.form;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.DeleteFormRequest;
import software.amazon.awssdk.services.amplifyuibuilder.model.DeleteFormResponse;
import software.amazon.cloudformation.proxy.*;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteHandlerTest extends AbstractTestBase {

  @Mock
  private AmazonWebServicesClientProxy proxy;

  @Mock
  private ProxyClient<AmplifyUiBuilderClient> proxyClient;

  @Mock
  private AmplifyUiBuilderClient sdkClient;

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

    final DeleteFormResponse deleteResponse = DeleteFormResponse.builder().build();

    when(proxyClient.client().deleteForm(any(DeleteFormRequest.class)))
        .thenReturn(deleteResponse);

    final ResourceModel model = ResourceModel.builder()
        .appId(APP_ID)
        .environmentName(ENV_NAME)
        .id(ID)
        .build();

    final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
        .desiredResourceState(model)
        .build();

    final ProgressEvent<ResourceModel, CallbackContext> response
        = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
    assertThat(response.getCallbackContext()).isNull();
    assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
    assertThat(response.getResourceModel()).isNull();
    assertThat(response.getResourceModels()).isNull();
    assertThat(response.getMessage()).isNull();
    assertThat(response.getErrorCode()).isNull();
  }
}
