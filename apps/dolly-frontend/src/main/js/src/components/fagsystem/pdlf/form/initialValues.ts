import { Bostedsadresse } from '~/components/fagsystem/pdlf/form/partials/adresser/bostedsadresse/Bostedsadresse'

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

export const initialBostedsadresse = {
	adressetype: null as string,
	angittFlyttedato: null as string,
	kilde: 'Dolly',
	master: 'FREG',
	gjeldende: true,
}

export const initialOppholdsadresse = {
	adressetype: null as string,
	kilde: 'Dolly',
	master: 'FREG',
	gjeldende: true,
}

export const initialKontaktadresse = {
	adressetype: null as string,
	kilde: 'Dolly',
	master: 'FREG',
	gjeldende: true,
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
	gjeldende: true,
}
