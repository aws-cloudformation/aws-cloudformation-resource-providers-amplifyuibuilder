# AWS::AmplifyUIBuilder::Component Predicate

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#or" title="Or">Or</a>" : <i>[ <a href="predicate.md">Predicate</a>, ... ]</i>,
    "<a href="#and" title="And">And</a>" : <i>[ <a href="predicate.md">Predicate</a>, ... ]</i>,
    "<a href="#field" title="Field">Field</a>" : <i>String</i>,
    "<a href="#operator" title="Operator">Operator</a>" : <i>String</i>,
    "<a href="#operand" title="Operand">Operand</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#or" title="Or">Or</a>: <i>
      - <a href="predicate.md">Predicate</a></i>
<a href="#and" title="And">And</a>: <i>
      - <a href="predicate.md">Predicate</a></i>
<a href="#field" title="Field">Field</a>: <i>String</i>
<a href="#operator" title="Operator">Operator</a>: <i>String</i>
<a href="#operand" title="Operand">Operand</a>: <i>String</i>
</pre>

## Properties

#### Or

_Required_: No

_Type_: List of <a href="predicate.md">Predicate</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### And

_Required_: No

_Type_: List of <a href="predicate.md">Predicate</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Field

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Operator

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Operand

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
