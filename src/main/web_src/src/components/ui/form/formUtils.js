import _has from 'lodash/has'
import _isNil from 'lodash/isNil'

export const fieldError = meta => {
	return !!meta.touched && !!meta.error ? { feilmelding: meta.error } : null
}

export const panelError = (formikBag, attributtPath) => {
	// Ignore if values ikke er satt
	if (_isNil(attributtPath)) return false

	// Strings er akseptert, men konverter til Array
	if (!Array.isArray(attributtPath)) attributtPath = [attributtPath]

	return attributtPath.some(attr => _has(formikBag.errors, attr))
}

export const SyntEvent = (name, value) => ({ target: { name, value } })

export const erForste = (values, attributt) => {
	const rootPaths = [
		'tpsf.alder',
		'tpsf.foedtEtter',
		'tpsf.foedtFoer',
		'tpsf.doedsdato',
		'tpsf.statsborgerskap',
		'tpsf.innvandretFraLand',
		'tpsf.utvandretTilLand',
		'tpsf.kjonn',
		'tpsf.harMellomnavn',
		'tpsf.sivilstand',
		'tpsf.sprakKode',
		'tpsf.egenAnsattDatoFom',
		'tpsf.egenAnsattDatoTom',
		'tpsf.spesreg',
		'tpsf.erForsvunnet',
		'tpsf.harBankkontonr',
		'tpsf.telefonnummer_1',
		'tpsf.identHistorikk',
		'tpsf.vergemaal',
		'tpsf.boadresse',
		'tpsf.postadresse',
		'tpsf.midlertidigAdresse',
		'tpsf.relasjoner',
		'aareg',
		'sigrunstub',
		'inntektstub',
		'sykemelding',
		'brregstub',
		'pdlforvalter.falskIdentitet',
		'pdlforvalter.utenlandskIdentifikasjonsnummer',
		'pdlforvalter.kontaktinformasjonForDoedsbo',
		'instdata',
		'krrstub',
		'arenaforvalter',
		'udistub',
		'pensjonforvalter',
		'inntektsmelding',
		'dokarkiv'
	]

	const valgteAttributter = []

	rootPaths.forEach(path => {
		if (_has(values, path)) {
			valgteAttributter.push(path)
		}
	})
	return attributt.includes(valgteAttributter[0])
}
