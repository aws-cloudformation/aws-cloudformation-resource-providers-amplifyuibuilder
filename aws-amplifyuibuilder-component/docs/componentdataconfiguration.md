# AWS::AmplifyUIBuilder::Component ComponentDataConfiguration

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#model" title="Model">Model</a>" : <i>String</i>,
    "<a href="#sort" title="Sort">Sort</a>" : <i>[ <a href="sortproperty.md">SortProperty</a>, ... ]</i>,
    "<a href="#identifiers" title="Identifiers">Identifiers</a>" : <i>[ String, ... ]</i>,
    "<a href="#predicate" title="Predicate">Predicate</a>" : <i><a href="predicate.md">Predicate</a></i>
}
</pre>

### YAML

<pre>
<a href="#model" title="Model">Model</a>: <i>String</i>
<a href="#sort" title="Sort">Sort</a>: <i>
      - <a href="sortproperty.md">SortProperty</a></i>
<a href="#identifiers" title="Identifiers">Identifiers</a>: <i>
      - String</i>
<a href="#predicate" title="Predicate">Predicate</a>: <i><a href="predicate.md">Predicate</a></i>
</pre>

## Properties

#### Model

_Required_: Yes

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Sort

_Required_: No

_Type_: List of <a href="sortproperty.md">SortProperty</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Identifiers

_Required_: No

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Predicate

_Required_: No

_Type_: <a href="predicate.md">Predicate</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
