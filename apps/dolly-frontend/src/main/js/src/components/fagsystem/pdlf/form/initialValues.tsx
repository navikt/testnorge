const navn = {
	fornavn: undefined as unknown as string,
	mellomnavn: undefined as unknown as string,
	etternavn: undefined as unknown as string,
}

export const initialPdlPerson = {
	identtype: null as unknown as string,
	kjoenn: null as unknown as string,
	foedtEtter: null as unknown as string,
	foedtFoer: null as unknown as string,
	alder: null as unknown as number,
	syntetisk: true,
	nyttNavn: {
		hasMellomnavn: false,
	},
	statsborgerskapLandkode: null as unknown as string,
	gradering: null as unknown as string,
}

export const initialPdlBiPerson = {
	foedselsdato: null as unknown as string,
	kjoenn: null as unknown as string,
	navn: navn,
	statsborgerskap: null as unknown as string,
}

export const getInitialKontaktadresse = (master = 'FREG') => ({
	adressetype: null as unknown as string,
	gyldigFraOgMed: null as unknown as string,
	gyldigTilOgMed: null as unknown as string,
	opprettCoAdresseNavn: null as unknown as object,
	kilde: 'Dolly',
	master: master,
})

export const getInitialOppholdsadresse = (master = 'FREG') => ({
	...getInitialKontaktadresse(master),
})

export const getInitialBostedsadresse = (master = 'FREG') => ({
	...getInitialKontaktadresse(master),
	angittFlyttedato: null as unknown as string,
})

export const initialDeltBosted = {
	startdatoForKontrakt: null as unknown as string,
	sluttdatoForKontrakt: null as unknown as string,
	master: 'FREG',
	kilde: 'Dolly',
}

export const getInitialAdressebeskyttelse = (master = 'FREG') => ({
	gradering: null as unknown as string,
	kilde: 'Dolly',
	master: master,
})

export const initialVegadresse = {
	adressekode: null as unknown as string,
	adressenavn: null as unknown as string,
	tilleggsnavn: null as unknown as string,
	bruksenhetsnummer: null as unknown as string,
	husbokstav: null as unknown as string,
	husnummer: null as unknown as string,
	kommunenummer: null as unknown as string,
	postnummer: null as unknown as string,
}

export const initialMatrikkeladresse = {
	kommunenummer: null as unknown as string,
	gaardsnummer: null as unknown as number,
	bruksnummer: null as unknown as number,
	postnummer: null as unknown as string,
	bruksenhetsnummer: null as unknown as string,
	tilleggsnavn: null as unknown as string,
}

export const initialUtenlandskAdresse = {
	adressenavnNummer: null as unknown as string,
	postboksNummerNavn: null as unknown as string,
	postkode: null as unknown as string,
	bySted: null as unknown as string,
	landkode: null as unknown as string,
	bygningEtasjeLeilighet: null as unknown as string,
	regionDistriktOmraade: null as unknown as string,
}

export const initialPostboksadresse = {
	postboks: null as unknown as string,
	postbokseier: null as unknown as string,
	postnummer: null as unknown as string,
}

export const initialUkjentBosted = {
	bostedskommune: null as unknown as string,
}

export const initialKontaktinfoForDoedebo = {
	skifteform: null as unknown as string,
	attestutstedelsesdato: null as unknown as string,
	kontaktType: null as unknown as string,
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
	gyldigTilOgMed: null as unknown as string,
	kilde: 'Dolly',
	master: 'PDL',
}

export const initialMedl = {
	fraOgMed: new Date(),
	tilOgMed: new Date(),
	status: '',
	statusaarsak: '',
}

export const initialMedlMelosys = {
	...initialMedl,
	kilde: 'srvmelosys',
	grunnlag: '',
	dekning: '',
	lovvalg: '',
	lovvalgsland: '',
	kildedokument: '',
}

export const initialMedlGosys = {
	...initialMedl,
	kilde: 'srvgosys',
	grunnlag: '',
	dekning: '',
	lovvalg: '',
	lovvalgsland: '',
	kildedokument: '',
}

export const initialMedlLaanekassen = {
	...initialMedl,
	kilde: 'LAANEKASSEN',
	kildedokument: '',
	studieinformasjon: {
		statsborgerland: '',
		studieland: '',
		delstudie: false,
		soeknadInnvilget: false,
	},
}

export const initialMedlAvgangssystem = {
	...initialMedl,
	kilde: 'AVGSYS',
	grunnlag: '',
	dekning: '',
}

export const getInitialStatsborgerskap = (master = 'FREG') => ({
	landkode: null as unknown as string,
	gyldigFraOgMed: null as unknown as string,
	gyldigTilOgMed: null as unknown as string,
	kilde: 'Dolly',
	master: master,
})

export const initialTilrettelagtKommunikasjon = {
	spraakForTaletolk: null as unknown as string,
	spraakForTegnspraakTolk: null as unknown as string,
	kilde: 'Dolly',
	master: 'PDL',
}

export const getInitialDoedsfall = (master = 'FREG') => ({
	doedsdato: new Date(),
	kilde: 'Dolly',
	master: master,
})

export const getInitialFoedsel = (master = 'FREG') => ({
	foedekommune: null as unknown as string,
	foedeland: null as unknown as string,
	foedested: null as unknown as string,
	foedselsaar: null as unknown as number,
	foedselsdato: null as unknown as string,
	kilde: 'Dolly',
	master: master,
})

