package software.amazon.amplifyuibuilder.form;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.*;
import software.amazon.cloudformation.proxy.*;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends AbstractTestBase {

        @Mock
        AmplifyUiBuilderClient sdkClient;
        @Mock
        private AmazonWebServicesClientProxy proxy;
        @Mock
        private ProxyClient<AmplifyUiBuilderClient> proxyClient;

        @BeforeEach
        public void setup() {
                proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS,
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

                final GetFormResponse getResponse = GetFormResponse.builder()
                                .form(Form.builder()
                                                .id(ID)
                                                .appId(APP_ID)
                                                .environmentName(ENV_NAME)
                                                .name(NAME)
                                                .cta(Translator.mapCtaCFNToSDK(CTA))
                                                .labelDecorator(LABEL_DECORATOR)
                                                .fields(MODEL_FIELDS)
                                                .tags(TAGS)
                                                .build())
                                .build();

                when(proxyClient.client().getForm(any(GetFormRequest.class)))
                                .thenReturn(getResponse);

                final CreateFormResponse createResponse = CreateFormResponse.builder()
                                .entity(Form.builder()
                                                // Only need the ID because it's assigned to the model then used in the
                                                // ReadHandler
                                                .id(ID)
                                                .build())
                                .build();

                when(proxyClient.client().createForm(any(CreateFormRequest.class)))
                                .thenReturn(createResponse);

                final ResourceModel model = ResourceModel.builder()
                                .appId(APP_ID)
                                .environmentName(ENV_NAME)
                                .name(NAME)
                                .dataType(DATA_TYPE)
                                .formActionType(ACTION_TYPE)
                                .fields(FIELDS)
                                .style(STYLES)
                                .sectionalElements(SECTIONAL_ELEMENTS)
                                .cta(CTA)
                                .labelDecorator(LABEL_DECORATOR)
                                .schemaVersion(SCHEMA_VERSION)
                                .tags(TAGS)
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
                assertThat(response.getResourceModel().getAppId())
                                .isEqualTo(request.getDesiredResourceState().getAppId());
                assertThat(response.getResourceModels()).isNull();
                assertThat(response.getMessage()).isNull();
                assertThat(response.getErrorCode()).isNull();
        }

        @Test
        public void handleRequest_NoTags() {
                final CreateHandler handler = new CreateHandler();

                final GetFormResponse getResponse = GetFormResponse.builder()
                                .form(Form.builder()
                                                .id(ID)
                                                .appId(APP_ID)
                                                .environmentName(ENV_NAME)
                                                .name(NAME)
                                                .cta(Translator.mapCtaCFNToSDK(CTA))
                                                .labelDecorator(LABEL_DECORATOR)
                                                .fields(MODEL_FIELDS)
                                                .build())
                                .build();

                when(proxyClient.client().getForm(any(GetFormRequest.class)))
                                .thenReturn(getResponse);

                final CreateFormResponse createResponse = CreateFormResponse.builder()
                                .entity(Form.builder()
                                                // Only need the ID because it's assigned to the model then used in the
                                                // ReadHandler
                                                .id(ID)
                                                .build())
                                .build();

                when(proxyClient.client().createForm(any(CreateFormRequest.class)))
                                .thenReturn(createResponse);

                final ResourceModel model = ResourceModel.builder()
                                .appId(APP_ID)
                                .environmentName(ENV_NAME)
                                .name(NAME)
                                .dataType(DATA_TYPE)
                                .formActionType(ACTION_TYPE)
                                .fields(FIELDS)
                                .style(STYLES)
                                .sectionalElements(SECTIONAL_ELEMENTS)
                                .cta(CTA)
                                .labelDecorator(LABEL_DECORATOR)
                                .schemaVersion(SCHEMA_VERSION)
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
                assertThat(response.getResourceModel().getAppId())
                                .isEqualTo(request.getDesiredResourceState().getAppId());
                assertThat(response.getResourceModels()).isNull();
                assertThat(response.getMessage()).isNull();
                assertThat(response.getErrorCode()).isNull();
        }
}
