# NodeScript Example Mod

This example mod demonstrates how to use the owo-lib NodeScript visual scripting system to create event-driven scripts without writing code.

## Getting Started

1. Run the `NodeScript Example Client` run configuration
2. Press **M** (or **N** from owo-lib's default keybinding) to open the NodeScript editor
3. Create your visual script by adding and connecting nodes

## Example: Block Chat Messages Containing "spam"

This example creates a script that blocks any chat message containing the word "spam":

### Step-by-step

1. **Open the editor** - Press N in-game
2. **Add an event node** - From the "Events" category in the left panel, click "Allow Chat Event"
3. **Add a string check** - From the "Logic" category, click "String Includes"
4. **Connect the message** - Click the "Message" port on the Allow Chat Event node, then click the "String" port on String Includes
5. **Add a search term** - From the "Constants" category, click "String Constant"
6. **Set the value** - Select the String Constant node and enter "spam" in the Properties panel
7. **Connect the search** - Connect String Constant's "Value" to String Includes' "Search"
8. **Add NOT gate** - From "Logic", add a "NOT" node (we want to block matches, not allow them)
9. **Connect the logic** - Connect String Includes' "Result" to NOT's "Value"
10. **Connect the result** - Connect NOT's "Result" to Allow Chat Event's "Result"
11. **Save and activate** - Click "Save" to save to disk, then "Execute" to activate

Now any message containing "spam" will be blocked!

## Available Nodes

### Events
- **Allow Chat Event** - Fires when a chat message is received. Outputs the message string, accepts a boolean to allow/block.
- **HUD Render Event** - Fires every frame during HUD rendering.
- **Client Tick Event** - Fires every client tick.
- **Key Press Event** - Fires when a key is pressed.

### Logic
- **String Includes** - Checks if a string contains a substring (String.contains)
- **AND** - Boolean AND operation
- **OR** - Boolean OR operation
- **NOT** - Boolean NOT operation
- **Number Compare** - Compares two numbers (equals, less than, greater than)

### Constants
- **String Constant** - A configurable string value
- **Boolean Constant** - A configurable true/false value
- **Number Constant** - A configurable number value

### Actions
- **Print to Chat** - Prints a message to the chat

## Saving and Loading

Scripts are saved as JSON files in the `nodescripts/` directory in your Minecraft game folder. They are automatically loaded when the game starts.

## API Usage

You can also programmatically open the editor from your mod:

```java
import io.wispforest.owo.nodescript.ui.NodeEditorScreen;
import net.minecraft.client.Minecraft;

// Open the editor
Minecraft.getInstance().setScreen(new NodeEditorScreen());
```
