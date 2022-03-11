# AWS::AmplifyUIBuilder::Component

Definition of AWS::AmplifyUIBuilder::Component Resource Type

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::AmplifyUIBuilder::Component",
    "Properties" : {
        "<a href="#bindingproperties" title="BindingProperties">BindingProperties</a>" : <i><a href="bindingproperties.md">BindingProperties</a></i>,
        "<a href="#children" title="Children">Children</a>" : <i>[ <a href="componentchild.md">ComponentChild</a>, ... ]</i>,
        "<a href="#collectionproperties" title="CollectionProperties">CollectionProperties</a>" : <i><a href="collectionproperties.md">CollectionProperties</a></i>,
        "<a href="#componenttype" title="ComponentType">ComponentType</a>" : <i>String</i>,
        "<a href="#events" title="Events">Events</a>" : <i><a href="componentchild-events.md">Events</a></i>,
        "<a href="#name" title="Name">Name</a>" : <i>String</i>,
        "<a href="#overrides" title="Overrides">Overrides</a>" : <i><a href="overrides.md">Overrides</a></i>,
        "<a href="#properties" title="Properties">Properties</a>" : <i><a href="componentchild-properties.md">Properties</a></i>,
        "<a href="#schemaversion" title="SchemaVersion">SchemaVersion</a>" : <i>String</i>,
        "<a href="#sourceid" title="SourceId">SourceId</a>" : <i>String</i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i><a href="tags.md">Tags</a></i>,
        "<a href="#variants" title="Variants">Variants</a>" : <i>[ <a href="componentvariant.md">ComponentVariant</a>, ... ]</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::AmplifyUIBuilder::Component
Properties:
    <a href="#bindingproperties" title="BindingProperties">BindingProperties</a>: <i><a href="bindingproperties.md">BindingProperties</a></i>
    <a href="#children" title="Children">Children</a>: <i>
      - <a href="componentchild.md">ComponentChild</a></i>
    <a href="#collectionproperties" title="CollectionProperties">CollectionProperties</a>: <i><a href="collectionproperties.md">CollectionProperties</a></i>
    <a href="#componenttype" title="ComponentType">ComponentType</a>: <i>String</i>
    <a href="#events" title="Events">Events</a>: <i><a href="componentchild-events.md">Events</a></i>
    <a href="#name" title="Name">Name</a>: <i>String</i>
    <a href="#overrides" title="Overrides">Overrides</a>: <i><a href="overrides.md">Overrides</a></i>
    <a href="#properties" title="Properties">Properties</a>: <i><a href="componentchild-properties.md">Properties</a></i>
    <a href="#schemaversion" title="SchemaVersion">SchemaVersion</a>: <i>String</i>
    <a href="#sourceid" title="SourceId">SourceId</a>: <i>String</i>
    <a href="#tags" title="Tags">Tags</a>: <i><a href="tags.md">Tags</a></i>
    <a href="#variants" title="Variants">Variants</a>: <i>
      - <a href="componentvariant.md">ComponentVariant</a></i>
</pre>

## Properties

#### BindingProperties

_Required_: Yes

_Type_: <a href="bindingproperties.md">BindingProperties</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Children

_Required_: No

_Type_: List of <a href="componentchild.md">ComponentChild</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### CollectionProperties

_Required_: No

_Type_: <a href="collectionproperties.md">CollectionProperties</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ComponentType

_Required_: Yes

_Type_: String

_Minimum_: <code>1</code>

_Maximum_: <code>255</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Events

_Required_: No

_Type_: <a href="componentchild-events.md">Events</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Name

_Required_: Yes

_Type_: String

_Minimum_: <code>1</code>

_Maximum_: <code>255</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Overrides

_Required_: Yes

_Type_: <a href="overrides.md">Overrides</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Properties

_Required_: Yes

_Type_: <a href="componentchild-properties.md">Properties</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### SchemaVersion

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### SourceId

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

_Required_: No

_Type_: <a href="tags.md">Tags</a>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### Variants

_Required_: Yes

_Type_: List of <a href="componentvariant.md">ComponentVariant</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### AppId

Returns the <code>AppId</code> value.

#### EnvironmentName

Returns the <code>EnvironmentName</code> value.

#### Id

Returns the <code>Id</code> value.
