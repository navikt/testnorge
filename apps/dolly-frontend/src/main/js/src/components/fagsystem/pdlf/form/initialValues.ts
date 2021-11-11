export const initialPdlPerson = {
	identtype: null,
	kjoenn: null,
	foedtEtter: null,
	foedtFoer: null,
	alder: '',
	syntetisk: false,
	nyttNavn: {
		hasMellomnavn: false,
	},
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
