import { arrayToString } from '@/utils/DataFormatter'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { GjenopprettModal } from './GjenopprettModal'
import {
	REGEX_BACKEND_BESTILLINGER,
	REGEX_BACKEND_ORGANISASJONER,
	useMatchMutate,
} from '@/utils/hooks/useMutate'

export default function GjenopprettBestilling(props) {
	const { bestilling, closeModal } = props
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
		closeModal()
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
			// brukertype={brukertype}
		/>
	)
}
