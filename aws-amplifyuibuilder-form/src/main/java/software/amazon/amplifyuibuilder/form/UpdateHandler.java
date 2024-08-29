package software.amazon.amplifyuibuilder.form;

import software.amazon.amplifyuibuilder.common.ClientWrapper;
import software.amazon.amplifyuibuilder.common.TaggingHelpers;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.UpdateFormResponse;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.*;

public class UpdateHandler extends BaseHandlerStd {

  public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
      final AmazonWebServicesClientProxy proxy,
      final ResourceHandlerRequest<ResourceModel> request,
      final CallbackContext callbackContext,
      final ProxyClient<AmplifyUiBuilderClient> proxyClient,
      final Logger logger) {

    final ResourceModel model = request.getDesiredResourceState();

    // Handle update without create
    if (model.getId() == null || model.getId().trim().equals("")) {
      throw new CfnNotFoundException(ResourceModel.TYPE_NAME, model.getId());
    }

    return ProgressEvent.progress(model, callbackContext)
        .then(progress -> proxy
            .initiate("AWS-AmplifyUIBuilder-Form::Update", proxyClient, model, progress.getCallbackContext())
            .translateToServiceRequest(Translator::translateToUpdateRequest)
            .makeServiceCall((updateFormRequest, proxyInvocation) -> {
              UpdateFormResponse response = (UpdateFormResponse) ClientWrapper.execute(
                  proxy,
                  updateFormRequest,
                  proxyInvocation.client()::updateForm,
                  ResourceModel.TYPE_NAME,
                  model.getId(),
                  logger);

              final String formArn = TaggingHelpers.generateArn(
                  request.getRegion(),
                  request.getAwsAccountId(),
                  updateFormRequest.appId(),
                  updateFormRequest.environmentName(),
                  "forms",
                  updateFormRequest.id());

              TaggingHelpers.updateTags(proxy, proxyInvocation, model.getAppId(), formArn, ResourceModel.TYPE_NAME,
                  response.entity().tags(), model.getTags(), logger);
              return response;
            })
            .handleError((awsRequest, exception, client, model1, context) -> {
              return handleErrorInternal(request, exception, proxyClient, model1, callbackContext);
            })
            .progress())
        .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
  }
}
