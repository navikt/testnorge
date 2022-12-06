import Formatters from '@/utils/DataFormatter'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { GjenopprettModal } from './GjenopprettModal'
import {
	REGEX_BACKEND_BESTILLINGER,
	REGEX_BACKEND_ORGANISASJONER,
	useMatchMutate,
} from '@/utils/hooks/useMutate'

export default function GjenopprettBestilling(props) {
	const { bestilling, closeModal, brukertype } = props
	const { environments } = bestilling
	const erOrganisasjon = bestilling.hasOwnProperty('organisasjonNummer')

	const mutate = useMatchMutate()

	const submitFormik = async (values) => {
		const envsQuery = Formatters.arrayToString(values.environments).replace(/ /g, '').toLowerCase()
		erOrganisasjon
			? await props.gjenopprettOrganisasjonBestilling(envsQuery)
			: await props.gjenopprettBestilling(envsQuery)
		mutate(REGEX_BACKEND_ORGANISASJONER)
		mutate(REGEX_BACKEND_BESTILLINGER)
		closeModal()
	}

	const gjenopprettHeader = (
		<div style={{ paddingLeft: 20, paddingRight: 20 }}>
			<h1>Gjenopprett bestilling #{bestilling.id}</h1>
			<br />
			<TitleValue title="Bestilt miljø" value={Formatters.arrayToString(environments)} />
			<hr />
		</div>
	)

	return (
		<GjenopprettModal
			gjenopprettHeader={gjenopprettHeader}
			environments={environments}
			submitFormik={submitFormik}
			closeModal={closeModal}
			bestilling={bestilling}
			brukertype={brukertype}
		/>
	)
}
