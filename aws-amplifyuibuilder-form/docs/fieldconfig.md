# AWS::AmplifyUIBuilder::Form FieldConfig

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#label" title="Label">Label</a>" : <i>String</i>,
    "<a href="#position" title="Position">Position</a>" : <i><a href="fieldposition.md">FieldPosition</a></i>,
    "<a href="#excluded" title="Excluded">Excluded</a>" : <i>Boolean</i>,
    "<a href="#inputtype" title="InputType">InputType</a>" : <i><a href="fieldinputconfig.md">FieldInputConfig</a></i>,
    "<a href="#validations" title="Validations">Validations</a>" : <i>[ <a href="fieldvalidationconfiguration.md">FieldValidationConfiguration</a>, ... ]</i>
}
</pre>

### YAML

<pre>
<a href="#label" title="Label">Label</a>: <i>String</i>
<a href="#position" title="Position">Position</a>: <i><a href="fieldposition.md">FieldPosition</a></i>
<a href="#excluded" title="Excluded">Excluded</a>: <i>Boolean</i>
<a href="#inputtype" title="InputType">InputType</a>: <i><a href="fieldinputconfig.md">FieldInputConfig</a></i>
<a href="#validations" title="Validations">Validations</a>: <i>
      - <a href="fieldvalidationconfiguration.md">FieldValidationConfiguration</a></i>
</pre>

## Properties

#### Label

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Position

_Required_: No

_Type_: <a href="fieldposition.md">FieldPosition</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Excluded

_Required_: No

_Type_: Boolean

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### InputType

_Required_: No

_Type_: <a href="fieldinputconfig.md">FieldInputConfig</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Validations

_Required_: No

_Type_: List of <a href="fieldvalidationconfiguration.md">FieldValidationConfiguration</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
