package no.nav.registre.medl.database.multitenancy;

public class TenantContext {

    private TenantContext() {
    }

    private static ThreadLocal<String> tenant = new ThreadLocal<>();

    public static String getTenant() {
        return tenant.get();
    }

    public static void setTenant(String tenantName) {
        tenant.set(tenantName);
    }

}