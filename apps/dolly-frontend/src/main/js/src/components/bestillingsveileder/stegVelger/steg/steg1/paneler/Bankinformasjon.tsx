import React from 'react'
import Panel from '@/components/ui/panel/Panel'
import { Attributt } from '../Attributt'

// @ts-ignore
export const BankinformasjonPanel = ({ stateModifier, formValues }) => {
    const sm: any = stateModifier(BankinformasjonPanel.initialValues, formValues)

    const getIgnoreKeys = () => {
        let ignoreKeys = []
        if (sm.attrs.utenlandskBankkonto.checked) {
            ignoreKeys.push('norskBankkonto')
        } else {
            ignoreKeys.push('utenlandskBankkonto')
        }
        return ignoreKeys
    }

    return (
        <Panel
            heading={BankinformasjonPanel.heading}
            checkAttributeArray={
                (() => {
                    sm.batchAdd(getIgnoreKeys())
                }) as any
            }
            uncheckAttributeArray={sm.batchRemove}
            iconType={'adresse'}
        >
                <Attributt
                    attr={sm.attrs.norskBankkonto}
                    vis={true}
                    disabled={sm.attrs.utenlandskBankkonto.checked}
                />
                <Attributt
                    attr={sm.attrs.utenlandskBankkonto}
                    vis={true}
                    disabled={sm.attrs.norskBankkonto.checked}
                />
        </Panel>
    )
}

BankinformasjonPanel.heading = 'Bankinformasjon'

// @ts-ignore
BankinformasjonPanel.initialValues = ({ set, opts, del, has }) => {
    const paths = {
        norskBankkonto: 'bankkonto.norskBankkonto',
        utenlandskBankkonto: 'bankkonto.utenlandskBankkonto',
    }

    return {
        norskBankkonto: {
            label: 'Norsk bank',
            checked: has(paths.norskBankkonto),
            add: () =>
                set(paths.norskBankkonto, {
                    kontonummer: '',
                    tilfeldigKontonummer: opts?.antall && opts?.antall > 1,
                }),
            remove: () => del(paths.norskBankkonto),
        },
        utenlandskBankkonto: {
            label: 'Utenlandsk bank',
            checked: has(paths.utenlandskBankkonto),
            add: () =>
                set(paths.utenlandskBankkonto, {
                    kontonummer: '',
                    tilfeldigKontonummer: false,
                    swift: 'BANKXX11222',
                    landkode: null,
                    banknavn: '',
                    iban: '',
                    valuta: null,
                    bankAdresse1: '',
                    bankAdresse2: '',
                    bankAdresse3: '',
                }),
            remove: () => del(paths.utenlandskBankkonto),
        },
    }
}