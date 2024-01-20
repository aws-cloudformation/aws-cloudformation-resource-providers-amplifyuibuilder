package software.amazon.amplifyuibuilder.form;

import software.amazon.amplifyuibuilder.common.ClientWrapper;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.GetFormResponse;
import software.amazon.cloudformation.proxy.*;

public class ReadHandler extends BaseHandlerStd {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<AmplifyUiBuilderClient> proxyClient,
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();

        return proxy.initiate("AWS-AmplifyUIBuilder-Form::Read", proxyClient, model, callbackContext)
            .translateToServiceRequest(Translator::translateToReadRequest)
            .makeServiceCall((getFormRequest, proxyInvocation) -> (GetFormResponse) ClientWrapper.execute(
                proxy,
                getFormRequest,
                proxyInvocation.client()::getForm,
                ResourceModel.TYPE_NAME,
                model.getId(),
                logger
            ))
            .done(getFormResponse -> {
                ResourceModel modelRet = Translator.translateFromReadResponse(getFormResponse);
                logger.log("INFO: returning form Id: " + modelRet.getId());
                return ProgressEvent.defaultSuccessHandler(modelRet);
            });
    }
}
