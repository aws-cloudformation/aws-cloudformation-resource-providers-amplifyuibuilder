package software.amazon.amplifyuibuilder.component;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import software.amazon.awssdk.services.amplifyuibuilder.model.*;
import software.amazon.awssdk.services.amplifyuibuilder.model.ComponentBindingPropertiesValue;
import software.amazon.awssdk.services.amplifyuibuilder.model.ComponentBindingPropertiesValueProperties;
import software.amazon.awssdk.services.amplifyuibuilder.model.ComponentChild;
import software.amazon.awssdk.services.amplifyuibuilder.model.ComponentConditionProperty;
import software.amazon.awssdk.services.amplifyuibuilder.model.ComponentDataConfiguration;
import software.amazon.awssdk.services.amplifyuibuilder.model.ComponentProperty;
import software.amazon.awssdk.services.amplifyuibuilder.model.ComponentPropertyBindingProperties;
import software.amazon.awssdk.services.amplifyuibuilder.model.ComponentVariant;
import software.amazon.awssdk.services.amplifyuibuilder.model.CreateComponentData;
import software.amazon.awssdk.services.amplifyuibuilder.model.FormBindingElement;
import software.amazon.awssdk.services.amplifyuibuilder.model.Predicate;
import software.amazon.awssdk.services.amplifyuibuilder.model.SortProperty;
import software.amazon.awssdk.services.amplifyuibuilder.model.UpdateComponentData;

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
  // software.amazon.amplifyuibuilder.component
  // to
  // software.amazon.awssdk.services.amplifyuibuilder.model
  static CreateComponentRequest translateToCreateRequest(
      final ResourceModel model
  ) {
    CreateComponentRequest.Builder createComponentRequest = CreateComponentRequest
        .builder()
        .appId(model.getAppId())
        .environmentName(model.getEnvironmentName());

    final CreateComponentData.Builder createComponent = CreateComponentData.builder();
    createComponent.name(model.getName());
    createComponent.componentType(model.getComponentType());
    createComponent.bindingProperties(translateBindingPropertiesFromCFNToSDK(model.getBindingProperties()));
    createComponent.children(translateChildComponentsFromCFNToSDK(model.getChildren()));
    createComponent.overrides(model.getOverrides());
    createComponent.properties(translatePropertiesFromCFNToSDK(model.getProperties()));
    createComponent.tags(model.getTags());
    createComponent.variants(translateVariantsFromCFNToSDK(model.getVariants()));
    createComponent.collectionProperties(translateCollectionPropertiesFromCFNToSDK(model.getCollectionProperties()));
    createComponentRequest.componentToCreate(createComponent.build());
    return createComponentRequest.build();
  }

  public static Map<String, ComponentDataConfiguration> translateCollectionPropertiesFromCFNToSDK(Map<String, software.amazon.amplifyuibuilder.component.ComponentDataConfiguration> collectionProperties) {
    if (collectionProperties == null) {
      return null;
    }

    Map<String, ComponentDataConfiguration> translated = new HashMap<>();
    collectionProperties.forEach((k, v) -> {
      ComponentDataConfiguration componentDataConfiguration = ComponentDataConfiguration
          .builder()
          .identifiers(v.getIdentifiers())
          .predicate(translatePredicateFromCFNToSDK(v.getPredicate()))
          .sort(translateSortFromCFNToSDK(v.getSort()))
          .model(v.getModel())
          .build();
      translated.put(k, componentDataConfiguration);
    });
    return translated;
  }

  private static List<SortProperty> translateSortFromCFNToSDK(List<software.amazon.amplifyuibuilder.component.SortProperty> sort) {
    if (sort == null) {
      return null;
    }
    List<SortProperty> translated = new ArrayList<>();
    for (software.amazon.amplifyuibuilder.component.SortProperty sortProperty : sort) {
      translated.add(
          SortProperty
              .builder()
              .direction(sortProperty.getDirection())
              .field(sortProperty.getField())
              .build()
      );
    }
    return translated;
  }

  public static List<ComponentVariant> translateVariantsFromCFNToSDK(List<software.amazon.amplifyuibuilder.component.ComponentVariant> variants) {
    if (variants == null) {
      return null;
    }
    List<ComponentVariant> translated = new ArrayList<>();
    for (software.amazon.amplifyuibuilder.component.ComponentVariant variant : variants) {
      translated.add(ComponentVariant.builder()
          .overrides(variant.getOverrides())
          .variantValues((variant.getVariantValues()))
          .build());
    }
    return translated;
  }

  public static Map<String, ComponentProperty> translatePropertiesFromCFNToSDK(Map<String, software.amazon.amplifyuibuilder.component.ComponentProperty> properties) {
    if (properties == null) {
      return null;
    }
    Map<String, ComponentProperty> translated = new HashMap<>();
    properties.forEach((k, v) -> {
      translated.put(k, translateComponentPropertyFromCFNToSDK(v));
    });
    return translated;
  }

  private static ComponentConditionProperty translateConditionFromCFNToSDK(software.amazon.amplifyuibuilder.component.ComponentConditionProperty condition) {
    if (condition == null) {
      return null;
    }
    ComponentConditionProperty.Builder builder = ComponentConditionProperty
        .builder()
        .field(condition.getField())
        .operand(condition.getOperand())
        .operator(condition.getOperator())
        .property(condition.getProperty());
    if (condition.getElse_() != null) {
      builder.elseValue(translateComponentPropertyFromCFNToSDK(condition.getElse_()));
    }
    if (condition.getThen() != null) {
      builder.then(translateComponentPropertyFromCFNToSDK(condition.getThen()));
    }
    return builder.build();
  }

  private static ComponentProperty translateComponentPropertyFromCFNToSDK(software.amazon.amplifyuibuilder.component.ComponentProperty property) {
    // ComponentProperty is flattened to include properties of many types.
    // So check each property for null because there can only be a subset of values assigned to these properties
    // See StudioComponent.properties type in https://code.amazon.com/packages/AmplifyStudioCommon/blobs/mainline/--/src/types/index.ts
    ComponentProperty.Builder builder = ComponentProperty
        .builder()
        .type(property.getType())
        .importedValue(property.getImportedValue())
        .defaultValue(property.getDefaultValue())
        .event(property.getEvent())
        .model(property.getModel())
        .value(property.getValue())
        .bindingProperties(translateComponentPropertyBindingPropertiesFromCFNToSDK(property.getBindingProperties()))
        .condition(translateConditionFromCFNToSDK(property.getCondition()))
        .concat(translateComponentPropertiesFromCFNToSDK(property.getConcat()));

    if (property.getBindings() != null) {
      builder.bindings(translateFormBindingElementsFromCFNToSDK(property.getBindings()));
    }
    return builder.build();
  }

  private static List<ComponentProperty> translateComponentPropertiesFromCFNToSDK(List<software.amazon.amplifyuibuilder.component.ComponentProperty> properties) {
    if (properties == null) {
      return null;
    }
    List<ComponentProperty> translated = new ArrayList<>();
    for (software.amazon.amplifyuibuilder.component.ComponentProperty property : properties) {
      translated.add(translateComponentPropertyFromCFNToSDK(property));
    }
    return translated;
  }

  private static ComponentPropertyBindingProperties translateComponentPropertyBindingPropertiesFromCFNToSDK(software.amazon.amplifyuibuilder.component.ComponentPropertyBindingProperties bindingProperties) {
    if (bindingProperties == null) {
      return null;
    }
    return ComponentPropertyBindingProperties
        .builder()
        .field(bindingProperties.getField())
        .property(bindingProperties.getProperty())
        .build();
  }

  private static Map<String, FormBindingElement> translateFormBindingElementsFromCFNToSDK(Map<String, software.amazon.amplifyuibuilder.component.FormBindingElement> bindings) {
    Map<String, FormBindingElement> translated = new HashMap<>();
    bindings.forEach((k, v) -> translated.put(k,
            FormBindingElement.builder()
                .element(v.getElement())
                .property(v.getProperty())
                .build()
        )
    );
    return translated;
  }

  public static List<ComponentChild> translateChildComponentsFromCFNToSDK(List<software.amazon.amplifyuibuilder.component.ComponentChild> children) {
    if (children == null) {
      return null;
    }
    List<ComponentChild> translated = new ArrayList<>();
    for (software.amazon.amplifyuibuilder.component.ComponentChild c : children) {
      translated.add(
          ComponentChild.builder()
              .componentType(c.getComponentType())
              .name(c.getName())
              .properties(translatePropertiesFromCFNToSDK(c.getProperties()))
              .children(translateChildComponentsFromCFNToSDK(c.getChildren()))
              .build()
      );
    }
    return translated;
  }

  public static Map<String, ComponentBindingPropertiesValue> translateBindingPropertiesFromCFNToSDK(Map<String, software.amazon.amplifyuibuilder.component.ComponentBindingPropertiesValue> bindingProperties) {
    if (bindingProperties == null) {
      return null;
    }
    Map<String, ComponentBindingPropertiesValue> translated = new HashMap<>();
    bindingProperties.forEach((k, v) -> {
      ComponentBindingPropertiesValue.Builder bindingPropertiesValue = ComponentBindingPropertiesValue.builder();
      // ComponentBindingPropertiesValue is flattened to include properties of many types.
      // See StudioComponent.bindingProperties type in https://code.amazon.com/packages/AmplifyStudioCommon/blobs/mainline/--/src/types/index.ts
      bindingPropertiesValue.type(v.getType());
      bindingPropertiesValue.defaultValue(v.getDefaultValue());

      software.amazon.amplifyuibuilder.component.ComponentBindingPropertiesValueProperties valueProperties = v.getBindingProperties();
      if (valueProperties != null) {
        ComponentBindingPropertiesValueProperties.Builder bindingPropertiesValueProperties = ComponentBindingPropertiesValueProperties.builder();
        bindingPropertiesValueProperties.bucket(valueProperties.getBucket());
        bindingPropertiesValueProperties.field(valueProperties.getField());
        bindingPropertiesValueProperties.key(valueProperties.getKey());
        bindingPropertiesValueProperties.model(valueProperties.getModel());
        bindingPropertiesValueProperties.predicates(translatePredicatesFromCFNToSDK(valueProperties.getPredicates()));
        bindingPropertiesValueProperties.userAttribute(valueProperties.getUserAttribute());
        bindingPropertiesValue.bindingProperties(bindingPropertiesValueProperties.build());
      }
      translated.put(k, bindingPropertiesValue.build());
    });
    return translated;
  }

  private static Predicate translatePredicateFromCFNToSDK(software.amazon.amplifyuibuilder.component.Predicate predicate) {
    if (predicate == null) {
      return null;
    }
    return Predicate
        .builder()
        .and(translatePredicatesFromCFNToSDK(predicate.getAnd()))
        .or(translatePredicatesFromCFNToSDK(predicate.getOr()))
        .field(predicate.getField())
        .operand(predicate.getOperand())
        .operator(predicate.getOperator())
        .build();
  }

  private static List<Predicate> translatePredicatesFromCFNToSDK(List<software.amazon.amplifyuibuilder.component.Predicate> predicates) {
    if (predicates == null) {
      return null;
    }
    List<Predicate> translated = new ArrayList<>();
    for (software.amazon.amplifyuibuilder.component.Predicate predicate : predicates) {
      translated.add(translatePredicateFromCFNToSDK(predicate));
    }
    return translated;
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
      final GetComponentResponse getComponentResponse
  ) {
    software.amazon.awssdk.services.amplifyuibuilder.model.Component componentResponse = getComponentResponse.component();
    return ResourceModel.builder()
        .appId(componentResponse.appId())
        .id(componentResponse.id())
        .environmentName(componentResponse.environmentName())
        .componentType(componentResponse.componentType())
        .name(componentResponse.name())
        .bindingProperties(translateBindingPropertiesFromSDKToCFN(componentResponse.bindingProperties()))
        .overrides(componentResponse.overrides())
        .tags(componentResponse.tags())
        .properties(translatePropertiesFromSDKToCFN(componentResponse.properties()))
        .children(translateChildrenFromSDKToCFN(componentResponse.children()))
        .variants(translateVariantsFromSDKToCFN(componentResponse.variants()))
        .collectionProperties(translateCollectionPropertiesFromSDKToCFN(componentResponse.collectionProperties()))
        .build();
  }

  private static Map<String, software.amazon.amplifyuibuilder.component.ComponentDataConfiguration> translateCollectionPropertiesFromSDKToCFN(Map<String, ComponentDataConfiguration> collectionProperties) {
    Map<String, software.amazon.amplifyuibuilder.component.ComponentDataConfiguration> translated = new HashMap<>();
    collectionProperties.forEach((k, v) -> {
      software.amazon.amplifyuibuilder.component.ComponentDataConfiguration.ComponentDataConfigurationBuilder componentDataConfigurationBuilder = software.amazon.amplifyuibuilder.component.ComponentDataConfiguration
          .builder()
          .model(v.model())
          .identifiers(v.identifiers())
          .predicate(translatePredicateFromSDKToCFN(v.predicate()));
      if (v.sort() != null) {
        componentDataConfigurationBuilder.sort(translateSortFromSDKToCFN(v.sort()));
      }
      translated.put(k, componentDataConfigurationBuilder.build());
    });
    return translated;
  }

  private static software.amazon.amplifyuibuilder.component.Predicate translatePredicateFromSDKToCFN(Predicate predicate) {
    if (predicate == null) {
      return null;
    }
    software.amazon.amplifyuibuilder.component.Predicate.PredicateBuilder builder = software.amazon.amplifyuibuilder.component.Predicate
        .builder()
        .operator(predicate.operator())
        .field(predicate.field())
        .operand(predicate.operand());
    if (predicate.or() != null) {
      builder.or(translatePredicatesFromSDKToCFN(predicate.or()));
    }
    if (predicate.and() != null) {
      builder.and(translatePredicatesFromSDKToCFN(predicate.and()));
    }
    return builder.build();
  }

  private static List<software.amazon.amplifyuibuilder.component.Predicate> translatePredicatesFromSDKToCFN(List<Predicate> predicates) {
    List<software.amazon.amplifyuibuilder.component.Predicate> translated = new ArrayList<>();
    for (Predicate predicate : predicates) {
      translated.add(translatePredicateFromSDKToCFN(predicate));
    }
    return translated;
  }

  private static List<software.amazon.amplifyuibuilder.component.SortProperty> translateSortFromSDKToCFN(List<SortProperty> sort) {
    List<software.amazon.amplifyuibuilder.component.SortProperty> translated = new ArrayList<>();
    for (SortProperty sortProperty : sort) {
      translated.add(
          software.amazon.amplifyuibuilder.component.SortProperty
              .builder()
              .field(sortProperty.field())
              .direction(sortProperty.directionAsString())
              .build()
      );
    }
    return translated;
  }

  private static List<software.amazon.amplifyuibuilder.component.ComponentVariant> translateVariantsFromSDKToCFN(List<ComponentVariant> variants) {
    List<software.amazon.amplifyuibuilder.component.ComponentVariant> translated = new ArrayList<>();
    for (ComponentVariant variant : variants) {
      translated.add(software.amazon.amplifyuibuilder.component.ComponentVariant.builder()
          .variantValues(variant.variantValues())
          .overrides(variant.overrides())
          .build());
    }
    return translated;
  }

  private static List<software.amazon.amplifyuibuilder.component.ComponentChild> translateChildrenFromSDKToCFN(List<ComponentChild> children) {
    List<software.amazon.amplifyuibuilder.component.ComponentChild> translated = new ArrayList<>();
    for (ComponentChild child : children) {
      software.amazon.amplifyuibuilder.component.ComponentChild.ComponentChildBuilder builder = software.amazon.amplifyuibuilder.component.ComponentChild.builder()
          .componentType(child.componentType());
      if (child.properties() != null) {
        builder.properties(translatePropertiesFromSDKToCFN(child.properties()));
      }
      if (child.children() != null) {
        builder.children(translateChildrenFromSDKToCFN(child.children()));
      }
      translated.add(builder.build());
    }
    return translated;
  }

  private static Map<String, software.amazon.amplifyuibuilder.component.ComponentProperty> translatePropertiesFromSDKToCFN(Map<String, ComponentProperty> properties) {
    Map<String, software.amazon.amplifyuibuilder.component.ComponentProperty> translated = new HashMap<>();
    properties.forEach((k, v) -> {
      if (v != null) {
        translated.put(k, translateComponentPropertyFromSDKToCFN(v));
      }
    });
    return translated;
  }

  private static software.amazon.amplifyuibuilder.component.ComponentConditionProperty translateConditionFromSDKToCFN(ComponentConditionProperty condition) {
    if (condition == null) {
      return null;
    }
    software.amazon.amplifyuibuilder.component.ComponentConditionProperty.ComponentConditionPropertyBuilder builder = software.amazon.amplifyuibuilder.component.ComponentConditionProperty
        .builder()
        .operator(condition.operator())
        .field(condition.field())
        .operand(condition.operand())
        .property(condition.property());
    if (condition.elseValue() != null) {
      builder.else_(translateComponentPropertyFromSDKToCFN(condition.elseValue()));
    }
    if (condition.then() != null) {
      builder.then(translateComponentPropertyFromSDKToCFN(condition.then()));
    }
    return builder.build();
  }

  private static software.amazon.amplifyuibuilder.component.ComponentProperty translateComponentPropertyFromSDKToCFN(ComponentProperty property) {
    // ComponentProperty is flattened to include properties of many types.
    // See StudioComponentProperty type in https://code.amazon.com/packages/AmplifyStudioCommon/blobs/mainline/--/src/types/index.ts
    software.amazon.amplifyuibuilder.component.ComponentProperty.ComponentPropertyBuilder builder = software.amazon.amplifyuibuilder.component.ComponentProperty
        .builder()
        .type(property.type())
        .importedValue(property.importedValue())
        .defaultValue(property.defaultValue())
        .event(property.event())
        .model(property.model())
        .value(property.value())
        .bindingProperties(translateComponentPropertyBindingPropertiesFromSDKToCFN(property.bindingProperties()))
        .condition(translateConditionFromSDKToCFN(property.condition()))
        .bindings(translateFormBindingElementsFromSDKToCFN(property.bindings()));
    if (property.concat() != null) {
      builder.concat(translateComponentPropertiesFromSDKToCFN(property.concat()));
    }
    return builder.build();
  }

  private static List<software.amazon.amplifyuibuilder.component.ComponentProperty> translateComponentPropertiesFromSDKToCFN(List<ComponentProperty> properties) {
    List<software.amazon.amplifyuibuilder.component.ComponentProperty> translated = new ArrayList<>();
    for (ComponentProperty property : properties) {
      translated.add(translateComponentPropertyFromSDKToCFN(property));
    }
    return translated;
  }

  private static software.amazon.amplifyuibuilder.component.ComponentPropertyBindingProperties translateComponentPropertyBindingPropertiesFromSDKToCFN(ComponentPropertyBindingProperties bindingProperties) {
    if (bindingProperties == null) {
      return null;
    }
    return software.amazon.amplifyuibuilder.component.ComponentPropertyBindingProperties
        .builder()
        .field(bindingProperties.field())
        .property(bindingProperties.property())
        .build();
  }

  private static Map<String, software.amazon.amplifyuibuilder.component.FormBindingElement> translateFormBindingElementsFromSDKToCFN(Map<String, FormBindingElement> bindings) {
    if (bindings == null) {
      return null;
    }
    Map<String, software.amazon.amplifyuibuilder.component.FormBindingElement> translated = new HashMap<>();
    bindings.forEach((k, v) -> translated.put(k, software.amazon.amplifyuibuilder.component.FormBindingElement.builder()
        .element(v.element())
        .property(v.property())
        .build()
    ));
    return translated;
  }

  private static Map<String, software.amazon.amplifyuibuilder.component.ComponentBindingPropertiesValue> translateBindingPropertiesFromSDKToCFN(Map<String, ComponentBindingPropertiesValue> bindingProperties) {
    if (bindingProperties == null) {
      return null;
    }
    Map<String, software.amazon.amplifyuibuilder.component.ComponentBindingPropertiesValue> translated = new HashMap<>();
    bindingProperties.forEach((k, v) -> {
      software.amazon.amplifyuibuilder.component.ComponentBindingPropertiesValue.ComponentBindingPropertiesValueBuilder bindingPropertiesValue = software.amazon.amplifyuibuilder.component.ComponentBindingPropertiesValue.builder();
      bindingPropertiesValue.type(v.type());
      bindingPropertiesValue.defaultValue(v.defaultValue());

      if (v.bindingProperties() != null) {
        software.amazon.amplifyuibuilder.component.ComponentBindingPropertiesValueProperties.ComponentBindingPropertiesValuePropertiesBuilder bindingPropertiesValueProperties = software.amazon.amplifyuibuilder.component.ComponentBindingPropertiesValueProperties.builder();
        bindingPropertiesValueProperties.field(v.bindingProperties().field());
        bindingPropertiesValueProperties.bucket(v.bindingProperties().bucket());
        bindingPropertiesValueProperties.userAttribute(v.bindingProperties().userAttribute());
        bindingPropertiesValueProperties.model(v.bindingProperties().model());
        bindingPropertiesValueProperties.key(v.bindingProperties().key());
        bindingPropertiesValueProperties.defaultValue(v.bindingProperties().defaultValue());
        if (v.bindingProperties().predicates() != null) {
          bindingPropertiesValueProperties.predicates(translatePredicatesFromSDKToCFN(v.bindingProperties().predicates()));
        }
        bindingPropertiesValue.bindingProperties(bindingPropertiesValueProperties.build());
      }
      translated.put(k, bindingPropertiesValue.build());
    });
    return translated;
  }

  /**
   * Request to delete a resource
   *
   * @param model resource model
   * @return awsRequest the aws service request to delete a resource
   */
  static DeleteComponentRequest translateToDeleteRequest(
      final ResourceModel model
  ) {
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
      final ResourceModel model
  ) {
    UpdateComponentRequest.Builder updateComponentRequest = UpdateComponentRequest
        .builder()
        .appId(model.getAppId())
        .environmentName(model.getEnvironmentName())
        .id(model.getId());

    UpdateComponentData.Builder updateComponentDataBuilder = UpdateComponentData.builder();
    updateComponentDataBuilder
        .id(model.getId())
        .componentType(model.getComponentType())
        .name(model.getName())
        .bindingProperties(translateBindingPropertiesFromCFNToSDK(model.getBindingProperties()))
        .children(translateChildComponentsFromCFNToSDK(model.getChildren()))
        .overrides(model.getOverrides())
        .properties(translatePropertiesFromCFNToSDK(model.getProperties()))
        .variants(translateVariantsFromCFNToSDK(model.getVariants()))
        .collectionProperties(translateCollectionPropertiesFromCFNToSDK(model.getCollectionProperties()));

    return updateComponentRequest.updatedComponent(updateComponentDataBuilder.build()).build();
  }

  /**
   * Request to list resources
   *
   * @param nextToken token passed to the aws service list resources request
   * @return awsRequest the aws service request to list resources within aws account
   */
  static ListComponentsRequest translateToListRequest(final String nextToken, final ResourceModel model) {
    return ListComponentsRequest.builder()
        .nextToken(nextToken)
        .appId(model.getAppId())
        .environmentName(model.getEnvironmentName())
        .build();
  }

  /**
   * Translates resource objects from sdk into a resource model (primary identifier only)
   *
   * @param listComponentsResponse the aws service describe resource response
   * @return list of resource models
   */
  static List<ResourceModel> translateFromListRequest(
      final ListComponentsResponse listComponentsResponse
  ) {
    return streamOfOrEmpty(listComponentsResponse.entities())
        .map(resource -> ResourceModel.builder()
            .appId(resource.appId())
            .environmentName(resource.environmentName())
            .id(resource.id())
            .name(resource.name())
            .componentType(resource.componentType())
            .build()
        )
        .collect(Collectors.toList());
  }

  private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
    return Optional
        .ofNullable(collection)
        .map(Collection::stream)
        .orElseGet(Stream::empty);
  }
}
