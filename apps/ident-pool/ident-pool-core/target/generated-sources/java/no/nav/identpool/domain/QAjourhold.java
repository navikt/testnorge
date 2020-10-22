package no.nav.identpool.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QAjourhold is a Querydsl query type for Ajourhold
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QAjourhold extends EntityPathBase<Ajourhold> {

    private static final long serialVersionUID = 1584992786L;

    public static final QAjourhold ajourhold = new QAjourhold("ajourhold");

    public final StringPath feilmelding = createString("feilmelding");

    public final NumberPath<Long> identity = createNumber("identity", Long.class);

    public final DateTimePath<java.time.LocalDateTime> sistOppdatert = createDateTime("sistOppdatert", java.time.LocalDateTime.class);

    public final EnumPath<javax.batch.runtime.BatchStatus> status = createEnum("status", javax.batch.runtime.BatchStatus.class);

    public QAjourhold(String variable) {
        super(Ajourhold.class, forVariable(variable));
    }

    public QAjourhold(Path<? extends Ajourhold> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAjourhold(PathMetadata metadata) {
        super(Ajourhold.class, metadata);
    }

}

