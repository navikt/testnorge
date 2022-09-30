import React from 'react'
import Maler from './maler/Maloversikt'
import GruppeImport from './GruppeImport'
import Profil from './Profil'

import './MinSide.less'
import { useBrukerProfil, useCurrentBruker } from '~/utils/hooks/useBruker'

export default () => {
	const { brukerProfil } = useBrukerProfil()
	const { currentBruker } = useCurrentBruker()

	const AzureADProfil = brukerProfil && brukerProfil.type && brukerProfil.type === 'AzureAD'
	return (
		<>
			<h1>Min side</h1>
			<Profil />
			{AzureADProfil && <GruppeImport />}
			{brukerProfil && <Maler brukerId={currentBruker?.brukerId} />}
		</>
	)
}
