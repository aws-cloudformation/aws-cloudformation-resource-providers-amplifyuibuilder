# AWS::AmplifyUIBuilder::Theme

Definition of AWS::AmplifyUIBuilder::Theme Resource Type

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::AmplifyUIBuilder::Theme",
    "Properties" : {
        "<a href="#name" title="Name">Name</a>" : <i>String</i>,
        "<a href="#overrides" title="Overrides">Overrides</a>" : <i>[ <a href="themevalues.md">ThemeValues</a>, ... ]</i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i><a href="tags.md">Tags</a></i>,
        "<a href="#values" title="Values">Values</a>" : <i>[ <a href="themevalues.md">ThemeValues</a>, ... ]</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::AmplifyUIBuilder::Theme
Properties:
    <a href="#name" title="Name">Name</a>: <i>String</i>
    <a href="#overrides" title="Overrides">Overrides</a>: <i>
      - <a href="themevalues.md">ThemeValues</a></i>
    <a href="#tags" title="Tags">Tags</a>: <i><a href="tags.md">Tags</a></i>
    <a href="#values" title="Values">Values</a>: <i>
      - <a href="themevalues.md">ThemeValues</a></i>
</pre>

## Properties

#### Name

_Required_: No

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>255</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Overrides

_Required_: No

_Type_: List of <a href="themevalues.md">ThemeValues</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

_Required_: No

_Type_: <a href="tags.md">Tags</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Values

_Required_: No

_Type_: List of <a href="themevalues.md">ThemeValues</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### AppId

Returns the <code>AppId</code> value.

#### CreatedAt

Returns the <code>CreatedAt</code> value.

#### EnvironmentName

Returns the <code>EnvironmentName</code> value.

#### Id

Returns the <code>Id</code> value.

#### ModifiedAt

Returns the <code>ModifiedAt</code> value.
