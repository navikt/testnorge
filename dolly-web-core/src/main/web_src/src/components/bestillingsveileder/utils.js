import _has from 'lodash/has'

const rootPaths = [
	'tpsf',
	'pdlforvalter',
	'aareg',
	'sigrunstub',
	'instdata',
	'krrstub',
	'arenaforvalter',
	'udistub'
]

export const harAvhukedeAttributter = values => {
	return rootPaths.some(path => _has(values, path))
}
