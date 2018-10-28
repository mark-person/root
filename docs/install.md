



# eclipse 设置
```
# 基本设置(UTF-8, Unix)
Preferences -> General -> Workspace:Text file encodeing:UTF-8, New text file line delimiter:Unix

# properties乱码
Preferences -> General -> Content Types:Java Properties File:Default encoding:UTF-8

# pom.xml的execution标签错误
菜单:Window->Perferences->Maven->Lifecycle Mapping插入内容
<?xml version="1.0" encoding="UTF-8"?>
<lifecycleMappingMetadata>
	<pluginExecutions>   
 		<pluginExecution>
			<pluginExecutionFilter>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <goals>
                    <goal>unpack</goal>
                </goals>
                <versionRange>[1.3,)</versionRange>
            </pluginExecutionFilter>
            <action>
                <ignore />
            </action>
		</pluginExecution>        
	</pluginExecutions>
</lifecycleMappingMetadata>

# 去掉 WARNING: An illegal reflective access operation has occurred
${jrebel_args} --add-opens java.base/java.lang=ALL-UNNAMED

```









