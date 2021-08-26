import React from 'react'
import Maler from './maler/Maloversikt'
import GruppeImport from './GruppeImport'
import Profil from './Profil'

import './MinSide.less'

export default ({ brukerBilde, brukerProfil }) => {
	return (
		<>
			<h1>Min side</h1>
			<Profil bilde={brukerBilde} info={brukerProfil} />
			<GruppeImport />
			{brukerProfil && <Maler brukerId={brukerProfil.visningsNavn} />}
		</>
	)
}
