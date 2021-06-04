import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'

export const ArenaPanel = ({ stateModifier }) => {
	const sm = stateModifier(ArenaPanel.initialValues)

	return (
		<Panel
			heading={ArenaPanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="arena"
		>
			<AttributtKategori title={'Aktiv bruker'}>
				<Attributt disabled={sm.attrs.ikkeServicebehov.checked} attr={sm.attrs.aap115} />
				<Attributt disabled={sm.attrs.ikkeServicebehov.checked} attr={sm.attrs.aap} />
				<Attributt disabled={sm.attrs.ikkeServicebehov.checked} attr={sm.attrs.dagpenger} />
			</AttributtKategori>

			<AttributtKategori title={'Inaktiv bruker'}>
				<Attributt
					disabled={sm.attrs.aap.checked || sm.attrs.aap115.checked || sm.attrs.dagpenger.checked}
					attr={sm.attrs.ikkeServicebehov}
				/>
			</AttributtKategori>
		</Panel>
	)
}

ArenaPanel.heading = 'Arbeidsytelser'

ArenaPanel.initialValues = ({ setMulti, del, delMulti, has }) => ({
	aap115: {
		label: '11.5-vedtak',
		checked: has('arenaforvalter.aap115'),
		add() {
			setMulti(
				[
					'arenaforvalter.aap115[0]',
					{
						fraDato: null,
						tilDato: null
					}
				],
				['arenaforvalter.arenaBrukertype', 'MED_SERVICEBEHOV']
			)
		},
		remove() {
			del('arenaforvalter.aap115')
			!has('arenaforvalter.aap') && !has('arenaforvalter.dagpenger') && del('arenaforvalter')
		}
	},

	aap: {
		label: 'AAP-vedtak',
		checked: has('arenaforvalter.aap'),
		add() {
			setMulti(
				[
					'arenaforvalter.aap[0]',
					{
						fraDato: null,
						tilDato: null
					}
				],
				['arenaforvalter.arenaBrukertype', 'MED_SERVICEBEHOV']
			)
		},
		remove() {
			del('arenaforvalter.aap')
			!has('arenaforvalter.aap115') && !has('arenaforvalter.dagpenger') && del('arenaforvalter')
		}
	},

	dagpenger: {
		label: 'Dagpengevedtak',
		checked: has('arenaforvalter.dagpenger'),
		add() {
			setMulti(
				[
					'arenaforvalter.dagpenger[0]',
					{
						vedtakstype: 'O',
						rettighetKode: 'DAGO',
						fraDato: null,
						tilDato: null,
						mottattDato: null
					}
				],
				['arenaforvalter.arenaBrukertype', 'MED_SERVICEBEHOV']
			)
		},
		remove() {
			del('arenaforvalter.dagpenger')
			!has('arenaforvalter.aap115') && !has('arenaforvalter.aap') && del('arenaforvalter')
		}
	},

	ikkeServicebehov: {
		label: 'Har ikke servicebehov',
		checked: has('arenaforvalter.inaktiveringDato'),
		add() {
			setMulti(
				['arenaforvalter.arenaBrukertype', 'UTEN_SERVICEBEHOV'],
				['arenaforvalter.inaktiveringDato', null]
			)
		},
		remove() {
			del(['arenaforvalter.arenaBrukertype', 'arenaforvalter.inaktiveringDato'])
		}
	}
})
