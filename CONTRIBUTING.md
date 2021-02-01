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
If you need help with code for this you can go to [Geyser discord](https://discord.geysermc.org) and ping @YHDiamond or @Jens. Do NOT ping anybody else for this. 
