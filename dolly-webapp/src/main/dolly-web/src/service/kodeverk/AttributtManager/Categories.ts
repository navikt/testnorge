import { KategoriTypes } from './Types'
import { DEFAULT_ECDH_CURVE } from 'tls'

export const Kategorier: KategoriTypes = {
	PersInfo: {
		id: 'personinfo',
		navn: 'Personinformasjon',
		order: 10
	},
	FamilieRelasjoner: {
		id: 'familierelasjoner',
		navn: 'Familierelasjoner',
		order: 20
	},
	Adresser: {
		id: 'adresser',
		navn: 'Adresser',
		order: 30
	},
	Inntekter: {
		id: 'inntekt',
		navn: 'Inntekter',
		order: 40
	},
	KontaktInfo: {
		id: 'kontaktInfo',
		navn: 'Kontaktinformasjon og reservasjon',
		order: 50
	}
}

export const SubKategorier: KategoriTypes = {
	Alder: {
		id: 'alder',
		navn: 'Alder',
		order: 10
	},
	Nasjonalitet: {
		id: 'nasjonalitet',
		navn: 'Nasjonalitet',
		order: 20
	},
	Diverse: {
		id: 'diverse',
		navn: 'Diverse',
		order: 30
	},
	Boadresse: {
		id: 'boadresse',
		navn: 'Boadresse',
		order: 10
	},
	// PostadrInnland: {
	// 	id: 'postadresseInnland',
	// 	navn: 'Postadresse innland',
	// 	order: 20,
	// 	multiple: true
	// },
	// PostadrUtland: {
	// 	id: 'postadresseUtland',
	// 	navn: 'Postadresse utland',
	// 	order: 30,
	// 	multiple: true
	// },
	Krr: {
		id: 'krr',
		navn: 'Kontakt- og reservasjonsregisteret',
		order: 10,
		showInSummary: true
	},
	Partner: {
		id: 'partner',
		navn: 'Partner',
		order: 10,
		showInSummary: true
	},
	Barn: {
		id: 'barn',
		navn: 'Barn',
		order: 20,
		showInSummary: true
	},
	Inntekt: {
		id: 'inntekt',
		navn: 'Inntekt',
		order: 30,
		showInSummary: true
	}
}
