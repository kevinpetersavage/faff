public class SingleRead {
    private final char nucleotide;
    private final Integer imageId;
    private final Integer imageLocation;

    public SingleRead(char nucleotide, Integer imageId, Integer imageLocation) {
        this.nucleotide = nucleotide;
        this.imageId = imageId;
        this.imageLocation = imageLocation;
    }
}
