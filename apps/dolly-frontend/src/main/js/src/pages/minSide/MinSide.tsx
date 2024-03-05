import Maler from './maler/Maloversikt'
import Profil from './Profil'

import './MinSide.less'
import { useBrukerProfil, useCurrentBruker } from '@/utils/hooks/useBruker'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'

export default () => {
	const { brukerProfil } = useBrukerProfil()
	const { currentBruker } = useCurrentBruker()

	return (
		<>
			<h1>Min side</h1>
			<ErrorBoundary>
				<Profil />
				{brukerProfil && <Maler brukerId={currentBruker?.brukerId} />}
			</ErrorBoundary>
		</>
	)
}
