import { useBrukerProfil, useCurrentBruker } from '@/utils/hooks/useBruker'
import DollyStatistikk from '@/pages/statistikk/dollyStatistikk/DollyStatistikk'

export default () => {
	const { brukerProfil } = useBrukerProfil()
	const { currentBruker } = useCurrentBruker()

	return <>{brukerProfil && <DollyStatistikk brukerId={currentBruker?.brukerId} />}</>
}
