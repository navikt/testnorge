import Panel from '@/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import { arenaPath } from '@/components/fagsystem/arena/form/Form'

export const ArenaPanel = ({ stateModifier, formikBag }) => {
	const sm = stateModifier(ArenaPanel.initialValues)

	return (
		<Panel
			heading={ArenaPanel.heading}
			checkAttributeArray={() => {
				if (!sm.attrs.ingenYtelser.checked && !sm.attrs.ikkeServicebehov.checked) {
					sm.batchAdd(['ikkeServicebehov', 'ingenYtelser'])
				}
			}}
			uncheckAttributeArray={sm.batchRemove}
			iconType="arena"
			startOpen={harValgtAttributt(formikBag.values, [arenaPath])}
		>
			<AttributtKategori title={'Aktiv bruker'} attr={sm.attrs}>
				<Attributt
					disabled={
						sm.attrs.ikkeServicebehov.checked ||
						sm.attrs.aap115.checked ||
						sm.attrs.aap.checked ||
						sm.attrs.dagpenger.checked
					}
					attr={sm.attrs.ingenYtelser}
				/>
				<Attributt
					disabled={sm.attrs.ikkeServicebehov.checked || sm.attrs.ingenYtelser.checked}
					attr={sm.attrs.aap115}
				/>
				<Attributt
					disabled={sm.attrs.ikkeServicebehov.checked || sm.attrs.ingenYtelser.checked}
					attr={sm.attrs.aap}
				/>
				<Attributt
					disabled={sm.attrs.ikkeServicebehov.checked || sm.attrs.ingenYtelser.checked}
					attr={sm.attrs.dagpenger}
				/>
			</AttributtKategori>

			<AttributtKategori title={'Inaktiv bruker'} attr={sm.attrs}>
				<Attributt
					disabled={
						sm.attrs.ingenYtelser.checked ||
						sm.attrs.aap.checked ||
						sm.attrs.aap115.checked ||
						sm.attrs.dagpenger.checked
					}
					attr={sm.attrs.ikkeServicebehov}
				/>
			</AttributtKategori>
		</Panel>
	)
}

ArenaPanel.heading = 'Arbeidsytelser'

ArenaPanel.initialValues = ({ set, setMulti, del, has, opts }) => {
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

	const sisteBestillingServicebehov = getServiceBehov()

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
							fraDato: null,
							tilDato: null,
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
							fraDato: null,
							tilDato: null,
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
							fraDato: null,
							tilDato: null,
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
