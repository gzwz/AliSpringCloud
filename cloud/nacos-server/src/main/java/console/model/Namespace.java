/* Location: file://E:/workspace/project/nacos-server-2.0.3.tar/nacos/target/nacos-server/BOOT-INF/classes/com/alibaba/nacos/console/model/Namespace.class
 * Java language version: 8
 * Class File: 52.0
 * JD-Core Version: 1.1.3
 */

package console.model;





























public class Namespace
{
    private String namespace;
    private String namespaceShowName;
    private int quota;
    private int configCount;
    private int type;
    
    public String getNamespaceShowName() {
        return this.namespaceShowName;
    }
    
    public void setNamespaceShowName(String namespaceShowName) {
        this.namespaceShowName = namespaceShowName;
    }
    
    public String getNamespace() {
        return this.namespace;
    }
    
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    
    public Namespace() {}
    
    public Namespace(String namespace, String namespaceShowName) {
        this.namespace = namespace;
        this.namespaceShowName = namespaceShowName;
    }
    
    public Namespace(String namespace, String namespaceShowName, int quota, int configCount, int type) {
        this.namespace = namespace;
        this.namespaceShowName = namespaceShowName;
        this.quota = quota;
        this.configCount = configCount;
        this.type = type;
    }
    
    public int getQuota() {
        return this.quota;
    }
    
    public void setQuota(int quota) {
        this.quota = quota;
    }
    
    public int getConfigCount() {
        return this.configCount;
    }
    
    public void setConfigCount(int configCount) {
        this.configCount = configCount;
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setType(int type) {
        this.type = type;
    }
}
