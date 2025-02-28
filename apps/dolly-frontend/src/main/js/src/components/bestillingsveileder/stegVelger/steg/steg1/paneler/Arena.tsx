import Panel from '@/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import { arenaPath } from '@/components/fagsystem/arena/form/Form'
import { runningE2ETest } from '@/service/services/Request'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { useContext } from 'react'

export const ArenaPanel = ({ stateModifier, formValues }) => {
	const sm = stateModifier(ArenaPanel.initialValues)

	const veileder = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const maaned = veileder.personFoerLeggTil?.pdl?.ident?.substring?.(2, 4)
	const syntetisk = maaned === undefined || maaned >= 40

	const infoTekst = syntetisk
		? 'Arbeidsytelser sendes til Arena for valgt(e) miljø(er). ' +
			'For å ha rett på dagpenger kreves det opptjening på minimum 1,5G ' +
			'de siste 12 måneder, eller 3G de siste 36 måneder. '
		: 'Arena har sluttet å støtte ikke-syntetiske identer, ' +
			'og disse kan ikke benyttes mer for arbeidsytelser.'

	return (
		<Panel
			heading={ArenaPanel.heading}
			informasjonstekst={infoTekst}
			checkAttributeArray={() => {
				if (!sm.attrs.ingenYtelser.checked && !sm.attrs.ikkeServicebehov.checked) {
					sm.batchAdd(['ikkeServicebehov', 'ingenYtelser'])
				}
				if (!syntetisk) {
					sm.batchAdd(['ikkeServicebehov', 'ingenYtelser', 'aap', 'aap115', 'dagpenger'])
				}
			}}
			uncheckAttributeArray={sm.batchRemove}
			iconType="arena"
			startOpen={harValgtAttributt(formValues, [arenaPath])}
		>
			<AttributtKategori title={'Aktiv bruker'} attr={sm.attrs}>
				<Attributt
					disabled={
						sm.attrs.ikkeServicebehov.checked ||
						sm.attrs.aap115.checked ||
						sm.attrs.aap.checked ||
						sm.attrs.dagpenger.checked ||
						!syntetisk
					}
					attr={sm.attrs.ingenYtelser}
				/>
				<Attributt
					disabled={
						sm.attrs.ikkeServicebehov.checked || sm.attrs.ingenYtelser.checked || !syntetisk
					}
					attr={sm.attrs.aap115}
				/>
				<Attributt
					disabled={
						sm.attrs.ikkeServicebehov.checked || sm.attrs.ingenYtelser.checked || !syntetisk
					}
					attr={sm.attrs.aap}
				/>
				<Attributt
					disabled={
						sm.attrs.ikkeServicebehov.checked || sm.attrs.ingenYtelser.checked || !syntetisk
					}
					attr={sm.attrs.dagpenger}
				/>
			</AttributtKategori>

			<AttributtKategori title={'Inaktiv bruker'} attr={sm.attrs}>
				<Attributt
					disabled={
						sm.attrs.ingenYtelser.checked ||
						sm.attrs.aap.checked ||
						sm.attrs.aap115.checked ||
						sm.attrs.dagpenger.checked ||
						!syntetisk
					}
					attr={sm.attrs.ikkeServicebehov}
				/>
			</AttributtKategori>
		</Panel>
	)
}

ArenaPanel.heading = 'Arbeidsytelser'

ArenaPanel.initialValues = ({ set, opts, setMulti, del, has }) => {
	const getServiceBehov = () => {
		const okArenaBestillinger = opts?.tidligereBestillinger?.filter((bestilling) => {
			const arenaStatus = bestilling?.status?.find(
				(bestStatus) => bestStatus?.id === 'ARENA_BRUKER' || bestStatus?.id === 'ARENA',
			)
			const okArena = arenaStatus?.statuser?.some((bestStatus) => {
				return bestStatus?.melding === 'OK'
			})
			return bestilling?.data?.arenaforvalter && okArena
		})
		return okArenaBestillinger?.find(
			(bestilling) => bestilling?.data?.arenaforvalter?.kvalifiseringsgruppe,
		)?.data?.arenaforvalter?.kvalifiseringsgruppe
	}

	const sisteBestillingServicebehov = runningE2ETest() ? 'IKVAL' : getServiceBehov()

	const MED_SERVICEBEHOV = ['arenaforvalter.arenaBrukertype', 'MED_SERVICEBEHOV']
	const AUTOMATISK_INNSENDING_MELDEKORT = ['arenaforvalter.automatiskInnsendingAvMeldekort', true]
	const KVALIFISERINGSGRUPPE = [
		'arenaforvalter.kvalifiseringsgruppe',
		sisteBestillingServicebehov || null,
	]
	const AKTIVERINGDATO = ['arenaforvalter.aktiveringDato', null]

	return {
		aap115: {
			label: '11.5-vedtak',
			checked: has('arenaforvalter.aap115'),
			add() {
				setMulti(
					[
						'arenaforvalter.aap115[0]',
						{
							fraDato: runningE2ETest() ? new Date(2020, 1) : null,
							tilDato: runningE2ETest() ? new Date(2020, 2) : null,
						},
					],
					MED_SERVICEBEHOV,
					AUTOMATISK_INNSENDING_MELDEKORT,
					KVALIFISERINGSGRUPPE,
					AKTIVERINGDATO,
				)
			},
			remove() {
				del('arenaforvalter.aap115')
				!has('arenaforvalter.aap') && !has('arenaforvalter.dagpenger') && del('arenaforvalter')
			},
		},

		aap: {
			label: 'AAP-vedtak',
			checked: has('arenaforvalter.aap'),
			add() {
				setMulti(
					[
						'arenaforvalter.aap[0]',
						{
							fraDato: runningE2ETest() ? new Date(2021, 1) : null,
							tilDato: runningE2ETest() ? new Date(2022, 1) : null,
						},
					],
					MED_SERVICEBEHOV,
					AUTOMATISK_INNSENDING_MELDEKORT,
					KVALIFISERINGSGRUPPE,
					AKTIVERINGDATO,
				)
			},
			remove() {
				del('arenaforvalter.aap')
				!has('arenaforvalter.aap115') && !has('arenaforvalter.dagpenger') && del('arenaforvalter')
			},
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
							fraDato: runningE2ETest() ? new Date(2023, 1) : null,
							tilDato: runningE2ETest() ? new Date(2023, 2) : null,
							mottattDato: null,
						},
					],
					MED_SERVICEBEHOV,
					AUTOMATISK_INNSENDING_MELDEKORT,
					KVALIFISERINGSGRUPPE,
					AKTIVERINGDATO,
				)
			},
			remove() {
				del('arenaforvalter.dagpenger')
				!has('arenaforvalter.aap115') && !has('arenaforvalter.aap') && del('arenaforvalter')
			},
		},

		ikkeServicebehov: {
			label: 'Har ikke servicebehov',
			checked: has('arenaforvalter.inaktiveringDato'),
			add() {
				set('arenaforvalter', {
					inaktiveringDato: null,
					automatiskInnsendingAvMeldekort: true,
					arenaBrukertype: 'UTEN_SERVICEBEHOV',
					aktiveringDato: null,
				})
			},
			remove() {
				del('arenaforvalter')
			},
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
				setMulti(
					MED_SERVICEBEHOV,
					AUTOMATISK_INNSENDING_MELDEKORT,
					KVALIFISERINGSGRUPPE,
					AKTIVERINGDATO,
				)
			},
			remove() {
				del('arenaforvalter')
			},
		},
	}
}
