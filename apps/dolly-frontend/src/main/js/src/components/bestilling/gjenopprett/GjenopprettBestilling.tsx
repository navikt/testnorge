import { arrayToString } from '@/utils/DataFormatter'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { GjenopprettModal } from './GjenopprettModal'
import { REGEX_BACKEND_BESTILLINGER, useMatchMutate } from '@/utils/hooks/useMutate'
import { useSWRConfig } from 'swr'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import * as _ from 'lodash-es'

export default function GjenopprettBestilling(props) {
	const { bestilling, closeModal } = props
	const { environments } = bestilling
	const erOrganisasjon = bestilling.hasOwnProperty('organisasjonNummer')

	const matchMutate = useMatchMutate()
	const { mutate: globalMutate } = useSWRConfig()
	const { currentBruker } = useCurrentBruker()

	const submitForm = async (values) => {
		const envsQuery = arrayToString(values.environments).replace(/ /g, '').toLowerCase()
		const response = erOrganisasjon
			? await props.gjenopprettOrganisasjonBestilling(envsQuery)
			: await props.gjenopprettBestilling(envsQuery)

		if (erOrganisasjon) {
			const newBestilling = _.get(response, 'action.payload.data', null)
			const brukerId =
				currentBruker?.representererTeam?.brukerId ?? currentBruker?.brukerId
			if (newBestilling && brukerId) {
				const key = `/dolly-backend/api/v1/organisasjon/bestilling/bestillingsstatus?brukerId=${brukerId}`
				void globalMutate(
					key,
					(current: any[]) =>
						current ? [...current, newBestilling] : [newBestilling],
				)
			}
		}

		closeModal()

		if (!erOrganisasjon) {
			matchMutate(REGEX_BACKEND_BESTILLINGER)
		}
	}

	const gjenopprettHeader = (
		<div style={{ paddingLeft: 20, paddingRight: 20 }}>
			<h1>Gjenopprett bestilling #{bestilling.id}</h1>
			{environments?.length > 0 && (
				<>
					<br />
					<TitleValue title="Bestilt miljø" value={arrayToString(environments)?.toUpperCase()} />
					<hr />
				</>
			)}
		</div>
	)

	return (
		<GjenopprettModal
			gjenopprettHeader={gjenopprettHeader}
			environments={environments}
			submitForm={submitForm}
			closeModal={closeModal}
			bestilling={bestilling}
		/>
	)
}
