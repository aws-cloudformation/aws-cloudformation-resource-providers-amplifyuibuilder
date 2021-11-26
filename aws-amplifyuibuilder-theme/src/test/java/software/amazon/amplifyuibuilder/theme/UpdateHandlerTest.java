package software.amazon.amplifyuibuilder.theme;

import java.time.Duration;
import org.junit.jupiter.api.Assertions;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.*;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
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
public class UpdateHandlerTest extends AbstractTestBase {

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

    @Test
    public void handleRequest_SimpleSuccess() {
        final UpdateHandler handler = new UpdateHandler();

        final GetThemeResponse getResponse = GetThemeResponse.builder()
            .theme(Theme.builder()
                .id(ID)
                .name(NAME)
                .tags(TAGS)
                .appId(APP_ID)
                .overrides(Translator.translateThemeValuesListFromCFNToSDK(THEME_VALUES_LIST))
                .values(Translator.translateThemeValuesListFromCFNToSDK(THEME_VALUES_LIST))
                .environmentName(ENV_NAME)
                .modifiedAt(NOW)
                .createdAt(NOW)
                .build())
            .build();

        when(proxyClient.client().getTheme(any(GetThemeRequest.class)))
            .thenReturn(getResponse);

        final UpdateThemeResponse updateResponse = UpdateThemeResponse.builder()
            .entity(Theme.builder().build())
            .build();

        when(proxyClient.client().updateTheme(any(UpdateThemeRequest.class)))
            .thenReturn(updateResponse);

        final ResourceModel model = ResourceModel.builder()
            .appId(APP_ID)
            .environmentName(ENV_NAME)
            .id(ID)
            .name(NAME)
            .overrides(THEME_VALUES_LIST)
            .values(THEME_VALUES_LIST)
            .tags(TAGS)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        ResourceModel actual = response.getResourceModel();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(actual.getAppId()).isEqualTo(model.getAppId());
        assertThat(actual.getId()).isEqualTo(model.getId());
        assertThat(actual.getEnvironmentName()).isEqualTo(model.getEnvironmentName());
        assertThat(actual.getValues().size()).isEqualTo(model.getValues().size());
        assertThat(actual.getOverrides().size()).isEqualTo(model.getOverrides().size());
        assertThat(actual.getName()).isEqualTo(model.getName());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_NullID() {
        final UpdateHandler handler = new UpdateHandler();

        final ResourceModel model = ResourceModel.builder()
            .appId(APP_ID)
            .environmentName(ENV_NAME)
            .name(NAME)
            .overrides(THEME_VALUES_LIST)
            .values(THEME_VALUES_LIST)
            .tags(TAGS)
            .build();

        final ResourceModel emptyStringIDModel = ResourceModel.builder()
            .appId(APP_ID)
            .id("")
            .environmentName(ENV_NAME)
            .name(NAME)
            .overrides(THEME_VALUES_LIST)
            .values(THEME_VALUES_LIST)
            .tags(TAGS)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        final ResourceHandlerRequest<ResourceModel> emptyStringIDRequest = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(emptyStringIDModel)
            .build();

        Assertions.assertThrows(CfnNotFoundException.class, () -> {
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        });

        Assertions.assertThrows(CfnNotFoundException.class, () -> {
            handler.handleRequest(proxy, emptyStringIDRequest, new CallbackContext(), proxyClient, logger);
        });
    }
}
