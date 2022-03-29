package sn.awi.redis.constants;

import java.util.Arrays;
import java.util.List;

/**
 * Created by kjak on 2016-09-19.
 */
public enum PublicationInternalStatusEnum {
    NEW(PublicationDisplayStatusEnum.PLANNED),
    CANCELLED(PublicationDisplayStatusEnum.CANCELLED),
    GENERATED(PublicationDisplayStatusEnum.PLANNED),
    PENDING(PublicationDisplayStatusEnum.PLANNED),
    FAILED(PublicationDisplayStatusEnum.FAILED),
    ERROR(PublicationDisplayStatusEnum.FAILED),
    PUBLISHED(PublicationDisplayStatusEnum.PUBLISHED),
    SUCCESS(PublicationDisplayStatusEnum.PUBLISHED);

    public static final List<PublicationInternalStatusEnum> IN_PROGRESS = Arrays.asList(NEW, GENERATED, PENDING);

    public static final List<PublicationInternalStatusEnum> DONE = Arrays.asList(FAILED, ERROR, SUCCESS, PUBLISHED, CANCELLED);

    private PublicationDisplayStatusEnum displayStatus;

    PublicationInternalStatusEnum(PublicationDisplayStatusEnum displayStatus) {
        this.displayStatus = displayStatus;
    }

    public PublicationDisplayStatusEnum getDisplayStatus() {
        return displayStatus;
    }
}
