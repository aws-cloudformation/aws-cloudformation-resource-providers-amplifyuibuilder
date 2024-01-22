package software.amazon.amplifyuibuilder.component;

import software.amazon.awssdk.services.amplifyuibuilder.model.ActionParameters;
import software.amazon.awssdk.services.amplifyuibuilder.model.ComponentBindingPropertiesValue;
import software.amazon.awssdk.services.amplifyuibuilder.model.ComponentBindingPropertiesValueProperties;
import software.amazon.awssdk.services.amplifyuibuilder.model.ComponentChild;
import software.amazon.awssdk.services.amplifyuibuilder.model.ComponentConditionProperty;
import software.amazon.awssdk.services.amplifyuibuilder.model.ComponentDataConfiguration;
import software.amazon.awssdk.services.amplifyuibuilder.model.ComponentEvent;
import software.amazon.awssdk.services.amplifyuibuilder.model.ComponentProperty;
import software.amazon.awssdk.services.amplifyuibuilder.model.ComponentPropertyBindingProperties;
import software.amazon.awssdk.services.amplifyuibuilder.model.ComponentVariant;
import software.amazon.awssdk.services.amplifyuibuilder.model.FormBindingElement;
import software.amazon.awssdk.services.amplifyuibuilder.model.MutationActionSetStateParameter;
import software.amazon.awssdk.services.amplifyuibuilder.model.Predicate;
import software.amazon.awssdk.services.amplifyuibuilder.model.SortProperty;
import software.amazon.awssdk.services.amplifyuibuilder.model.*;

