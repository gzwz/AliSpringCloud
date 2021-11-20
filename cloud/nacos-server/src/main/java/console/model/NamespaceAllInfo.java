/* Location: file://E:/workspace/project/nacos-server-2.0.3.tar/nacos/target/nacos-server/BOOT-INF/classes/com/alibaba/nacos/console/model/NamespaceAllInfo.class
 * Java language version: 8
 * Class File: 52.0
 * JD-Core Version: 1.1.3
 */

package console.model;





















public class NamespaceAllInfo
    extends Namespace
{
    private String namespaceDesc;
    
    public String getNamespaceDesc() {
        return this.namespaceDesc;
    }
    
    public void setNamespaceDesc(String namespaceDesc) {
        this.namespaceDesc = namespaceDesc;
    }

    
    public NamespaceAllInfo() {}

    
    public NamespaceAllInfo(String namespace, String namespaceShowName, int quota, int configCount, int type, String namespaceDesc) {
        super(namespace, namespaceShowName, quota, configCount, type);
        this.namespaceDesc = namespaceDesc;
    }
}
