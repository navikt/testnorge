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
		informasjonstekst:
			'Lignede inntekter - årlig: \n' +
			'Lignede inntekter er stemplet og godkjent. Inntektene her blir lagt i Sigrun-stub.',
		order: 40
	},
	KontaktInfo: {
		id: 'kontaktInfo',
		navn: 'Kontaktinformasjon og reservasjon',
		informasjonstekst:
			'KRR - benyttes for offentlige virksomheter for å avklare om den enkelte bruker har reservert seg mot digital kommunikasjon eller ikke. I tillegg skal varslene som sendes til bruker benytte den kontaktinformasjonen som ligger i registeret. Dette kan enten være mobiltelefonnummer for utsendelse av sms, eller epostadresse for utsendelse av epost. \n' +
			'Dataene her blir lagt i Krr-stub.',
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
