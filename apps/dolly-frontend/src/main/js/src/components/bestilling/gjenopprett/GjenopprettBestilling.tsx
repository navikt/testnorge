import { arrayToString } from '@/utils/DataFormatter'
import { GjenopprettModal } from './GjenopprettModal'
import {
	REGEX_BACKEND_BESTILLINGER,
	REGEX_BACKEND_ORGANISASJONER,
	useMatchMutate,
} from '@/utils/hooks/useMutate'

export default function GjenopprettBestilling(props) {
	const { bestilling, disabled, title } = props
	const { environments } = bestilling
	const erOrganisasjon = bestilling.hasOwnProperty('organisasjonNummer')

	const mutate = useMatchMutate()

	const submitForm = async (values) => {
		const envsQuery = arrayToString(values.environments).replace(/ /g, '').toLowerCase()
		erOrganisasjon
			? await props.gjenopprettOrganisasjonBestilling(envsQuery)
			: await props.gjenopprettBestilling(envsQuery)
		if (erOrganisasjon) {
			mutate(REGEX_BACKEND_ORGANISASJONER)
		} else {
			mutate(REGEX_BACKEND_BESTILLINGER)
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
