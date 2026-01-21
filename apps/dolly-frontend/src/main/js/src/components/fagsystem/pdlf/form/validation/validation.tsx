import * as Yup from 'yup'
import { ifPresent, requiredDate, requiredString } from '@/utils/YupValidations'
import { tpsMessagingValidation } from '@/components/fagsystem/tpsmessaging/form/validation'
import { testDatoFom, testDatoTom } from '@/components/fagsystem/utils'
import { isSameDay } from 'date-fns'
import {
	adressebeskyttelse,
	bostedsadresse,
	kontaktadresse,
	oppholdsadresse,
} from '@/components/fagsystem/pdlf/form/validation/partials/adresser'
import { sikkerhetstiltak } from '@/components/fagsystem/pdlf/form/validation/partials/sikkerhetstiltak'
import { tilrettelagtKommunikasjon } from '@/components/fagsystem/pdlf/form/validation/partials/tilrettelagtKommunikasjon'
import {
	falskIdentitet,
	utenlandskId,
} from '@/components/fagsystem/pdlf/form/validation/partials/identifikasjon'
import { telefonnummer } from '@/components/fagsystem/pdlf/form/validation/partials/telefonnummer'
import {
	doedfoedtBarn,
	forelderBarnRelasjon,
	foreldreansvar,
	sivilstand,
} from '@/components/fagsystem/pdlf/form/validation/partials/familierelasjoner'
import { utflytting } from '@/components/fagsystem/pdlf/form/validation/partials/utflytting'
import { kontaktDoedsbo } from '@/components/fagsystem/pdlf/form/validation/partials/kontaktinformasjonForDoedsbo'
import { vergemaal } from '@/components/fagsystem/pdlf/form/validation/partials/vergemaal'
import { statsborgerskap } from '@/components/fagsystem/pdlf/form/validation/partials/statborgerskap'
import { innflytting } from '@/components/fagsystem/pdlf/form/validation/partials/innvandring'

const testGyldigFom = (val) => {
	return val.test('is-unique', (selected: string, testContext: any) => {
		const personFoerLeggTil = testContext?.options?.context?.personFoerLeggTil
		const fullForm = testContext.from && testContext.from[testContext.from.length - 1]?.value
		if (selected === null || selected === '') {
			return true
		}
		const navn = fullForm?.navn ? [fullForm.navn] : fullForm?.pdldata?.person?.navn
		const navnFoerLeggTil = personFoerLeggTil?.pdlforvalter?.person?.navn
		let antallLike = 0
		navn?.concat(navnFoerLeggTil)?.forEach((navn) => {
			if (isSameDay(new Date(navn?.gyldigFraOgMed), new Date(selected))) {
				antallLike = antallLike + 1
			}
		})
		return antallLike > 1
			? testContext.createError({ message: 'Denne datoen er valgt for et annet navn' })
			: true
	})
}

const testGyldigAlder = (val) => {
	return val.test('is-valid', (alder, testContext) => {
		if (alder === null || alder === '') {
			return true
		}
		if (parseInt(alder) < 0) {
			return testContext.createError({ message: 'Alder må være minst 0 år' })
		}
		const identtype = testContext.parent?.identtype
		const minYear = identtype === 'NPID' ? 1870 : 1900
		if (new Date().getFullYear() - parseInt(alder) < minYear) {
			return testContext.createError({
				message: `Alder må være mindre enn ${new Date().getFullYear() - (minYear - 1)} år`,
			})
		}
		return true
	})
}

export const doedsfall = Yup.object({
	doedsdato: requiredDate.nullable(),
})

export const kjoenn = Yup.object({
	kjoenn: requiredString,
})

export const navn = Yup.object().shape(
	{
		fornavn: Yup.mixed().when('etternavn', {
			is: (etternavn) => etternavn != null && etternavn != '',
			then: () => requiredString,
			otherwise: () => Yup.mixed().notRequired(),
		}),
		mellomnavn: Yup.string().nullable(),
		etternavn: Yup.mixed().when('fornavn', {
			is: (fornavn) => fornavn != null && fornavn != '',
			then: () => requiredString,
			otherwise: () => Yup.mixed().notRequired(),
		}),
		hasMellomnavn: Yup.boolean().nullable(),
		gyldigFraOgMed: testGyldigFom(Yup.mixed().nullable()),
	},
	['fornavn', 'etternavn'],
)

