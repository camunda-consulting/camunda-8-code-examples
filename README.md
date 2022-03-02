# zeebe-snippets

This is your place for code snippets that rely on Zeebe.

Please make sure you have a meaningful name for your folder that gives a first idea what your snippet is about.

Please include a README that explains:
- Which version was this example built with?
- What is your snippet about?
- How is it developed and built?
- Important additional information

If a snippet is still relevant in the future, please ensure that it gets upgraded.

Also, please make sure you do not commit useless/private stuff like:
- build artifacts
- cluster credentials
- IDE-specific files

Tip: When using Spring-Boot, put this in your `application.yaml`:

````yaml
spring:
  profiles:
    active: dev
````
Then, you can create another file next to it called `application-dev.yaml` that contains your private properties. This file will be ignored automatically.


To ignore them, please append rules to the `.gitignore` in root.
