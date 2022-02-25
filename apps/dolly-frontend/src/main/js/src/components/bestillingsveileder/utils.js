import _has from 'lodash/has'
import { getPdlIdent } from '~/ducks/fagsystem'

export const rootPaths = [
	'tpsf',
	'tpsMessaging',
	'pdldata.person',
	'pdldata.opprettNyPerson.alder',
	'pdldata.opprettNyPerson.foedtEtter',
	'pdldata.opprettNyPerson.foedtFoer',
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
	if (identMaster === 'PDLF') return personFoerLeggTil.pdlforvalter?.person?.ident
	if (identMaster === 'PDL') return getPdlIdent(personFoerLeggTil.pdl.data.hentIdenter.identer)
	return undefined
}
