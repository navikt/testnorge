import * as Yup from 'yup'
import { ifPresent, requiredDate, requiredString } from '@/utils/YupValidations'
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

export const doedsfall = Yup.object({
	doedsdato: requiredDate.nullable(),
})

export const kjoenn = Yup.object({
	kjoenn: requiredString,
})

export const navn = Yup.object({
	fornavn: Yup.string().nullable(),
	mellomnavn: Yup.string().nullable(),
	etternavn: Yup.string().nullable(),
	hasMellomnavn: Yup.boolean().nullable(),
})

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
			fullmakt: ifPresent('$pdldata.person.fullmakt', fullmakt),
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
				utenlandskId
			),
			kontaktinformasjonForDoedsbo: ifPresent(
				'$pdldata.person.kontaktinformasjonForDoedsbo',
				Yup.array().of(kontaktDoedsbo)
			),
			forelderBarnRelasjon: ifPresent('$pdldata.person.forelderBarnRelasjon', forelderBarnRelasjon),
			sivilstand: ifPresent('$pdldata.person.sivilstand', Yup.array().of(sivilstand)),
			kjoenn: ifPresent('$pdldata.person.kjoenn', Yup.array().of(kjoenn)),
			navn: ifPresent('$pdldata.person.navn', Yup.array().of(navn)),
			vergemaal: ifPresent('$pdldata.person.vergemaal', Yup.array().of(vergemaal)),
			foreldreansvar: ifPresent('$pdldata.person.foreldreansvar', foreldreansvar),
		}).nullable(),
	}),
	...tpsMessagingValidation,
	...bankkontoValidation,
}
