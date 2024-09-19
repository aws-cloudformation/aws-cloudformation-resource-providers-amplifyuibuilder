package software.amazon.amplifyuibuilder.form;

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
import software.amazon.awssdk.services.amplifyuibuilder.model.*;
import software.amazon.cloudformation.proxy.*;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    public void handleRequest_WithSystemAndPreviousTags() {
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
                .previousSystemTags(Map.of("system-tag-key-1", "system-tag-value-1"))
                .previousResourceTags(Map.of("resource-tag-key-1", "resource-tag-value-1"))
                .desiredResourceState(model)
                .systemTags(Map.of("system-tag-key-3", "system-tag-value-4"))
                .desiredResourceTags(Map.of("resource-tag-key-3", "resource-tag-value-4"))
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

    @Test
    public void handleRequest_taggingError_failure() {
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

        AwsServiceException e = AwsServiceException.builder()
                .awsErrorDetails(AwsErrorDetails.builder().errorCode("AccessDeniedException").build())
                .message(TaggingHelpers.SAMPLE_TAGGING_ACCESS_DENIED_MESSAGE)
                .build();

        when(proxyClient.client().createForm(any(CreateFormRequest.class)))
                .thenThrow(e);

        ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        ProgressEvent<ResourceModel, CallbackContext> response = new CreateHandler().handleRequest(proxy, request,
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
