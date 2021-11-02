import _has from 'lodash/has'

export const rootPaths = [
	'tpsf',
	'pdldata',
	'pdlforvalter',
	'aareg',
	'sigrunstub',
	'pensjonforvalter',
	'inntektstub',
	'instdata',
	'krrstub',
	'arenaforvalter',
	'udistub',
	'inntektsmelding',
	'brregstub',
	'dokarkiv',
	'sykemelding',
	'organisasjon',
]

export const harAvhukedeAttributter = (values) => {
	return rootPaths.some((path) => _has(values, path))
}
