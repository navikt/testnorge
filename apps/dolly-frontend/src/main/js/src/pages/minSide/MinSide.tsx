import React from 'react'
import Maler from './maler/Maloversikt'
import Profil from './Profil'

import './MinSide.less'
import { useBrukerProfil } from '~/utils/hooks/useBruker'

export default () => {
	const { brukerProfil } = useBrukerProfil()

	return (
		<>
			<h1>Min side</h1>
			<Profil />
			{brukerProfil && <Maler brukernavn={brukerProfil?.visningsNavn} />}
		</>
	)
}
