import _has from 'lodash/has'
import _get from "lodash/get";
import _isNil from "lodash/isNil";

const rootPaths = [
    'tpsf',
    'pdlforvalter',
    'aareg',
    'sigrunstub',
    'pensjonforvalter',
    'inntektstub',
    'instdata',
    'krrstub',
    'arenaforvalter',
    'udistub'
]

export const harAvhukedeAttributter = values => {
    return rootPaths.some(path => _has(values, path))
}
