package software.amazon.amplifyuibuilder.form;

import software.amazon.amplifyuibuilder.common.ClientWrapper;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.CreateFormResponse;
import software.amazon.cloudformation.proxy.*;

import java.util.Map;

public class CreateHandler extends BaseHandlerStd {

  public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
      final AmazonWebServicesClientProxy proxy,
      final ResourceHandlerRequest<ResourceModel> request,
      final CallbackContext callbackContext,
      final ProxyClient<AmplifyUiBuilderClient> proxyClient,
      final Logger logger) {
    logger.log("Create Handler Invoked");
    final ResourceModel model = request.getDesiredResourceState();
    logger.log("System Tags: " + request.getSystemTags());
    logger.log("Stack Tags: " + request.getDesiredResourceTags());

    return ProgressEvent.progress(model, callbackContext)
        .then(progress ->
            proxy.initiate("AWS-AmplifyUIBuilder-Form::Create", proxyClient, request.getDesiredResourceState(), callbackContext)
                .translateToServiceRequest(resourceModel -> Translator.translateToCreateRequest(resourceModel, request.getDesiredResourceTags()))
                .makeServiceCall((createFormRequest, proxyInvocation) -> {
                  CreateFormResponse response = (CreateFormResponse) ClientWrapper.execute(
                      proxy,
                      createFormRequest,
                      proxyInvocation.client()::createForm,
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
