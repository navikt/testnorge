import * as Yup from 'yup'
import { requiredDate, requiredString } from '@/utils/YupValidations'
import { matrikkeladresse, vegadresse } from '@/components/fagsystem/pdlf/form/validation/partials'
import { testDatoFom, testDatoTom } from '@/components/fagsystem/utils'
import * as _ from 'lodash-es'

const testForeldreansvar = (val) => {
	return val.test('er-gyldig-foreldreansvar', function erGyldigForeldreansvar(selected) {
		let feilmelding = null
		const values = this.options.context

		const foreldrerelasjoner = []
			.concat(
				_.get(values, 'pdldata.person.forelderBarnRelasjon'),
				_.get(values, 'personFoerLeggTil.pdl.hentPerson.forelderBarnRelasjon')
			)
			?.map((a) => a?.minRolleForPerson)
			?.filter((a) => a)

		const sivilstander = []
			.concat(
				_.get(values, 'pdldata.person.sivilstand'),
				_.get(values, 'personFoerLeggTil.pdl.hentPerson.sivilstand')
			)
			?.map((a) => a?.type)
			?.filter((a) => a)

		const barn = []
			.concat(
				_.get(values, 'pdldata.person.forelderBarnRelasjon'),
				_.get(values, 'personFoerLeggTil.pdl.hentPerson.forelderBarnRelasjon')
			)
			?.filter((a) => a?.relatertPersonsRolle === 'BARN')

		const kjoennListe = []
			.concat(
				_.get(values, 'pdldata.person.kjoenn'),
				_.get(values, 'pdldata.person.personFoerLeggTil.pdl.hentPerson.kjoenn')
			)
			?.filter((a) => a)

		const gyldigeSivilstander = ['GIFT', 'REGISTRERT_PARTNER', 'SEPARERT', 'SEPARERT_PARTNER']

		if (selected === 'MOR' || selected === 'MEDMOR') {
			const gyldigeRelasjoner = ['MOR', 'MEDMOR']
			if (
				(foreldrerelasjoner?.includes('FORELDER') &&
					!kjoennListe?.some((a) => a.kjoenn === 'KVINNE') &&
					!gyldigeSivilstander.some((a) => sivilstander?.includes(a))) ||
				(!foreldrerelasjoner?.includes('FORELDER') &&
					!gyldigeRelasjoner.some((a) => foreldrerelasjoner?.includes(a)) &&
					!gyldigeSivilstander.some((a) => sivilstander?.includes(a)))
			) {
				feilmelding = 'Barn med foreldrerolle mor eller medmor finnes ikke'
			}
			if (!barn?.some((a) => !a.partnerErIkkeForelder)) {
				feilmelding = 'Partner er ikke forelder'
			}
		}
		if (selected === 'FAR') {
			if (
				(foreldrerelasjoner?.includes('FORELDER') &&
					!kjoennListe?.some((a) => a.kjoenn === 'MANN') &&
					!gyldigeSivilstander.some((a) => sivilstander?.includes(a))) ||
				(!foreldrerelasjoner?.includes('FORELDER') &&
					!foreldrerelasjoner?.includes('FAR') &&
					!gyldigeSivilstander.some((a) => sivilstander?.includes(a)))
			) {
				feilmelding = 'Barn med foreldrerolle far finnes ikke'
			}
			if (!barn?.some((a) => !a.partnerErIkkeForelder)) {
				feilmelding = 'Partner er ikke forelder'
			}
		}
		if (selected === 'FELLES') {
			if (!gyldigeSivilstander?.some((a) => sivilstander?.includes(a))) {
				feilmelding =
					'Partner med sivilstand gift, registrert partner, separert eller separert partner finnes ikke'
			}
			if (!barn?.some((a) => !a.partnerErIkkeForelder)) {
				feilmelding = 'Partner er ikke forelder'
			}
		}
		return feilmelding ? this.createError({ message: feilmelding }) : true
	})
}

