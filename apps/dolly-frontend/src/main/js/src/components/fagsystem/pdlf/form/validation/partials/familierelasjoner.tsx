import * as Yup from 'yup'
import { ifPresent, requiredDate, requiredString } from '@/utils/YupValidations'
import { testDatoFom, testDatoTom } from '@/components/fagsystem/utils'
import * as _ from 'lodash-es'
import {
	matrikkeladresse,
	vegadresse,
} from '@/components/fagsystem/pdlf/form/validation/partials/adresser'

const testForeldreansvar = (val: Yup.StringSchema<string, Yup.AnyObject>) => {
	return val.test('er-gyldig-foreldreansvar', (selected, testContext) => {
		let feilmelding = null
		const context = testContext.options.context
		const fullForm = testContext.from && testContext.from[testContext.from.length - 1]?.value

		if (context.leggTilPaaGruppe || context.personFoerLeggTil) {
			return true
		}

		const foreldrerelasjoner = []
			.concat(
				_.get(fullForm, 'pdldata.person.forelderBarnRelasjon'),
				_.get(context, 'personFoerLeggTil.pdl.hentPerson.forelderBarnRelasjon'),
			)
			?.map((a) => a?.minRolleForPerson)
			?.filter((a) => a)

		const sivilstander = []
			.concat(
				_.get(fullForm, 'pdldata.person.sivilstand'),
				_.get(context, 'personFoerLeggTil.pdl.hentPerson.sivilstand'),
			)
			?.map((a) => a?.type)
			?.filter((a) => a)

		const barn = []
			.concat(
				_.get(fullForm, 'pdldata.person.forelderBarnRelasjon'),
				_.get(context, 'personFoerLeggTil.pdl.hentPerson.forelderBarnRelasjon'),
			)
			?.filter((a) => a?.relatertPersonsRolle === 'BARN')

		const kjoennListe = []
			.concat(
				_.get(fullForm, 'pdldata.person.kjoenn'),
				_.get(context, 'pdldata.person.personFoerLeggTil.pdl.hentPerson.kjoenn'),
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
		return feilmelding ? testContext.createError({ message: feilmelding }) : true
	})
}

const testForeldreansvarForBarn = (val) => {
	return val.test('er-gyldig-foreldreansvar-for-barn', (selected, testContext) => {
		let feilmelding = null
		const fullForm = testContext.from && testContext.from[testContext.from.length - 1]?.value

		const foreldrerelasjoner = _.get(fullForm, 'personValues.forelderBarnRelasjon')
			?.map((a) => a?.relatertPersonsRolle)
			?.filter((a) => {
				return a && a !== 'BARN'
			})

		if (!foreldrerelasjoner || foreldrerelasjoner?.length < 1) {
			return true
		}

		if (
			(selected === 'MOR' || selected === 'MEDMOR') &&
			!foreldrerelasjoner.includes('MOR') &&
			!foreldrerelasjoner.includes('MEDMOR')
		) {
			feilmelding = 'Forelder med rolle mor eller medmor finnes ikke'
		}
		if (selected === 'FAR' && !foreldrerelasjoner.includes('FAR')) {
			feilmelding = 'Forelder med rolle far finnes ikke'
		}
		if (selected === 'FELLES' && foreldrerelasjoner.length < 2) {
			feilmelding = 'Barn har færre enn to foreldre'
		}

		return feilmelding ? testContext.createError({ message: feilmelding }) : true
	})
}

const testDeltBostedAdressetype = (value: Yup.StringSchema<string, Yup.AnyObject>) => {
	return value.test('har-gyldig-adressetype', (selected, testContext) => {
		let feilmelding = null
		const context = testContext.options.context
		const fullForm = testContext.from && testContext.from[testContext.from.length - 1]?.value
		if (selected === 'PARTNER_ADRESSE') {
			const personFoerLeggTil = context?.personFoerLeggTil
			let fantPartner = false
			const nyePartnere = _.get(fullForm, 'pdldata.person.sivilstand')

			if (nyePartnere?.length > 0) {
				fantPartner = nyePartnere.find((partner) => partner.borIkkeSammen)
			} else if (personFoerLeggTil?.pdlforvalter?.relasjoner) {
				const partnere = personFoerLeggTil.pdlforvalter.relasjoner.filter(
					(relasjon) => relasjon.relasjonType === 'EKTEFELLE_PARTNER',
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

		return feilmelding ? testContext.createError({ message: feilmelding }) : true
	})
}

const testSivilstandsdatoBekreftelsesdato = (value) => {
	return value.test('har-gyldig-sivilstandsdato', (value, testContext) => {
		let feilmelding = null
		const parent = testContext.parent
		const master = parent?.master
		const sivilstandsdato = parent?.sivilstandsdato
		const bekreftelsesdato = parent?.bekreftelsesdato

		if (master === 'PDL' && !sivilstandsdato && !bekreftelsesdato) {
			feilmelding = 'Master PDL krever at enten sivilstandsdato eller bekreftelsesdato er satt'
		}

		return feilmelding ? testContext.createError({ message: feilmelding }) : true
	})
}

export const nyPerson = Yup.object({
	identtype: Yup.string().nullable(),
	kjoenn: Yup.string().nullable(),
	alder: Yup.mixed().nullable(),
	foedtEtter: testDatoFom(Yup.mixed().nullable(), 'foedtFoer', 'Dato må være før født før-dato'),
	foedtFoer: testDatoTom(
		Yup.mixed().nullable(),
		'foedtEtter',
		'Dato må være etter født etter-dato',
	),
	nyttNavn: Yup.object({
		hasMellomnavn: Yup.boolean().nullable(),
	}).nullable(),
	statsborgerskapLandkode: Yup.string().nullable(),
	gradering: Yup.string().nullable(),
})

export const doedfoedtBarn = Yup.object({
	dato: requiredDate.nullable(),
})

export const sivilstand = Yup.object({
	type: requiredString,
	sivilstandsdato: Yup.mixed().when('type', {
		is: (type) => type === 'SAMBOER',
		then: () => requiredDate,
		otherwise: () => testSivilstandsdatoBekreftelsesdato(Yup.mixed().nullable()),
	}),
	relatertVedSivilstand: Yup.string()
		.test('feltet-mangler', 'Partner er påkrevd', (value, testcontext) => {
			return (
				value ||
				(testcontext.parent.type !== 'GIFT' &&
					testcontext.parent.type !== 'SEPARERT' &&
					testcontext.parent.type !== 'REGISTRERT_PARTNER' &&
					testcontext.parent.type !== 'SEPARERT_PARTNER' &&
					testcontext.parent.type !== 'SAMBOER') ||
				testcontext.options.context?.identMaster !== 'PDL'
			)
		})
		.nullable(),
	bekreftelsesdato: Yup.mixed().when('type', {
		is: (type) => type === 'SAMBOER',
		then: () => Yup.mixed().notRequired(),
		otherwise: () => testSivilstandsdatoBekreftelsesdato(Yup.mixed().nullable()),
	}),
	borIkkeSammen: Yup.boolean().nullable(),
	nyRelatertPerson: nyPerson,
})

export const deltBosted = Yup.object().shape(
	{
		adressetype: ifPresent('adressetype', testDeltBostedAdressetype(requiredString)),
		startdatoForKontrakt: testDatoFom(
			Yup.mixed().optional().nullable(),
			'sluttdatoForKontrakt',
			'Dato må være før sluttdato',
		),
		sluttdatoForKontrakt: testDatoTom(
			Yup.mixed().optional().nullable(),
			'startdatoForKontrakt',
			'Dato må være etter startdato',
		),
		vegadresse: vegadresse.nullable(),
		matrikkeladresse: matrikkeladresse.nullable(),
		ukjentBosted: Yup.mixed()
			.when('adressetype', {
				is: 'UKJENT_BOSTED',
				then: () =>
					Yup.object({
						bostedskommune: requiredString,
					}).nullable(),
			})
			.nullable(),
	},
	[['adressetype', 'adressetype']],
)

export const forelderBarnRelasjon = Yup.object().shape(
	{
		minRolleForPerson: requiredString,
		relatertPersonsRolle: requiredString,
		relatertPerson: Yup.string()
			.test('er-testnorge-paakrevd', 'Person fra Testnorge er påkrevd', (value, testcontext) => {
				return testcontext.options.context?.identMaster !== 'PDL' || value
			})
			.test(
				'er-eksisterende-paakrevd',
				'Relatert person er påkrevd ved "Eksisterende person"',
				(value, testcontext) => {
					const forelderBarnRelasjon = testcontext?.from?.find(
						(element) => element.value?.forelderBarnRelasjon?.length > 0,
					)

					let isOK = true
					forelderBarnRelasjon?.value.forelderBarnRelasjon
						.filter(
							(relasjon) =>
								relasjon.typeForelderBarn === 'EKSISTERENDE' && !relasjon.relatertPerson && !value,
						)
						.map(() => (isOK = false))

					return isOK
				},
			)
			.test(
				'er-identisk-person',
				'Person kan kun benyttes en gang i relasjon',
				(value, testContext) => {
					const forelderBarnRelasjon = testContext?.from?.find(
						(element) => element.value?.forelderBarnRelasjon?.length > 0,
					)

					if (!forelderBarnRelasjon) {
						return true
					}

					const relasjoner = forelderBarnRelasjon?.value.forelderBarnRelasjon
						.filter((element) => element.relatertPerson)
						.map((element) => element.relatertPerson)

					return relasjoner?.length === new Set(relasjoner).size
				},
			)
			.nullable(),
		borIkkeSammen: Yup.mixed().when('relatertPersonsRolle', {
			is: 'BARN',
			then: () => Yup.mixed().notRequired(),
			otherwise: () => Yup.boolean().nullable(),
		}),
		nyRelatertPerson: nyPerson.nullable(),
		deltBosted: Yup.mixed().when('deltBosted', {
			is: (deltBosted) => !!deltBosted,
			then: () => deltBosted.nullable(),
			otherwise: () => Yup.mixed().notRequired(),
		}),
	},
	[['deltBosted', 'deltBosted']],
)

export const foreldreansvar = Yup.object({
	ansvar: testForeldreansvar(requiredString),
	gyldigFraOgMed: testDatoFom(Yup.mixed().nullable(), 'gyldigTilOgMed'),
	gyldigTilOgMed: testDatoTom(Yup.mixed().nullable(), 'gyldigFraOgMed'),
	typeAnsvarlig: Yup.string()
		.test('type-Ansvarlig-paakrevd', 'Type Ansvarlig må velges', (value, testcontext) => {
			return !!value || testcontext.parent?.ansvar !== 'ANDRE'
		})
		.nullable(),
	ansvarlig: Yup.string()
		.test('ansvarlig-andre-paakrevd', 'Ansvarlig person må velges', (value, testcontext) => {
			return (
				!!value ||
				testcontext.parent?.ansvarssubjekt ||
				testcontext.parent?.ansvar !== 'ANDRE' ||
				testcontext.parent?.ansvarligUtenIdentifikator ||
				testcontext.parent?.nyAnsvarlig
			)
		})
		.nullable(),
	ansvarssubjekt: Yup.string()
		.test('ansvarssubjekt-er-paakrevd', 'Ansvarssubjekt er påkrevd', (value, testcontext) => {
			return (
				!!value ||
				testcontext.parent?.ansvarlig ||
				testcontext.parent?.typeAnsvarlig !== 'EKSISTERENDE' ||
				!testcontext.options?.context?.personFoerLeggTil
			)
		})
		.nullable(),
})

export const foreldreansvarForBarn = Yup.object({
	ansvar: testForeldreansvarForBarn(requiredString),
	gyldigFraOgMed: testDatoFom(Yup.mixed().nullable(), 'gyldigTilOgMed'),
	gyldigTilOgMed: testDatoTom(Yup.mixed().nullable(), 'gyldigFraOgMed'),
	ansvarlig: Yup.string()
		.test('ansvarlig-andre-paakrevd', 'Ansvarlig person må velges', (value, testcontext) => {
			return (
				!!value ||
				testcontext.parent?.ansvar !== 'ANDRE' ||
				testcontext.parent?.ansvarligUtenIdentifikator ||
				testcontext.parent?.nyAnsvarlig
			)
		})
		.nullable(),
})
