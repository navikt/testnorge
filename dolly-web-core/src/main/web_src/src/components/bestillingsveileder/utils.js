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

export const pdlfNamePaths = [
    `pdlforvalter.kontaktinformasjonForDoedsbo.adressat.navn`,
    `pdlforvalter.kontaktinformasjonForDoedsbo.adressat.kontaktperson`,
    `pdlforvalter.falskIdentitet.rettIdentitet.personnavn`
]

export const pdlfIdPaths = [
    `pdlforvalter.kontaktinformasjonForDoedsbo.adressat.idnummer`,
    `pdlforvalter.falskIdentitet.rettIdentitet.rettIdentitetVedIdentifikasjonsnummer`
]

export const harAvhukedeAttributter = values => {
    return rootPaths.some(path => _has(values, path))
}
