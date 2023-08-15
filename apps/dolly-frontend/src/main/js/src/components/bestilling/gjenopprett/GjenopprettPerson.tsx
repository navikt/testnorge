import DollyService from '@/service/services/dolly/DollyService'
import Button from '@/components/ui/button/Button'
import useBoolean from '@/utils/hooks/useBoolean'
import Loading from '@/components/ui/loading/Loading'
import {
	REGEX_BACKEND_BESTILLINGER,
	REGEX_BACKEND_GRUPPER,
	useMatchMutate,
} from '@/utils/hooks/useMutate'
import { GjenopprettModal } from '@/components/bestilling/gjenopprett/GjenopprettModal'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { arrayToString } from '@/utils/DataFormatter'

export const GjenopprettPerson = ({ ident }: { ident: number }) => {
	const [loading, setLoading] = useBoolean(false)
	const [isGjenopprettModalOpen, openGjenopprettModal, closeGjenoprettModal] = useBoolean(false)
	const mutate = useMatchMutate()

	const handleClick = async () => {
		setLoading(true)
		await DollyService.gjenopprettPerson(ident)
			.then(() => {
				mutate(REGEX_BACKEND_BESTILLINGER)
				setLoading(false)
			})
			.catch(() => {
				setLoading(false)
			})
	}

	const gjenopprettHeader = (
		<div style={{ paddingLeft: 20, paddingRight: 20 }}>
			<h1>Gjenopprett person {ident}</h1>
			{/*<br />*/}
			{/*<TitleValue title="Bestilt miljø" value={arrayToString(environments)} />*/}
			{/*<hr />*/}
		</div>
	)

	return (
		<>
			<Button onClick={openGjenopprettModal} kind="synchronize">
				GJENOPPRETT PERSON
			</Button>
			{isGjenopprettModalOpen && (
				<GjenopprettModal
					gjenopprettHeader={gjenopprettHeader}
					submitFormik={handleClick}
					closeModal={() => {
						closeGjenoprettModal()
						// mutate(REGEX_BACKEND_BESTILLINGER)
						mutate(REGEX_BACKEND_GRUPPER)
					}}
				/>
			)}
		</>
	)

	// return loading ? (
	// 	<Loading label="Gjenoppretter..." />
	// ) : (
	// 	<Button onClick={() => handleClick()} kind="synchronize">
	// 		GJENOPPRETT PERSON
	// 	</Button>
	// )
}
