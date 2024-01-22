package software.amazon.amplifyuibuilder.form;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import software.amazon.awssdk.awscore.AwsRequest;
import software.amazon.awssdk.awscore.AwsResponse;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Credentials;
import software.amazon.cloudformation.proxy.LoggerProxy;
import software.amazon.cloudformation.proxy.ProxyClient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class AbstractTestBase {
        protected static final Credentials MOCK_CREDENTIALS;
        protected static final LoggerProxy logger;
        protected static String ID = "f-1234XYZ";
        protected static String APP_ID = "d1234";
        protected static String ENV_NAME = "dev";
        protected static String NAME = "CreatePostForm";
        protected static String ACTION_TYPE = "Create";
        protected static String SCHEMA_VERSION = "1.0";
        protected static FormDataTypeConfig DATA_TYPE = getDataType();
        protected static Map<String, FieldConfig> FIELDS = getFields();
        protected static Map<String, software.amazon.awssdk.services.amplifyuibuilder.model.FieldConfig> MODEL_FIELDS = getModelFields();
        protected static FormStyle STYLES = getStyles();
        protected static Map<String, SectionalElement> SECTIONAL_ELEMENTS = getSectionalElements();
        protected static Map<String, String> TAGS = ImmutableMap.of("Test", "Tag");
        protected static FormCTA CTA = getFormCta();
        protected static String LABEL_DECORATOR = "none";

        private static FormCTA getFormCta() {
                return FormCTA.builder()
                                .position("bottom")
                                .submit(FormButton.builder()
                                                .position(FieldPosition.builder().fixed("first").build())
                                                .children("Submit")
                                                .excluded(false)
                                                .build())
                                .cancel(FormButton.builder()
                                                .position(FieldPosition.builder().fixed("first").build())
                                                .children("Cancel")
                                                .excluded(false)
                                                .build())
                                .clear(FormButton.builder()
                                                .position(FieldPosition.builder().fixed("first").build())
                                                .children("Clear")
                                                .excluded(false)
                                                .build())
                                .build();
        }

        private static Map<String, SectionalElement> getSectionalElements() {
                return new HashMap<>(ImmutableMap.of("mySection", SectionalElement.builder()
                                .type("text")
                                .text("Address")
                                .level(1.0)
                                .position(getFieldPosition())
                                .excluded(false)
                                .build()));
        }

        private static FormStyle getStyles() {
                return FormStyle.builder()
                                .horizontalGap(getStyleConfig())
                                .verticalGap(getStyleConfig())
                                .outerPadding(getStyleConfig())
                                .build();
        }

        private static FormStyleConfig getStyleConfig() {
                return FormStyleConfig.builder()
                                .tokenReference("token.reference.padding")
                                .value("10px")
                                .build();
        }

        private static Map<String, FieldConfig> getFields() {
                return new HashMap<>(ImmutableMap.of("name", FieldConfig.builder()
                                .label("Full Name")
                                .excluded(true)
                                .position(getFieldPosition())
                                .inputType(getInputType())
                                .validations(ImmutableList.of(FieldValidationConfiguration
                                                .builder()
                                                .numValues(Arrays.asList(1d, 2d, 3d))
                                                .strValues(ImmutableList.of("Hey", "There"))
                                                .validationMessage("Oh no!")
                                                .build()))
                                .build()));
        }

        private static Map<String, software.amazon.awssdk.services.amplifyuibuilder.model.FieldConfig> getModelFields() {
                return new HashMap<>(ImmutableMap.of("name",
                                software.amazon.awssdk.services.amplifyuibuilder.model.FieldConfig
                                                .builder()
                                                .label("Full Name")
                                                .excluded(true)
                                                .position(software.amazon.awssdk.services.amplifyuibuilder.model.FieldPosition
                                                                .builder()
                                                                .fixed("first")
                                                                .below("title")
                                                                .rightOf("birthday")
                                                                .build())
                                                .validations(software.amazon.awssdk.services.amplifyuibuilder.model.FieldValidationConfiguration
                                                                .builder()
                                                                .numValues(ImmutableList.of(1, 2, 3, 4, 5))
                                                                .strValues(ImmutableList.of("Hey", "There"))
                                                                .validationMessage("Oh no!")
                                                                .build())
                                                .inputType(software.amazon.awssdk.services.amplifyuibuilder.model.FieldInputConfig
                                                                .builder()
                                                                .type("TextField")
                                                                .required(true)
                                                                .readOnly(true)
                                                                .placeholder("Full Name")
                                                                .minValue(1.0F)
                                                                .maxValue(100.0F)
                                                                .step(5.0F)
                                                                .valueMappings(software.amazon.awssdk.services.amplifyuibuilder.model.ValueMappings
                                                                                .builder()
                                                                                .values(ImmutableList.of(
                                                                                                software.amazon.awssdk.services.amplifyuibuilder.model.ValueMapping
                                                                                                                .builder()
                                                                                                                .value(software.amazon.awssdk.services.amplifyuibuilder.model.FormInputValueProperty
                                                                                                                                .builder()
                                                                                                                                .value("richard")
                                                                                                                                .build())
                                                                                                                .displayValue(
                                                                                                                                software.amazon.awssdk.services.amplifyuibuilder.model.FormInputValueProperty
                                                                                                                                                .builder()
                                                                                                                                                .value("Richard")
                                                                                                                                                .build())
                                                                                                                .build()))
                                                                                .build())
                                                                .isArray(false)
                                                                .fileUploaderConfig(
                                                                                software.amazon.awssdk.services.amplifyuibuilder.model.FileUploaderFieldConfig
                                                                                                .builder()
                                                                                                .maxFileCount((int) 1.0)
                                                                                                .maxSize((int) 1000.0)
                                                                                                .acceptedFileTypes(
                                                                                                                ImmutableList.of(
                                                                                                                                ".png",
                                                                                                                                ".jpg"))
                                                                                                .isResumable(false)
                                                                                                .accessLevel("public")
                                                                                                .showThumbnails(false)
                                                                                                .build())
                                                                .build())
                                                .build()));
        }

        private static FieldInputConfig getInputType() {
                return FieldInputConfig.builder()
                                .type("TextField")
                                .required(true)
                                .readOnly(true)
                                .placeholder("Full Name")
                                .minValue(1.0)
                                .maxValue(100.0)
                                .step(5.0)
                                .valueMappings(getValueMappings())
                                .isArray(false)
                                .fileUploaderConfig(getFileUploaderConfig())
                                .build();
        }

        private static ValueMappings getValueMappings() {
                return ValueMappings.builder()
                                .values(ImmutableList.of(ValueMapping.builder().value(FormInputValueProperty.builder()
                                                .value("richard")
                                                .build()).displayValue(FormInputValueProperty.builder()
                                                                .value("Richard")
                                                                .build())
                                                .build()))
                                .build();
        }

        private static FieldPosition getFieldPosition() {
                return FieldPosition.builder()
                                .fixed("first")
                                .below("title")
                                .rightOf("birthday")
                                .build();
        }

        private static FormDataTypeConfig getDataType() {
                return FormDataTypeConfig.builder()
                                .dataSourceType("DataStore")
                                .dataTypeName("Post")
                                .build();
        }

        private static FileUploaderFieldConfig getFileUploaderConfig() {
                return FileUploaderFieldConfig.builder()
                                .maxFileCount(1.0)
                                .maxSize(1000.0)
                                .acceptedFileTypes(ImmutableList.of(".png", ".jpg"))
                                .isResumable(false)
                                .accessLevel("public")
                                .showThumbnails(false)
                                .build();
        }

        static {
                MOCK_CREDENTIALS = new Credentials("accessKey", "secretKey", "token");
                logger = new LoggerProxy();
        }

        static ProxyClient<AmplifyUiBuilderClient> MOCK_PROXY(
                        final AmazonWebServicesClientProxy proxy,
                        final AmplifyUiBuilderClient sdkClient) {
                return new ProxyClient<AmplifyUiBuilderClient>() {
                        @Override
                        public <RequestT extends AwsRequest, ResponseT extends AwsResponse> ResponseT injectCredentialsAndInvokeV2(
                                        RequestT request, Function<RequestT, ResponseT> requestFunction) {
                                return proxy.injectCredentialsAndInvokeV2(request, requestFunction);
                        }

                        @Override
                        public <RequestT extends AwsRequest, ResponseT extends AwsResponse> CompletableFuture<ResponseT> injectCredentialsAndInvokeV2Async(
                                        RequestT request,
                                        Function<RequestT, CompletableFuture<ResponseT>> requestFunction) {
                                throw new UnsupportedOperationException();
                        }

                        @Override
                        public <RequestT extends AwsRequest, ResponseT extends AwsResponse, IterableT extends SdkIterable<ResponseT>> IterableT injectCredentialsAndInvokeIterableV2(
                                        RequestT request, Function<RequestT, IterableT> requestFunction) {
                                return proxy.injectCredentialsAndInvokeIterableV2(request, requestFunction);
                        }

                        @Override
                        public <RequestT extends AwsRequest, ResponseT extends AwsResponse> ResponseInputStream<ResponseT> injectCredentialsAndInvokeV2InputStream(
                                        RequestT requestT,
                                        Function<RequestT, ResponseInputStream<ResponseT>> function) {
                                throw new UnsupportedOperationException();
                        }

                        @Override
                        public <RequestT extends AwsRequest, ResponseT extends AwsResponse> ResponseBytes<ResponseT> injectCredentialsAndInvokeV2Bytes(
                                        RequestT requestT, Function<RequestT, ResponseBytes<ResponseT>> function) {
                                throw new UnsupportedOperationException();
                        }

                        @Override
                        public AmplifyUiBuilderClient client() {
                                return sdkClient;
                        }
                };
        }
}
