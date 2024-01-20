package software.amazon.amplifyuibuilder.component;

import software.amazon.amplifyuibuilder.common.ClientWrapper;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.ListComponentsRequest;
import software.amazon.awssdk.services.amplifyuibuilder.model.ListComponentsResponse;
import software.amazon.cloudformation.proxy.*;

public class ListHandler extends BaseHandlerStd {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<AmplifyUiBuilderClient> proxyClient,
            final Logger logger) {

        // Construct a body of a request
        final ListComponentsRequest listRequest = Translator.translateToListRequest(
                request.getNextToken(),
                request.getDesiredResourceState());
        logger.log("translateToListRequest succeeded");

        // Make getComponents api call
        ListComponentsResponse listComponentsResponse = (ListComponentsResponse) ClientWrapper.execute(
                proxy,
                listRequest,
                proxyClient.client()::listComponents,
                ResourceModel.TYPE_NAME,
                logger);

        logger.log("getComponents request succeeded for appId: " + listRequest.appId() + " envName: "
                + listRequest.environmentName());
        // Get a token for the next page
        String nextToken = listComponentsResponse.nextToken();

        return ProgressEvent
                .<ResourceModel, CallbackContext>builder()
                .resourceModels(Translator.translateFromListRequest(listComponentsResponse))
                .nextToken(nextToken)
                .status(OperationStatus.SUCCESS)
                .build();
    }
}
