package software.amazon.amplifyuibuilder.theme;

import software.amazon.amplifyuibuilder.common.ClientWrapper;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.GetThemeResponse;
import software.amazon.cloudformation.proxy.*;

public class ReadHandler extends BaseHandlerStd {
  private Logger logger;

  protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
      final AmazonWebServicesClientProxy proxy,
      final ResourceHandlerRequest<ResourceModel> request,
      final CallbackContext callbackContext,
      final ProxyClient<AmplifyUiBuilderClient> proxyClient,
      final Logger logger) {

    this.logger = logger;
    final ResourceModel model = request.getDesiredResourceState();

    return proxy.initiate("AWS-AmplifyUIBuilder-Theme::Read", proxyClient, model, callbackContext)
        .translateToServiceRequest(Translator::translateToReadRequest)
        .makeServiceCall((getThemeRequest, proxyInvocation) -> (GetThemeResponse) ClientWrapper.execute(
            proxy,
            getThemeRequest,
            proxyInvocation.client()::getTheme,
            ResourceModel.TYPE_NAME,
            model.getId(),
            logger
        ))
        .done(getThemeResponse -> {
          ResourceModel modelRet = Translator.translateFromReadResponse(getThemeResponse);
          logger.log("INFO: returning theme Id: " + modelRet.getId());
          return ProgressEvent.defaultSuccessHandler(modelRet);
        });
  }
}
