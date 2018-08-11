package seq;

public class SourceImageLocation {
    private final int imageId;
    private final int locationWithinImage;

    public SourceImageLocation(Integer imageId, Integer locationWithinImage) {
        this.imageId = imageId;
        this.locationWithinImage = locationWithinImage;
    }

    public int getLocationWithinImage() {
        return locationWithinImage;
    }
}
