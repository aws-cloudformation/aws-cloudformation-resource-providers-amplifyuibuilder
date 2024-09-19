package software.amazon.amplifyuibuilder.common;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.TagResourceRequest;
import software.amazon.awssdk.services.amplifyuibuilder.model.UntagResourceRequest;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.Logger;

public class TaggingHelpers {
  protected static final String ACCESS_DENIED_ERROR_CODE = "AccessDeniedException";
  protected static final String ACESSS_DENIED_MESSAGE_MATCHER = "is not authorized to perform";
  protected static final String[] TAGGING_PERMISSIONS = { "amplifyuibuilder:TagResource",
      "amplifyuibuilder:UntagResource" };

  public static final String SAMPLE_TAGGING_ACCESS_DENIED_MESSAGE = "An error occurred (AccessDeniedException) when calling the CreateCustomActionType operation: User: arn:aws:sts::0123456789:assumed-role/admin/someUser is not authorized to perform: amplifyuibuilder:TagResource on resource: arn:aws:amplifyuibuilder:us-west-2:0123456789:actiontype:Custom/Source/TestingCustomSource/2 with an explicit deny in an identity-based policy";

  static String getErrorCode(Exception e) {
    if (e instanceof AwsServiceException && ((AwsServiceException) e).awsErrorDetails() != null) {
      return ((AwsServiceException) e).awsErrorDetails().errorCode();
    }
    return e.getMessage();
  }

  public static Boolean isTagBasedAccessDenied(final Exception e) {
    String exceptionMessage = e.getMessage();
    boolean isAccessDeniedException = ACCESS_DENIED_ERROR_CODE.equals(getErrorCode(e))
        && exceptionMessage != null
        && exceptionMessage.contains(ACESSS_DENIED_MESSAGE_MATCHER);

    if (isAccessDeniedException) {
      for (String permission : TAGGING_PERMISSIONS) {
        if (e.getMessage().contains(permission)) {
          return true;
        }
      }
    }
    return false;
  }

  public static String generateArn(final String region, final String accountId, final String appId, final String environmentName, final String resource, final String id) {
    return String.format("arn:aws:amplifyuibuilder:%s:%s:app/%s/environment/%s/%s/%s",region, accountId, appId, environmentName, resource, id);
  }

  public static void updateTags(final AmazonWebServicesClientProxy proxy,
      final ProxyClient<AmplifyUiBuilderClient> proxyClient,
      final String appId,
      final String resourceArn,
      final String typeName,
      final Map<String, String> existingTags,
      final Map<String, String> desiredTags,
      final Logger logger) {

    Map<String, String> safeExistingTags = existingTags == null ? Map.of() : existingTags;
    Map<String, String> safeDesiredTags = desiredTags == null ? Map.of() : desiredTags;

    Map<String, String> tagsToAdd = safeDesiredTags.entrySet().stream()
      .filter(entry -> !entry.getValue().equals(safeExistingTags.get(entry.getKey())))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    Map<String, String> tagsToRemove = safeExistingTags.entrySet().stream()
      .filter(entry -> !safeDesiredTags.containsKey(entry.getKey()))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    if (tagsToRemove.size() > 0) {
      Collection<String> tagKeys = tagsToRemove.keySet();
      final UntagResourceRequest untagResourceRequest = UntagResourceRequest.builder().resourceArn(resourceArn)
          .tagKeys(tagKeys).build();
      ClientWrapper.execute(proxy, untagResourceRequest, proxyClient.client()::untagResource, typeName,
          appId, logger);
    }

    if (tagsToAdd.size() > 0) {
      final TagResourceRequest tagResourceRequest = TagResourceRequest.builder()
          .resourceArn(resourceArn).tags(tagsToAdd).build();
      ClientWrapper.execute(proxy, tagResourceRequest, proxyClient.client()::tagResource, typeName,
          appId, logger);
    }
    logger.log("INFO: Successfully Updated Tags");
  }
}
