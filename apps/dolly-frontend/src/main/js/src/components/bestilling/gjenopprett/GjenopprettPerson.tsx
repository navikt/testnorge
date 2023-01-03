import DollyService from '@/service/services/dolly/DollyService'
import Button from '@/components/ui/button/Button'
import { useBoolean } from 'react-use'
import Loading from '@/components/ui/loading/Loading'
import { REGEX_BACKEND_BESTILLINGER, useMatchMutate } from '@/utils/hooks/useMutate'

export const GjenopprettPerson = ({ ident }: { ident: number }) => {
	const [loading, setLoading] = useBoolean(false)
	const mutate = useMatchMutate()

	const handleClick = async () => {
		setLoading(true)
		await DollyService.gjenopprettPerson(ident).then(() => {
			mutate(REGEX_BACKEND_BESTILLINGER)
			setLoading(false)
		})
	}

	return loading ? (
		<Loading label="Gjenoppretter..." />
	) : (
		<Button onClick={() => handleClick()} kind="synchronize">
			GJENOPPRETT PERSON
		</Button>
	)
}
