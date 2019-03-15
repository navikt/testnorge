package no.nav.registre.tp.database.multitenancy;

public class TenantContext {

    private static ThreadLocal<String> tenant = new ThreadLocal<>();

    public static String getTenant() {
        return tenant.get();
    }

    public static void setTenant(String tenantName) {
        tenant.set(tenantName);
    }

}