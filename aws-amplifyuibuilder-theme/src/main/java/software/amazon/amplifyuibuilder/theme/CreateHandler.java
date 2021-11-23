package software.amazon.amplifyuibuilder.theme;

import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.CreateThemeResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;


public class CreateHandler extends BaseHandlerStd {
    private Logger logger;

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<AmplifyUiBuilderClient> proxyClient,
        final Logger logger) {

        this.logger = logger;
        ResourceModel model = request.getDesiredResourceState();

        return ProgressEvent.progress(model, callbackContext)
            .then(progress ->
                proxy.initiate("AWS-AmplifyUIBuilder-Theme::Create", proxyClient,progress.getResourceModel(), progress.getCallbackContext())
                    .translateToServiceRequest(Translator::translateToCreateRequest)
                    .makeServiceCall((createThemeRequest, proxyInvocation) -> {
                      CreateThemeResponse response = (CreateThemeResponse) ClientWrapper.execute(
                          proxy,
                          createThemeRequest,
                          proxyInvocation.client()::createTheme,
                          ResourceModel.TYPE_NAME,
                          model.getId(),
                          logger
                      );
                      // Set ID for the following read request
                      model.setId(response.entity().id());
                      return response;
                    })
                    .progress()
                )
            .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }
}
