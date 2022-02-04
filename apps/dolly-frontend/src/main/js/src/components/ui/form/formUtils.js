import _has from 'lodash/has'
import _isNil from 'lodash/isNil'

export const fieldError = (meta) => {
	return !!meta.touched && !!meta.error ? { feilmelding: meta.error } : null
}

export const panelError = (formikBag, attributtPath) => {
	// Ignore if values ikke er satt
	if (_isNil(attributtPath)) return false

	// Strings er akseptert, men konverter til Array
	if (!Array.isArray(attributtPath)) attributtPath = [attributtPath]

	return attributtPath.some((attr) => _has(formikBag.errors, attr))
}

export const SyntEvent = (name, value) => ({ target: { name, value } })

export const erForste = (values, attributt) => {
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
		'pdldata.person.bostedsadresse',
		'pdldata.person.oppholdsadresse',
		'pdldata.person.kontaktadresse',
		'pdldata.person.adressebeskyttelse',
		'pdldata.person.sivilstand',
		'pdldata.person.doedfoedtBarn',
		'pdldata.person.foreldreBarnRelasjon',
		'aareg',
		'sigrunstub',
		'inntektstub',
		'sykemelding',
		'brregstub',
		'instdata',
		'krrstub',
		'arenaforvalter',
		'udistub',
		'pensjonforvalter',
		'inntektsmelding',
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
		if (_has(values, path)) {
			valgteAttributter.push(path)
		}
	})
	return attributt.includes(valgteAttributter[0])
}
