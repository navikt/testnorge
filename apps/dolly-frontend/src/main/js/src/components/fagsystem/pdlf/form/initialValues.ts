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

export const initialUtenlandskAdresse = {
	adressenavnNummer: null as string,
	postboksNummerNavn: null as string,
	postkode: null as string,
	bySted: null as string,
	landkode: null as string,
	bygningEtasjeLeilighet: null as string,
	regionDistriktOmraade: null as string,
}

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
	gjeldende: true,
}

export const initialSikkerhetstiltak = {
	tiltakstype: '',
	beskrivelse: '',
	kontaktperson: {
		personident: '',
		enhet: '',
	},
	gyldigFraOgMed: new Date(),
	gyldigTilOgMed: null,
	kilde: 'Dolly',
	master: 'PDL',
	gjeldende: true,
}

export const initialTpsSikkerhetstiltak = {
	tiltakstype: '',
	beskrivelse: '',
	gyldigFraOgMed: new Date(),
	gyldigTilOgMed: null,
}

export const initialStatsborgerskap = {
	landkode: null,
	gyldigFraOgMed: new Date(),
	gyldigTilOgMed: null,
	bekreftelsesdato: null,
	kilde: 'Dolly',
	master: 'PDL',
	gjeldende: true,
}

export const initialTelefonnummer = {
	landskode: '',
	nummer: '',
	prioritet: 2,
	kilde: 'Dolly',
	master: 'PDL',
	gjeldende: true,
}

export const initialTpsTelefonnummer = {
	landkode: '',
	telefonnummer: '',
	telefontype: 'HJET',
}

export const initialTilrettelagtKommunikasjon = {
	spraakForTaletolk: '',
	spraakForTegnspraakTolk: '',
	kilde: 'Dolly',
	master: 'PDL',
	gjeldende: true,
}

export const initialDoedsfall = {
	doedsdato: new Date(),
	kilde: 'Dolly',
	master: 'PDL',
	gjeldende: true,
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
	gjeldende: true,
}

export const initialFalskIdentitetValues = {
	erFalsk: true,
	kilde: 'Dolly',
	master: 'FREG',
	gjeldende: true,
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
