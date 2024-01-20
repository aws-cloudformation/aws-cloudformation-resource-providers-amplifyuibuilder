package software.amazon.amplifyuibuilder.component;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.*;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static software.amazon.amplifyuibuilder.common.Transformer.transformList;
import static software.amazon.amplifyuibuilder.common.Transformer.transformMap;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest extends AbstractTestBase {

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
    final UpdateHandler handler = new UpdateHandler();

    final GetComponentResponse getResponse = GetComponentResponse
        .builder()
        .component(
            Component
                .builder()
                .name(NAME)
                .id(ID)
                .appId(APP_ID)
                .environmentName(ENV_NAME)
                .componentType(TYPE)
                .variants(transformList(VARIANT_CFN, Translator::translateVariantFromCFNToSDK))
                .bindingProperties(transformMap(BINDING_PROPERTIES_CFN, Translator::translateBindingPropertyFromCFNToSDK))
                .overrides(OVERRIDES)
                .createdAt(Instant.now())
                .modifiedAt(Instant.now())
                .properties(transformMap(PROPERTIES_CFN, Translator::translateComponentPropertyFromCFNToSDK))
                .collectionProperties(transformMap(COLLECTION_PROPERTIES_CFN, Translator::translateCollectionPropertyFromCFNToSDK))
                .children(transformList(CHILDREN_CFN, Translator::translateChildComponentFromCFNToSDK))
                .tags(TAGS)
                .schemaVersion(SCHEMA_VERSION)
                .build()
        )
        .build();

    when(proxyClient.client().getComponent(any(GetComponentRequest.class)))
        .thenReturn(getResponse);

    final UpdateComponentResponse updateResponse = UpdateComponentResponse
        .builder()
        .entity(
            Component
                .builder()
                .build()

        )
        .build();

    when(proxyClient.client().updateComponent(any(UpdateComponentRequest.class)))
        .thenReturn(updateResponse);

    final ResourceModel model = ResourceModel
        .builder()
        .appId(APP_ID)
        .environmentName(ENV_NAME)
        .id(ID)
        .name(NAME)
        .id(ID)
        .componentType(TYPE)
        .children(new ArrayList<>())
        .variants(VARIANT_CFN)
        .bindingProperties(BINDING_PROPERTIES_CFN)
        .overrides(OVERRIDES)
        .properties(PROPERTIES_CFN)
        .collectionProperties(COLLECTION_PROPERTIES_CFN)
        .children(CHILDREN_CFN)
        .schemaVersion(SCHEMA_VERSION)
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

    ResourceModel component = response.getResourceModel();

    assertThat(response.getResourceModels()).isNull();
    assertThat(component.getAppId()).isEqualTo(model.getAppId());
    assertThat(component.getEnvironmentName()).isEqualTo(model.getEnvironmentName());
    assertThat(component.getId()).isEqualTo(model.getId());
    assertThat(component.getComponentType()).isEqualTo(model.getComponentType());
    assertThat(component.getName()).isEqualTo(model.getName());
    assertThat(component.getSchemaVersion()).isEqualTo(model.getSchemaVersion());
    assertThat(component.getVariants().size()).isEqualTo(model.getVariants().size());
    assertThat(component.getBindingProperties().keySet()).isEqualTo(model.getBindingProperties().keySet());
    assertThat(component.getOverrides()).isEqualTo(model.getOverrides());
    assertThat(component.getProperties().keySet()).isEqualTo(model.getProperties().keySet());
    assertThat(component.getCollectionProperties().keySet()).isEqualTo(model.getCollectionProperties().keySet());
    assertThat(component.getChildren().size()).isEqualTo(model.getChildren().size());
  }
}
