import Profil from '../profil/Profil'

import { useBrukerProfil, useCurrentBruker } from '@/utils/hooks/useBruker'
import DollyStatistikk from '@/pages/statistikk/dollyStatistikk/DollyStatistikk'

export default () => {
	const { brukerProfil } = useBrukerProfil()
	const { currentBruker } = useCurrentBruker()

	return (
		<>
			<h1>Statistikk</h1>
			<Profil />
			{brukerProfil && <DollyStatistikk brukerId={currentBruker?.brukerId} />}
		</>
	)
}
