import { KategoriTypes } from './Types'
import { DEFAULT_ECDH_CURVE } from 'tls'

export const Kategorier: KategoriTypes = {
	PersInfo: {
		id: 'personinfo',
		navn: 'Personinformasjon',
		order: 10
	},
	Adresser: {
		id: 'adresser',
		navn: 'Adresser',
		order: 20
	},
	FamilieRelasjoner: {
		id: 'familierelasjoner',
		navn: 'Familierelasjoner',
		order: 30
	},
	Inntekter: {
		id: 'inntekt',
		navn: 'Inntekter',
		order: 40
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
		navn: 'Register inntekt',
		order: 30,
		showInSummary: true,
		singleChoice: true
	}
}
