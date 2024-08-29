package software.amazon.amplifyuibuilder.form;

import software.amazon.amplifyuibuilder.common.ClientWrapper;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.DeleteFormResponse;
import software.amazon.cloudformation.proxy.*;

public class DeleteHandler extends BaseHandlerStd {

  public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
      final AmazonWebServicesClientProxy proxy,
      final ResourceHandlerRequest<ResourceModel> request,
      final CallbackContext callbackContext,
      final ProxyClient<AmplifyUiBuilderClient> proxyClient,
      final Logger logger) {

    final ResourceModel model = request.getDesiredResourceState();

    return ProgressEvent.progress(model, callbackContext)
        .then(progress ->
            proxy.initiate("AWS-AmplifyUIBuilder-Form::Delete", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                .translateToServiceRequest(Translator::translateToDeleteRequest)
                .makeServiceCall((deleteFormRequest, proxyInvocation) -> (DeleteFormResponse) ClientWrapper.execute(
                    proxy,
                    deleteFormRequest,
                    proxyInvocation.client()::deleteForm,
                    ResourceModel.TYPE_NAME,
                    model.getId(),
                    logger
                ))
                .handleError((awsRequest, exception, client, model1, context) -> {
                  return handleErrorInternal(request, exception, proxyClient, model1, callbackContext);
                })
                .progress()
        )
        .then(progress -> ProgressEvent.defaultSuccessHandler(null));
  }
}
