package software.amazon.amplifyuibuilder.component;

import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.CreateComponentResponse;
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
      final Logger logger
  ) {
    this.logger = logger;
    ResourceModel model = request.getDesiredResourceState();
    logger.log("CreateHandler invoked");
    return ProgressEvent.progress(model, callbackContext)
        .then(progress ->
            proxy
                .initiate(
                    "AWS-AmplifyUIBuilder-Component::Create",
                    proxyClient,
                    request.getDesiredResourceState(),
                    callbackContext
                )
                .translateToServiceRequest(Translator::translateToCreateRequest)
                .makeServiceCall((createComponentRequest, proxyInvocation) -> {
                  CreateComponentResponse response = createComponent(createComponentRequest, proxyInvocation);
                  logger.log("Successfully created component with id: " + response.entity().id());
                  // Set the ID from the created component to do a read request next
                  model.setId(response.entity().id());
                  return response;
                })
                .progress()
        )
        .then(progress ->
            new ReadHandler()
                .handleRequest(proxy, request, callbackContext, proxyClient, logger));
  }
}
