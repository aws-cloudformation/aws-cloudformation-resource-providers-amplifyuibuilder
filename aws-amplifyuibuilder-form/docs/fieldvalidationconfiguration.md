# AWS::AmplifyUIBuilder::Form FieldValidationConfiguration

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#type" title="Type">Type</a>" : <i>String</i>,
    "<a href="#strvalues" title="StrValues">StrValues</a>" : <i>[ String, ... ]</i>,
    "<a href="#numvalues" title="NumValues">NumValues</a>" : <i>[ Double, ... ]</i>,
    "<a href="#validationmessage" title="ValidationMessage">ValidationMessage</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#type" title="Type">Type</a>: <i>String</i>
<a href="#strvalues" title="StrValues">StrValues</a>: <i>
      - String</i>
<a href="#numvalues" title="NumValues">NumValues</a>: <i>
      - Double</i>
<a href="#validationmessage" title="ValidationMessage">ValidationMessage</a>: <i>String</i>
</pre>

## Properties

#### Type

_Required_: Yes

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### StrValues

_Required_: No

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### NumValues

_Required_: No

_Type_: List of Double

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ValidationMessage

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
