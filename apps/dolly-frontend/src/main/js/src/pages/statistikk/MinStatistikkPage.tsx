import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BrukerStatistikk } from './BrukerStatistikk'

export default () => {
	const { currentBruker } = useCurrentBruker()
	const statistikkEierId = currentBruker?.representererTeam?.brukerId ?? currentBruker?.brukerId

	return (
		<>
			<h1>Min statistikk</h1>
			<ErrorBoundary>
				<BrukerStatistikk
					key={statistikkEierId}
					eierId={statistikkEierId}
					teamNavn={currentBruker?.representererTeam?.navn}
				/>
			</ErrorBoundary>
		</>
	)
}