export const folkeregisterpersonstatus = Yup.object({
	status: requiredString,
	gyldigFraOgMed: testDatoFom(Yup.mixed().nullable(), 'gyldigTilOgMed'),
	gyldigTilOgMed: testDatoTom(Yup.mixed().nullable(), 'gyldigFraOgMed'),
})

export const validation = {
	pdldata: ifPresent(
		'$pdldata',
		Yup.object({
			opprettNyPerson: ifPresent(
				'$opprettNyPerson',
				Yup.object({
					alder: testGyldigAlder(Yup.string().nullable()),
					foedtEtter: testDatoFom(
						Yup.date().nullable(),
						'foedtFoer',
						'Dato må være før født før-dato',
					),
					foedtFoer: testDatoTom(
						Yup.date().nullable(),
						'foedtEtter',
						'Dato må være etter født etter-dato',
					),
					identtype: ifPresent('$identtype', Yup.string().required('Velg en identtype')),
				}).nullable(),
			),
			person: Yup.object({
				bostedsadresse: ifPresent('$pdldata.person.bostedsadresse', Yup.array().of(bostedsadresse)),
				oppholdsadresse: ifPresent(
					'$pdldata.person.oppholdsadresse',
					Yup.array().of(oppholdsadresse),
				),
				kontaktadresse: ifPresent('$pdldata.person.kontaktadresse', Yup.array().of(kontaktadresse)),
				adressebeskyttelse: ifPresent(
					'$pdldata.person.adressebeskyttelse',
					Yup.array().of(adressebeskyttelse),
				),
				sikkerhetstiltak: ifPresent('$pdldata.person.sikkerhetstiltak', sikkerhetstiltak),
				tilrettelagtKommunikasjon: ifPresent(
					'$pdldata.person.tilrettelagtKommunikasjon',
					tilrettelagtKommunikasjon,
				),
				falskIdentitet: ifPresent('$pdldata.person.falskIdentitet', falskIdentitet),
				telefonnummer: ifPresent('$pdldata.person.telefonnummer', Yup.array().of(telefonnummer)),
				statsborgerskap: ifPresent(
					'$pdldata.person.statsborgerskap',
					Yup.array().of(statsborgerskap),
				),
				doedsfall: ifPresent('$pdldata.person.doedsfall', Yup.array().of(doedsfall)),
				doedfoedtBarn: ifPresent('$pdldata.person.doedfoedtBarn', Yup.array().of(doedfoedtBarn)),
				innflytting: ifPresent('$pdldata.person.innflytting', Yup.array().of(innflytting)),
				utflytting: ifPresent('$pdldata.person.utflytting', Yup.array().of(utflytting)),
				utenlandskIdentifikasjonsnummer: ifPresent(
					'$pdldata.person.utenlandskIdentifikasjonsnummer',
					Yup.array().of(utenlandskId),
				),
				kontaktinformasjonForDoedsbo: ifPresent(
					'$pdldata.person.kontaktinformasjonForDoedsbo',
					Yup.array().of(kontaktDoedsbo),
				),
				forelderBarnRelasjon: ifPresent(
					'$pdldata.person.forelderBarnRelasjon',
					Yup.array().of(forelderBarnRelasjon),
				),
				sivilstand: ifPresent('$pdldata.person.sivilstand', Yup.array().of(sivilstand)),
				kjoenn: ifPresent('$pdldata.person.kjoenn', Yup.array().of(kjoenn)),
				navn: ifPresent('$pdldata.person.navn', Yup.array().of(navn)),
				vergemaal: ifPresent('$pdldata.person.vergemaal', Yup.array().of(vergemaal)),
				foreldreansvar: ifPresent('$pdldata.person.foreldreansvar', Yup.array().of(foreldreansvar)),
			}).nullable(),
		}),
	),
	...tpsMessagingValidation,
}
