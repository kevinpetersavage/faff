package seq;

import java.util.UUID;

public class SourceImageLocation {
    private final int imageId;
    private final UUID locationWithinImage;

    public SourceImageLocation(Integer imageId, UUID locationWithinImage) {
        this.imageId = imageId;
        this.locationWithinImage = locationWithinImage;
    }

    public UUID getLocationWithinImage() {
        return locationWithinImage;
    }

    public int getImageId() {
        return imageId;
    }
}
