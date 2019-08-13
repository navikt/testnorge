import { KategoriTypes } from './Types'
import { ArenaApi } from '~/service/Api'
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
	ArbeidOgInntekt: {
		id: 'arbeidOgInntekt',
		navn: 'Arbeid og inntekt',
		informasjonstekst:
			'Arbeidsforhold: \n' +
			'Dataene her blir lagt til AAREG. \n\n' +
			'Inntekt: \n' +
			'Lignede inntekter - årlig: ' +
			'Lignede inntekter er stemplet og godkjent. Inntektene her blir lagt i Sigrun-stub.',

		order: 40
	},
	KontaktInfo: {
		id: 'kontaktInfo',
		navn: 'Kontakt- og reservasjonsregisteret',
		informasjonstekst:
			'KRR - benyttes for offentlige virksomheter for å avklare om den enkelte bruker har reservert seg mot digital kommunikasjon eller ikke. I tillegg skal varslene som sendes til bruker benytte den kontaktinformasjonen som ligger i registeret. Dette kan enten være mobiltelefonnummer for utsendelse av sms, eller epostadresse for utsendelse av epost.',
		order: 50
	},
	KontaktInfoDoedsbo: {
		id: 'kontaktinfoDoedsbo',
		navn: 'Kontaktinformasjon for dødsbo',
		informasjonstekst:
			'Kontaktinformasjon for dødsbo blir kun distribuert til Q2, og dette miljøet må derfor velges i siste steg.',
		order: 50
	},
	Arena: {
		id: 'arena',
		navn: 'Arena',
		informasjonstekst:
			'Arena-data blir ikke distribuert til alle miljøer, og et eller flere av miljøene under må derfor velges i siste steg.',
		tilgjengeligeMiljoeEndepunkt: ArenaApi.getTilgjengeligeMiljoe(),
		order: 60
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
	Identifikasjon: {
		id: 'identifikasjon',
		navn: 'Identifikasjon',
		order: 30,
		showInSummary: true
	},
	Doedsbo: {
		id: 'doedsbo',
		navn: '',
		order: 20,
		showInSummary: true
	},
	Adressat: {
		id: 'adressat',
		navn: 'Adressat',
		order: 10,
		showInSummary: true
	},
	DoedsboAdresse: {
		id: 'doedsboAdresse',
		navn: 'Adresse',
		order: 10,
		showInSummary: true
	},
	Diverse: {
		id: 'diverse',
		navn: 'Diverse',
		order: 40
	},
	Boadresse: {
		id: 'boadresse',
		navn: 'Boadresse',
		order: 10
	},
	Postadresse: {
		id: 'postadresse',
		navn: 'Postadresse',
		order: 20
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

	Arbeidsforhold: {
		id: 'arbeidsforhold',
		navn: 'Arbeidsforhold',
		order: 10,
		showInSummary: true
	},

	Inntekt: {
		id: 'inntekt',
		navn: 'Inntekt',
		order: 20,
		showInSummary: true
	},

	Krr: {
		id: 'krr',
		navn: '',
		order: 10,
		showInSummary: true
	},

	Arena: {
		id: 'arena',
		navn: '',
		order: 10,
		showInSummary: true
	}
}

export const SubSubKategorier: KategoriTypes = {
	Permisjon: {
		id: 'permisjon',
		navn: 'Permisjon',
		order: 10
	},
	Utenlandsopphold: {
		id: 'utenlandsopphold',
		navn: 'Utenlandsopphold',
		order: 10
	},
	Adressat: {
		id: 'adressat',
		navn: 'Adressat',
		order: 10
	}
}
