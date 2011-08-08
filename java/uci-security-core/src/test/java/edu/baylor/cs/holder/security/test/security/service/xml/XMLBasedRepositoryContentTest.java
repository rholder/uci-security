package edu.baylor.cs.holder.security.test.security.service.xml;

import java.io.InputStream;

import edu.baylor.cs.holder.security.service.AccessService;
import edu.baylor.cs.holder.security.service.SecurityRepository;
import edu.baylor.cs.holder.security.service.impl.Dom4jSecurityRepository;
import edu.baylor.cs.holder.security.service.impl.RepositoryAccessServiceImpl;
import edu.baylor.cs.holder.security.test.security.service.AbstractRepositoryContentTestBase;

public class XMLBasedRepositoryContentTest extends AbstractRepositoryContentTestBase {

    protected AccessService getAccessService() throws Exception {

        // bootstrap an access service
        if (accessService == null) {
            RepositoryAccessServiceImpl RepositoryAccessService = new RepositoryAccessServiceImpl();
            RepositoryAccessService.update(getSecurityRepository());
            accessService = RepositoryAccessService;
        }

        return accessService;
    }

    protected SecurityRepository getSecurityRepository() throws Exception {
        
        // populate a SecurityRepository
        if (securityRepository == null) {
            InputStream xmlInput = AbstractRepositoryContentTestBase.class.getResourceAsStream("/security-test.xml");
            Dom4jSecurityRepository dom4jSecurityRepository = new Dom4jSecurityRepository();
            dom4jSecurityRepository.populate(xmlInput);
            xmlInput.close();
            securityRepository = dom4jSecurityRepository;
        }
        
        return securityRepository;
    }
}
