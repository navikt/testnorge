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

const namePaths = [
    `pdlforvalter.kontaktinformasjonForDoedsbo.adressat.navn`,
    `pdlforvalter.kontaktinformasjonForDoedsbo.adressat.kontaktperson`,
    `pdlforvalter.falskIdentitet.rettIdentitet.personnavn`
]

const idPaths = [
    `pdlforvalter.kontaktinformasjonForDoedsbo.adressat.idnummer`,
    `pdlforvalter.falskIdentitet.rettIdentitet.rettIdentitetVedIdentifikasjonsnummer`
]

export const harAvhukedeAttributter = values => {
    return rootPaths.some(path => _has(values, path))
}

export const separateFullNames = (values, formikBag) => {
    for (let i = 0; i < namePaths.length; i++) {
        const path = namePaths[i]
        const fullName = _get(values, `${path}`)
        if (!_isNil(fullName)) {
            const deltNavn = (fullName + '').split(" ")

            formikBag.setFieldValue(`${path}`, {
                fornavn: deltNavn[0],
                mellomnavn: deltNavn.length === 3 ? deltNavn[1] : '',
                etternavn: deltNavn[deltNavn.length - 1]
            })
        }
    }
}

export const combineNames = (formikBag) => {
    for (let i = 0; i < namePaths.length; i++) {
        const path = namePaths[i]
        const fornavn = _get(formikBag.values, `${path}.fornavn`)
        if (!_isNil(fornavn)) {
            const mellomnavn = _get(formikBag.values, `${path}.mellomnavn`)
            const etternavn = " " + _get(formikBag.values, `${path}.etternavn`)

            formikBag.setFieldValue(`${path}`,
                fornavn + (mellomnavn !=='' ? " " + mellomnavn : '') + etternavn)
        }
    }
}

export const separateIdsFromNames = (values, formikBag) => {
    for (let i = 0; i < idPaths.length; i++) {
        const path = idPaths[i]
        const fnrOgNavn = _get(values, `${path}`)
        if (!_isNil(fnrOgNavn)) {
            const deltTekst = (fnrOgNavn + '').split(" ")
            formikBag.setFieldValue(`${path}`, deltTekst[deltTekst.length - 1])
        }
    }
}