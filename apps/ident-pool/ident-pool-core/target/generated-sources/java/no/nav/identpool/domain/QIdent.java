package no.nav.identpool.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QIdent is a Querydsl query type for Ident
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QIdent extends EntityPathBase<Ident> {

    private static final long serialVersionUID = 1646762560L;

    public static final QIdent ident = new QIdent("ident");

    public final BooleanPath finnesHosSkatt = createBoolean("finnesHosSkatt");

    public final DatePath<java.time.LocalDate> foedselsdato = createDate("foedselsdato", java.time.LocalDate.class);

    public final NumberPath<Long> identity = createNumber("identity", Long.class);

    public final EnumPath<Identtype> identtype = createEnum("identtype", Identtype.class);

    public final EnumPath<Kjoenn> kjoenn = createEnum("kjoenn", Kjoenn.class);

    public final StringPath personidentifikator = createString("personidentifikator");

    public final EnumPath<Rekvireringsstatus> rekvireringsstatus = createEnum("rekvireringsstatus", Rekvireringsstatus.class);

    public final StringPath rekvirertAv = createString("rekvirertAv");

    public QIdent(String variable) {
        super(Ident.class, forVariable(variable));
    }

    public QIdent(Path<? extends Ident> path) {
        super(path.getType(), path.getMetadata());
    }

    public QIdent(PathMetadata metadata) {
        super(Ident.class, metadata);
    }

}

