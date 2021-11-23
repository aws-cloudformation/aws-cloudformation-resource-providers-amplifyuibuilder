package software.amazon.amplifyuibuilder.theme;

import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.UpdateThemeResponse;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class UpdateHandler extends BaseHandlerStd {
    private Logger logger;

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<AmplifyUiBuilderClient> proxyClient,
        final Logger logger) {

        this.logger = logger;
        ResourceModel model = request.getDesiredResourceState();

        // To pass contract test: contract_update_without_create
        if (model.getId() == null || model.getId().equals("")) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, model.getId());
        }

        return ProgressEvent.progress(model, callbackContext)
            .then(progress ->
                proxy.initiate("AWS-AmplifyUIBuilder-Theme::Update", proxyClient, model, progress.getCallbackContext())
                    .translateToServiceRequest(Translator::translateToUpdateRequest)
                    .makeServiceCall((updateThemeRequest, proxyInvocation) -> (UpdateThemeResponse) ClientWrapper.execute(
                        proxy,
                        updateThemeRequest,
                        proxyInvocation.client()::updateTheme,
                        ResourceModel.TYPE_NAME,
                        model.getId(),
                        logger
                    ))
                    .progress())
            .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }
}
