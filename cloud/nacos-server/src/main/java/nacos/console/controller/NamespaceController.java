/* Location: file://E:/workspace/project/nacos-server-2.0.3.tar/nacos/target/nacos-server/BOOT-INF/classes/com/alibaba/nacos/nacos.console/controller/NamespaceController.class
 * Java language version: 8
 * Class File: 52.0
 * JD-Core Version: 1.1.3
 */

package nacos.console.controller;

import com.alibaba.nacos.auth.annotation.Secured;
import com.alibaba.nacos.auth.common.ActionTypes;
import com.alibaba.nacos.common.model.RestResult;
import com.alibaba.nacos.common.model.RestResultUtils;
import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.config.server.model.TenantInfo;
import com.alibaba.nacos.config.server.service.repository.PersistService;
import com.alibaba.nacos.console.enums.NamespaceTypeEnum;
import com.alibaba.nacos.console.model.Namespace;
import com.alibaba.nacos.console.model.NamespaceAllInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

























@RestController
@RequestMapping({"/v1/console/namespaces"})
public class NamespaceController
{
    @Autowired
    private PersistService persistService;
    private final Pattern namespaceIdCheckPattern = Pattern.compile("^[\\w-]+");

    
    private static final int NAMESPACE_ID_MAX_LENGTH = 128;

    
    private static final String DEFAULT_NAMESPACE = "public";

    
    private static final int DEFAULT_QUOTA = 200;

    
    private static final String DEFAULT_CREATE_SOURCE = "nacos";

    
    private static final String DEFAULT_NAMESPACE_SHOW_NAME = "Public";

    
    private static final String DEFAULT_NAMESPACE_DESCRIPTION = "Public Namespace";

    
    private static final String DEFAULT_TENANT = "";
    
    private static final String DEFAULT_KP = "1";

    
    @GetMapping
    public RestResult<List<Namespace>> getNamespaces(HttpServletRequest request, HttpServletResponse response) {
        List<TenantInfo> tenantInfos = this.persistService.findTenantByKp("1");
        
        Namespace namespace0 = new Namespace("", "public", 200, this.persistService.configInfoCount(""), NamespaceTypeEnum.GLOBAL.getType());
        List<Namespace> namespaces = new ArrayList<>();
        namespaces.add(namespace0);
        for (TenantInfo tenantInfo : tenantInfos) {
            int configCount = this.persistService.configInfoCount(tenantInfo.getTenantId());
            
            Namespace namespaceTmp = new Namespace(tenantInfo.getTenantId(), tenantInfo.getTenantName(), 200, configCount, NamespaceTypeEnum.CUSTOM.getType());
            namespaces.add(namespaceTmp);
        } 
        return RestResultUtils.success(namespaces);
    }










    
    @GetMapping(params = {"show=all"})
    public NamespaceAllInfo getNamespace(HttpServletRequest request, HttpServletResponse response, @RequestParam("namespaceId") String namespaceId) {
        if (StringUtils.isBlank(namespaceId)) {
            return new NamespaceAllInfo(namespaceId, "Public", 200, this.persistService.configInfoCount(""), NamespaceTypeEnum.GLOBAL
                    .getType(), "Public Namespace");
        }
        TenantInfo tenantInfo = this.persistService.findTenantByKp("1", namespaceId);
        int configCount = this.persistService.configInfoCount(namespaceId);
        return new NamespaceAllInfo(namespaceId, tenantInfo.getTenantName(), 200, configCount, NamespaceTypeEnum.CUSTOM.getType(), tenantInfo
                .getTenantDesc());
    }













    
    @PostMapping
    @Secured(resource = "console/namespaces", action = ActionTypes.WRITE)
    public Boolean createNamespace(HttpServletRequest request, HttpServletResponse response, @RequestParam("customNamespaceId") String namespaceId, @RequestParam("namespaceName") String namespaceName, @RequestParam(value = "namespaceDesc", required = false) String namespaceDesc) {
        if (StringUtils.isBlank(namespaceId)) {
            namespaceId = UUID.randomUUID().toString();
        } else {
            namespaceId = namespaceId.trim();
            if (!this.namespaceIdCheckPattern.matcher(namespaceId).matches()) {
                return Boolean.valueOf(false);
            }
            if (namespaceId.length() > 128) {
                return Boolean.valueOf(false);
            }
            if (this.persistService.tenantInfoCountByTenantId(namespaceId) > 0) {
                return Boolean.valueOf(false);
            }
        } 
        this.persistService.insertTenantInfoAtomic("1", namespaceId, namespaceName, namespaceDesc, "nacos", 
                System.currentTimeMillis());
        return Boolean.valueOf(true);
    }






    
    @GetMapping(params = {"checkNamespaceIdExist=true"})
    public Boolean checkNamespaceIdExist(@RequestParam("customNamespaceId") String namespaceId) {
        if (StringUtils.isBlank(namespaceId)) {
            return Boolean.valueOf(false);
        }
        return Boolean.valueOf((this.persistService.tenantInfoCountByTenantId(namespaceId) > 0));
    }











    
    @PutMapping
    @Secured(resource = "console/namespaces", action = ActionTypes.WRITE)
    public Boolean editNamespace(@RequestParam("namespace") String namespace, @RequestParam("namespaceShowName") String namespaceShowName, @RequestParam(value = "namespaceDesc", required = false) String namespaceDesc) {
        this.persistService.updateTenantNameAtomic("1", namespace, namespaceShowName, namespaceDesc);
        return Boolean.valueOf(true);
    }









    
    @DeleteMapping
    @Secured(resource = "console/namespaces", action = ActionTypes.WRITE)
    public Boolean deleteConfig(HttpServletRequest request, HttpServletResponse response, @RequestParam("namespaceId") String namespaceId) {
        this.persistService.removeTenantInfoAtomic("1", namespaceId);
        return Boolean.valueOf(true);
    }
}
