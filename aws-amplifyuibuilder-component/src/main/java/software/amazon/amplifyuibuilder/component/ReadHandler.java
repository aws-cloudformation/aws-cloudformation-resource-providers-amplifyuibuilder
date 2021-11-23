package software.amazon.amplifyuibuilder.component;

import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.GetComponentResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ReadHandler extends BaseHandlerStd {

  protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
      final AmazonWebServicesClientProxy proxy,
      final ResourceHandlerRequest<ResourceModel> request,
      final CallbackContext callbackContext,
      final ProxyClient<AmplifyUiBuilderClient> proxyClient,
      final Logger logger
  ) {
    this.logger = logger;

    ResourceModel model = request.getDesiredResourceState();
    logger.log("ReadHandler invoked");
    return proxy
        .initiate(
            "AWS-AmplifyUIBuilder-Component::Read",
            proxyClient,
            model,
            callbackContext
        )
        .translateToServiceRequest(Translator::translateToReadRequest)
        .makeServiceCall((getComponentRequest, proxyInvocation) -> {
          GetComponentResponse response = getComponent(getComponentRequest, proxyInvocation);
          logger.log("getComponent succeeded with component ID: " + response.component().id());
          return response;
        })
        .done(this::constructResourceModelFromResponse);
  }

  private ProgressEvent<ResourceModel, CallbackContext> constructResourceModelFromResponse(
      GetComponentResponse response
  ) {
    logger.log("Translating from read response");
    return ProgressEvent.defaultSuccessHandler(
        Translator.translateFromReadResponse(response)
    );
  }
}
