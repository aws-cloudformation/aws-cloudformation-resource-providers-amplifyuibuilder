package software.amazon.amplifyuibuilder.theme;

import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.ListThemesRequest;
import software.amazon.awssdk.services.amplifyuibuilder.model.ListThemesResponse;
import software.amazon.cloudformation.proxy.*;

public class ListHandler extends BaseHandlerStd {

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<AmplifyUiBuilderClient> proxyClient,
        final Logger logger) {
        final ListThemesRequest listRequest = Translator.translateToListRequest(request.getNextToken(), request.getDesiredResourceState());

        final ListThemesResponse response = (ListThemesResponse) ClientWrapper.execute(
            proxy,
            listRequest,
            proxyClient.client()::listThemes,
            ResourceModel.TYPE_NAME,
            logger
        );

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModels(Translator.translateFromListRequest(response))
            .nextToken(response.nextToken())
            .status(OperationStatus.SUCCESS)
            .build();
    }
}
