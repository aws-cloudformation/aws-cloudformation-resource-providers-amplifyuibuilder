package software.amazon.amplifyuibuilder.theme;

import java.time.Duration;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.*;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<AmplifyUiBuilderClient> proxyClient;

    @Mock
    AmplifyUiBuilderClient sdkClient;

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
        final CreateHandler handler = new CreateHandler();

        final GetThemeResponse getResponse = GetThemeResponse.builder()
            .theme(Theme.builder()
                .id(ID)
                .appId(APP_ID)
                .environmentName(ENV_NAME)
                .name(NAME)
                .createdAt(NOW)
                .modifiedAt(NOW)
                .values(Translator.translateThemeValuesListFromCFNToSDK(THEME_VALUES_LIST))
                .overrides(Translator.translateThemeValuesListFromCFNToSDK(THEME_VALUES_LIST))
                .tags(TAGS)
                .build())
            .build();

        when(proxyClient.client().getTheme(any(GetThemeRequest.class)))
            .thenReturn(getResponse);

        final CreateThemeResponse createResponse = CreateThemeResponse.builder()
            .entity(Theme.builder()
                // Use this returned ID to pass to read handler after component is created
                .id(ID)
                .build())
            .build();

        when(proxyClient.client().createTheme(any(CreateThemeRequest.class)))
            .thenReturn(createResponse);

        final ResourceModel model = ResourceModel.builder()
            .appId(APP_ID)
            .environmentName(ENV_NAME)
            .overrides(THEME_VALUES_LIST)
            .values(THEME_VALUES_LIST)
            .tags(TAGS)
            .name(NAME)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        final ResourceModel actual = response.getResourceModel();
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(actual.getOverrides().size()).isEqualTo(model.getOverrides().size());
        assertThat(actual.getValues().size()).isEqualTo(model.getValues().size());
        assertThat(actual.getTags()).isEqualTo(model.getTags());
        assertThat(actual.getName()).isEqualTo(model.getName());
        assertThat(actual.getId()).isEqualTo(model.getId());
        assertThat(actual.getEnvironmentName()).isEqualTo(model.getEnvironmentName());
        assertThat(actual.getAppId()).isEqualTo(model.getAppId());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_NullSimpleSuccess() {
        final CreateHandler handler = new CreateHandler();

        final GetThemeResponse getResponse = GetThemeResponse.builder()
            .theme(Theme.builder()
                .id(ID)
                .appId(APP_ID)
                .environmentName(ENV_NAME)
                .name(NAME)
                .createdAt(NOW)
                .modifiedAt(NOW)
                .tags(TAGS)
                .build())
            .build();

        when(proxyClient.client().getTheme(any(GetThemeRequest.class)))
            .thenReturn(getResponse);

        final CreateThemeResponse createResponse = CreateThemeResponse.builder()
            .entity(Theme.builder()
                // Use this returned ID to pass to read handler after component is created
                .id(ID)
                .build())
            .build();

        when(proxyClient.client().createTheme(any(CreateThemeRequest.class)))
            .thenReturn(createResponse);

        final ResourceModel model = ResourceModel.builder()
            .appId(APP_ID)
            .environmentName(ENV_NAME)
            .tags(TAGS)
            .name(NAME)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        final ResourceModel actual = response.getResourceModel();
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(actual.getOverrides().size()).isEqualTo(0);
        assertThat(actual.getValues().size()).isEqualTo(0);
        assertThat(actual.getTags()).isEqualTo(model.getTags());
        assertThat(actual.getName()).isEqualTo(model.getName());
        assertThat(actual.getId()).isEqualTo(model.getId());
        assertThat(actual.getEnvironmentName()).isEqualTo(model.getEnvironmentName());
        assertThat(actual.getAppId()).isEqualTo(model.getAppId());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }
}
