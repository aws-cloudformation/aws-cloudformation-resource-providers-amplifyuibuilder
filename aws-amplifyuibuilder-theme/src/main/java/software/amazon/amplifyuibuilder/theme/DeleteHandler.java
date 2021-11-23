package software.amazon.amplifyuibuilder.theme;

import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.DeleteThemeResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class DeleteHandler extends BaseHandlerStd {
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
                proxy.initiate("AWS-AmplifyUIBuilder-Theme::Delete", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                    .translateToServiceRequest(Translator::translateToDeleteRequest)
                    .makeServiceCall((deleteThemeRequest, proxyInvocation) -> (DeleteThemeResponse) ClientWrapper.execute(
                        proxy,
                        deleteThemeRequest,
                        proxyInvocation.client()::deleteTheme,
                        ResourceModel.TYPE_NAME,
                        model.getId(),
                        logger
                    ))
                    .progress()
            )
            .then(progress -> ProgressEvent.defaultSuccessHandler(null));
    }
}
