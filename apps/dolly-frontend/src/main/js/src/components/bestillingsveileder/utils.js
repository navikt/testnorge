import _has from 'lodash/has'

export const rootPaths = [
	'tpsf',
	'tpsMessaging',
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

export const getLeggTilIdent = (personFoerLeggTil, identMaster) => {
	if (identMaster === 'TPSF') return personFoerLeggTil.tpsf.ident
	if (identMaster === 'PDL') return personFoerLeggTil.pdl.data.hentIdenter.identer[0].ident
	return undefined
}
