package sn.awi.redis.constants;

public interface IConstantsPublicationError {

    /**
     * <p>
     * Error return when you try to publish a dcx without a dcxVersion related to.
     * </p>
     * Parameters:
     * <ul>
     * <li>id of the dcx</li>
     * <li>id of the version of the dcx</li>
     * </ul>
     */
    String DCX_VERSION_REQUIRED = "dcxVersionMandatoryForPublication";
}
