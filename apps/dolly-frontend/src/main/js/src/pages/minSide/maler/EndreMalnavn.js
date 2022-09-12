import React, { useState } from 'react'
import { malerApi } from './MalerApi'
import Button from '~/components/ui/button/Button'
import { TextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { REGEX_BACKEND_BESTILLINGER, useMatchMutate } from '~/utils/hooks/useMutate'

export const EndreMalnavn = ({ malInfo, avbrytRedigering }) => {
	const { malNavn, id } = malInfo
	const [nyttMalnavn, setMalnavn] = useState(malNavn)
	const mutate = useMatchMutate()

	const lagreEndring = (nyttMalnavn, id) => {
		malerApi
			.endreMalNavn(id, nyttMalnavn)
			.then(() => mutate(REGEX_BACKEND_BESTILLINGER))
			.then(() => avbrytRedigering(id))
	}

	return (
		<ErrorBoundary>
			<div className="endreMalnavn">
				<TextInput
					name="malnavn"
					value={nyttMalnavn}
					onChange={(e) => setMalnavn(e.target.value)}
					className="navnInput"
				/>
				<Button className="lagre" onClick={() => lagreEndring(nyttMalnavn, id)}>
					LAGRE
				</Button>
			</div>
		</ErrorBoundary>
	)
}
