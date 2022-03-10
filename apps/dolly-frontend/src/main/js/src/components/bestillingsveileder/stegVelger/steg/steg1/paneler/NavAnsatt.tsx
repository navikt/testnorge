import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import _get from 'lodash/get'

export const NavAnsattPanel = ({ stateModifier }: any) => {
	const sm = stateModifier(NavAnsattPanel.initialValues)
	return (
		// @ts-ignore
		<Panel
			heading={NavAnsattPanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="organisasjon"
		>
			<AttributtKategori title={null}>
				<Attributt attr={sm.attrs.navIdent} />
				<Attributt attr={sm.attrs.egenAnsattDatoFom} />
			</AttributtKategori>
		</Panel>
	)
}

NavAnsattPanel.heading = 'NAV ansatt'

NavAnsattPanel.initialValues = ({ set, setMulti, del, has, opts }: any) => {
	const { personFoerLeggTil } = opts
	return {
		navIdent: {
			label: 'NAV ident',
			checked: has('nomData'),
			add: () =>
				set('nomData', {
					opprettNavIdent: true,
				}),
			remove: () => del('nomData'),
		},
		egenAnsattDatoFom: {
			label: 'Skjerming',
			checked: has('tpsf.egenAnsattDatoFom') || has('tpsMessaging.egenAnsattDatoFom'),
			add() {
				setMulti(
					[
						'tpsMessaging.egenAnsattDatoFom',
						_get(personFoerLeggTil, 'skjermingsregister.skjermetFra')?.substring(0, 10) ||
							_get(personFoerLeggTil, 'tpsMessaging.egenAnsattDatoFom') ||
							new Date(),
					],
					['tpsMessaging.egenAnsattDatoTom', undefined],
					[
						'skjerming.egenAnsattDatoFom',
						_get(personFoerLeggTil, 'skjermingsregister.skjermetFra')?.substring(0, 10) ||
							_get(personFoerLeggTil, 'tpsMessaging.egenAnsattDatoFom') ||
							new Date(),
					],
					['skjerming.egenAnsattDatoTom', undefined]
				)
			},
			remove() {
				del([
					'tpsMessaging.egenAnsattDatoFom',
					'tpsMessaging.egenAnsattDatoTom',
					'tpsf.egenAnsattDatoFom',
					'tpsf.egenAnsattDatoTom',
					'skjerming',
				])
			},
		},
	}
}