export const getInitialFoedested = (master = 'FREG') => ({
	foedekommune: null as unknown as string,
	foedeland: null as unknown as string,
	foedested: null as unknown as string,
	kilde: 'Dolly',
	master: master,
})

export const getInitialFoedselsdato = (master = 'FREG') => ({
	foedselsaar: null as unknown as number,
	foedselsdato: null as unknown as string,
	kilde: 'Dolly',
	master: master,
})

export const initialOrganisasjon = {
	organisasjonsnummer: null as unknown as string,
	organisasjonsnavn: null as unknown as string,
	kontaktperson: navn,
}

export const initialPerson = {
	identifikasjonsnummer: null as unknown as string,
	foedselsdato: null as unknown as string,
	navn: null as unknown as typeof navn,
}

export const initialNyPerson = {
	nyKontaktperson: initialPdlPerson,
}

export const initialFalskIdentitetValues = {
	erFalsk: true,
	kilde: 'Dolly',
	master: 'FREG',
}

export const initialInnvandring = {
	fraflyttingsland: '',
	fraflyttingsstedIUtlandet: '',
	innflyttingsdato: null as unknown as string,
	master: 'FREG',
	kilde: 'Dolly',
}

export const initialUtvandring = {
	tilflyttingsland: '',
	tilflyttingsstedIUtlandet: '',
	utflyttingsdato: null as unknown as string,
	master: 'FREG',
	kilde: 'Dolly',
}

export const getInitialSivilstand = (master = 'FREG') => ({
	type: null as unknown as string,
	sivilstandsdato: null as unknown as string,
	relatertVedSivilstand: null as unknown as string,
	bekreftelsesdato: null as unknown as string,
	borIkkeSammen: false,
	nyRelatertPerson: initialPdlPerson,
	kilde: 'Dolly',
	master: master,
})

export const getInitialKjoenn = (master = 'FREG') => ({
	kjoenn: null as unknown as string,
	kilde: 'Dolly',
	master: master,
})

export const getInitialNavn = (master = 'FREG') => ({
	fornavn: undefined as unknown as string,
	mellomnavn: undefined as unknown as string,
	etternavn: undefined as unknown as string,
	hasMellomnavn: false,
	gyldigFraOgMed: null as unknown as string,
	kilde: 'Dolly',
	master: master,
})

export const initialVergemaal = {
	vergemaalEmbete: null as unknown as string,
	sakType: null as unknown as string,
	gyldigFraOgMed: null as unknown as string,
	gyldigTilOgMed: null as unknown as string,
	nyVergeIdent: initialPdlPerson as unknown,
	vergeIdent: null as unknown as string,
	mandatType: null as unknown as string,
	kilde: 'Dolly',
	master: 'FREG',
}

export const getInitialForelder = (master = 'FREG') => ({
	minRolleForPerson: 'BARN',
	relatertPersonsRolle: 'FORELDER',
	borIkkeSammen: false,
	typeForelderBarn: null as unknown as string,
	kilde: 'Dolly',
	master: master,
})

export const getInitialBarn = (master = 'FREG') => ({
	minRolleForPerson: 'FORELDER',
	relatertPersonsRolle: 'BARN',
	partnerErIkkeForelder: false,
	typeForelderBarn: null as unknown as string,
	deltBosted: null as any,
	kilde: 'Dolly',
	master: master,
})

export const initialDoedfoedtBarn = {
	dato: new Date(),
	kilde: 'Dolly',
	master: 'FREG',
}

export const getInitialForeldreansvar = (kanHaForeldreansvar = true) => ({
	harForeldreansvar: kanHaForeldreansvar ? true : undefined,
	erAnsvarssubjekt: kanHaForeldreansvar ? undefined : true,
	typeAnsvarlig: null as unknown as string,
	ansvar: null as unknown as string,
	gyldigFraOgMed: null as unknown as string,
	gyldigTilOgMed: null as unknown as string,
	kilde: 'Dolly',
	master: 'FREG',
})

export const getInitialUtenlandskIdentifikasjonsnummer = (master = 'FREG') => ({
	identifikasjonsnummer: '',
	opphoert: false,
	utstederland: '',
	kilde: 'Dolly',
	master: master,
})

export const getInitialNyIdent = (master = 'FREG') => ({
	eksisterendeIdent: null as unknown as string,
	identtype: null as unknown as string,
	kjoenn: null as unknown as string,
	foedtEtter: null as unknown as string,
	foedtFoer: null as unknown as string,
	alder: '',
	syntetisk: true,
	nyttNavn: {
		hasMellomnavn: false,
	},
	eksisterendePerson: false,
	kilde: 'Dolly',
	master: master,
})

export const initialFullmakt = {
	omraade: [{ tema: '', handling: [] }] as any,
	gyldigFraOgMed: new Date(),
	gyldigTilOgMed: null as unknown as string,
}

export const initialOpphold = {
	oppholdFra: null as unknown as Date,
	oppholdTil: null as unknown as Date,
	type: 'OPPLYSNING_MANGLER',
	kilde: 'Dolly',
	master: 'FREG',
}

export const initialTelefonnummer = {
	landskode: '+47',
	nummer: '',
	prioritet: 2,
	kilde: 'Dolly',
	master: 'PDL',
}

export const initialPersonstatus = {
	status: null as unknown as string,
	gyldigFraOgMed: null as unknown as string,
	gyldigTilOgMed: null as unknown as string,
	kilde: 'Dolly',
	master: 'FREG',
}
