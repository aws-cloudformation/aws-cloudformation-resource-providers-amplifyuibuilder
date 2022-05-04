package software.amazon.amplifyuibuilder.component;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.GetComponentRequest;
import software.amazon.awssdk.services.amplifyuibuilder.model.GetComponentResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static software.amazon.amplifyuibuilder.common.Transformer.transformList;
import static software.amazon.amplifyuibuilder.common.Transformer.transformMap;

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
    verify(sdkClient, atLeastOnce()).serviceName();
    verifyNoMoreInteractions(sdkClient);
  }

  @Test
  public void handleRequest_SimpleSuccess() {
    final ReadHandler handler = new ReadHandler();

    final GetComponentResponse getResponse = GetComponentResponse
        .builder()
        .component(
            software.amazon.awssdk.services.amplifyuibuilder.model.Component
                .builder()
                .name(NAME)
                .id(ID)
                .appId(APP_ID)
                .environmentName(ENV_NAME)
                .componentType(TYPE)
                .variants(transformList(VARIANT_CFN, Translator::translateVariantFromCFNToSDK))
                .bindingProperties(transformMap(BINDING_PROPERTIES_CFN, Translator::translateBindingPropertyFromCFNToSDK))
                .overrides(OVERRIDES)
                .properties(transformMap(PROPERTIES_CFN, Translator::translateComponentPropertyFromCFNToSDK))
                .collectionProperties(transformMap(COLLECTION_PROPERTIES_CFN, Translator::translateCollectionPropertyFromCFNToSDK))
                .children(transformList(CHILDREN_CFN, Translator::translateChildComponentFromCFNToSDK))
                .tags(TAGS)
                .build()
        )
        .build();

    when(proxyClient.client().getComponent(any(GetComponentRequest.class)))
        .thenReturn(getResponse);

    final ResourceModel model = ResourceModel
        .builder()
        .id(ID)
        .appId(APP_ID)
        .environmentName(ENV_NAME)
        .name(NAME)
        .build();

    final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest
        .<ResourceModel>builder()
        .desiredResourceState(model)
        .build();

    final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(
        proxy,
        request,
        new CallbackContext(),
        proxyClient,
        logger
    );

    ResourceModel responseResourceModel = response.getResourceModel();
    ResourceModel requestDesiredResourceState = request.getDesiredResourceState();
    assertThat(responseResourceModel.getAppId()).isEqualTo(requestDesiredResourceState.getAppId());
    assertThat(responseResourceModel.getEnvironmentName()).isEqualTo(requestDesiredResourceState.getEnvironmentName());
    assertThat(responseResourceModel.getId()).isEqualTo(requestDesiredResourceState.getId());
    assertThat(responseResourceModel.getName()).isEqualTo(requestDesiredResourceState.getName());
  }
}
