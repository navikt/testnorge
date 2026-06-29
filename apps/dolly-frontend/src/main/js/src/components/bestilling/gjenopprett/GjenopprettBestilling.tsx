import { arrayToString } from '@/utils/DataFormatter'
import { GjenopprettModal } from './GjenopprettModal'
import { REGEX_BACKEND_BESTILLINGER, useMatchMutate } from '@/utils/hooks/useMutate'
import { useSWRConfig } from 'swr'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import * as _ from 'lodash-es'

export default function GjenopprettBestilling(props) {
	const { bestilling, disabled, title } = props
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
			const brukerId = currentBruker?.representererTeam?.brukerId ?? currentBruker?.brukerId
			if (newBestilling && brukerId) {
				const key = `/dolly-backend/api/v1/organisasjon/bestilling/bestillingsstatus?brukerId=${brukerId}`
				void globalMutate(key, (current: any[]) => {
					if (!current) return [newBestilling]
					if (current.some((b) => b.id === newBestilling.id)) return current
					return [...current, newBestilling]
				})
			}
		}

		if (!erOrganisasjon) {
			matchMutate(REGEX_BACKEND_BESTILLINGER)
		}
	}

	return (
		<GjenopprettModal
			environments={environments}
			submitForm={submitForm}
			title={`Gjenopprett bestilling #${bestilling.id}`}
			bestilling={bestilling}
			bestilteMiljoer={environments}
			disabled={disabled}
			disabledTitle={disabled ? title : undefined}
		/>
	)
}
