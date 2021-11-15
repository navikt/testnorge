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
	statsborgerskapLandkode: null,
	gradering: null,
}

export const initialKontaktinfoForDoedebo = {
	skifteform: null,
	attestutstedelsesdato: null,
	adresse: {
		adresselinje1: '',
		adresselinje2: '',
		postnummer: '',
		poststedsnavn: '',
		landkode: 'NOR',
	},
	// advokatSomKontakt: {},
	// personSomKontakt: {},
	// organisasjonSomKontakt: {},
	kilde: 'Dolly',
	master: 'FREG',
	gjeldende: true,
}
