import { useFormContext } from 'react-hook-form'
import React, { useContext } from 'react'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { useGruppeIdenter } from '@/utils/hooks/useGruppe'
import Panel from '@/components/ui/panel/Panel'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import {
	Attributt,
	AttributtKategori,
} from '@/components/bestillingsveileder/stegVelger/steg/steg1/Attributt'
import * as _ from 'lodash-es'

export const NavAnsattPanel = ({ stateModifier, formValues }) => {
	const formMethods = useFormContext()
	const sm = stateModifier(NavAnsattPanel.initialValues)
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const formGruppeId = formMethods.watch('gruppeId')

	const gruppeId = formGruppeId || opts?.gruppeId || opts?.gruppe?.id
	const { identer, loading: gruppeLoading, error: gruppeError } = useGruppeIdenter(gruppeId)
	const harTestnorgeIdenter = identer?.filter((ident) => ident.master === 'PDL')?.length > 0

	const npidPerson = opts?.identtype === 'NPID'
	const leggTilPaaGruppe = !!opts?.leggTilPaaGruppe

	const getIgnoreKeys = () => {
		if (npidPerson || (harTestnorgeIdenter && leggTilPaaGruppe)) {
			return ['kontaktinformasjonForDoedsbo']
		}
		return []
	}

	// TODO: Trenger vi begrensning paa testnorgeIdent? Se PersoninformasjonPanel
	return (
		<Panel
			heading={NavAnsattPanel.heading}
			checkAttributeArray={() => sm.batchAdd(getIgnoreKeys())}
			uncheckAttributeArray={sm.batchRemove}
			iconType="nav"
			startOpen={harValgtAttributt(formValues, ['nomdata', 'skjerming'])}
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
			add: () => set(paths.nom, { startDato: new Date(), sluttDato: null as unknown as string }),
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
