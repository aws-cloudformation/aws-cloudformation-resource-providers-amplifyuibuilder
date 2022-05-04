package software.amazon.amplifyuibuilder.component;

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

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class AbstractTestBase {

  protected static final Credentials MOCK_CREDENTIALS;
  protected static final LoggerProxy logger;
  protected static String NAME = "CollectionOfCustomButtons";
  protected static String ID = "c-1234XYZ";
  protected static String APP_ID = "d1234";
  protected static String ENV_NAME = "dev";
  protected static String TYPE = "Collection";
  protected static String NEXT_TOKEN = "next";
  protected static String SCHEMA_VERSION = "1.0.0";
  protected static Map<String, String> TAGS = Collections.singletonMap("Stage", "Dev");
  protected static Map<String, Map<String, String>> OVERRIDES = Collections.singletonMap("label", Collections.singletonMap("test", "test"));
  protected static List<ComponentVariant> VARIANT_CFN = buildTestVariants();
  protected static Map<String, ComponentBindingPropertiesValue> BINDING_PROPERTIES_CFN = buildTestBindingProperties();
  protected static Map<String, ComponentProperty> PROPERTIES_CFN = buildTestProperties();
  protected static Map<String, ComponentDataConfiguration> COLLECTION_PROPERTIES_CFN = buildTestCollectionProperties();
  protected static List<ComponentChild> CHILDREN_CFN = buildTestComponentChildren();
  protected static Map<String, ComponentEvent> EVENTS_CFN = buildTestComponentEvents();

  private static Map<String, ComponentEvent> buildTestComponentEvents() {
    Map<String, ComponentEvent> events = new HashMap<>();
    events.put("click", ComponentEvent.builder()
        .action("Amplify.AuthSignOut")
        .parameters(ActionParameters.builder()
            .fields(Collections.singletonMap("username", ComponentProperty.builder()
                .componentName("FakeComponent")
                .property("fakeProp")
                .build()))
            .build())
        .build());
    // Most likely impossible to have all these fields on one event, but more efficient for code coverage
    events.put("mouseover", ComponentEvent.builder()
        .action("Amplify.Navigation")
        .parameters(ActionParameters.builder()
            .type(ComponentProperty.builder()
                .value("url")
                .build())
            .url(ComponentProperty.builder()
                .value("https://www.amazon.com/")
                .build())
            .anchor(ComponentProperty.builder()
                .value("https://www.amazon.com/")
                .build())
            .model("FakeModel")
            .id(ComponentProperty.builder()
                .value("1234")
                .build())
            .fields(Collections.singletonMap("Stage", ComponentProperty.builder()
                .value("1234")
                .build()))
            .target(ComponentProperty.builder()
                .value("_blank")
                .build())
            .state(MutationActionSetStateParameter.builder()
                .componentName("FakeComponent")
                .property("fakeProp")
                .set(ComponentProperty.builder()
                    .value("a mutation")
                    .build())
                .build())
            .build())
        .build());

    return events;
  }

  private static List<ComponentChild> buildTestComponentChildren() {
    List<ComponentChild> parent = new ArrayList<>();
    Map<String, ComponentProperty> childProperties = new HashMap<>();
    childProperties.put("label", ComponentProperty
        .builder()
        .collectionBindingProperties(
            ComponentPropertyBindingProperties
                .builder()
                .property("buttonUser")
                .field("userName")
                .build()
        )
        .defaultValue("test@mail.com")
        .build());
    List<ComponentChild> children = new ArrayList<>();
    children.add(ComponentChild
        .builder()
        .name("MyButton")
        .componentType("Button")
        .properties(childProperties)
        .build()
    );
    ComponentChild componentChild = ComponentChild
        .builder()
        .name("Parent")
        .componentType("Flex")
        .properties(new HashMap<>())
        .children(children)
        .build();
    parent.add(
        componentChild
    );
    return parent;
  }

  private static Map<String, ComponentDataConfiguration> buildTestCollectionProperties() {
    Map<String, ComponentDataConfiguration> collectionProperties = new HashMap<>();
    collectionProperties.put("buttonUser", ComponentDataConfiguration
        .builder()
        .model("User")
        .identifiers(ImmutableList.of("abcdefg", "1234567"))
        .sort(ImmutableList.of(SortProperty.builder().direction("ASC").field("testField").build()))
        .build());
    collectionProperties.put("inputUser", ComponentDataConfiguration
        .builder()
        .model("User")
        .identifiers(ImmutableList.of("abcdefg", "1234567"))
        .predicate(Predicate.builder()
            .and(ImmutableList.of(Predicate.builder()
                .field("test").build()))
            .or(ImmutableList.of(Predicate.builder()
                .operand("operand").build()))
            .operator("operator")
            .build())
        .build());
    return collectionProperties;
  }

  private static Map<String, ComponentProperty> buildTestProperties() {
    Map<String, ComponentProperty> properties = new HashMap<>();
    properties.put("type", ComponentProperty
        .builder()
        .value("list")
        .build());
    properties.put("gap", ComponentProperty
        .builder()
        .value("1.5rem")
        .build());
    properties.put("backgroundColor", ComponentProperty
        .builder()
        .bindingProperties(
            ComponentPropertyBindingProperties
                .builder()
                .property("backgroundColor")
                .build()
        )
        .build());
    properties.put("prompt", ComponentProperty
        .builder()
        .condition(ComponentConditionProperty
            .builder()
            .property("buttonUser")
            .field("age")
            .operator("gt")
            .operand("18")
            .then(ComponentProperty
                .builder()
                .concat(ImmutableList.of(
                    ComponentProperty
                        .builder()
                        .bindingProperties(ComponentPropertyBindingProperties
                            .builder()
                            .property("buttonUser")
                            .field("firstName")
                            .build())
                        .build(),
                    ComponentProperty
                        .builder()
                        .value(", cast your vote")
                        .build()))
                .build()
            )
            .else_(ComponentProperty
                .builder()
                .value("Sorry you cannot vote")
                .build()
            )
            .build())
        .build());
    properties.put("epic", ComponentProperty.builder()
        .bindings(ImmutableMap.of("formBinding", FormBindingElement.builder()
            .property("Name")
            .element("Input")
            .build()))
        .build());
    return properties;
  }

  private static List<ComponentVariant> buildTestVariants() {
    final Map<String, String> variantValues = Collections.singletonMap("A", "32");
    return ImmutableList.of(
        ComponentVariant
            .builder()
            .variantValues(variantValues)
            .overrides(OVERRIDES)
            .build()
    );
  }

  private static Map<String, ComponentBindingPropertiesValue> buildTestBindingProperties() {
    Map<String, ComponentBindingPropertiesValue> bindingProperties = new HashMap<>();
    ComponentBindingPropertiesValue bindingPropertiesValue = ComponentBindingPropertiesValue
        .builder()
        .defaultValue("John Smith")
        .type("String")
        .bindingProperties(
            ComponentBindingPropertiesValueProperties
                .builder()
                .model("User")
                .field("userName")
                .build()
        )
        .build();
    ComponentBindingPropertiesValue bindingPropertiesValue2 = ComponentBindingPropertiesValue
        .builder()
        .defaultValue("John Smith")
        .type("String")
        .build();
    bindingProperties.put("user", bindingPropertiesValue);
    bindingProperties.put("betaUser", bindingPropertiesValue2);
    return bindingProperties;
  }

  static {
    MOCK_CREDENTIALS = new Credentials("accessKey", "secretKey", "token");
    logger = new LoggerProxy();
  }

  static ProxyClient<AmplifyUiBuilderClient> MOCK_PROXY(
      final AmazonWebServicesClientProxy proxy,
      final AmplifyUiBuilderClient sdkClient
  ) {
    return new ProxyClient<AmplifyUiBuilderClient>() {
      @Override
      public <RequestT extends AwsRequest, ResponseT extends AwsResponse> ResponseT injectCredentialsAndInvokeV2(
          RequestT request,
          Function<RequestT, ResponseT> requestFunction
      ) {
        return proxy.injectCredentialsAndInvokeV2(request, requestFunction);
      }

      @Override
      public <RequestT extends AwsRequest, ResponseT extends AwsResponse> CompletableFuture<ResponseT> injectCredentialsAndInvokeV2Async(
          RequestT request,
          Function<RequestT, CompletableFuture<ResponseT>> requestFunction
      ) {
        throw new UnsupportedOperationException();
      }

      @Override
      public <RequestT extends AwsRequest, ResponseT extends AwsResponse, IterableT extends SdkIterable<ResponseT>> IterableT injectCredentialsAndInvokeIterableV2(
          RequestT request,
          Function<RequestT, IterableT> requestFunction
      ) {
        return proxy.injectCredentialsAndInvokeIterableV2(
            request,
            requestFunction
        );
      }

      @Override
      public <RequestT extends AwsRequest, ResponseT extends AwsResponse> ResponseInputStream<ResponseT> injectCredentialsAndInvokeV2InputStream(
          RequestT requestT,
          Function<RequestT, ResponseInputStream<ResponseT>> function
      ) {
        throw new UnsupportedOperationException();
      }

      @Override
      public <RequestT extends AwsRequest, ResponseT extends AwsResponse> ResponseBytes<ResponseT> injectCredentialsAndInvokeV2Bytes(
          RequestT requestT,
          Function<RequestT, ResponseBytes<ResponseT>> function
      ) {
        throw new UnsupportedOperationException();
      }

      @Override
      public AmplifyUiBuilderClient client() {
        return sdkClient;
      }
    };
  }
}