import java.text.DateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static software.amazon.amplifyuibuilder.common.Transformer.*;

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

  // It's going to translate from:
  // software.amazon.amplifyuibuilder.componenttranslateChildFromSDKToCFN
  // to
  // software.amazon.awssdk.services.amplifyuibuilder.model
  static CreateComponentRequest translateToCreateRequest(
      final ResourceModel model,
      final Map<String, String> desiredResourceTags,
      final String requestToken) {
    Map<String, String> tagsToCreate = getTagsToCreate(model, desiredResourceTags);

    CreateComponentRequest.Builder createComponentRequest = CreateComponentRequest
        .builder()
        .appId(model.getAppId())
        .environmentName(model.getEnvironmentName())
        .clientToken(requestToken);

    final CreateComponentData.Builder createComponent = CreateComponentData.builder();
    createComponent.name(model.getName());
    createComponent.componentType(model.getComponentType());
    createComponent.bindingProperties(
        transformMap(model.getBindingProperties(), Translator::translateBindingPropertyFromCFNToSDK));
    createComponent.children(transformList(model.getChildren(), Translator::translateChildComponentFromCFNToSDK));
    createComponent.overrides(model.getOverrides());
    createComponent.properties(transformMap(model.getProperties(), Translator::translateComponentPropertyFromCFNToSDK));
    createComponent.variants(transformList(model.getVariants(), Translator::translateVariantFromCFNToSDK));
    createComponent.collectionProperties(
        transformMap(model.getCollectionProperties(), Translator::translateCollectionPropertyFromCFNToSDK));
    createComponent.events(transformMap(model.getEvents(), Translator::translateEventFromCFNToSDK));
    createComponent.schemaVersion(model.getSchemaVersion());
    createComponent.sourceId(model.getSourceId());
    createComponent.tags(tagsToCreate.isEmpty() ? null : tagsToCreate);

    createComponentRequest.componentToCreate(createComponent.build());
    return createComponentRequest.build();
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
  static GetComponentRequest translateToReadRequest(final ResourceModel model) {
    return GetComponentRequest
        .builder()
        .appId(model.getAppId())
        .environmentName(model.getEnvironmentName())
        .id(model.getId())
        .build();
  }

  /**
   * Translates resource object from sdk into a resource model
   *
   * @param getComponentResponse the aws service describe resource response
   * @return model resource model
   */
  static ResourceModel translateFromReadResponse(
      final GetComponentResponse getComponentResponse) {
    software.amazon.awssdk.services.amplifyuibuilder.model.Component componentResponse = getComponentResponse
        .component();

    ResourceModel.ResourceModelBuilder model = ResourceModel.builder()
        .appId(componentResponse.appId())
        .id(componentResponse.id())
        .environmentName(componentResponse.environmentName())
        .componentType(componentResponse.componentType())
        .name(componentResponse.name())
        .bindingProperties(transformMap(componentResponse.bindingProperties(),
            Translator::translateBindingPropertiesValueFromSDKToCFN))
        .overrides(componentResponse.overrides())
        .tags(componentResponse.tags())
        .sourceId(componentResponse.sourceId())
        .createdAt(componentResponse.createdAt().toString())
        .modifiedAt(componentResponse.modifiedAt().toString())
        .properties(transformMap(componentResponse.properties(), Translator::translateComponentPropertyFromSDKToCFN))
        .children(transformList(componentResponse.children(), Translator::translateChildFromSDKToCFN))
        .variants(transformList(componentResponse.variants(), Translator::translateVariantFromSDKToCFN))
        .collectionProperties(
            transformMap(componentResponse.collectionProperties(), Translator::translateCollectionPropertyFromSDKToCFN))
        .events(transformMap(componentResponse.events(), Translator::translateEventFromSDKToCFN))
        .schemaVersion(componentResponse.schemaVersion());

    if (componentResponse.sourceId() == null) {
      model.sourceId("");
    }

    return model.build();
  }

  /**
   * Request to delete a resource
   *
   * @param model resource model
   * @return awsRequest the aws service request to delete a resource
   */
  static DeleteComponentRequest translateToDeleteRequest(
      final ResourceModel model) {
    return DeleteComponentRequest
        .builder()
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
  static UpdateComponentRequest translateToUpdateRequest(
      final ResourceModel model,
      final Map<String, String> desiredResourceTags,
      final String requestToken) {
    UpdateComponentRequest.Builder updateComponentRequest = UpdateComponentRequest
        .builder()
        .appId(model.getAppId())
        .environmentName(model.getEnvironmentName())
        .id(model.getId())
        .clientToken(requestToken);

    UpdateComponentData.Builder updateComponentDataBuilder = UpdateComponentData.builder();
    updateComponentDataBuilder
        .id(model.getId())
        .componentType(model.getComponentType())
        .name(model.getName())
        .bindingProperties(transformMap(model.getBindingProperties(), Translator::translateBindingPropertyFromCFNToSDK))
        .children(transformList(model.getChildren(), Translator::translateChildComponentFromCFNToSDK))
        .overrides(model.getOverrides())
        .properties(transformMap(model.getProperties(), Translator::translateComponentPropertyFromCFNToSDK))
        .variants(transformList(model.getVariants(), Translator::translateVariantFromCFNToSDK))
        .collectionProperties(
            transformMap(model.getCollectionProperties(), Translator::translateCollectionPropertyFromCFNToSDK))
        .events(transformMap(model.getEvents(), Translator::translateEventFromCFNToSDK))
        .sourceId(model.getSourceId())
        .schemaVersion(model.getSchemaVersion());

    return updateComponentRequest.updatedComponent(updateComponentDataBuilder.build()).build();
  }

  /**
   * Request to list resources
   *
   * @param nextToken token passed to the aws service list resources request
   * @return awsRequest the aws service request to list resources within aws
   *         account
   */
  static ListComponentsRequest translateToListRequest(final String nextToken, final ResourceModel model) {
    return ListComponentsRequest.builder()
        .nextToken(nextToken)
        .appId(model.getAppId())
        .environmentName(model.getEnvironmentName())
        .build();
  }

  /**
   * Translates resource objects from sdk into a resource model (primary
   * identifier only)
   *
   * @param listComponentsResponse the aws service describe resource response
   * @return list of resource models
   */
  static List<ResourceModel> translateFromListRequest(
      final ListComponentsResponse listComponentsResponse) {
    return streamOfOrEmpty(listComponentsResponse.entities())
        .map(resource -> ResourceModel.builder()
            .appId(resource.appId())
            .environmentName(resource.environmentName())
            .id(resource.id())
            .name(resource.name())
            .componentType(resource.componentType())
            .build())
        .collect(Collectors.toList());
  }

  public static ComponentBindingPropertiesValue translateBindingPropertyFromCFNToSDK(
      software.amazon.amplifyuibuilder.component.ComponentBindingPropertiesValue v) {
    ComponentBindingPropertiesValue.Builder bindingPropertiesValue = ComponentBindingPropertiesValue.builder();
    // ComponentBindingPropertiesValue is flattened to include properties of many
    // types.
    // See StudioComponent.bindingProperties type in
    // https://code.amazon.com/packages/AmplifyStudioCommon/blobs/mainline/--/src/types/index.ts
    bindingPropertiesValue.type(v.getType());
    bindingPropertiesValue.defaultValue(v.getDefaultValue());
    software.amazon.amplifyuibuilder.component.ComponentBindingPropertiesValueProperties valueProperties = v
        .getBindingProperties();
    if (valueProperties != null) {
      ComponentBindingPropertiesValueProperties.Builder bindingPropertiesValueProperties = ComponentBindingPropertiesValueProperties
          .builder();
      bindingPropertiesValueProperties.bucket(valueProperties.getBucket());
      bindingPropertiesValueProperties.field(valueProperties.getField());
      bindingPropertiesValueProperties.key(valueProperties.getKey());
      bindingPropertiesValueProperties.model(valueProperties.getModel());
      bindingPropertiesValueProperties
          .predicates(transformList(valueProperties.getPredicates(), Translator::translatePredicateFromCFNToSDK));
      bindingPropertiesValueProperties.userAttribute(valueProperties.getUserAttribute());
      bindingPropertiesValue.bindingProperties(bindingPropertiesValueProperties.build());
    }
    return bindingPropertiesValue.build();
  }

  public static ComponentChild translateChildComponentFromCFNToSDK(
      software.amazon.amplifyuibuilder.component.ComponentChild c) {
    return ComponentChild.builder()
        .componentType(c.getComponentType())
        .name(c.getName())
        .properties(transformMap(c.getProperties(), Translator::translateComponentPropertyFromCFNToSDK))
        .children(transformList(c.getChildren(), Translator::translateChildComponentFromCFNToSDK))
        .events(transformMap(c.getEvents(), Translator::translateEventFromCFNToSDK))
        .sourceId(c.getSourceId())
        .build();
  }

  public static ComponentDataConfiguration translateCollectionPropertyFromCFNToSDK(
      software.amazon.amplifyuibuilder.component.ComponentDataConfiguration property) {
    return ComponentDataConfiguration
        .builder()
        .identifiers(property.getIdentifiers())
        .predicate(transformObj(property.getPredicate(), Translator::translatePredicateFromCFNToSDK))
        .sort(transformList(property.getSort(), Translator::translateSortFromCFNToSDK))
        .model(property.getModel())
        .build();
  }

  private static SortProperty translateSortFromCFNToSDK(software.amazon.amplifyuibuilder.component.SortProperty sort) {
    return SortProperty
        .builder()
        .direction(sort.getDirection())
        .field(sort.getField())
        .build();
  }

  public static ComponentVariant translateVariantFromCFNToSDK(
      software.amazon.amplifyuibuilder.component.ComponentVariant variant) {
    return ComponentVariant.builder()
        .overrides(variant.getOverrides())
        .variantValues((variant.getVariantValues()))
        .build();
  }

  private static ComponentConditionProperty translateConditionFromCFNToSDK(
      software.amazon.amplifyuibuilder.component.ComponentConditionProperty condition) {
    return ComponentConditionProperty
        .builder()
        .field(condition.getField())
        .operand(condition.getOperand())
        .operator(condition.getOperator())
        .property(condition.getProperty())
        .elseValue(translateComponentPropertyFromCFNToSDK(condition.getElse_()))
        .then(translateComponentPropertyFromCFNToSDK(condition.getThen()))
        .build();
  }

  public static ComponentProperty translateComponentPropertyFromCFNToSDK(
      software.amazon.amplifyuibuilder.component.ComponentProperty property) {
    // ComponentProperty is flattened to include properties of many Component
    // Property types.

    return ComponentProperty
        .builder()
        .type(property.getType())
        .importedValue(property.getImportedValue())
        .defaultValue(property.getDefaultValue())
        .event(property.getEvent())
        .model(property.getModel())
        .value(property.getValue())
        .property(property.getProperty())
        .componentName(property.getComponentName())
        .collectionBindingProperties(transformObj(property.getCollectionBindingProperties(),
            Translator::translateComponentPropertyBindingPropertiesFromCFNToSDK))
        .bindingProperties(transformObj(property.getBindingProperties(),
            Translator::translateComponentPropertyBindingPropertiesFromCFNToSDK))
        .condition(transformObj(property.getCondition(), Translator::translateConditionFromCFNToSDK))
        .concat(transformList(property.getConcat(), Translator::translateComponentPropertyFromCFNToSDK))
        .bindings(transformMap(property.getBindings(), Translator::translateFormBindingElementFromCFNToSDK))
        .build();
  }

  private static ComponentPropertyBindingProperties translateComponentPropertyBindingPropertiesFromCFNToSDK(
      software.amazon.amplifyuibuilder.component.ComponentPropertyBindingProperties bindingProperties) {
    return ComponentPropertyBindingProperties
        .builder()
        .field(bindingProperties.getField())
        .property(bindingProperties.getProperty())
        .build();
  }

  private static FormBindingElement translateFormBindingElementFromCFNToSDK(
      software.amazon.amplifyuibuilder.component.FormBindingElement binding) {
    return FormBindingElement.builder()
        .element(binding.getElement())
        .property(binding.getProperty())
        .build();
  }

  public static ComponentEvent translateEventFromCFNToSDK(
      software.amazon.amplifyuibuilder.component.ComponentEvent event) {
    return ComponentEvent.builder()
        .action(event.getAction())
        .parameters(transformObj(event.getParameters(), Translator::translateEventParametersFromCFNToSDK))
        .build();
  }

  private static ActionParameters translateEventParametersFromCFNToSDK(
      software.amazon.amplifyuibuilder.component.ActionParameters parameters) {
    return ActionParameters.builder()
        .type(transformObj(parameters.getType(), Translator::translateComponentPropertyFromCFNToSDK))
        .url(transformObj(parameters.getUrl(), Translator::translateComponentPropertyFromCFNToSDK))
        .anchor(transformObj(parameters.getAnchor(), Translator::translateComponentPropertyFromCFNToSDK))
        .target(transformObj(parameters.getTarget(), Translator::translateComponentPropertyFromCFNToSDK))
        .global(transformObj(parameters.getGlobal(), Translator::translateComponentPropertyFromCFNToSDK))
        .model(parameters.getModel())
        .id(transformObj(parameters.getId(), Translator::translateComponentPropertyFromCFNToSDK))
        .fields(transformMap(parameters.getFields(), Translator::translateComponentPropertyFromCFNToSDK))
        .state(transformObj(parameters.getState(), Translator::translateMutationActionSetStateParameterFromCFNToSDK))
        .build();
  }

  private static MutationActionSetStateParameter translateMutationActionSetStateParameterFromCFNToSDK(
      software.amazon.amplifyuibuilder.component.MutationActionSetStateParameter state) {
    return MutationActionSetStateParameter.builder()
        .componentName(state.getComponentName())
        .property(state.getProperty())
        .set(transformObj(state.getSet(), Translator::translateComponentPropertyFromCFNToSDK))
        .build();
  }

  private static Predicate translatePredicateFromCFNToSDK(
      software.amazon.amplifyuibuilder.component.Predicate predicate) {
    return Predicate
        .builder()
        .and(transformList(predicate.getAnd(), Translator::translatePredicateFromCFNToSDK))
        .or(transformList(predicate.getOr(), Translator::translatePredicateFromCFNToSDK))
        .field(predicate.getField())
        .operand(predicate.getOperand())
        .operator(predicate.getOperator())
        .build();
  }

  private static software.amazon.amplifyuibuilder.component.ComponentDataConfiguration translateCollectionPropertyFromSDKToCFN(
      ComponentDataConfiguration v) {
    return software.amazon.amplifyuibuilder.component.ComponentDataConfiguration.builder()
        .model(v.model())
        .identifiers(v.identifiers())
        .predicate(transformObj(v.predicate(), Translator::translatePredicateFromSDKToCFN))
        .sort(transformList(v.sort(), Translator::translateSortFromSDKToCFN))
        .build();
  }

  private static software.amazon.amplifyuibuilder.component.Predicate translatePredicateFromSDKToCFN(
      Predicate predicate) {
    return software.amazon.amplifyuibuilder.component.Predicate
        .builder()
        .operator(predicate.operator())
        .field(predicate.field())
        .operand(predicate.operand())
        .or(transformList(predicate.or(), Translator::translatePredicateFromSDKToCFN))
        .and(transformList(predicate.and(), Translator::translatePredicateFromSDKToCFN))
        .build();
  }

  private static software.amazon.amplifyuibuilder.component.SortProperty translateSortFromSDKToCFN(SortProperty sort) {
    return software.amazon.amplifyuibuilder.component.SortProperty
        .builder()
        .field(sort.field())
        .direction(sort.directionAsString())
        .build();
  }

  private static software.amazon.amplifyuibuilder.component.ComponentVariant translateVariantFromSDKToCFN(
      ComponentVariant variant) {
    return software.amazon.amplifyuibuilder.component.ComponentVariant.builder()
        .variantValues(variant.variantValues())
        .overrides(variant.overrides())
        .build();
  }

  private static software.amazon.amplifyuibuilder.component.ComponentChild translateChildFromSDKToCFN(
      ComponentChild child) {
    software.amazon.amplifyuibuilder.component.ComponentChild.ComponentChildBuilder builder = software.amazon.amplifyuibuilder.component.ComponentChild
        .builder()
        .componentType(child.componentType())
        .name(child.name())
        .sourceId(child.sourceId())
        .properties(transformMap(child.properties(), Translator::translateComponentPropertyFromSDKToCFN))
        .children(transformList(child.children(), Translator::translateChildFromSDKToCFN))
        .events(transformMap(child.events(), Translator::translateEventFromSDKToCFN));

    return builder.build();
  }

  private static software.amazon.amplifyuibuilder.component.ComponentEvent translateEventFromSDKToCFN(
      ComponentEvent v) {
    return software.amazon.amplifyuibuilder.component.ComponentEvent.builder()
        .action(v.action())
        .parameters(transformObj(v.parameters(), Translator::translateEventParametersFromSDKToCFN))
        .build();
  }

  private static software.amazon.amplifyuibuilder.component.ActionParameters translateEventParametersFromSDKToCFN(
      ActionParameters parameters) {
    return software.amazon.amplifyuibuilder.component.ActionParameters.builder()
        .type(transformObj(parameters.type(), Translator::translateComponentPropertyFromSDKToCFN))
        .url(transformObj(parameters.url(), Translator::translateComponentPropertyFromSDKToCFN))
        .anchor(transformObj(parameters.anchor(), Translator::translateComponentPropertyFromSDKToCFN))
        .target(transformObj(parameters.target(), Translator::translateComponentPropertyFromSDKToCFN))
        .global(transformObj(parameters.global(), Translator::translateComponentPropertyFromSDKToCFN))
        .model(parameters.model())
        .id(transformObj(parameters.id(), Translator::translateComponentPropertyFromSDKToCFN))
        .fields(transformMap(parameters.fields(), Translator::translateComponentPropertyFromSDKToCFN))
        .state(transformObj(parameters.state(), Translator::translateMutationActionSetStateParameterFromSDKToCFN))
        .build();
  }

  private static software.amazon.amplifyuibuilder.component.MutationActionSetStateParameter translateMutationActionSetStateParameterFromSDKToCFN(
      MutationActionSetStateParameter state) {
    return software.amazon.amplifyuibuilder.component.MutationActionSetStateParameter.builder()
        .componentName(state.componentName())
        .property(state.property())
        .set(transformObj(state.set(), Translator::translateComponentPropertyFromSDKToCFN))
        .build();
  }

  private static software.amazon.amplifyuibuilder.component.ComponentConditionProperty translateConditionFromSDKToCFN(
      ComponentConditionProperty condition) {
    return software.amazon.amplifyuibuilder.component.ComponentConditionProperty
        .builder()
        .operator(condition.operator())
        .field(condition.field())
        .operand(condition.operand())
        .property(condition.property())
        .else_(transformObj(condition.elseValue(), Translator::translateComponentPropertyFromSDKToCFN))
        .then(transformObj(condition.then(), Translator::translateComponentPropertyFromSDKToCFN))
        .build();
  }

  private static software.amazon.amplifyuibuilder.component.ComponentProperty translateComponentPropertyFromSDKToCFN(
      ComponentProperty property) {
    // ComponentProperty is flattened to include properties of many types.
    return software.amazon.amplifyuibuilder.component.ComponentProperty
        .builder()
        .type(property.type())
        .importedValue(property.importedValue())
        .defaultValue(property.defaultValue())
        .event(property.event())
        .model(property.model())
        .value(property.value())
        .property(property.property())
        .componentName(property.componentName())
        .collectionBindingProperties(transformObj(property.collectionBindingProperties(),
            Translator::translateComponentPropertyBindingPropertiesFromSDKToCFN))
        .bindingProperties(transformObj(property.bindingProperties(),
            Translator::translateComponentPropertyBindingPropertiesFromSDKToCFN))
        .condition(transformObj(property.condition(), Translator::translateConditionFromSDKToCFN))
        .bindings(transformMap(property.bindings(), Translator::translateFormBindingElementFromSDKToCFN))
        .concat(transformList(property.concat(), Translator::translateComponentPropertyFromSDKToCFN))
        .build();
  }

  private static software.amazon.amplifyuibuilder.component.ComponentPropertyBindingProperties translateComponentPropertyBindingPropertiesFromSDKToCFN(
      ComponentPropertyBindingProperties bindingProperties) {
    return software.amazon.amplifyuibuilder.component.ComponentPropertyBindingProperties.builder()
        .field(bindingProperties.field())
        .property(bindingProperties.property())
        .build();
  }

  private static software.amazon.amplifyuibuilder.component.FormBindingElement translateFormBindingElementFromSDKToCFN(
      FormBindingElement v) {
    return software.amazon.amplifyuibuilder.component.FormBindingElement.builder()
        .element(v.element())
        .property(v.property())
        .build();
  }

  private static software.amazon.amplifyuibuilder.component.ComponentBindingPropertiesValue translateBindingPropertiesValueFromSDKToCFN(
      ComponentBindingPropertiesValue v) {
    software.amazon.amplifyuibuilder.component.ComponentBindingPropertiesValue.ComponentBindingPropertiesValueBuilder bindingPropertiesValue = software.amazon.amplifyuibuilder.component.ComponentBindingPropertiesValue
        .builder();
    bindingPropertiesValue.type(v.type());
    bindingPropertiesValue.defaultValue(v.defaultValue());

    if (v.bindingProperties() != null) {
      software.amazon.amplifyuibuilder.component.ComponentBindingPropertiesValueProperties.ComponentBindingPropertiesValuePropertiesBuilder bindingPropertiesValueProperties = software.amazon.amplifyuibuilder.component.ComponentBindingPropertiesValueProperties
          .builder();
      bindingPropertiesValueProperties.field(v.bindingProperties().field());
      bindingPropertiesValueProperties.bucket(v.bindingProperties().bucket());
      bindingPropertiesValueProperties.userAttribute(v.bindingProperties().userAttribute());
      bindingPropertiesValueProperties.model(v.bindingProperties().model());
      bindingPropertiesValueProperties.key(v.bindingProperties().key());
      bindingPropertiesValueProperties.defaultValue(v.bindingProperties().defaultValue());
      bindingPropertiesValueProperties
          .predicates(transformList(v.bindingProperties().predicates(), Translator::translatePredicateFromSDKToCFN));
      bindingPropertiesValue.bindingProperties(bindingPropertiesValueProperties.build());
    }
    return bindingPropertiesValue.build();
  }

  private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
    return Optional
        .ofNullable(collection)
        .map(Collection::stream)
        .orElseGet(Stream::empty);
  }
}
