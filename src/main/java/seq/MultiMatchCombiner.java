package seq;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;

import java.util.List;
import java.util.UUID;

import static org.jooq.lambda.Seq.seq;
import static org.jooq.lambda.Seq.zip;
import static org.jooq.lambda.tuple.Tuple.tuple;

class MultiMatchCombiner {
    private final Multimap<UUID, Alignment> alignmentsSoFar;

    MultiMatchCombiner(){
        alignmentsSoFar = HashMultimap.create();
    }

    List<Alignment> combineWithPrevious(List<AlignedReadSegment> segments) {
        Seq<List<Alignment>> alignments = seq(segments).map(s -> seq(alignmentsSoFar.get(s.getReadId())).toList());
        List<Tuple2<AlignedReadSegment, List<Alignment>>> zip = zip(segments, alignments).toList();
        Seq<Tuple2<AlignedReadSegment, Alignment>> matches = seq(zip).flatMap(t -> seq(t.v2).map(a -> tuple(t.v1, a)));

        Seq<AlignedReadSegment> nonMatches = seq(zip).filter(t -> t.v2.isEmpty()).map(Tuple2::v1);
        nonMatches.forEach(nonMatch -> alignmentsSoFar.put(nonMatch.getReadId(),
                new Alignment(nonMatch.getReadId(), nonMatch.getStart(), nonMatch.getEnd())));

        Seq<Tuple3<UUID, Alignment, Alignment>> newAlignments = matches
                .map(t -> tuple(t.v1.getReadId(), t.v2, t.v2.mergeIn(t.v1)));
        newAlignments.forEach(t -> {
            alignmentsSoFar.remove(t.v1, t.v2);
            alignmentsSoFar.put(t.v1, t.v3);
        });

        return seq(segments).map(AlignedReadSegment::getReadId).map(alignmentsSoFar::get).flatMap(Seq::seq).toList();
    }
}
