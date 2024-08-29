package software.amazon.amplifyuibuilder.component;

import software.amazon.amplifyuibuilder.common.ClientWrapper;
import software.amazon.amplifyuibuilder.common.TaggingHelpers;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.UpdateComponentResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class UpdateHandler extends BaseHandlerStd {

  protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
      final AmazonWebServicesClientProxy proxy,
      final ResourceHandlerRequest<ResourceModel> request,
      final CallbackContext callbackContext,
      final ProxyClient<AmplifyUiBuilderClient> proxyClient,
      final Logger logger) {

    ResourceModel model = request.getDesiredResourceState();

    return ProgressEvent
        .progress(request.getDesiredResourceState(), callbackContext)
        .then(
            progress -> proxy
                .initiate(
                    "AWS-AmplifyUIBuilder-Component::Update",
                    proxyClient,
                    progress.getResourceModel(),
                    progress.getCallbackContext())
                .translateToServiceRequest(
                    resourceModel -> Translator.translateToUpdateRequest(resourceModel,
                        request.getDesiredResourceTags(), request.getClientRequestToken()))
                .makeServiceCall((updateComponentRequest, proxyInvocation) -> {
                  UpdateComponentResponse response = (UpdateComponentResponse) ClientWrapper.execute(
                      proxy,
                      updateComponentRequest,
                      proxyInvocation.client()::updateComponent,
                      ResourceModel.TYPE_NAME,
                      model.getId(),
                      logger);
                  logger.log("Successfully updated component with ID: " + response.entity().id());

                  final String componentArn = TaggingHelpers.generateArn(
                      request.getRegion(),
                      request.getAwsAccountId(),
                      updateComponentRequest.appId(),
                      updateComponentRequest.environmentName(),
                      "components",
                      updateComponentRequest.id());

                  TaggingHelpers.updateTags(proxy, proxyInvocation, model.getAppId(), componentArn, ResourceModel.TYPE_NAME,
                      response.entity().tags(), model.getTags(), logger);

                  return response;
                })
                .handleError((awsRequest, exception, client, model1, context) -> {
                  return handleErrorInternal(request, exception, proxyClient, model1, callbackContext);
                })
                .progress())
        .then(
            progress -> new ReadHandler()
                .handleRequest(proxy, request, callbackContext, proxyClient, logger));
  }
}
