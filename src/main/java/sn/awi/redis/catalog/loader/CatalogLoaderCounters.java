package sn.awi.redis.catalog.loader;

public class CatalogLoaderCounters {

    private static int nbBasePropertyNullDCX = 0;
    private static int nbBasePropertyNullO = 0;
    private static Integer nbBasePropertyNullMO = 0;

    private CatalogLoaderCounters() {}

    public static Integer getNbBasePropertyNullDCX() {
        return CatalogLoaderCounters.nbBasePropertyNullDCX;
    }

    public static Integer getNbBasePropertyNullMO() {
        return CatalogLoaderCounters.nbBasePropertyNullMO;
    }

    public static Integer getNbBasePropertyNullO() {
        return CatalogLoaderCounters.nbBasePropertyNullO;
    }

    public static void incrementNbBasePropertyNullDCX() {
        nbBasePropertyNullDCX++;
    }

    public static void incrementNbBasePropertyNullO() {
        nbBasePropertyNullO++;
    }

    public static void incrementNbBasePropertyNullMO() {
        nbBasePropertyNullO++;
    }

}
