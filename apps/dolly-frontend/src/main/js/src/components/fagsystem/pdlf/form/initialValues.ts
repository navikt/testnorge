export const initialPdlPerson = {
	identtype: null as string,
	kjoenn: null as string,
	foedtEtter: null as string,
	foedtFoer: null as string,
	alder: '',
	syntetisk: false,
	nyttNavn: {
		hasMellomnavn: false,
	},
	statsborgerskapLandkode: null as string,
	gradering: null as string,
}

export const initialKontaktadresse = {
	adressetype: null as string,
	gyldigFraOgMed: null as string,
	gyldigTilOgMed: null as string,
	kilde: 'Dolly',
	master: 'FREG',
}

export const initialOppholdsadresse = {
	...initialKontaktadresse,
}

export const initialBostedsadresse = {
	...initialKontaktadresse,
	angittFlyttedato: null as string,
}

export const initialAdressebeskyttelse = {
	gradering: null as string,
	kilde: 'Dolly',
	master: 'FREG',
}

export const initialVegadresse = {
	adressekode: null as string,
	adressenavn: null as string,
	tilleggsnavn: null as string,
	bruksenhetsnummer: null as string,
	husbokstav: null as string,
	husnummer: null as string,
	kommunenummer: null as string,
	postnummer: null as string,
}

export const initialMatrikkeladresse = {
	kommunenummer: null as string,
	gaardsnummer: null as number,
	bruksnummer: null as number,
	postnummer: null as string,
	bruksenhetsnummer: null as string,
	tilleggsnavn: null as string,
}

export const initialUtenlandskAdresse = {
	adressenavnNummer: null as string,
	postboksNummerNavn: null as string,
	postkode: null as string,
	bySted: null as string,
	landkode: null as string,
	bygningEtasjeLeilighet: null as string,
	regionDistriktOmraade: null as string,
}

export const initialPostboksadresse = {
	postboks: null as string,
	postbokseier: null as string,
	postnummer: null as string,
}

export const initialUkjentBosted = {
	bostedskommune: null as string,
}

export const initialOppholdAnnetSted = null as string

export const initialKontaktinfoForDoedebo = {
	skifteform: null as string,
	attestutstedelsesdato: null as string,
	kontaktType: null as string,
	adresse: {
		adresselinje1: '',
		adresselinje2: '',
		postnummer: '',
		poststedsnavn: '',
		landkode: '',
	},
	kilde: 'Dolly',
	master: 'FREG',
}

export const initialSikkerhetstiltak = {
	tiltakstype: '',
	beskrivelse: '',
	kontaktperson: {
		personident: '',
		enhet: '',
	},
	gyldigFraOgMed: new Date(),
	gyldigTilOgMed: null as string,
	kilde: 'Dolly',
	master: 'PDL',
}

export const initialTpsSikkerhetstiltak = {
	tiltakstype: '',
	beskrivelse: '',
	gyldigFraOgMed: new Date(),
	gyldigTilOgMed: null as string,
}

export const initialStatsborgerskap = {
	landkode: null as string,
	gyldigFraOgMed: null as string,
	gyldigTilOgMed: null as string,
	kilde: 'Dolly',
	master: 'FREG',
}

export const initialTilrettelagtKommunikasjon = {
	spraakForTaletolk: '',
	spraakForTegnspraakTolk: '',
	kilde: 'Dolly',
	master: 'PDL',
}

export const initialDoedsfall = {
	doedsdato: new Date(),
	kilde: 'Dolly',
	master: 'PDL',
}

export const initialFoedsel = {
	fodekommune: null as string,
	foedeland: null as string,
	foedested: null as string,
	foedselsaar: null as number,
	foedselsdato: null as string,
	kilde: 'Dolly',
	master: 'FREG',
}

export const initialOrganisasjon = {
	organisasjonsnummer: null as string,
	organisasjonsnavn: null as string,
	kontaktperson: {
		fornavn: null as string,
		mellomnavn: null as string,
		etternavn: null as string,
	},
}

export const initialPerson = {
	foedselsdato: null as string,
	navn: {
		fornavn: null as string,
		mellomnavn: null as string,
		etternavn: null as string,
	},
}

export const initialNyPerson = {
	nyKontaktperson: initialPdlPerson,
}

export const initialUtenlandsIdValues = {
	identifikasjonsnummer: '',
	opphoert: false,
	utstederland: '',
	kilde: 'Dolly',
	master: 'FREG',
}

export const initialFalskIdentitetValues = {
	erFalsk: true,
	kilde: 'Dolly',
	master: 'FREG',
}

export const initialInnvandring = {
	fraflyttingsland: '',
	fraflyttingsstedIUtlandet: '',
	innflyttingsdato: new Date(),
	master: 'FREG',
	kilde: 'Dolly',
}

export const initialUtvandring = {
	tilflyttingsland: '',
	tilflyttingsstedIUtlandet: '',
	utflyttingsdato: new Date(),
	master: 'FREG',
	kilde: 'Dolly',
}

export const initialSivilstand = {
	type: null as string,
	sivilstandsdato: null as string,
	relatertVedSivilstand: null as string,
	bekreftelsesdato: null as string,
	borIkkeSammen: false,
	nyRelatertPerson: initialPdlPerson,
	kilde: 'Dolly',
	master: 'PDL',
}

export const initialKjoenn = {
	kjoenn: null as string,
	kilde: 'Dolly',
	master: 'FREG',
}

export const initialNavn = {
	fornavn: null as string,
	mellomnavn: null as string,
	etternavn: null as string,
	hasMellomnavn: false,
	kilde: 'Dolly',
	master: 'FREG',
}

export const initialVergemaal = {
	vergemaalEmbete: null as string,
	sakType: null as string,
	gyldigFraOgMed: null as string,
	gyldigTilOgMed: null as string,
	nyVergeIdent: initialPdlPerson,
	vergeIdent: null as string,
	mandatType: null as string,
	kilde: 'Dolly',
	master: 'FREG',
}

export const initialForelder = {
	minRolleForPerson: 'BARN',
	relatertPersonsRolle: 'FORELDER',
	relatertPerson: null as string,
	borIkkeSammen: false,
	nyRelatertPerson: initialPdlPerson,
	kilde: 'Dolly',
	master: 'FREG',
	gjeldende: true,
}

export const initialBarn = {
	...initialForelder,
	minRolleForPerson: 'FORELDER',
	relatertPersonsRolle: 'BARN',
	partnerErIkkeForelder: false,
}

export const initialDoedfoedtBarn = {
	dato: new Date(),
	kilde: 'Dolly',
	master: 'FREG',
	gjeldende: true,
}
