import * as Yup from 'yup'
import { ifNotBlank, ifPresent, requiredDate, requiredString } from '@/utils/YupValidations'
import {
	adressebeskyttelse,
	bostedsadresse,
	doedfoedtBarn,
	falskIdentitet,
	forelderBarnRelasjon,
	foreldreansvar,
	fullmakt,
	innflytting,
	kontaktadresse,
	kontaktDoedsbo,
	oppholdsadresse,
	sikkerhetstiltak,
	sivilstand,
	statsborgerskap,
	telefonnummer,
	tilrettelagtKommunikasjon,
	utenlandskId,
	utflytting,
	vergemaal,
} from '@/components/fagsystem/pdlf/form/validation/partials'
import { bankkontoValidation } from '@/components/fagsystem/bankkonto/form'
import { tpsMessagingValidation } from '@/components/fagsystem/tpsmessaging/form/validation'
import { testDatoFom, testDatoTom } from '@/components/fagsystem/utils'
import { isSameDay } from 'date-fns'

const testGyldigFom = (val) => {
	return val.test('is-unique', function datoErUnik(selected) {
		if (selected === null || selected === '') {
			return true
		}
		const values = this?.options?.context
		const navn = values?.navn ? [values.navn] : values?.pdldata?.person?.navn
		const navnFoerLeggTil = values?.personFoerLeggTil?.pdlforvalter?.person?.navn
		let antallLike = 0
		navn?.concat(navnFoerLeggTil)?.forEach((navn) => {
			if (isSameDay(new Date(navn?.gyldigFraOgMed), new Date(selected))) {
				antallLike = antallLike + 1
			}
		})
		return antallLike > 1
			? this.createError({ message: 'Denne datoen er valgt for et annet navn' })
			: true
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
	['fornavn', 'etternavn']
)

export const folkeregisterpersonstatus = Yup.object({
	status: requiredString,
	gyldigFraOgMed: testDatoFom(Yup.mixed().nullable(), 'gyldigTilOgMed'),
	gyldigTilOgMed: testDatoTom(Yup.mixed().nullable(), 'gyldigFraOgMed'),
})

export const validation = {
	pdldata: Yup.object({
		opprettNyPerson: Yup.object({
			alder: Yup.mixed()
				.test(
					'max',
					`Alder må være mindre enn ${new Date().getFullYear() - 1899} år`,
					(val) => !val || new Date().getFullYear() - parseInt(val) >= 1900
				)
				.test('min', 'Alder må være minst 0 år', (val) => !val || parseInt(val) >= 0)
				.nullable(),
			foedtEtter: testDatoFom(Yup.date().nullable(), 'foedtFoer', 'Dato må være før født før-dato'),
			foedtFoer: testDatoTom(
				Yup.date().nullable(),
				'foedtEtter',
				'Dato må være etter født etter-dato'
			),
		}).nullable(),
		person: Yup.object({
			bostedsadresse: ifPresent('$pdldata.person.bostedsadresse', Yup.array().of(bostedsadresse)),
			oppholdsadresse: ifPresent(
				'$pdldata.person.oppholdsadresse',
				Yup.array().of(oppholdsadresse)
			),
			kontaktadresse: ifPresent('$pdldata.person.kontaktadresse', Yup.array().of(kontaktadresse)),
			adressebeskyttelse: ifPresent(
				'$pdldata.person.adressebeskyttelse',
				Yup.array().of(adressebeskyttelse)
			),
			fullmakt: ifPresent('$pdldata.person.fullmakt', Yup.array().of(fullmakt)),
			sikkerhetstiltak: ifPresent('$pdldata.person.sikkerhetstiltak', sikkerhetstiltak),
			tilrettelagtKommunikasjon: ifPresent(
				'$pdldata.person.tilrettelagtKommunikasjon',
				tilrettelagtKommunikasjon
			),
			falskIdentitet: ifPresent('$pdldata.person.falskIdentitet', falskIdentitet),
			telefonnummer: ifPresent('$pdldata.person.telefonnummer', Yup.array().of(telefonnummer)),
			statsborgerskap: ifPresent(
				'$pdldata.person.statsborgerskap',
				Yup.array().of(statsborgerskap)
			),
			doedsfall: ifPresent('$pdldata.person.doedsfall', Yup.array().of(doedsfall)),
			doedfoedtBarn: ifPresent('$pdldata.person.doedfoedtBarn', doedfoedtBarn),
			innflytting: ifPresent('$pdldata.person.innflytting', Yup.array().of(innflytting)),
			utflytting: ifPresent('$pdldata.person.utflytting', Yup.array().of(utflytting)),
			utenlandskIdentifikasjonsnummer: ifPresent(
				'$pdldata.person.utenlandskIdentifikasjonsnummer',
				Yup.array().of(utenlandskId)
			),
			kontaktinformasjonForDoedsbo: ifPresent(
				'$pdldata.person.kontaktinformasjonForDoedsbo',
				Yup.array().of(kontaktDoedsbo)
			),
			forelderBarnRelasjon: ifPresent(
				'$pdldata.person.forelderBarnRelasjon',
				Yup.array().of(forelderBarnRelasjon)
			),
			sivilstand: ifPresent('$pdldata.person.sivilstand', Yup.array().of(sivilstand)),
			kjoenn: ifPresent('$pdldata.person.kjoenn', Yup.array().of(kjoenn)),
			navn: ifPresent('$pdldata.person.navn', Yup.array().of(navn)),
			vergemaal: ifPresent('$pdldata.person.vergemaal', Yup.array().of(vergemaal)),
			foreldreansvar: ifPresent('$pdldata.person.foreldreansvar', Yup.array().of(foreldreansvar)),
		}).nullable(),
	}),
	...tpsMessagingValidation,
	...bankkontoValidation,
}
