To clone the repo run `git clone https://github.com/YHDiamond/GeyserUpdater.git` in your command line or terminal.

You can build locally and install dependencies using maven. `mvn clean install` to install all require dependencies and `mvn clean package` to build. The built jar will be located in the target folder.

Please follow the guidelines below for any code you wish to contribute, we are always open for PRs!

```java
public class LongClassName {
    private static final int AIR_ITEM = 0; // Static item names should be capitalized

    public Int2IntMap items = new Int2IntOpenHashMap(); // Use the interface as the class type but initialize with the implementation.

    public int nameWithMultipleWords = 0;

    /**
    * Javadoc comment to explain what a function does.
    */
    @RandomAnnotation(stuff = true, moreStuff = "might exist")
    public void applyStuff() {
        Variable variable = new Variable();
        Variable otherVariable = new Variable();

        if (condition) {
	        // Do stuff.
        } else if (anotherCondition) {
	    	// Do something else.
        }

        switch (value) {
            case 0:
                stuff();
                break;
            case 1:
                differentStuff();
                break;
        }
    }
}
```


If you are making a change to the README.md, fixing a typo, or changing code format, please append `[ci skip]` to the end of your commit message so it won't trigger a new build.
If you need help with code for this you can go to [our Discord](https://discord.gg/xXzzdAXa2b). Don't ping anyone that doesn't have the "Pingable" role!
