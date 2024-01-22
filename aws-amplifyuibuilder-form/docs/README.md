# AWS::AmplifyUIBuilder::Form

Definition of AWS::AmplifyUIBuilder::Form Resource Type

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::AmplifyUIBuilder::Form",
    "Properties" : {
        "<a href="#cta" title="Cta">Cta</a>" : <i><a href="formcta.md">FormCTA</a></i>,
        "<a href="#datatype" title="DataType">DataType</a>" : <i><a href="formdatatypeconfig.md">FormDataTypeConfig</a></i>,
        "<a href="#fields" title="Fields">Fields</a>" : <i><a href="fields.md">Fields</a></i>,
        "<a href="#formactiontype" title="FormActionType">FormActionType</a>" : <i>String</i>,
        "<a href="#labeldecorator" title="LabelDecorator">LabelDecorator</a>" : <i>String</i>,
        "<a href="#name" title="Name">Name</a>" : <i>String</i>,
        "<a href="#schemaversion" title="SchemaVersion">SchemaVersion</a>" : <i>String</i>,
        "<a href="#sectionalelements" title="SectionalElements">SectionalElements</a>" : <i><a href="sectionalelements.md">SectionalElements</a></i>,
        "<a href="#style" title="Style">Style</a>" : <i><a href="formstyle.md">FormStyle</a></i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i><a href="tags.md">Tags</a></i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::AmplifyUIBuilder::Form
Properties:
    <a href="#cta" title="Cta">Cta</a>: <i><a href="formcta.md">FormCTA</a></i>
    <a href="#datatype" title="DataType">DataType</a>: <i><a href="formdatatypeconfig.md">FormDataTypeConfig</a></i>
    <a href="#fields" title="Fields">Fields</a>: <i><a href="fields.md">Fields</a></i>
    <a href="#formactiontype" title="FormActionType">FormActionType</a>: <i>String</i>
    <a href="#labeldecorator" title="LabelDecorator">LabelDecorator</a>: <i>String</i>
    <a href="#name" title="Name">Name</a>: <i>String</i>
    <a href="#schemaversion" title="SchemaVersion">SchemaVersion</a>: <i>String</i>
    <a href="#sectionalelements" title="SectionalElements">SectionalElements</a>: <i><a href="sectionalelements.md">SectionalElements</a></i>
    <a href="#style" title="Style">Style</a>: <i><a href="formstyle.md">FormStyle</a></i>
    <a href="#tags" title="Tags">Tags</a>: <i><a href="tags.md">Tags</a></i>
</pre>

## Properties

#### Cta

_Required_: No

_Type_: <a href="formcta.md">FormCTA</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### DataType

_Required_: No

_Type_: <a href="formdatatypeconfig.md">FormDataTypeConfig</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Fields

_Required_: No

_Type_: <a href="fields.md">Fields</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### FormActionType

_Required_: No

_Type_: String

_Allowed Values_: <code>create</code> | <code>update</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### LabelDecorator

_Required_: No

_Type_: String

_Allowed Values_: <code>required</code> | <code>optional</code> | <code>none</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Name

_Required_: No

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>255</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### SchemaVersion

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### SectionalElements

_Required_: No

_Type_: <a href="sectionalelements.md">SectionalElements</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Style

_Required_: No

_Type_: <a href="formstyle.md">FormStyle</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

_Required_: No

_Type_: <a href="tags.md">Tags</a>

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
