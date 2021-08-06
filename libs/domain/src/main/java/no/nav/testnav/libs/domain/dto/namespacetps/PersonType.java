package no.nav.testnav.libs.domain.dto.namespacetps;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "personType",
        propOrder = { "meldingId", "personidTPS", "personIdent", "personIdentstatus", "personInfo", "personStatus", "navn", "spraak", "sivilstand", "statsborger", "dod", "telefon", "tknr", "spesReg", "tiltak",
                "egenansatt", "boadresse", "prioritertadresse", "geti", "foreldreansvar", "oppholdstillatelse", "gironummer", "tillegg", "utad", "post", "migrasjon", "vergemaal", "behov", "utlandbank", "utlandinfo" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class PersonType {

    @XmlElement(
            required = true
    )
    protected BigInteger meldingId;
    @XmlElement(
            required = true
    )
    protected BigInteger personidTPS;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<PersonIdentType> personIdent;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<PersonIdentStatusType> personIdentstatus;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<PersonInfoType> personInfo;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<PersonStatusType> personStatus;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<NavnType> navn;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<SpraakType> spraak;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<SivilstandType> sivilstand;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<StatsborgerType> statsborger;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<DodType> dod;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<TelefonType> telefon;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<TknrType> tknr;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<SpesRegType> spesReg;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<TiltakType> tiltak;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<EgenansattType> egenansatt;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<BoadresseType> boadresse;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<PrioritertadresseType> prioritertadresse;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<GetiType> geti;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<ForeldreansvarType> foreldreansvar;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<OppholdstillatelseType> oppholdstillatelse;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<GironummerType> gironummer;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<TilleggType> tillegg;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<UtadType> utad;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<PostType> post;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<MigrasjonType> migrasjon;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<VergeType> vergemaal;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<BehovType> behov;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<UtlandbankType> utlandbank;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<UtlandinfoType> utlandinfo;

    public PersonType withMeldingId(BigInteger value) {
        this.setMeldingId(value);
        return this;
    }

    public PersonType withPersonidTPS(BigInteger value) {
        this.setPersonidTPS(value);
        return this;
    }

    public PersonType withPersonIdent(PersonIdentType... values) {
        if (values != null) {
            PersonIdentType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                PersonIdentType value = var2[var4];
                this.getPersonIdent().add(value);
            }
        }

        return this;
    }

    public PersonType withPersonIdent(Collection<PersonIdentType> values) {
        if (values != null) {
            this.getPersonIdent().addAll(values);
        }

        return this;
    }

    public PersonType withPersonIdentstatus(PersonIdentStatusType... values) {
        if (values != null) {
            PersonIdentStatusType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                PersonIdentStatusType value = var2[var4];
                this.getPersonIdentstatus().add(value);
            }
        }

        return this;
    }

    public PersonType withPersonIdentstatus(Collection<PersonIdentStatusType> values) {
        if (values != null) {
            this.getPersonIdentstatus().addAll(values);
        }

        return this;
    }

    public PersonType withPersonInfo(PersonInfoType... values) {
        if (values != null) {
            PersonInfoType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                PersonInfoType value = var2[var4];
                this.getPersonInfo().add(value);
            }
        }

        return this;
    }

    public PersonType withPersonInfo(Collection<PersonInfoType> values) {
        if (values != null) {
            this.getPersonInfo().addAll(values);
        }

        return this;
    }

    public PersonType withPersonStatus(PersonStatusType... values) {
        if (values != null) {
            PersonStatusType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                PersonStatusType value = var2[var4];
                this.getPersonStatus().add(value);
            }
        }

        return this;
    }

    public PersonType withPersonStatus(Collection<PersonStatusType> values) {
        if (values != null) {
            this.getPersonStatus().addAll(values);
        }

        return this;
    }

    public PersonType withNavn(NavnType... values) {
        if (values != null) {
            NavnType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                NavnType value = var2[var4];
                this.getNavn().add(value);
            }
        }

        return this;
    }

    public PersonType withNavn(Collection<NavnType> values) {
        if (values != null) {
            this.getNavn().addAll(values);
        }

        return this;
    }

    public PersonType withSpraak(SpraakType... values) {
        if (values != null) {
            SpraakType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                SpraakType value = var2[var4];
                this.getSpraak().add(value);
            }
        }

        return this;
    }

    public PersonType withSpraak(Collection<SpraakType> values) {
        if (values != null) {
            this.getSpraak().addAll(values);
        }

        return this;
    }

    public PersonType withSivilstand(SivilstandType... values) {
        if (values != null) {
            SivilstandType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                SivilstandType value = var2[var4];
                this.getSivilstand().add(value);
            }
        }

        return this;
    }

    public PersonType withSivilstand(Collection<SivilstandType> values) {
        if (values != null) {
            this.getSivilstand().addAll(values);
        }

        return this;
    }

    public PersonType withStatsborger(StatsborgerType... values) {
        if (values != null) {
            StatsborgerType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                StatsborgerType value = var2[var4];
                this.getStatsborger().add(value);
            }
        }

        return this;
    }

    public PersonType withStatsborger(Collection<StatsborgerType> values) {
        if (values != null) {
            this.getStatsborger().addAll(values);
        }

        return this;
    }

    public PersonType withDod(DodType... values) {
        if (values != null) {
            DodType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                DodType value = var2[var4];
                this.getDod().add(value);
            }
        }

        return this;
    }

    public PersonType withDod(Collection<DodType> values) {
        if (values != null) {
            this.getDod().addAll(values);
        }

        return this;
    }

    public PersonType withTelefon(TelefonType... values) {
        if (values != null) {
            TelefonType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                TelefonType value = var2[var4];
                this.getTelefon().add(value);
            }
        }

        return this;
    }

    public PersonType withTelefon(Collection<TelefonType> values) {
        if (values != null) {
            this.getTelefon().addAll(values);
        }

        return this;
    }

    public PersonType withTknr(TknrType... values) {
        if (values != null) {
            TknrType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                TknrType value = var2[var4];
                this.getTknr().add(value);
            }
        }

        return this;
    }

    public PersonType withTknr(Collection<TknrType> values) {
        if (values != null) {
            this.getTknr().addAll(values);
        }

        return this;
    }

    public PersonType withSpesReg(SpesRegType... values) {
        if (values != null) {
            SpesRegType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                SpesRegType value = var2[var4];
                this.getSpesReg().add(value);
            }
        }

        return this;
    }

    public PersonType withSpesReg(Collection<SpesRegType> values) {
        if (values != null) {
            this.getSpesReg().addAll(values);
        }

        return this;
    }

    public PersonType withTiltak(TiltakType... values) {
        if (values != null) {
            TiltakType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                TiltakType value = var2[var4];
                this.getTiltak().add(value);
            }
        }

        return this;
    }

    public PersonType withTiltak(Collection<TiltakType> values) {
        if (values != null) {
            this.getTiltak().addAll(values);
        }

        return this;
    }

    public PersonType withEgenansatt(EgenansattType... values) {
        if (values != null) {
            EgenansattType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                EgenansattType value = var2[var4];
                this.getEgenansatt().add(value);
            }
        }

        return this;
    }

    public PersonType withEgenansatt(Collection<EgenansattType> values) {
        if (values != null) {
            this.getEgenansatt().addAll(values);
        }

        return this;
    }

    public PersonType withBoadresse(BoadresseType... values) {
        if (values != null) {
            BoadresseType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                BoadresseType value = var2[var4];
                this.getBoadresse().add(value);
            }
        }

        return this;
    }

    public PersonType withBoadresse(Collection<BoadresseType> values) {
        if (values != null) {
            this.getBoadresse().addAll(values);
        }

        return this;
    }

    public PersonType withPrioritertadresse(PrioritertadresseType... values) {
        if (values != null) {
            PrioritertadresseType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                PrioritertadresseType value = var2[var4];
                this.getPrioritertadresse().add(value);
            }
        }

        return this;
    }

    public PersonType withPrioritertadresse(Collection<PrioritertadresseType> values) {
        if (values != null) {
            this.getPrioritertadresse().addAll(values);
        }

        return this;
    }

    public PersonType withGeti(GetiType... values) {
        if (values != null) {
            GetiType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                GetiType value = var2[var4];
                this.getGeti().add(value);
            }
        }

        return this;
    }

    public PersonType withGeti(Collection<GetiType> values) {
        if (values != null) {
            this.getGeti().addAll(values);
        }

        return this;
    }

    public PersonType withForeldreansvar(ForeldreansvarType... values) {
        if (values != null) {
            ForeldreansvarType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                ForeldreansvarType value = var2[var4];
                this.getForeldreansvar().add(value);
            }
        }

        return this;
    }

    public PersonType withForeldreansvar(Collection<ForeldreansvarType> values) {
        if (values != null) {
            this.getForeldreansvar().addAll(values);
        }

        return this;
    }

    public PersonType withOppholdstillatelse(OppholdstillatelseType... values) {
        if (values != null) {
            OppholdstillatelseType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                OppholdstillatelseType value = var2[var4];
                this.getOppholdstillatelse().add(value);
            }
        }

        return this;
    }

    public PersonType withOppholdstillatelse(Collection<OppholdstillatelseType> values) {
        if (values != null) {
            this.getOppholdstillatelse().addAll(values);
        }

        return this;
    }

    public PersonType withGironummer(GironummerType... values) {
        if (values != null) {
            GironummerType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                GironummerType value = var2[var4];
                this.getGironummer().add(value);
            }
        }

        return this;
    }

    public PersonType withGironummer(Collection<GironummerType> values) {
        if (values != null) {
            this.getGironummer().addAll(values);
        }

        return this;
    }

    public PersonType withTillegg(TilleggType... values) {
        if (values != null) {
            TilleggType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                TilleggType value = var2[var4];
                this.getTillegg().add(value);
            }
        }

        return this;
    }

    public PersonType withTillegg(Collection<TilleggType> values) {
        if (values != null) {
            this.getTillegg().addAll(values);
        }

        return this;
    }

    public PersonType withUtad(UtadType... values) {
        if (values != null) {
            UtadType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                UtadType value = var2[var4];
                this.getUtad().add(value);
            }
        }

        return this;
    }

    public PersonType withUtad(Collection<UtadType> values) {
        if (values != null) {
            this.getUtad().addAll(values);
        }

        return this;
    }

    public PersonType withPost(PostType... values) {
        if (values != null) {
            PostType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                PostType value = var2[var4];
                this.getPost().add(value);
            }
        }

        return this;
    }

    public PersonType withPost(Collection<PostType> values) {
        if (values != null) {
            this.getPost().addAll(values);
        }

        return this;
    }

    public PersonType withMigrasjon(MigrasjonType... values) {
        if (values != null) {
            MigrasjonType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                MigrasjonType value = var2[var4];
                this.getMigrasjon().add(value);
            }
        }

        return this;
    }

    public PersonType withMigrasjon(Collection<MigrasjonType> values) {
        if (values != null) {
            this.getMigrasjon().addAll(values);
        }

        return this;
    }

    public PersonType withVergemaal(VergeType... values) {
        if (values != null) {
            VergeType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                VergeType value = var2[var4];
                this.getVergemaal().add(value);
            }
        }

        return this;
    }

    public PersonType withVergemaal(Collection<VergeType> values) {
        if (values != null) {
            this.getVergemaal().addAll(values);
        }

        return this;
    }

    public PersonType withBehov(BehovType... values) {
        if (values != null) {
            BehovType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                BehovType value = var2[var4];
                this.getBehov().add(value);
            }
        }

        return this;
    }

    public PersonType withBehov(Collection<BehovType> values) {
        if (values != null) {
            this.getBehov().addAll(values);
        }

        return this;
    }

    public PersonType withUtlandbank(UtlandbankType... values) {
        if (values != null) {
            UtlandbankType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                UtlandbankType value = var2[var4];
                this.getUtlandbank().add(value);
            }
        }

        return this;
    }

    public PersonType withUtlandbank(Collection<UtlandbankType> values) {
        if (values != null) {
            this.getUtlandbank().addAll(values);
        }

        return this;
    }

    public PersonType withUtlandinfo(UtlandinfoType... values) {
        if (values != null) {
            UtlandinfoType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                UtlandinfoType value = var2[var4];
                this.getUtlandinfo().add(value);
            }
        }

        return this;
    }

    public PersonType withUtlandinfo(Collection<UtlandinfoType> values) {
        if (values != null) {
            this.getUtlandinfo().addAll(values);
        }

        return this;
    }
}

