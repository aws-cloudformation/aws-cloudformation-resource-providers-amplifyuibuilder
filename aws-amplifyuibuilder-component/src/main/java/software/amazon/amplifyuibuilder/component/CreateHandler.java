package software.amazon.amplifyuibuilder.component;

import software.amazon.amplifyuibuilder.common.ClientWrapper;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.CreateComponentResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class CreateHandler extends BaseHandlerStd {

  protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
      final AmazonWebServicesClientProxy proxy,
      final ResourceHandlerRequest<ResourceModel> request,
      final CallbackContext callbackContext,
      final ProxyClient<AmplifyUiBuilderClient> proxyClient,
      final Logger logger) {

    ResourceModel model = request.getDesiredResourceState();
    logger.log("CreateHandler invoked");

    return ProgressEvent.progress(model, callbackContext)
        .then(progress -> proxy
            .initiate(
                "AWS-AmplifyUIBuilder-Component::Create",
                proxyClient,
                request.getDesiredResourceState(),
                callbackContext)
            .translateToServiceRequest(resourceModel -> Translator
                .translateToCreateRequest(resourceModel,
                    TagHelper.getNewDesiredTags(request),
                    request.getClientRequestToken()))
            .makeServiceCall((createComponentRequest, proxyInvocation) -> {
              CreateComponentResponse response = (CreateComponentResponse) ClientWrapper
                  .execute(
                      proxy,
                      createComponentRequest,
                      proxyInvocation.client()::createComponent,
                      ResourceModel.TYPE_NAME,
                      model.getId(),
                      logger);
              logger.log("Successfully created component with id: "
                  + response.entity().id());
              // Set the ID from the created component to do a read request
              // next
              model.setId(response.entity().id());
              return response;
            })
            .handleError((awsRequest, exception, client, model1, context) -> {
              return handleErrorInternal(request, exception, proxyClient, model1, callbackContext);
            })
            .progress())
        .then(progress -> new ReadHandler()
            .handleRequest(proxy, request, callbackContext, proxyClient, logger));
  }
}
