# AWS::AmplifyUIBuilder::Component ComponentChild

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#componenttype" title="ComponentType">ComponentType</a>" : <i>String</i>,
    "<a href="#children" title="Children">Children</a>" : <i>[ <a href="componentchild.md">ComponentChild</a>, ... ]</i>,
    "<a href="#properties" title="Properties">Properties</a>" : <i><a href="componentchild-properties.md">Properties</a></i>,
    "<a href="#name" title="Name">Name</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#componenttype" title="ComponentType">ComponentType</a>: <i>String</i>
<a href="#children" title="Children">Children</a>: <i>
      - <a href="componentchild.md">ComponentChild</a></i>
<a href="#properties" title="Properties">Properties</a>: <i><a href="componentchild-properties.md">Properties</a></i>
<a href="#name" title="Name">Name</a>: <i>String</i>
</pre>

## Properties

#### ComponentType

_Required_: Yes

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Children

_Required_: No

_Type_: List of <a href="componentchild.md">ComponentChild</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Properties

_Required_: Yes

_Type_: <a href="componentchild-properties.md">Properties</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Name

_Required_: Yes

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
