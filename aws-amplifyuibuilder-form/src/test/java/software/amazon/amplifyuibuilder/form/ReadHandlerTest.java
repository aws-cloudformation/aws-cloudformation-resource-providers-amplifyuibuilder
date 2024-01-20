package software.amazon.amplifyuibuilder.form;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.Form;
import software.amazon.awssdk.services.amplifyuibuilder.model.GetFormRequest;
import software.amazon.awssdk.services.amplifyuibuilder.model.GetFormResponse;
import software.amazon.cloudformation.proxy.*;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static software.amazon.amplifyuibuilder.common.Transformer.*;

@ExtendWith(MockitoExtension.class)
public class ReadHandlerTest extends AbstractTestBase {

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

  @AfterEach
  public void tear_down() {
    verify(sdkClient, atLeastOnce()).serviceName();
    verifyNoMoreInteractions(sdkClient);
  }

  @Test
  public void handleRequest_SimpleSuccess() {
    final ReadHandler handler = new ReadHandler();

    final GetFormResponse getResponse;
    getResponse = GetFormResponse.builder()
        .form(Form.builder()
            .appId(APP_ID)
            .environmentName(ENV_NAME)
            .id(ID)
            .name(NAME)
            .formActionType(ACTION_TYPE)
            .style(Translator.mapStyleCFNToSDK(STYLES))
            .dataType(Translator.mapDataTypeCFNToSDK(DATA_TYPE))
            .fields(transformMap(FIELDS, Translator::mapFieldConfigCFNToSDK))
            .sectionalElements(transformMap(SECTIONAL_ELEMENTS, Translator::mapSectionalElementCFNToSDK))
            .schemaVersion(SCHEMA_VERSION)
            .labelDecorator(LABEL_DECORATOR)
            .tags(TAGS)
            .build())
        .build();

    when(proxyClient.client().getForm(any(GetFormRequest.class)))
        .thenReturn(getResponse);

    final ResourceModel model = ResourceModel.builder()
        .appId(APP_ID)
        .environmentName(ENV_NAME)
        .fields(FIELDS)
        .style(STYLES)
        .formActionType(ACTION_TYPE)
        .name(NAME)
        .id(ID)
        .build();

    final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
        .desiredResourceState(model)
        .build();

    final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request,
        new CallbackContext(), proxyClient, logger);
    ResourceModel responseResourceModel = response.getResourceModel();
    ResourceModel requestDesiredResourceState = request.getDesiredResourceState();

    assertThat(responseResourceModel).isNotNull();
    assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
    assertThat(response.getCallbackContext()).isNull();
    assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
    assertThat(responseResourceModel.getAppId()).isEqualTo(requestDesiredResourceState.getAppId());
    assertThat(responseResourceModel.getEnvironmentName()).isEqualTo(requestDesiredResourceState.getEnvironmentName());
    // assertThat(responseResourceModel.getFields()).isEqualTo(requestDesiredResourceState.getFields());
    // assertThat(responseResourceModel.getStyle()).isEqualTo(requestDesiredResourceState.getStyle());
    assertThat(responseResourceModel.getFormActionType()).isEqualTo(requestDesiredResourceState.getFormActionType());
    assertThat(response.getMessage()).isNull();
    assertThat(response.getErrorCode()).isNull();
  }
}
