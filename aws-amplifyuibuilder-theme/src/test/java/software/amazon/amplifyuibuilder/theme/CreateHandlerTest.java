package software.amazon.amplifyuibuilder.theme;

import org.junit.jupiter.api.*;
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
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static software.amazon.amplifyuibuilder.common.Transformer.transformList;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends AbstractTestBase {

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
    public void tearDown() {
        verify(sdkClient, atLeastOnce()).serviceName();
        verifyNoMoreInteractions(sdkClient);
    }

    private GetThemeResponse createGetThemeResponse(boolean withTags) {
        Theme.Builder themeBuilder = Theme.builder()
                .id(ID)
                .appId(APP_ID)
                .environmentName(ENV_NAME)
                .name(NAME)
                .createdAt(NOW)
                .modifiedAt(NOW)
                .values(transformList(THEME_VALUES_LIST, Translator::translateThemeValuesFromCFNToSDK))
                .overrides(transformList(THEME_VALUES_LIST, Translator::translateThemeValuesFromCFNToSDK));

        if (withTags) {
            themeBuilder.tags(TAGS);
        }

        return GetThemeResponse.builder()
                .theme(themeBuilder.build())
                .build();
    }

    private CreateThemeResponse createCreateThemeResponse() {
        return CreateThemeResponse.builder()
                .entity(Theme.builder().id(ID).build())
                .build();
    }

    private ResourceModel createResourceModel(boolean withTags) {
        ResourceModel.ResourceModelBuilder builder = ResourceModel.builder()
                .appId(APP_ID)
                .environmentName(ENV_NAME)
                .overrides(THEME_VALUES_LIST)
                .values(THEME_VALUES_LIST)
                .name(NAME);

        if (withTags) {
            builder.tags(TAGS);
        } else {
            builder.tags(new HashMap<>());
        }

        return builder.build();
    }

    private void assertModelEquality(ResourceModel actual, ResourceModel expected) {
        if (expected.getOverrides() != null) {
            assertThat(actual.getOverrides()).isNotNull();
            assertThat(actual.getOverrides().size()).isEqualTo(expected.getOverrides().size());
        } else {
            assertThat(actual.getOverrides()).isNull();
        }

        if (expected.getValues() != null) {
            assertThat(actual.getValues()).isNotNull();
            assertThat(actual.getValues().size()).isEqualTo(expected.getValues().size());
        } else {
            assertThat(actual.getValues()).isNull();
        }

        // Check tags separately as they are a Map and not a List
        if (expected.getTags() != null) {
            assertThat(actual.getTags()).isNotNull();
            assertThat(actual.getTags()).isEqualTo(expected.getTags());
        } else {
            assertThat(actual.getTags()).isNull();
        }

        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getEnvironmentName()).isEqualTo(expected.getEnvironmentName());
        assertThat(actual.getAppId()).isEqualTo(expected.getAppId());
    }

    private void assertResponseProperties(ProgressEvent<ResourceModel, CallbackContext> response, ResourceModel model) {
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertModelEquality(response.getResourceModel(), model);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    private ProgressEvent<ResourceModel, CallbackContext> executeHandler(ResourceModel model, boolean withTags) {
        CreateHandler handler = new CreateHandler();

        when(proxyClient.client().getTheme(any(GetThemeRequest.class)))
                .thenReturn(createGetThemeResponse(withTags));
        when(proxyClient.client().createTheme(any(CreateThemeRequest.class)))
                .thenReturn(createCreateThemeResponse());

        ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        return handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        ResourceModel model = createResourceModel(true);
        ProgressEvent<ResourceModel, CallbackContext> response = executeHandler(model, true);
        assertResponseProperties(response, model);
    }

    @Test
    public void handleRequest_EmptyTags() {
        ResourceModel model = createResourceModel(false);
        ProgressEvent<ResourceModel, CallbackContext> response = executeHandler(model, false);
        assertResponseProperties(response, model);
    }

    @Test
    public void handleRequest_NullSimpleSuccess() {
        ResourceModel model = ResourceModel.builder()
                .appId(APP_ID)
                .environmentName(ENV_NAME)
                .overrides(null)
                .values(null)
                .tags(null)
                .name(null)
                .build();

        GetThemeResponse getResponse = GetThemeResponse.builder()
                .theme(Theme.builder().id(ID).build()) // No overrides or values
                .build();

        CreateThemeResponse createResponse = CreateThemeResponse.builder()
                .entity(Theme.builder().id(ID).build())
                .build();

        when(proxyClient.client().getTheme(any(GetThemeRequest.class)))
                .thenReturn(getResponse);
        when(proxyClient.client().createTheme(any(CreateThemeRequest.class)))
                .thenReturn(createResponse);

        ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        ProgressEvent<ResourceModel, CallbackContext> response = new CreateHandler().handleRequest(proxy, request,
                new CallbackContext(), proxyClient, logger);

        assertThat(response.getResourceModel().getOverrides()).isNull();
        assertThat(response.getResourceModel().getValues()).isNull();
        assertThat(response.getResourceModel().getTags()).isNull();
        assertThat(response.getResourceModel().getName()).isNull();
    }

    @Test
    public void handleRequest_taggingError_failure() {
        ResourceModel model = ResourceModel.builder()
                .appId(APP_ID)
                .environmentName(ENV_NAME)
                .overrides(null)
                .values(null)
                .tags(null)
                .name(null)
                .build();

        AwsServiceException e = AwsServiceException.builder()
            .awsErrorDetails(AwsErrorDetails.builder().errorCode("AccessDeniedException").build())
            .message(TaggingHelpers.SAMPLE_TAGGING_ACCESS_DENIED_MESSAGE)
            .build();

        CreateThemeResponse createResponse = CreateThemeResponse.builder()
            .build();

        when(proxyClient.client().createTheme(any (CreateThemeRequest.class)))
            .thenThrow(e)
            .thenReturn(createResponse);

        ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        ProgressEvent<ResourceModel, CallbackContext> response =
            new CreateHandler().handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNotNull();
        assertThat(response.getErrorCode()).isNotNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.UnauthorizedTaggingOperation);
    }

}
