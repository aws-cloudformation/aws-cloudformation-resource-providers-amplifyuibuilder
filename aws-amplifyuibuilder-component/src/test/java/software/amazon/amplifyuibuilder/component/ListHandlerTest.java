package software.amazon.amplifyuibuilder.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.ComponentSummary;
import software.amazon.awssdk.services.amplifyuibuilder.model.ListComponentsRequest;
import software.amazon.awssdk.services.amplifyuibuilder.model.ListComponentsResponse;
import software.amazon.cloudformation.proxy.*;

import java.time.Duration;

@ExtendWith(MockitoExtension.class)
public class ListHandlerTest extends AbstractTestBase {

  @Mock
  private AmazonWebServicesClientProxy proxy;

  @Mock
  private ProxyClient<AmplifyUiBuilderClient> proxyClient;

  @Mock
  AmplifyUiBuilderClient sdkClient;

  @BeforeEach
  public void setup() {
    proxy =
        new AmazonWebServicesClientProxy(
            logger,
            MOCK_CREDENTIALS,
            () -> Duration.ofSeconds(600).toMillis()
        );
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

    final ListComponentsResponse getResponse = ListComponentsResponse
        .builder()
        .nextToken(NEXT_TOKEN)
        .entities(ImmutableList.of(
            ComponentSummary
                .builder()
                .appId(APP_ID)
                .componentType(TYPE)
                .environmentName(ENV_NAME)
                .name(NAME)
                .id(ID)
                .build()
        ))
        .build();

    when(proxyClient.client().listComponents(any(ListComponentsRequest.class)))
        .thenReturn(getResponse);

    final ResourceModel model = ResourceModel
        .builder()
        .appId(APP_ID)
        .environmentName(ENV_NAME)
        .build();

    CallbackContext context = new CallbackContext();

    final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest
        .<ResourceModel>builder()
        .desiredResourceState(model)
        .build();

    final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(
        proxy,
        request,
        context,
        proxyClient,
        logger
    );

    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
    assertThat(response.getCallbackContext()).isNull();
    assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
    assertThat(response.getResourceModel()).isNull();
    assertThat(response.getResourceModels()).isNotNull();
    assertThat(response.getMessage()).isNull();
    assertThat(response.getErrorCode()).isNull();
    assertThat(response.getNextToken()).isEqualTo(NEXT_TOKEN);
  }
}
