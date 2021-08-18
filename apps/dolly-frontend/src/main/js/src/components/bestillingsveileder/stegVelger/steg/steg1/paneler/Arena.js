import React, { useContext } from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'

export const ArenaPanel = ({ stateModifier }) => {
	const sm = stateModifier(ArenaPanel.initialValues)
	const opts = useContext(BestillingsveilederContext)

	const { personFoerLeggTil } = opts
	const alleredeRegistrert = personFoerLeggTil && personFoerLeggTil.arenaforvalteren
	const alleredeRegistertTitle = 'Bruker allerede registrert i Arena.'

	return (
		<Panel
			heading={ArenaPanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="arena"
		>
			<AttributtKategori title={'Aktiv bruker'}>
				<Attributt
					disabled={
						sm.attrs.ikkeServicebehov.checked ||
						sm.attrs.aap115.checked ||
						sm.attrs.aap.checked ||
						sm.attrs.dagpenger.checked ||
						alleredeRegistrert
					}
					attr={sm.attrs.ingenYtelser}
					title={alleredeRegistrert ? alleredeRegistertTitle : null}
				/>
				<Attributt
					disabled={
						sm.attrs.ikkeServicebehov.checked || sm.attrs.ingenYtelser.checked || alleredeRegistrert
					}
					attr={sm.attrs.aap115}
					title={alleredeRegistrert ? alleredeRegistertTitle : null}
				/>
				<Attributt
					disabled={
						sm.attrs.ikkeServicebehov.checked || sm.attrs.ingenYtelser.checked || alleredeRegistrert
					}
					attr={sm.attrs.aap}
					title={alleredeRegistrert ? alleredeRegistertTitle : null}
				/>
				<Attributt
					disabled={sm.attrs.ikkeServicebehov.checked || sm.attrs.ingenYtelser.checked}
					attr={sm.attrs.dagpenger}
				/>
			</AttributtKategori>

			<AttributtKategori title={'Inaktiv bruker'}>
				<Attributt
					disabled={
						sm.attrs.ingenYtelser.checked ||
						sm.attrs.aap.checked ||
						sm.attrs.aap115.checked ||
						sm.attrs.dagpenger.checked ||
						alleredeRegistrert
					}
					attr={sm.attrs.ikkeServicebehov}
					title={alleredeRegistrert ? alleredeRegistertTitle : null}
				/>
			</AttributtKategori>
		</Panel>
	)
}

ArenaPanel.heading = 'Arbeidsytelser'

const MED_SERVICEBEHOV = ['arenaforvalter.arenaBrukertype', 'MED_SERVICEBEHOV']
const AUTOMATISK_INNSENDING_MELDEKORT = ['arenaforvalter.automatiskInnsendingAvMeldekort', true]

ArenaPanel.initialValues = ({ set, setMulti, del, has }) => ({
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
				MED_SERVICEBEHOV,
				AUTOMATISK_INNSENDING_MELDEKORT
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
				MED_SERVICEBEHOV,
				AUTOMATISK_INNSENDING_MELDEKORT
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
				MED_SERVICEBEHOV,
				AUTOMATISK_INNSENDING_MELDEKORT
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
			set('arenaforvalter', {
				inaktiveringDato: null,
				automatiskInnsendingAvMeldekort: true,
				arenaBrukertype: 'UTEN_SERVICEBEHOV'
			})
		},
		remove() {
			del('arenaforvalter')
		}
	},

	ingenYtelser: {
		label: 'Ingen ytelser',
		checked:
			has('arenaforvalter.arenaBrukertype') &&
			!has('arenaforvalter.aap115') &&
			!has('arenaforvalter.aap') &&
			!has('arenaforvalter.dagpenger') &&
			!has('arenaforvalter.inaktiveringDato'),
		add() {
			setMulti(MED_SERVICEBEHOV, AUTOMATISK_INNSENDING_MELDEKORT)
		},
		remove() {
			del('arenaforvalter')
		}
	}
})
