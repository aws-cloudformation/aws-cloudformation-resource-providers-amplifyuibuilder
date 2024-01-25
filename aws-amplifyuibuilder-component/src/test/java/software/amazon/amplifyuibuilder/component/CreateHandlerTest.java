package software.amazon.amplifyuibuilder.component;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.CreateComponentRequest;
import software.amazon.awssdk.services.amplifyuibuilder.model.CreateComponentResponse;
import software.amazon.awssdk.services.amplifyuibuilder.model.GetComponentRequest;
import software.amazon.awssdk.services.amplifyuibuilder.model.GetComponentResponse;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.proxy.*;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import lombok.Getter;
import static software.amazon.amplifyuibuilder.common.Transformer.transformList;
import static software.amazon.amplifyuibuilder.common.Transformer.transformMap;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends AbstractTestBase {

        @Mock
        @Getter
        private AmazonWebServicesClientProxy proxy;

        @Mock
        @Getter
        private ProxyClient<AmplifyUiBuilderClient> proxyClient;

        @Mock
        AmplifyUiBuilderClient sdkClient;

        @BeforeEach
        public void setup() {
                proxy = new AmazonWebServicesClientProxy(
                                logger,
                                MOCK_CREDENTIALS,
                                () -> Duration.ofSeconds(600).toMillis());
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
                final CreateHandler handler = new CreateHandler();

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
                                                                .variants(transformList(VARIANT_CFN,
                                                                                Translator::translateVariantFromCFNToSDK))
                                                                .bindingProperties(transformMap(BINDING_PROPERTIES_CFN,
                                                                                Translator::translateBindingPropertyFromCFNToSDK))
                                                                .overrides(OVERRIDES)
                                                                .createdAt(Instant.now())
                                                                .modifiedAt(Instant.now())
                                                                .properties(transformMap(PROPERTIES_CFN,
                                                                                Translator::translateComponentPropertyFromCFNToSDK))
                                                                .collectionProperties(transformMap(
                                                                                COLLECTION_PROPERTIES_CFN,
                                                                                Translator::translateCollectionPropertyFromCFNToSDK))
                                                                .children(transformList(CHILDREN_CFN,
                                                                                Translator::translateChildComponentFromCFNToSDK))
                                                                .events(transformMap(EVENTS_CFN,
                                                                                Translator::translateEventFromCFNToSDK))
                                                                .schemaVersion(SCHEMA_VERSION)
                                                                .tags(TAGS)
                                                                .sourceId("123456")
                                                                .build())
                                .build();

                when(proxyClient.client().getComponent(any(GetComponentRequest.class)))
                                .thenReturn(getResponse);

                final CreateComponentResponse createResponse = CreateComponentResponse
                                .builder()
                                .entity(
                                                (software.amazon.awssdk.services.amplifyuibuilder.model.Component
                                                                .builder()
                                                                // Use this returned ID to pass to read handler after
                                                                // component is created
                                                                .id(ID)
                                                                .build()))
                                .build();

                when(
                                proxyClient.client().createComponent(any(CreateComponentRequest.class)))
                                .thenReturn(createResponse);

                final ResourceModel model = ResourceModel
                                .builder()
                                .environmentName(ENV_NAME)
                                .appId(APP_ID)
                                .name(NAME)
                                .componentType(TYPE)
                                .variants(VARIANT_CFN)
                                .bindingProperties(BINDING_PROPERTIES_CFN)
                                .overrides(OVERRIDES)
                                .properties(PROPERTIES_CFN)
                                .tags(TAGS)
                                .children(CHILDREN_CFN)
                                .collectionProperties(COLLECTION_PROPERTIES_CFN)
                                .events(EVENTS_CFN)
                                .schemaVersion(SCHEMA_VERSION)
                                .sourceId("123456")
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
                                logger);
                ResourceModel component = response.getResourceModel();

                assertThat(response).isNotNull();

                assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
                assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
                assertThat(response.getResourceModels()).isNull();
                assertThat(response.getMessage()).isNull();
                assertThat(response.getErrorCode()).isNull();

                assertThat(component.getChildren().size()).isEqualTo(model.getChildren().size());
                assertThat(component.getProperties().keySet()).isEqualTo(model.getProperties().keySet());
                assertThat(component.getVariants().size()).isEqualTo(model.getVariants().size());
                assertThat(component.getBindingProperties().keySet()).isEqualTo(model.getBindingProperties().keySet());
                assertThat(component.getOverrides()).isEqualTo(model.getOverrides());
                assertThat(component.getCollectionProperties().keySet())
                                .isEqualTo(model.getCollectionProperties().keySet());
                assertThat(component.getTags()).isEqualTo(model.getTags());
                assertThat(component.getSourceId()).isEqualTo(model.getSourceId());
                assertThat(component.getComponentType()).isEqualTo(model.getComponentType());
                assertThat(component.getEvents().keySet()).isEqualTo(model.getEvents().keySet());
                assertThat(component.getSchemaVersion()).isEqualTo(model.getSchemaVersion());
        }

        @Test
        public void handleRequest_NullTags() {
                final CreateHandler handler = new CreateHandler();

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
                                                                .variants(transformList(VARIANT_CFN,
                                                                                Translator::translateVariantFromCFNToSDK))
                                                                .bindingProperties(transformMap(BINDING_PROPERTIES_CFN,
                                                                                Translator::translateBindingPropertyFromCFNToSDK))
                                                                .overrides(OVERRIDES)
                                                                .createdAt(Instant.now())
                                                                .modifiedAt(Instant.now())
                                                                .properties(transformMap(PROPERTIES_CFN,
                                                                                Translator::translateComponentPropertyFromCFNToSDK))
                                                                .collectionProperties(transformMap(
                                                                                COLLECTION_PROPERTIES_CFN,
                                                                                Translator::translateCollectionPropertyFromCFNToSDK))
                                                                .children(transformList(CHILDREN_CFN,
                                                                                Translator::translateChildComponentFromCFNToSDK))
                                                                .events(transformMap(EVENTS_CFN,
                                                                                Translator::translateEventFromCFNToSDK))
                                                                .schemaVersion(SCHEMA_VERSION)
                                                                .build())
                                .build();

                when(proxyClient.client().getComponent(any(GetComponentRequest.class)))
                                .thenReturn(getResponse);

                final CreateComponentResponse createResponse = CreateComponentResponse
                                .builder()
                                .entity(
                                                (software.amazon.awssdk.services.amplifyuibuilder.model.Component
                                                                .builder()
                                                                // Use this returned ID to pass to read handler after
                                                                // component is created
                                                                .id(ID)
                                                                .build()))
                                .build();

                when(
                                proxyClient.client().createComponent(any(CreateComponentRequest.class)))
                                .thenReturn(createResponse);

                final ResourceModel model = ResourceModel
                                .builder()
                                .environmentName(ENV_NAME)
                                .appId(APP_ID)
                                .name(NAME)
                                .componentType(TYPE)
                                .variants(VARIANT_CFN)
                                .bindingProperties(BINDING_PROPERTIES_CFN)
                                .overrides(OVERRIDES)
                                .properties(PROPERTIES_CFN)
                                .children(CHILDREN_CFN)
                                .collectionProperties(COLLECTION_PROPERTIES_CFN)
                                .events(EVENTS_CFN)
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
                                logger);
                ResourceModel component = response.getResourceModel();

                assertThat(response).isNotNull();

                assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
                assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
                assertThat(response.getResourceModels()).isNull();
                assertThat(response.getMessage()).isNull();
                assertThat(response.getErrorCode()).isNull();

                assertThat(component.getChildren().size()).isEqualTo(model.getChildren().size());
                assertThat(component.getProperties().keySet()).isEqualTo(model.getProperties().keySet());
                assertThat(component.getVariants().size()).isEqualTo(model.getVariants().size());
                assertThat(component.getBindingProperties().keySet()).isEqualTo(model.getBindingProperties().keySet());
                assertThat(component.getOverrides()).isEqualTo(model.getOverrides());
                assertThat(component.getCollectionProperties().keySet())
                                .isEqualTo(model.getCollectionProperties().keySet());
                assertThat(component.getTags()).isEqualTo(new HashMap<>());
                assertThat(component.getSourceId()).isEqualTo("");
                assertThat(component.getComponentType()).isEqualTo(model.getComponentType());
                assertThat(component.getEvents().keySet()).isEqualTo(model.getEvents().keySet());
                assertThat(component.getSchemaVersion()).isEqualTo(model.getSchemaVersion());
        }

        @Test
        public void handleRequest_WithSystemAndPreviousTags() {
                final CreateHandler handler = new CreateHandler();

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
                                                                .variants(transformList(VARIANT_CFN,
                                                                                Translator::translateVariantFromCFNToSDK))
                                                                .bindingProperties(transformMap(BINDING_PROPERTIES_CFN,
                                                                                Translator::translateBindingPropertyFromCFNToSDK))
                                                                .overrides(OVERRIDES)
                                                                .createdAt(Instant.now())
                                                                .modifiedAt(Instant.now())
                                                                .properties(transformMap(PROPERTIES_CFN,
                                                                                Translator::translateComponentPropertyFromCFNToSDK))
                                                                .collectionProperties(transformMap(
                                                                                COLLECTION_PROPERTIES_CFN,
                                                                                Translator::translateCollectionPropertyFromCFNToSDK))
                                                                .children(transformList(CHILDREN_CFN,
                                                                                Translator::translateChildComponentFromCFNToSDK))
                                                                .events(transformMap(EVENTS_CFN,
                                                                                Translator::translateEventFromCFNToSDK))
                                                                .schemaVersion(SCHEMA_VERSION)
                                                                .build())
                                .build();

                when(proxyClient.client().getComponent(any(GetComponentRequest.class)))
                                .thenReturn(getResponse);

                final CreateComponentResponse createResponse = CreateComponentResponse
                                .builder()
                                .entity(
                                                (software.amazon.awssdk.services.amplifyuibuilder.model.Component
                                                                .builder()
                                                                // Use this returned ID to pass to read handler after
                                                                // component is created
                                                                .id(ID)
                                                                .build()))
                                .build();

                when(
                                proxyClient.client().createComponent(any(CreateComponentRequest.class)))
                                .thenReturn(createResponse);

                final ResourceModel model = ResourceModel
                                .builder()
                                .environmentName(ENV_NAME)
                                .appId(APP_ID)
                                .name(NAME)
                                .componentType(TYPE)
                                .variants(VARIANT_CFN)
                                .bindingProperties(BINDING_PROPERTIES_CFN)
                                .overrides(OVERRIDES)
                                .properties(PROPERTIES_CFN)
                                .children(CHILDREN_CFN)
                                .collectionProperties(COLLECTION_PROPERTIES_CFN)
                                .events(EVENTS_CFN)
                                .schemaVersion(SCHEMA_VERSION)
                                .build();

                CallbackContext context = new CallbackContext();

                final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest
                                .<ResourceModel>builder()
                                .previousSystemTags(Map.of("system-tag-key-1", "system-tag-value-1"))
                                .previousResourceTags(Map.of("resource-tag-key-1", "resource-tag-value-1"))
                                .desiredResourceState(model)
                                .systemTags(Map.of("system-tag-key-3", "system-tag-value-4"))
                                .desiredResourceTags(Map.of("resource-tag-key-3", "resource-tag-value-4"))
                                .build();

                final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(
                                proxy,
                                request,
                                context,
                                proxyClient,
                                logger);
                ResourceModel component = response.getResourceModel();

                assertThat(response).isNotNull();

                assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
                assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
                assertThat(response.getResourceModels()).isNull();
                assertThat(response.getMessage()).isNull();
                assertThat(response.getErrorCode()).isNull();

                assertThat(component.getChildren().size()).isEqualTo(model.getChildren().size());
                assertThat(component.getProperties().keySet()).isEqualTo(model.getProperties().keySet());
                assertThat(component.getVariants().size()).isEqualTo(model.getVariants().size());
                assertThat(component.getBindingProperties().keySet()).isEqualTo(model.getBindingProperties().keySet());
                assertThat(component.getOverrides()).isEqualTo(model.getOverrides());
                assertThat(component.getCollectionProperties().keySet())
                                .isEqualTo(model.getCollectionProperties().keySet());
                assertThat(component.getTags()).isEqualTo(new HashMap<>());
                assertThat(component.getSourceId()).isEqualTo("");
                assertThat(component.getComponentType()).isEqualTo(model.getComponentType());
                assertThat(component.getEvents().keySet()).isEqualTo(model.getEvents().keySet());
                assertThat(component.getSchemaVersion()).isEqualTo(model.getSchemaVersion());
        }

        // Tests resource model with null properties
        @Test
        public void handleRequest_NullProperties() {
                final CreateHandler handler = new CreateHandler();

                when(proxyClient.client().createComponent(any(CreateComponentRequest.class)))
                                .thenThrow(new CfnInvalidRequestException("Invalid parameters"));

                final ResourceModel model = ResourceModel
                                .builder()
                                .environmentName(ENV_NAME)
                                .appId(APP_ID)
                                .name(NAME)
                                .componentType(TYPE)
                                .build();

                CallbackContext context = new CallbackContext();

                final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest
                                .<ResourceModel>builder()
                                .desiredResourceState(model)
                                .build();

                Assertions.assertThrows(CfnInvalidRequestException.class, () -> handler.handleRequest(
                                proxy,
                                request,
                                context,
                                proxyClient,
                                logger));
        }
}
