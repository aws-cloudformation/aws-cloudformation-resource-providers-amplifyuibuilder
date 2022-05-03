package software.amazon.amplifyuibuilder.component;

import software.amazon.amplifyuibuilder.common.ClientWrapper;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.DeleteComponentResponse;
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
      final Logger logger
  ) {
    this.logger = logger;
    ResourceModel model = request.getDesiredResourceState();

    // Progress Chain
    // https://github.com/aws-cloudformation/cloudformation-cli-java-plugin/blob/master/src/main/java/software/amazon/cloudformation/proxy/CallChain.java
    return ProgressEvent
        .progress(request.getDesiredResourceState(), callbackContext)
        .then(
            progress ->
                proxy.initiate("AWS-AmplifyUIBuilder-Component::Delete", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                    .translateToServiceRequest(Translator::translateToDeleteRequest)
                    .makeServiceCall(((deleteComponentRequest, proxyInvocation) -> (DeleteComponentResponse) ClientWrapper.execute(
                        proxy,
                        deleteComponentRequest,
                        proxyInvocation.client()::deleteComponent,
                        ResourceModel.TYPE_NAME,
                        model.getId(),
                        logger
                    )))
                    .progress()
        )
        // Return the successful progress event without resource model
        .then(progress -> ProgressEvent.defaultSuccessHandler(null));
  }
}
