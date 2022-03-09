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
		'tpsf.alder',
		'tpsf.identtype',
		'tpsf.foedtEtter',
		'tpsf.foedtFoer',
		'tpsf.doedsdato',
		'tpsf.statsborgerskap',
		'tpsf.innvandretFraLand',
		'tpsf.utvandretTilLand',
		'tpsf.kjonn',
		'tpsf.harMellomnavn',
		'tpsf.harNyttNavn',
		'tpsf.sivilstand',
		'tpsf.sprakKode',
		'tpsf.egenAnsattDatoFom',
		'tpsf.egenAnsattDatoTom',
		'tpsf.spesreg',
		'tpsf.erForsvunnet',
		'tpsf.identHistorikk',
		'tpsf.vergemaal',
		'tpsf.fullmakt',
		'tpsMessaging.utenlandskBankkonto',
		'tpsMessaging.harBankkontonr',
		'tpsMessaging.sikkerhetstiltak',
		'tpsMessaging.spraakKode',
		'tpsMessaging.egenAnsattDatoFom',
		'tpsMessaging.egenAnsattDatoTom',
		'pdldata.person.bostedsadresse',
		'pdldata.person.oppholdsadresse',
		'pdldata.person.kontaktadresse',
		'pdldata.person.adressebeskyttelse',
		'tpsf.relasjoner',
		'aareg',
		'sigrunstub',
		'inntektstub',
		'sykemelding',
		'brregstub',
		'pdldata.person.telefonnummer',
		'pdldata.person.tilrettelagtKommunikasjon',
		'pdldata.person.doedsfall',
		'pdldata.person.statsborgerskap',
		'pdldata.person.innflytting',
		'pdldata.person.utflytting',
		'pdlforvalter.falskIdentitet',
		'pdlforvalter.utenlandskIdentifikasjonsnummer',
		'pdlforvalter.kontaktinformasjonForDoedsbo',
		'instdata',
		'krrstub',
		'nomData',
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
