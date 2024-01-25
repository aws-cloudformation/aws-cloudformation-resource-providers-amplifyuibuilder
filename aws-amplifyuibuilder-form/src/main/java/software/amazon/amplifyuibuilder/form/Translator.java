package software.amazon.amplifyuibuilder.form;

import software.amazon.amplifyuibuilder.common.Transformer;
import software.amazon.awssdk.services.amplifyuibuilder.model.*;
import software.amazon.awssdk.services.amplifyuibuilder.model.FieldValidationConfiguration;
import software.amazon.awssdk.services.amplifyuibuilder.model.FormButton;
import software.amazon.awssdk.services.amplifyuibuilder.model.FormCTA;
import software.amazon.awssdk.services.amplifyuibuilder.model.FormDataTypeConfig;
import software.amazon.awssdk.services.amplifyuibuilder.model.FieldPosition;
import software.amazon.awssdk.services.amplifyuibuilder.model.FieldInputConfig;
import software.amazon.awssdk.services.amplifyuibuilder.model.FieldConfig;
import software.amazon.awssdk.services.amplifyuibuilder.model.FormStyle;
import software.amazon.awssdk.services.amplifyuibuilder.model.FormStyleConfig;
import software.amazon.awssdk.services.amplifyuibuilder.model.SectionalElement;
import software.amazon.awssdk.services.amplifyuibuilder.model.ValueMapping;
import software.amazon.awssdk.services.amplifyuibuilder.model.FormInputValueProperty;
import software.amazon.awssdk.services.amplifyuibuilder.model.ValueMappings;

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

  static GetFormRequest translateToReadRequest(final ResourceModel model) {
    return GetFormRequest.builder()
        .appId(model.getAppId())
        .environmentName(model.getEnvironmentName())
        .id(model.getId())
        .build();
  }

  static ResourceModel translateFromReadResponse(final GetFormResponse response) {
    Form form = response.form();
    return ResourceModel.builder()
        .appId(form.appId())
        .id(form.id())
        .environmentName(form.environmentName())
        .name(form.name())
        .formActionType(form.formActionTypeAsString())
        .style(transformObj(form.style(), Translator::mapStyleSDKToCFN))
        .dataType(transformObj(form.dataType(), Translator::mapDataTypeSDKToCFN))
        .fields(transformMap(form.fields(), Translator::mapFieldConfigSDKToCFN))
        .sectionalElements(transformMap(form.sectionalElements(), Translator::mapSectionalElementSDKToCFN))
        .cta(transformObj(form.cta(), Translator::mapCtaSDKToCFN))
        .schemaVersion(form.schemaVersion())
        .labelDecorator(form.labelDecoratorAsString())
        .tags(form.tags())
        .build();
  }

  static CreateFormRequest translateToCreateRequest(final ResourceModel model,
      final Map<String, String> desiredResourceTags, final String requestToken) {

    return CreateFormRequest.builder()
        .appId(model.getAppId())
        .environmentName(model.getEnvironmentName())
        .clientToken(requestToken)
        .formToCreate(CreateFormData.builder()
            .name(model.getName())
            .dataType(transformObj(model.getDataType(), Translator::mapDataTypeCFNToSDK))
            .formActionType(model.getFormActionType())
            .fields(transformMap(model.getFields(), Translator::mapFieldConfigCFNToSDK))
            .style(transformObj(model.getStyle(), Translator::mapStyleCFNToSDK))
            .sectionalElements(transformMap(model.getSectionalElements(), Translator::mapSectionalElementCFNToSDK))
            .cta(transformObj(model.getCta(), Translator::mapCtaCFNToSDK))
            .schemaVersion(model.getSchemaVersion())
            .labelDecorator(model.getLabelDecorator())
            .tags(desiredResourceTags)
            .build())
        .build();
  }

  static UpdateFormRequest translateToUpdateRequest(final ResourceModel model) {
    return UpdateFormRequest.builder()
        .id(model.getId())
        .appId(model.getAppId())
        .environmentName(model.getEnvironmentName())
        .updatedForm(UpdateFormData.builder()
            .name(model.getName())
            .dataType(transformObj(model.getDataType(), Translator::mapDataTypeCFNToSDK))
            .formActionType(model.getFormActionType())
            .fields(transformMap(model.getFields(), Translator::mapFieldConfigCFNToSDK))
            .style(transformObj(model.getStyle(), Translator::mapStyleCFNToSDK))
            .sectionalElements(transformMap(model.getSectionalElements(), Translator::mapSectionalElementCFNToSDK))
            .cta(transformObj(model.getCta(), Translator::mapCtaCFNToSDK))
            .labelDecorator(model.getLabelDecorator())
            .schemaVersion(model.getSchemaVersion())
            .build())
        .build();
  }

  static DeleteFormRequest translateToDeleteRequest(final ResourceModel model) {
    return DeleteFormRequest.builder()
        .appId(model.getAppId())
        .environmentName(model.getEnvironmentName())
        .id(model.getId())
        .build();
  }

  static ListFormsRequest translateToListRequest(final String nextToken, final ResourceModel model) {
    return ListFormsRequest.builder()
        .nextToken(nextToken)
        .appId(model.getAppId())
        .environmentName(model.getEnvironmentName())
        .maxResults(100)
        .build();
  }

  static List<ResourceModel> translateFromListResponse(final ListFormsResponse response) {
    return streamOfOrEmpty(response.entities())
        .map(resource -> ResourceModel.builder()
            .id(resource.id())
            .appId(resource.appId())
            .environmentName(resource.environmentName())
            .dataType(transformObj(resource.dataType(), Translator::mapDataTypeSDKToCFN))
            .formActionType(resource.formActionTypeAsString())
            .name(resource.name())
            .build())
        .collect((Collectors.toList()));
  }

  private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
    return Optional.ofNullable(collection)
        .map(Collection::stream)
        .orElseGet(Stream::empty);
  }

  /**
   * Type mapper helpers
   */
  private static software.amazon.amplifyuibuilder.form.FormCTA mapCtaSDKToCFN(FormCTA cta) {
    return software.amazon.amplifyuibuilder.form.FormCTA.builder()
        .position(cta.positionAsString())
        .clear(transformObj(cta.clear(), Translator::mapFormButtonSDKToCFN))
        .cancel(transformObj(cta.cancel(), Translator::mapFormButtonSDKToCFN))
        .submit(transformObj(cta.submit(), Translator::mapFormButtonSDKToCFN))
        .build();
  }

  private static software.amazon.amplifyuibuilder.form.FormButton mapFormButtonSDKToCFN(FormButton button) {
    return software.amazon.amplifyuibuilder.form.FormButton.builder()
        .excluded(button.excluded())
        .children(button.children())
        .position(transformObj(button.position(), Translator::mapFieldPositionSDKToCFN))
        .build();
  }

  static FormCTA mapCtaCFNToSDK(software.amazon.amplifyuibuilder.form.FormCTA cta) {
    return FormCTA.builder()
        .position(cta.getPosition())
        .clear(transformObj(cta.getClear(), Translator::mapFormButtonCFNToSDK))
        .cancel(transformObj(cta.getCancel(), Translator::mapFormButtonCFNToSDK))
        .submit(transformObj(cta.getSubmit(), Translator::mapFormButtonCFNToSDK))
        .build();
  }

  private static FormButton mapFormButtonCFNToSDK(software.amazon.amplifyuibuilder.form.FormButton button) {
    return FormButton.builder()
        .excluded(button.getExcluded())
        .children(button.getChildren())
        .position(transformObj(button.getPosition(), Translator::mapFieldPositionCFNToSDK))
        .build();
  }

  static FormDataTypeConfig mapDataTypeCFNToSDK(software.amazon.amplifyuibuilder.form.FormDataTypeConfig dataType) {
    return FormDataTypeConfig.builder()
        .dataSourceType(dataType.getDataSourceType())
        .dataTypeName(dataType.getDataTypeName())
        .build();
  }

  static FieldConfig mapFieldConfigCFNToSDK(software.amazon.amplifyuibuilder.form.FieldConfig fieldConfig) {
    return FieldConfig.builder()
        .label(fieldConfig.getLabel())
        .excluded(fieldConfig.getExcluded())
        .position(transformObj(fieldConfig.getPosition(), Translator::mapFieldPositionCFNToSDK))
        .inputType(transformObj(fieldConfig.getInputType(), Translator::mapInputTypeCFNToSDK))
        .validations(transformList(fieldConfig.getValidations(), Translator::mapFieldValidationConfigurationCFNToSDK))
        .build();
  }

  private static FieldValidationConfiguration mapFieldValidationConfigurationCFNToSDK(
      software.amazon.amplifyuibuilder.form.FieldValidationConfiguration v) {
    return FieldValidationConfiguration.builder()
        .type(v.getType())
        .strValues(v.getStrValues())
        .numValues(transformList(v.getNumValues(), Transformer::mapDoubleToInt))
        .validationMessage(v.getValidationMessage())
        .build();
  }

  private static FieldInputConfig mapInputTypeCFNToSDK(
      software.amazon.amplifyuibuilder.form.FieldInputConfig inputType) {
    return FieldInputConfig.builder()
        .type(inputType.getType())
        .required(inputType.getRequired())
        .readOnly(inputType.getReadOnly())
        .placeholder(inputType.getPlaceholder())
        .minValue(mapDoubleToFloat(inputType.getMinValue()))
        .maxValue(mapDoubleToFloat(inputType.getMaxValue()))
        .step(mapDoubleToFloat(inputType.getStep()))
        .valueMappings(transformObj(inputType.getValueMappings(), Translator::mapValueMappingsCFNToSDK))
        .name(inputType.getName())
        .value(inputType.getValue())
        .isArray(inputType.getIsArray())
        .build();
  }

  private static ValueMappings mapValueMappingsCFNToSDK(software.amazon.amplifyuibuilder.form.ValueMappings v) {
    return ValueMappings.builder()
        .values(transformList(v.getValues(), Translator::mapValueMappingCFNToSDK))
        .build();
  }

  private static ValueMapping mapValueMappingCFNToSDK(software.amazon.amplifyuibuilder.form.ValueMapping valueMapping) {
    return ValueMapping.builder()
        .value(transformObj(valueMapping.getValue(), Translator::mapValueMappingValueCFNToSDK))
        .displayValue(transformObj(valueMapping.getDisplayValue(), Translator::mapValueMappingValueCFNToSDK))
        .build();
  }

  private static FormInputValueProperty mapValueMappingValueCFNToSDK(
      software.amazon.amplifyuibuilder.form.FormInputValueProperty property) {
    return FormInputValueProperty.builder()
        .value(property.getValue())
        .build();
  }

  private static FieldPosition mapFieldPositionCFNToSDK(software.amazon.amplifyuibuilder.form.FieldPosition position) {
    return FieldPosition.builder()
        .rightOf(position.getRightOf())
        .below(position.getBelow())
        .fixed(position.getFixed())
        .build();
  }

  static FormStyle mapStyleCFNToSDK(software.amazon.amplifyuibuilder.form.FormStyle style) {
    return FormStyle.builder()
        .horizontalGap(transformObj(style.getHorizontalGap(), Translator::mapStyleConfigCFNToSDK))
        .verticalGap(transformObj(style.getVerticalGap(), Translator::mapStyleConfigCFNToSDK))
        .outerPadding(transformObj(style.getOuterPadding(), Translator::mapStyleConfigCFNToSDK))
        .build();
  }

  private static FormStyleConfig mapStyleConfigCFNToSDK(software.amazon.amplifyuibuilder.form.FormStyleConfig config) {
    return FormStyleConfig.builder()
        .tokenReference(config.getTokenReference())
        .value(config.getValue())
        .build();
  }

  static SectionalElement mapSectionalElementCFNToSDK(software.amazon.amplifyuibuilder.form.SectionalElement element) {
    return SectionalElement.builder()
        .type(element.getType())
        .text(element.getText())
        .level(mapDoubleToInt(element.getLevel()))
        .position(transformObj(element.getPosition(), Translator::mapFieldPositionCFNToSDK))
        .excluded(element.getExcluded())
        .build();
  }

  private static software.amazon.amplifyuibuilder.form.SectionalElement mapSectionalElementSDKToCFN(
      SectionalElement element) {
    return software.amazon.amplifyuibuilder.form.SectionalElement.builder()
        .type(element.type())
        .orientation(element.orientation())
        .text(element.text())
        .level(mapIntToDouble(element.level()))
        .position(transformObj(element.position(), Translator::mapFieldPositionSDKToCFN))
        .excluded(element.excluded())
        .build();
  }

  private static software.amazon.amplifyuibuilder.form.FieldPosition mapFieldPositionSDKToCFN(FieldPosition position) {
    return software.amazon.amplifyuibuilder.form.FieldPosition.builder()
        .fixed(position.fixedAsString())
        .rightOf(position.rightOf())
        .below(position.below())
        .build();
  }

  private static software.amazon.amplifyuibuilder.form.FieldConfig mapFieldConfigSDKToCFN(FieldConfig config) {
    return software.amazon.amplifyuibuilder.form.FieldConfig.builder()
        .label(config.label())
        .excluded(config.excluded())
        .inputType(transformObj(config.inputType(), Translator::mapInputTypeSDKToCFN))
        .validations(transformList(config.validations(), Translator::mapFieldValidationConfigurationSDKToCFN))
        .build();
  }

  private static software.amazon.amplifyuibuilder.form.FieldValidationConfiguration mapFieldValidationConfigurationSDKToCFN(
      FieldValidationConfiguration v) {
    return software.amazon.amplifyuibuilder.form.FieldValidationConfiguration.builder()
        .type(v.type())
        .strValues(v.strValues())
        .numValues(transformList(v.numValues(), Transformer::mapIntToDouble))
        .validationMessage(v.validationMessage())
        .build();
  }

  private static software.amazon.amplifyuibuilder.form.FieldInputConfig mapInputTypeSDKToCFN(FieldInputConfig config) {
    return software.amazon.amplifyuibuilder.form.FieldInputConfig.builder()
        .type(config.type())
        .required(config.required())
        .readOnly(config.readOnly())
        .placeholder(config.placeholder())
        .defaultValue(config.defaultValue())
        .descriptiveText(config.descriptiveText())
        .defaultChecked(config.defaultChecked())
        .defaultCountryCode(config.defaultCountryCode())
        .valueMappings(mapValueMappingsSDKToCFN(config.valueMappings()))
        .name(config.name())
        .maxValue(mapFloatToDouble(config.maxValue()))
        .minValue(mapFloatToDouble(config.minValue()))
        .step(mapFloatToDouble(config.step()))
        .value(config.value())
        .isArray(config.isArray())
        .build();
  }

  private static software.amazon.amplifyuibuilder.form.ValueMappings mapValueMappingsSDKToCFN(ValueMappings v) {
    return software.amazon.amplifyuibuilder.form.ValueMappings.builder()
        .values(transformList(v.values(), Translator::mapValueMappingSDKToCFN))
        .build();
  }

  private static software.amazon.amplifyuibuilder.form.ValueMapping mapValueMappingSDKToCFN(ValueMapping v) {
    return software.amazon.amplifyuibuilder.form.ValueMapping.builder()
        .displayValue(transformObj(v.displayValue(), Translator::mapFormInputValuePropertySDKToCFN))
        .value(transformObj(v.value(), Translator::mapFormInputValuePropertySDKToCFN))
        .build();
  }

  private static software.amazon.amplifyuibuilder.form.FormInputValueProperty mapFormInputValuePropertySDKToCFN(
      FormInputValueProperty p) {
    return software.amazon.amplifyuibuilder.form.FormInputValueProperty.builder()
        .value(p.value())
        .build();
  }

  private static software.amazon.amplifyuibuilder.form.FormDataTypeConfig mapDataTypeSDKToCFN(
      FormDataTypeConfig dataType) {
    return software.amazon.amplifyuibuilder.form.FormDataTypeConfig.builder()
        .dataTypeName(dataType.dataTypeName())
        .dataSourceType(dataType.dataSourceTypeAsString())
        .build();
  }

  private static software.amazon.amplifyuibuilder.form.FormStyle mapStyleSDKToCFN(FormStyle style) {
    return software.amazon.amplifyuibuilder.form.FormStyle.builder()
        .horizontalGap(transformObj(style.horizontalGap(), Translator::mapStyleConfigSDKToCFN))
        .verticalGap(transformObj(style.verticalGap(), Translator::mapStyleConfigSDKToCFN))
        .outerPadding(transformObj(style.outerPadding(), Translator::mapStyleConfigSDKToCFN))
        .build();
  }

  private static software.amazon.amplifyuibuilder.form.FormStyleConfig mapStyleConfigSDKToCFN(FormStyleConfig config) {
    return software.amazon.amplifyuibuilder.form.FormStyleConfig.builder()
        .tokenReference(config.tokenReference())
        .value(config.value())
        .build();
  }
}
