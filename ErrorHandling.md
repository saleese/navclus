### `java.lang.OutOfMemoryError: PermGen space` ###

You could encounter the error. Edit eclipse.ini to modify or add the following lines below -vmargs.
` -Xms256m `
` -Xmx512m `
` -XX:MaxPermSize=512m `

http://stackoverflow.com/questions/3743992/java-lang-outofmemoryerror-permgen-space

### `Exception in thread "Text Viewer Hover Presenter"` ###

If you find the Eclipse crashes, you may have this error. Sorry for the inconvenience, but please, turn off the hovering function. Go to Window - Preferences; select Java / Editor / Howers on left hand side. You'll have to uncheck the Combined Hover option on the right

http://stackoverflow.com/questions/1540036/how-to-turn-off-the-javadoc-hover-in-eclipse-or-selectively-enable-it