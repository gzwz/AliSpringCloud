/* Location: file://E:/workspace/project/nacos-server-2.0.3.tar/nacos/target/nacos-server/BOOT-INF/classes/com/alibaba/nacos/nacos.console/enums/NamespaceTypeEnum.class
 * Java language version: 8
 * Class File: 52.0
 * JD-Core Version: 1.1.3
 */

package nacos.console.enums;

public enum NamespaceTypeEnum
{
    GLOBAL(0, "Global configuration"),


    PRIVATE(1, "Default private namespace"),

    CUSTOM(2, "Custom namespace");


    private final int type;


    
    private final String description;


    
    NamespaceTypeEnum(int type, String description) {
        this.type = type;
        this.description = description;
    }
    
    public int getType() {
        return this.type;
    }
    
    public String getDescription() {
        return this.description;
    }
}
