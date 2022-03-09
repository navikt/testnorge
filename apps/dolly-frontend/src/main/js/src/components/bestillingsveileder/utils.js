import _has from 'lodash/has'
import { getPdlIdent } from '~/ducks/fagsystem'

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
	'nom',
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
	if (identMaster === 'PDL') return getPdlIdent(personFoerLeggTil.pdl.data.hentIdenter.identer)
	return undefined
}
