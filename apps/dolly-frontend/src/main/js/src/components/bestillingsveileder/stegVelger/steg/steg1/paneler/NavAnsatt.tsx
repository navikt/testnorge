import React from 'react'
import Panel from '@/components/ui/panel/Panel'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import {
	Attributt,
	AttributtKategori,
} from '@/components/bestillingsveileder/stegVelger/steg/steg1/Attributt'
import * as _ from 'lodash-es'

export const NavAnsattPanel = ({ stateModifier, formValues }) => {
	const sm = stateModifier(NavAnsattPanel.initialValues)

	return (
		<Panel
			heading={NavAnsattPanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="nav"
			startOpen={harValgtAttributt(formValues, [
				'nomdata',
				'skjerming.egenAnsattDatoFom',
				'skjerming.egenAnsattDatoTom',
			])}
		>
			<AttributtKategori attr={sm.attrs}>
				<Attributt attr={sm.attrs.nom} />
				<Attributt attr={sm.attrs.egenAnsattDatoFom} />
			</AttributtKategori>
		</Panel>
	)
}

NavAnsattPanel.heading = 'Nav-ansatt'

NavAnsattPanel.initialValues = ({ set, opts, setMulti, del, has }) => {
	const { personFoerLeggTil } = opts

	const eksisterendeNomdataStartDato = _.get(personFoerLeggTil, 'nomdata.startDato')
	const eksisterendeNomdataSluttDato = _.get(personFoerLeggTil, 'nomdata.sluttDato')

	const paths = {
		nom: 'nomdata',
		egenAnsattDatoFom: {
			tpsM: 'tpsMessaging.egenAnsattDatoFom',
			skjerming: 'skjerming.egenAnsattDatoFom',
		},
		egenAnsattDatoTom: {
			tpsM: 'tpsMessaging.egenAnsattDatoTom',
			skjerming: 'skjerming.egenAnsattDatoTom',
		},
		skjermetFra: 'skjermingsregister.skjermetFra',
	}

	return {
		nom: {
			label: 'Er Nav-ansatt (NOM)',
			checked: has(paths.nom),
			add: () =>
				set(paths.nom, {
					startDato: eksisterendeNomdataStartDato
						? new Date(eksisterendeNomdataStartDato)
						: new Date(),
					sluttDato: eksisterendeNomdataSluttDato
						? new Date(eksisterendeNomdataSluttDato)
						: (null as unknown as string),
				}),
			remove: () => del(paths.nom),
		},
		egenAnsattDatoFom: {
			label: 'Skjerming (egen ansatt)',
			checked: has(paths.egenAnsattDatoFom.tpsM) || has(paths.egenAnsattDatoFom.skjerming),
			add() {
				setMulti(
					[
						paths.egenAnsattDatoFom.skjerming,
						_.get(personFoerLeggTil, paths.skjermetFra)?.substring(0, 10) ||
							_.get(personFoerLeggTil, paths.egenAnsattDatoFom.tpsM) ||
							new Date(),
					],
					[paths.egenAnsattDatoTom.skjerming, undefined],
				)
			},
			remove() {
				del(['skjerming', paths.egenAnsattDatoFom.tpsM, paths.egenAnsattDatoFom.skjerming])
			},
		},
	}
}
