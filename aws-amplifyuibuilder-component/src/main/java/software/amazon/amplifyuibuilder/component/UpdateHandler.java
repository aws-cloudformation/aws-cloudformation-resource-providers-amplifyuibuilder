package software.amazon.amplifyuibuilder.component;

import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.GetComponentResponse;
import software.amazon.awssdk.services.amplifyuibuilder.model.UpdateComponentResponse;
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
      final Logger logger
  ) {
    this.logger = logger;
    logger.log("UpdateHandler invoked");
    return ProgressEvent
        .progress(request.getDesiredResourceState(), callbackContext)
        .then(
            progress ->
                proxy
                    .initiate(
                        "AWS-AmplifyUIBuilder-Component::Update",
                        proxyClient,
                        progress.getResourceModel(),
                        progress.getCallbackContext()
                    )
                    .translateToServiceRequest((model) -> {
                      logger.log("translateToUpdateRequest for component with id: " + model.getId());
                      return Translator.translateToUpdateRequest(model);
                    })
                    .makeServiceCall((updateComponentRequest, proxyInvocation) -> {
                      UpdateComponentResponse response = updateComponent(updateComponentRequest, proxyInvocation);
                      logger.log("Successfully updated component with ID: " + response.entity().id());
                      return response;
                    })
                    .progress()
        )
        .then(
            progress ->
                new ReadHandler()
                    .handleRequest(proxy, request, callbackContext, proxyClient, logger)
        );
  }
}
