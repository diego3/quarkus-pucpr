package org.acme.filters;

import java.util.HashMap;
import java.util.stream.Stream;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.acme.exception.Forbidden;
import org.acme.login.Login;

import io.quarkus.logging.Log;

@Provider
public class SecurityRBAC implements ContainerRequestFilter {

    private String[] trustedPaths = new String[] {
        "sisrh/rest/loginunico/jwt",
        "sisrh/rest/user"
    };
    
    private HashMap<String, HashMap<String, String>> rbac = new HashMap<>();
    
    private void initRBAC() {
        //if (rbac.isEmpty()) {
            var admin = new HashMap<String, String>();
            admin.put("sisrh/rest/empregado", "GET,POST,PUT,DELETE");
            admin.put("sisrh/rest/user", "GET,POST,PUT,DELETE");
            admin.put("sistema", "GET");
            admin.put("sisrh/rest/solicitacao", "GET,POST,PUT,DELETE");

            var user = new HashMap<String, String>();
            user.put("sisrh/rest/empregado", "GET");
            user.put("sistema", "GET");
            user.put("sisrh/rest/solicitacao", "POST,GET");

            var gestor = new HashMap<String, String>();
            gestor.put("sisrh/rest/empregado", "GET,POST,PUT,DELETE");
            gestor.put("sisrh/rest/user", "GET");
            admin.put("sisrh/rest/solicitacao", "GET");

            rbac.put("ADMIN", admin);
            rbac.put("USER", user);
            rbac.put("GESTOR", gestor);
        //}
    }

    private boolean isTrusted(String path) {
       return Stream.of(trustedPaths).anyMatch(trust -> trust.contains(path));
    }

    private boolean canAccess(String profile, String resource, String method) {
        initRBAC();

        HashMap<String, String> perfil = rbac.get(profile);
        if (profile == null || profile.isEmpty()) {
            return false;
        }

        if (!perfil.containsKey(resource)) {
            return false;
        }

        var methods = perfil.get(resource);
        if (methods.isEmpty()) {
            return false;
        }

        if (methods.contains(",")) {
            if (Stream.of(methods.split(",")).noneMatch(m -> m.equalsIgnoreCase(method))) {
                return false;
            }
        } else if (!methods.equalsIgnoreCase(method)){
            return false;
        }

        return true;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        var requestUri = requestContext.getUriInfo().getMatchedURIs().get(0);
        var isGood = this.isTrusted(requestUri);
        Log.info("request uri = "+requestUri+ " trusted = "+isGood + " method = "+ requestContext.getMethod());
        
        if (!isGood) {
            var jwt = requestContext.getHeaderString("jwt");
            if (jwt == null || "".equals(jwt)) {
                throw new Forbidden("Acesso não autorizado, token não informado");
            }

            // validar o token
            var claims = Login.validateToken(jwt);
            var profile = claims.getBody().get("perfil").toString();

            if (!canAccess(profile, requestUri, requestContext.getMethod())) {
                throw new Forbidden("Acesso não autorizado, permissão negada");
            }
        }
    }
    
    
}
