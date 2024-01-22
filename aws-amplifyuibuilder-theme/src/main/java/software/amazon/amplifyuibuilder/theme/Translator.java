package software.amazon.amplifyuibuilder.theme;

import software.amazon.awssdk.services.amplifyuibuilder.model.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static software.amazon.amplifyuibuilder.common.Transformer.transformList;
import static software.amazon.amplifyuibuilder.common.Transformer.transformObj;

/**
 * This class is a centralized placeholder for
 * - api request construction
 * - object translation to/from aws sdk
 * - resource model construction for read/list handlers
 */

public class Translator {

  /**
   * Request to create a resource
   *
   * @param model resource model
   * @return awsRequest the aws service request to create a resource
   */
  static CreateThemeRequest translateToCreateRequest(final ResourceModel model,
      final Map<String, String> desiredResourceTags,
      final String requestToken) {
    Map<String, String> tagsToCreate = getTagsToCreate(model, desiredResourceTags);

    return CreateThemeRequest.builder()
        .appId(model.getAppId())
        .environmentName(model.getEnvironmentName())
        .themeToCreate(CreateThemeData.builder()
            .name(model.getName())
            .values(transformList(model.getValues(), Translator::translateThemeValuesFromCFNToSDK))
            .overrides(transformList(model.getOverrides(), Translator::translateThemeValuesFromCFNToSDK))
            .tags(tagsToCreate.isEmpty() ? null : tagsToCreate)
            .build())
        .build();
  }

  private static Map<String, String> getTagsToCreate(final ResourceModel model,
      final Map<String, String> desiredResourceTags) {
    Map<String, String> tagsToCreate = new HashMap<>();

    if (model.getTags() != null && !model.getTags().isEmpty()) {
      tagsToCreate.putAll(model.getTags());
    }
    return tagsToCreate;
  }

  /**
   * Request to read a resource
   *
   * @param model resource model
   * @return awsRequest the aws service request to describe a resource
   */
  static GetThemeRequest translateToReadRequest(final ResourceModel model) {
    return GetThemeRequest
        .builder()
        .id(model.getId())
        .appId(model.getAppId())
        .environmentName(model.getEnvironmentName())
        .build();
  }

  /**
   * Translates resource object from sdk into a resource model
   *
   * @param response the aws service describe resource response
   * @return model resource model
   */
  static ResourceModel translateFromReadResponse(final GetThemeResponse response) {
    Theme theme = response.theme();
    return ResourceModel.builder()
        .id(theme.id())
        .environmentName(theme.environmentName())
        .appId(theme.appId())
        .name(theme.name())
        .createdAt(theme.createdAt().toString())
        .modifiedAt(theme.modifiedAt().toString())
        .overrides(transformList(theme.overrides(), Translator::translateThemeValuesFromSDKToCFN))
        .values(transformList(theme.values(), Translator::translateThemeValuesFromSDKToCFN))
        .tags(theme.tags())
        .build();
  }

  /**
   * Request to delete a resource
   *
   * @param model resource model
   * @return awsRequest the aws service request to delete a resource
   */
  static DeleteThemeRequest translateToDeleteRequest(final ResourceModel model) {
    return DeleteThemeRequest.builder()
        .appId(model.getAppId())
        .environmentName(model.getEnvironmentName())
        .id(model.getId())
        .build();
  }

  /**
   * Request to update properties of a previously created resource
   *
   * @param model resource model
   * @return awsRequest the aws service request to modify a resource
   */
  static UpdateThemeRequest translateToUpdateRequest(final ResourceModel model) {
    return UpdateThemeRequest.builder()
        .id(model.getId())
        .appId(model.getAppId())
        .environmentName(model.getEnvironmentName())
        .updatedTheme(UpdateThemeData.builder()
            // no tags on update. tags are for create only
            .values(transformList(model.getValues(), Translator::translateThemeValuesFromCFNToSDK))
            .overrides(transformList(model.getOverrides(), Translator::translateThemeValuesFromCFNToSDK))
            .id(model.getId())
            .name(model.getName())
            .build())
        .build();
  }

  /**
   * Request to list resources
   *
   * @param nextToken token passed to the aws service list resources request
   * @return awsRequest the aws service request to list resources within aws
   *         account
   */
  static ListThemesRequest translateToListRequest(final String nextToken, final ResourceModel model) {
    return ListThemesRequest.builder()
        .nextToken(nextToken)
        .appId(model.getAppId())
        .environmentName(model.getEnvironmentName())
        .maxResults(100)
        .build();
  }

  /**
   * Translates resource objects from sdk into a resource model (primary
   * identifier only)
   *
   * @param response the aws service describe resource response
   * @return list of resource models
   */
  static List<ResourceModel> translateFromListRequest(final ListThemesResponse response) {
    return streamOfOrEmpty(response.entities())
        .map(resource -> ResourceModel.builder()
            .id(resource.id())
            .appId(resource.appId())
            .environmentName(resource.environmentName())
            .build())
        .collect(Collectors.toList());
  }

  private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
    return Optional.ofNullable(collection)
        .map(Collection::stream)
        .orElseGet(Stream::empty);
  }

  private static ThemeValue translateThemeValueFromSDKToCFN(
      software.amazon.awssdk.services.amplifyuibuilder.model.ThemeValue value) {
    return ThemeValue.builder()
        .value(value.value())
        .children(transformList(value.children(), Translator::translateThemeValuesFromSDKToCFN))
        .build();
  }

  private static ThemeValues translateThemeValuesFromSDKToCFN(
      software.amazon.awssdk.services.amplifyuibuilder.model.ThemeValues themeValues) {
    return ThemeValues
        .builder()
        .key(themeValues.key())
        .value(transformObj(themeValues.value(), Translator::translateThemeValueFromSDKToCFN))
        .build();
  }

  public static software.amazon.awssdk.services.amplifyuibuilder.model.ThemeValues translateThemeValuesFromCFNToSDK(
      ThemeValues themeValues) {
    return software.amazon.awssdk.services.amplifyuibuilder.model.ThemeValues
        .builder()
        .key(themeValues.getKey())
        .value(transformObj(themeValues.getValue(), Translator::translateThemeValueFromCFNToSDK))
        .build();
  }

  private static software.amazon.awssdk.services.amplifyuibuilder.model.ThemeValue translateThemeValueFromCFNToSDK(
      ThemeValue value) {
    return software.amazon.awssdk.services.amplifyuibuilder.model.ThemeValue
        .builder()
        .value(value.getValue())
        .children(transformList(value.getChildren(), Translator::translateThemeValuesFromCFNToSDK))
        .build();
  }
}
