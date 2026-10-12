import Maler from './Maloversikt'
import './Maler.less'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'

export default () => {
	const { currentBruker } = useCurrentBruker()

	return (
		<ErrorBoundary>
			<Maler brukerId={currentBruker?.brukerId} />
		</ErrorBoundary>
	)
}