const testDeltBostedAdressetype = (value) => {
	return value.test('har-gyldig-adressetype', function harGyldigAdressetype(selected) {
		let feilmelding = null
		if (selected === 'PARTNER_ADRESSE') {
			const values = this.options.context
			const personFoerLeggTil = values.personFoerLeggTil

			let fantPartner = false
			const nyePartnere = _.get(values, 'pdldata.person.sivilstand')
			if (nyePartnere?.length > 0) {
				fantPartner = nyePartnere[0].borIkkeSammen
			} else if (personFoerLeggTil?.pdlforvalter?.relasjoner) {
				const partnere = personFoerLeggTil.pdlforvalter.relasjoner.filter(
					(relasjon) => relasjon.relasjonType === 'EKTEFELLE_PARTNER'
				)
				if (partnere.length > 0) {
					const partnerAdresseId =
						partnere[0].relatertPerson?.bostedsadresse?.[0]?.adresseIdentifikatorFraMatrikkelen
					const identAdresseId =
						personFoerLeggTil?.pdlforvalter?.person?.bostedsadresse?.[0]
							?.adresseIdentifikatorFraMatrikkelen
					if (partnerAdresseId && partnerAdresseId !== identAdresseId) {
						fantPartner = true
					}
				}
			}
			feilmelding = fantPartner ? null : 'Fant ikke gyldig partner for delt bosted'
		}

		return feilmelding ? this.createError({ message: feilmelding }) : true
	})
}

export const nyPerson = Yup.object({
	identtype: Yup.string().nullable(),
	kjoenn: Yup.string().nullable(),
	alder: Yup.number()
		.transform((i, j) => (j === '' ? null : i))
		.nullable(),
	foedtEtter: testDatoFom(Yup.mixed().nullable(), 'foedtFoer', 'Dato må være før født før-dato'),
	foedtFoer: testDatoTom(
		Yup.mixed().nullable(),
		'foedtEtter',
		'Dato må være etter født etter-dato'
	),
	nyttNavn: Yup.object({
		hasMellomnavn: Yup.boolean().nullable(),
	}),
	statsborgerskapLandkode: Yup.string().nullable(),
	gradering: Yup.string().nullable(),
})

export const doedfoedtBarn = Yup.array().of(
	Yup.object({
		dato: requiredDate.nullable(),
	})
)

export const sivilstand = Yup.object({
	type: requiredString.nullable(),
	sivilstandsdato: Yup.string().nullable(),
	relatertVedSivilstand: Yup.string().nullable(),
	bekreftelsesdato: Yup.string().nullable(),
	borIkkeSammen: Yup.boolean().nullable(),
	nyRelatertPerson: nyPerson,
})

const deltBosted = Yup.object({
	adressetype: testDeltBostedAdressetype(requiredString.nullable()),
	startdatoForKontrakt: testDatoFom(
		Yup.date().optional().nullable(),
		'sluttdatoForKontrakt',
		'Dato må være før sluttdato'
	),
	sluttdatoForKontrakt: testDatoTom(
		Yup.date().optional().nullable(),
		'startdatoForKontrakt',
		'Dato må være etter startdato'
	),
	vegadresse: vegadresse.nullable(),
	matrikkeladresse: matrikkeladresse.nullable(),
	ukjentBosted: Yup.mixed().when('adressetype', {
		is: 'UKJENT_BOSTED',
		then: Yup.object({
			bostedskommune: requiredString.nullable(),
		}),
	}),
})

export const forelderBarnRelasjon = Yup.array().of(
	Yup.object({
		minRolleForPerson: requiredString,
		relatertPersonsRolle: requiredString,
		relatertPerson: Yup.string().nullable(),
		borIkkeSammen: Yup.mixed().when('relatertPersonsRolle', {
			is: 'BARN',
			then: Yup.mixed().notRequired(),
			otherwise: Yup.boolean(),
		}),
		nyRelatertPerson: nyPerson.nullable(),
		deltBosted: Yup.mixed().when('relatertPersonsRolle', {
			is: 'BARN',
			then: deltBosted.nullable(),
		}),
	})
)

export const foreldreansvar = Yup.array().of(
	Yup.object({
		ansvar: testForeldreansvar(requiredString.nullable()),
	})
)
