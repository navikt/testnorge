package no.nav.identpool.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QWhitelist is a Querydsl query type for Whitelist
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QWhitelist extends EntityPathBase<Whitelist> {

    private static final long serialVersionUID = 214470807L;

    public static final QWhitelist whitelist = new QWhitelist("whitelist");

    public final StringPath fnr = createString("fnr");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QWhitelist(String variable) {
        super(Whitelist.class, forVariable(variable));
    }

    public QWhitelist(Path<? extends Whitelist> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWhitelist(PathMetadata metadata) {
        super(Whitelist.class, metadata);
    }

}

