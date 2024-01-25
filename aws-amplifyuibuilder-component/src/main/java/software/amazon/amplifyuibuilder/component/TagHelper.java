package software.amazon.amplifyuibuilder.component;

import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.HashMap;
import java.util.Map;

public class TagHelper {
    /**
     * getNewDesiredTags
     *
     * If stack tags and resource tags are not merged together in Configuration
     * class,
     * we will get new desired system (with `aws:cloudformation` prefix) and user
     * defined tags from
     * handlerRequest.getSystemTags() (system tags),
     * handlerRequest.getDesiredResourceTags() (stack tags),
     * handlerRequest.getDesiredResourceState().getTags() (resource tags).
     *
     * System tags are an optional feature. Merge them to your tags if you have
     * enabled them for your resource.
     * System tags can change on resource update if the resource is imported to the
     * stack.
     *
     * @param handlerRequest the resource request to process
     */
    public static Map<String, String> getNewDesiredTags(final ResourceHandlerRequest<ResourceModel> handlerRequest) {
        final Map<String, String> desiredTags = new HashMap<>();

        // CloudFormation system tags
        // AmplifyUIBuilder currently does not support system tags and tags that begin
        // with ^aws
        if (handlerRequest.getSystemTags() != null) {
            desiredTags.putAll(handlerRequest.getSystemTags());
        }

        // AmplifyUIBuilder currently does not support tags that begin with ^aws
        // get desired stack level tags from handlerRequest
        if (handlerRequest.getDesiredResourceTags() != null) {
            desiredTags.putAll(handlerRequest.getDesiredResourceTags());
        }
        // resource level tags
        if (handlerRequest.getDesiredResourceState().getTags() != null) {
            desiredTags.putAll(handlerRequest.getDesiredResourceState().getTags());
        }

        return desiredTags;
    }
}
