# AWS::AmplifyUIBuilder::Form FormInputValueProperty

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#value" title="Value">Value</a>" : <i>String</i>,
    "<a href="#bindingproperties" title="BindingProperties">BindingProperties</a>" : <i><a href="forminputvaluepropertybindingproperties.md">FormInputValuePropertyBindingProperties</a></i>,
    "<a href="#concat" title="Concat">Concat</a>" : <i>[ <a href="forminputvalueproperty.md">FormInputValueProperty</a>, ... ]</i>
}
</pre>

### YAML

<pre>
<a href="#value" title="Value">Value</a>: <i>String</i>
<a href="#bindingproperties" title="BindingProperties">BindingProperties</a>: <i><a href="forminputvaluepropertybindingproperties.md">FormInputValuePropertyBindingProperties</a></i>
<a href="#concat" title="Concat">Concat</a>: <i>
      - <a href="forminputvalueproperty.md">FormInputValueProperty</a></i>
</pre>

## Properties

#### Value

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### BindingProperties

_Required_: No

_Type_: <a href="forminputvaluepropertybindingproperties.md">FormInputValuePropertyBindingProperties</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Concat

_Required_: No

_Type_: List of <a href="forminputvalueproperty.md">FormInputValueProperty</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
