const FAGSYSTEM_LABELS: Record<string, string> = {
	Aareg: 'Arbeidsforhold (Aareg)',
	Arena: 'Arena',
	ArbeidsplassenCV: 'Arbeidsplassen CV',
	Arbeidssoekerregisteret: 'Arbeidssøkerregisteret',
	Bankkonto: 'Bankkonto',
	Brregstub: 'Brønnøysundregistrene',
	Dokarkiv: 'Dokarkiv',
	EtterlatteYtelser: 'Etterlatteytelser',
	Fullmakt: 'Fullmakt',
	Histark: 'Histark',
	'Ingen data': 'Uspesifisert',
	Inntektsmelding: 'Inntektsmelding',
	Inntektstub: 'Inntekt (A-ordningen)',
	Instdata: 'Institusjonsopphold',
	InstdataKdi: 'Institusjonsopphold (KDI)',
	KelvinAap: 'AAP (Kelvin)',
	Krrstub: 'Kontakt- og reservasjonsregisteret',
	Medl: 'Medlemskap (MEDL)',
	Nomdata: 'NOM',
	Oppfoelgingsvedtak14a: 'Oppfølgingsvedtak § 14 a',
	PdlData: 'Persondata (PDL)',
	Pensjon: 'Pensjon',
	SigrunstubPensjonsgivende: 'Pensjonsgivende inntekt (Sigrun)',
	SigrunstubSummertSkattegrunnlag: 'Summert skattegrunnlag (Sigrun)',
	Skattekort: 'Skattekort',
	Skjerming: 'Skjermingsregisteret',
	Sykemelding: 'Sykmelding',
	TpsMessaging: 'TPS-meldinger',
	Udistub: 'UDI',
	Uspesifisert: 'Uspesifisert',
	Yrkesskader: 'Yrkesskader',
}

const DETALJ_LABELS: Record<string, string> = {
	AAP115: 'AAP 11-5',
	AfpOffentlig: 'AFP offentlig',
	ArenaBrukertype: 'Brukertype',
	'Array/matrise antall': 'Antall oppføringer',
	DeltBosted: 'Delt bosted',
	DoedfoedtBarn: 'Dødfødt barn',
	Doedsfall: 'Dødsfall',
	FalskIdentitet: 'Falsk identitet',
	FolkeregisterPersonstatus: 'Folkeregisterpersonstatus',
	Foedested: 'Fødested',
	Foedsel: 'Fødsel',
	Foedselsdato: 'Fødselsdato',
	ForelderBarnRelasjon: 'Foreldre og barn',
	FødtEtter: 'Født etter',
	FødtFør: 'Født før',
	Id2032: 'Ny identtype (2032)',
	Kjoenn: 'Kjønn',
	KontaktinformasjonForDoedsbo: 'Kontaktinformasjon for dødsbo',
	'Legg-til/endre': 'Legg til/endre på eksisterende person',
	NavPersonIdentifikator: 'Nav-personidentifikator',
	NorskBankkonto: 'Norsk bankkonto',
	Nyident: 'Ny ident',
	PoppInntekt: 'Pensjonsopptjening (POPP)',
	PoppSpesifisertInntekt: 'Spesifisert pensjonsopptjening (POPP)',
	TilrettelagtKommunikasjon: 'Tilrettelagt kommunikasjon',
	Uforetrygd: 'Uføretrygd',
	UtenlandskBankkonto: 'Utenlandsk bankkonto',
	UtenlandskIdentifikasjonsnummer: 'Utenlandsk identifikasjonsnummer',
	Vergemaal: 'Vergemål',
}

const humanize = (noekkel: string): string => {
	if (/^[A-ZÆØÅ0-9]+$/.test(noekkel)) {
		return noekkel
	}
	const medMellomrom = noekkel.replace(/([a-zæøå])([A-ZÆØÅ])/g, '$1 $2')
	return medMellomrom.charAt(0).toUpperCase() + medMellomrom.slice(1).toLowerCase()
}

export const fagsystemLabel = (fagsystem: string): string =>
	FAGSYSTEM_LABELS[fagsystem] ?? humanize(fagsystem)

export const detaljNoekkelLabel = (noekkel: string): string =>
	DETALJ_LABELS[noekkel] ?? humanize(noekkel)
