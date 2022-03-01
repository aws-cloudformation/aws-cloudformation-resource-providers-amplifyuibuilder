package software.amazon.amplifyuibuilder.component;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import software.amazon.awssdk.services.amplifyuibuilder.model.*;
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
import software.amazon.awssdk.services.amplifyuibuilder.model.CreateComponentData;
import software.amazon.awssdk.services.amplifyuibuilder.model.FormBindingElement;
import software.amazon.awssdk.services.amplifyuibuilder.model.MutationActionSetStateParameter;
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
    createComponent.events(translateEventsFromCFNToSDK(model.getEvents()));
    createComponent.schemaVersion(model.getSchemaVersion());

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
    properties.forEach((k, v) -> translated.put(k, translateComponentPropertyFromCFNToSDK(v)));
    return translated;
  }

  private static ComponentConditionProperty translateConditionFromCFNToSDK(software.amazon.amplifyuibuilder.component.ComponentConditionProperty condition) {
    if (condition == null) {
      return null;
    }
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

  private static ComponentProperty translateComponentPropertyFromCFNToSDK(software.amazon.amplifyuibuilder.component.ComponentProperty property) {
    if (property == null) return null;
    // ComponentProperty is flattened to include properties of many types.
    // So check each property for null because there can only be a subset of values assigned to these properties
    // See StudioComponent.properties type in https://code.amazon.com/packages/AmplifyStudioCommon/blobs/mainline/--/src/types/index.ts
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
        .bindingProperties(translateComponentPropertyBindingPropertiesFromCFNToSDK(property.getBindingProperties()))
        .condition(translateConditionFromCFNToSDK(property.getCondition()))
        .concat(translateComponentPropertiesFromCFNToSDK(property.getConcat()))
        .bindings(translateFormBindingElementsFromCFNToSDK(property.getBindings()))
        .build();
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
    if (bindings == null) return null;
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
              .events(translateEventsFromCFNToSDK(c.getEvents()))
              .build()
      );
    }
    return translated;
  }

  public static Map<String, ComponentEvent> translateEventsFromCFNToSDK(Map<String,software.amazon.amplifyuibuilder.component.ComponentEvent> events) {
    if (events == null) return null;
    Map<String, ComponentEvent> translated = new HashMap<>();
    events.forEach((k,v) -> translated.put(k,
        ComponentEvent.builder()
            .action(v.getAction())
            .parameters(translateEventParametersFromCFNToSDK(v.getParameters()))
            .build()
    ));
    return translated;
  }

  private static ActionParameters translateEventParametersFromCFNToSDK(software.amazon.amplifyuibuilder.component.ActionParameters parameters) {
    if (parameters == null) return null;
    return ActionParameters.builder()
        .type(translateComponentPropertyFromCFNToSDK(parameters.getType()))
        .url(translateComponentPropertyFromCFNToSDK(parameters.getUrl()))
        .anchor(translateComponentPropertyFromCFNToSDK(parameters.getAnchor()))
        .target(translateComponentPropertyFromCFNToSDK(parameters.getTarget()))
        .global(translateComponentPropertyFromCFNToSDK(parameters.getGlobal()))
        .model(parameters.getModel())
        .id(translateComponentPropertyFromCFNToSDK(parameters.getId()))
        .fields(translatePropertiesFromCFNToSDK(parameters.getFields()))
        .state(translateMutationActionSetStateParameterFromCFNToSDK(parameters.getState()))
        .build();
  }

  private static MutationActionSetStateParameter translateMutationActionSetStateParameterFromCFNToSDK(software.amazon.amplifyuibuilder.component.MutationActionSetStateParameter state) {
    if (state == null) return null;
    return MutationActionSetStateParameter.builder()
        .componentName(state.getComponentName())
        .property(state.getProperty())
        .set(translateComponentPropertyFromCFNToSDK(state.getSet()))
        .build();
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
        .events(translateEventsFromSDKToCFN(componentResponse.events()))
        .schemaVersion(componentResponse.schemaVersion())
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
    if (variants == null) return null;
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
    if (children == null) return null;

    List<software.amazon.amplifyuibuilder.component.ComponentChild> translated = new ArrayList<>();
    for (ComponentChild child : children) {
      translated.add(software.amazon.amplifyuibuilder.component.ComponentChild.builder()
        .componentType(child.componentType())
        .properties(translatePropertiesFromSDKToCFN(child.properties()))
        .children(translateChildrenFromSDKToCFN(child.children()))
        .events(translateEventsFromSDKToCFN(child.events()))
        .build()
      );
    }
    return translated;
  }

  private static Map<String,software.amazon.amplifyuibuilder.component.ComponentEvent> translateEventsFromSDKToCFN(Map<String, ComponentEvent> events) {
    if (events == null) return null;
    Map<String,software.amazon.amplifyuibuilder.component.ComponentEvent> translated = new HashMap<>();
    events.forEach((k, v) -> translated.put(k, software.amazon.amplifyuibuilder.component.ComponentEvent.builder()
        .action(v.action())
        .parameters(translateEventParametersFromSDKToCFN(v.parameters()))
        .build()));
    return translated;
  }

  private static software.amazon.amplifyuibuilder.component.ActionParameters translateEventParametersFromSDKToCFN(ActionParameters parameters) {
    if (parameters == null) return null;
    return software.amazon.amplifyuibuilder.component.ActionParameters.builder()
        .type(translateComponentPropertyFromSDKToCFN(parameters.type()))
        .url(translateComponentPropertyFromSDKToCFN(parameters.url()))
        .anchor(translateComponentPropertyFromSDKToCFN(parameters.anchor()))
        .target(translateComponentPropertyFromSDKToCFN(parameters.target()))
        .global(translateComponentPropertyFromSDKToCFN(parameters.global()))
        .model(parameters.model())
        .id(translateComponentPropertyFromSDKToCFN(parameters.id()))
        .fields(translatePropertiesFromSDKToCFN(parameters.fields()))
        .state(translateMutationActionSetStateParameterFromSDKToCFN(parameters.state()))
        .build();
  }

  private static software.amazon.amplifyuibuilder.component.MutationActionSetStateParameter translateMutationActionSetStateParameterFromSDKToCFN(MutationActionSetStateParameter state) {
    if (state == null) return null;
    return software.amazon.amplifyuibuilder.component.MutationActionSetStateParameter.builder()
        .componentName(state.componentName())
        .property(state.property())
        .set(translateComponentPropertyFromSDKToCFN(state.set()))
        .build();
  }

  private static Map<String, software.amazon.amplifyuibuilder.component.ComponentProperty> translatePropertiesFromSDKToCFN(Map<String, ComponentProperty> properties) {
    if (properties == null) return null;
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
      builder.else_(translateComponentPropertyFromSDKToCFN(condition.elseValue()));
      builder.then(translateComponentPropertyFromSDKToCFN(condition.then()));
    return builder.build();
  }

  private static software.amazon.amplifyuibuilder.component.ComponentProperty translateComponentPropertyFromSDKToCFN(ComponentProperty property) {
    if (property == null) return null;
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
        .property(property.property())
        .componentName(property.componentName())
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
        .collectionProperties(translateCollectionPropertiesFromCFNToSDK(model.getCollectionProperties()))
        .events(translateEventsFromCFNToSDK(model.getEvents()))
        .schemaVersion(model.getSchemaVersion());

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
