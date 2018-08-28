package seq;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;

import java.util.Collections;
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
        Seq<Seq<Alignment>> matches = seq(segments)
                .map(seg -> seq(alignmentsSoFar.get(seg.getReadId())).filter(al -> al.intersects(seg)));
        List<Tuple3<UUID, Alignment, Alignment>> newAlignments = zip(segments, matches)
                .flatMap(t -> seq(t.v2).map(alignment -> tuple(t.v1.getReadId(), alignment, alignment.mergeIn(t.v1))))
                .toList();
        newAlignments.forEach(t -> {
            alignmentsSoFar.remove(t.v1, t.v2);
            alignmentsSoFar.put(t.v1, t.v3);
        });

        return seq(segments).map(AlignedReadSegment::getReadId).map(alignmentsSoFar::get).flatMap(Seq::seq).toList();
    }
}
