# AWS::AmplifyUIBuilder::Form FileUploaderFieldConfig

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#accesslevel" title="AccessLevel">AccessLevel</a>" : <i>String</i>,
    "<a href="#acceptedfiletypes" title="AcceptedFileTypes">AcceptedFileTypes</a>" : <i>[ String, ... ]</i>,
    "<a href="#showthumbnails" title="ShowThumbnails">ShowThumbnails</a>" : <i>Boolean</i>,
    "<a href="#isresumable" title="IsResumable">IsResumable</a>" : <i>Boolean</i>,
    "<a href="#maxfilecount" title="MaxFileCount">MaxFileCount</a>" : <i>Double</i>,
    "<a href="#maxsize" title="MaxSize">MaxSize</a>" : <i>Double</i>
}
</pre>

### YAML

<pre>
<a href="#accesslevel" title="AccessLevel">AccessLevel</a>: <i>String</i>
<a href="#acceptedfiletypes" title="AcceptedFileTypes">AcceptedFileTypes</a>: <i>
      - String</i>
<a href="#showthumbnails" title="ShowThumbnails">ShowThumbnails</a>: <i>Boolean</i>
<a href="#isresumable" title="IsResumable">IsResumable</a>: <i>Boolean</i>
<a href="#maxfilecount" title="MaxFileCount">MaxFileCount</a>: <i>Double</i>
<a href="#maxsize" title="MaxSize">MaxSize</a>: <i>Double</i>
</pre>

## Properties

#### AccessLevel

_Required_: Yes

_Type_: String

_Allowed Values_: <code>public</code> | <code>protected</code> | <code>private</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### AcceptedFileTypes

_Required_: Yes

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ShowThumbnails

_Required_: No

_Type_: Boolean

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### IsResumable

_Required_: No

_Type_: Boolean

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### MaxFileCount

_Required_: No

_Type_: Double

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### MaxSize

_Required_: No

_Type_: Double

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
