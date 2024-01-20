package software.amazon.amplifyuibuilder.form;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.FormSummary;
import software.amazon.awssdk.services.amplifyuibuilder.model.ListFormsRequest;
import software.amazon.awssdk.services.amplifyuibuilder.model.ListFormsResponse;
import software.amazon.cloudformation.proxy.*;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ListHandlerTest extends AbstractTestBase {

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
    verifyNoMoreInteractions(sdkClient);
  }

  @Test
  public void handleRequest_SimpleSuccess() {
    final ListHandler handler = new ListHandler();

    final ListFormsResponse listResponse = ListFormsResponse.builder()
        .entities(ImmutableList.of(FormSummary.builder()
            .appId(APP_ID)
            .environmentName(ENV_NAME)
            .id(ID)
            .name(NAME)
            .build()))
        .build();

    when(proxyClient.client().listForms(any(ListFormsRequest.class)))
        .thenReturn(listResponse);

    final ResourceModel model = ResourceModel.builder()
        .appId(APP_ID)
        .environmentName(ENV_NAME)
        .build();

    final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
        .desiredResourceState(model)
        .build();

    final ProgressEvent<ResourceModel, CallbackContext> response =
        handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
    assertThat(response.getCallbackContext()).isNull();
    assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
    assertThat(response.getResourceModel()).isNull();
    assertThat(response.getResourceModels()).isNotNull();
    assertThat(response.getMessage()).isNull();
    assertThat(response.getErrorCode()).isNull();
  }
}
