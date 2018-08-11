package seq;

public class SingleRead {
    private final char nucleotide;
    private final SourceImageLocation sourceImageLocation;

    public SingleRead(int nucleotide, SourceImageLocation sourceImageLocation) {
        this((char)nucleotide, sourceImageLocation);
    }

    public SingleRead(char nucleotide, SourceImageLocation sourceImageLocation) {
        this.nucleotide = nucleotide;
        this.sourceImageLocation = sourceImageLocation;
    }

    public char getNucleotide() {
        return nucleotide;
    }

    public int getImageId() {
        return sourceImageLocation.getImageId();
    }

    public int getLocationWithImage() {
        return sourceImageLocation.getLocationWithinImage();
    }
}
