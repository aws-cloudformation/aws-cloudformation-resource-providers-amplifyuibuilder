package software.amazon.amplifyuibuilder.theme;

import java.time.Duration;

import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.GetThemeRequest;
import software.amazon.awssdk.services.amplifyuibuilder.model.GetThemeResponse;
import software.amazon.awssdk.services.amplifyuibuilder.model.Theme;
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
public class ReadHandlerTest extends AbstractTestBase {

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
        final ReadHandler handler = new ReadHandler();

        final GetThemeResponse getResponse = GetThemeResponse
            .builder()
            .theme(Theme.builder()
                .appId(APP_ID)
                .environmentName(ENV_NAME)
                .id(ID)
                .createdAt(NOW)
                .modifiedAt(NOW)
                .name(NAME)
                .tags(TAGS)
                .values(Translator.translateThemeValuesListFromCFNToSDK(THEME_VALUES_LIST))
                .build())
            .build();

        when(proxyClient.client().getTheme(any(GetThemeRequest.class)))
            .thenReturn(getResponse);

        final ResourceModel model = ResourceModel.builder()
            .id(ID)
            .appId(APP_ID)
            .environmentName(ENV_NAME)
            .createdAt(NOW.toString())
            .modifiedAt(NOW.toString())
            .name(NAME)
            .values(THEME_VALUES_LIST)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        ResourceModel actual = response.getResourceModel();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
        assertThat(actual.getAppId()).isEqualTo(model.getAppId());
        assertThat(actual.getId()).isEqualTo(model.getId());
        assertThat(actual.getName()).isEqualTo(model.getName());
        assertThat(actual.getEnvironmentName()).isEqualTo(model.getEnvironmentName());
        assertThat(actual.getValues().size()).isEqualTo(model.getValues().size());
        assertThat(model.getOverrides()).isNull();
        assertThat(actual.getOverrides().size()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();

    }
}
