package software.amazon.amplifyuibuilder.theme;

import software.amazon.amplifyuibuilder.common.ClientWrapper;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.CreateThemeResponse;
import software.amazon.cloudformation.proxy.*;

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
        .then(progress -> proxy.initiate(
            "AWS-AmplifyUIBuilder-Theme::Create",
            proxyClient,
            request.getDesiredResourceState(),
            callbackContext)
            .translateToServiceRequest(resourceModel -> Translator.translateToCreateRequest(resourceModel,
                request.getDesiredResourceTags(), request.getClientRequestToken()))
            .makeServiceCall((createThemeRequest, proxyInvocation) -> {
              CreateThemeResponse response = (CreateThemeResponse) ClientWrapper.execute(
                  proxy,
                  createThemeRequest,
                  proxyInvocation.client()::createTheme,
                  ResourceModel.TYPE_NAME,
                  model.getId(),
                  logger);

              logger.log("Successfully created theme with id: " + response.entity().id());

              // Set ID for the following read request
              model.setId(response.entity().id());
              return response;
            })
            .handleError((awsRequest, exception, client, model1, context) -> {
              return handleErrorInternal(request, exception, proxyClient, model1, callbackContext);
            })
            .progress())
        .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
  }
}
