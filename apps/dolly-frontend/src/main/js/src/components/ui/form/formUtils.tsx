import * as _ from 'lodash-es'
import { runningCypressE2E } from '@/service/services/Request'

export const fieldError = (meta) => {
	return !!meta.touched && !!meta.error ? { feilmelding: meta.error } : null
}

export const panelError = (formikBag, attributtPath) => {
	// Ignore if values ikke er satt
	if (_.isNil(attributtPath)) return false

	// Strings er akseptert, men konverter til Array
	if (!Array.isArray(attributtPath)) attributtPath = [attributtPath]

	return attributtPath.some((attr) => _.has(formikBag.errors, attr))
}

export const SyntEvent = (name, value) => ({ target: { name, value } })

const getValgteAttributter = (values) => {
	const rootPaths = [
		'pdldata.opprettNyPerson.alder',
		'pdldata.person.foedsel',
		'pdldata.person.doedsfall',
		'pdldata.person.statsborgerskap',
		'pdldata.person.innflytting',
		'pdldata.person.utflytting',
		'pdldata.person.kjoenn',
		'pdldata.person.navn',
		'pdldata.person.telefonnummer',
		'pdldata.person.vergemaal',
		'pdldata.person.fullmakt',
		'pdldata.person.sikkerhetstiltak',
		'pdldata.person.tilrettelagtKommunikasjon',
		'tpsMessaging.spraakKode',
		'tpsMessaging.egenAnsattDatoFom',
		'tpsMessaging.egenAnsattDatoTom',
		'tpsMessaging.utenlandskBankkonto',
		'tpsMessaging.norskBankkonto',
		'bankkonto.utenlandskBankkonto',
		'bankkonto.norskBankkonto',
		'pdldata.person.bostedsadresse',
		'pdldata.person.oppholdsadresse',
		'pdldata.person.kontaktadresse',
		'pdldata.person.adressebeskyttelse',
		'pdldata.person.sivilstand',
		'pdldata.person.doedfoedtBarn',
		'pdldata.person.forelderBarnRelasjon',
		'pdldata.person.foreldreansvar',
		'pdldata.person.falskIdentitet',
		'pdldata.person.utenlandskIdentifikasjonsnummer',
		'pdldata.person.nyident',
		'pdldata.person.kontaktinformasjonForDoedsbo',
		'aareg',
		'sigrunstub',
		'inntektstub',
		'inntektsmelding',
		'pensjonforvalter.inntekt',
		'pensjonforvalter.tp',
		'arenaforvalter',
		'sykemelding',
		'brregstub',
		'instdata',
		'krrstub',
		'udistub',
		'dokarkiv',
		'organisasjon.enhetstype',
		'organisasjon.naeringskode',
		'organisasjon.sektorkode',
		'organisasjon.formaal',
		'organisasjon.stiftelsesdato',
		'organisasjon.maalform',
		'organisasjon.telefon',
		'organisasjon.epost',
		'organisasjon.nettside',
		'organisasjon.forretningsadresse',
		'organisasjon.postadresse',
	]

	const valgteAttributter = []

	rootPaths.forEach((path) => {
		if (_.has(values, path)) {
			valgteAttributter.push(path)
		}
	})
	return valgteAttributter
}

export const erForsteEllerTest = (values, attributter) => {
	const valgteAttributter = getValgteAttributter(values)
	return runningCypressE2E() || attributter.includes(valgteAttributter[0])
}

export const harValgtAttributt = (values, attributter) => {
	const valgteAttributter = getValgteAttributter(values)
	return attributter.some((attr) => valgteAttributter.includes(attr))
}
