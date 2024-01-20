package software.amazon.amplifyuibuilder.form;

import software.amazon.amplifyuibuilder.common.ClientWrapper;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.ListFormsRequest;
import software.amazon.awssdk.services.amplifyuibuilder.model.ListFormsResponse;
import software.amazon.cloudformation.proxy.*;

public class ListHandler extends BaseHandlerStd {

  public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
      final AmazonWebServicesClientProxy proxy,
      final ResourceHandlerRequest<ResourceModel> request,
      final CallbackContext callbackContext,
      final ProxyClient<AmplifyUiBuilderClient> proxyClient,
      final Logger logger) {

    final ListFormsRequest listRequest = Translator.translateToListRequest(request.getNextToken(), request.getDesiredResourceState());

    final ListFormsResponse response = (ListFormsResponse) ClientWrapper.execute(
        proxy,
        listRequest,
        proxyClient.client()::listForms,
        ResourceModel.TYPE_NAME,
        logger
    );

    return ProgressEvent.<ResourceModel, CallbackContext>builder()
        .resourceModels(Translator.translateFromListResponse(response))
        .nextToken(response.nextToken())
        .status(OperationStatus.SUCCESS)
        .build();
  }
}
