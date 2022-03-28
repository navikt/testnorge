import React, { useState } from 'react'
import { malerApi } from './MalerApi'
import Button from '~/components/ui/button/Button'
import { TextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

export const EndreMalnavn = ({ malInfo, setMaler, avbrytRedigering }) => {
	const { malNavn, id } = malInfo
	const [nyttMalnavn, setMalnavn] = useState(malNavn)

	return (
		<ErrorBoundary>
			<div className="endreMalnavn">
				<TextInput
					name="malnavn"
					value={nyttMalnavn}
					onChange={(e) => setMalnavn(e.target.value)}
					className="navnInput"
				/>
				<Button
					className="lagre"
					onClick={() => lagreEndring(nyttMalnavn, setMaler, id, avbrytRedigering)}
				>
					LAGRE
				</Button>
			</div>
		</ErrorBoundary>
	)
}

const lagreEndring = (nyttMalnavn, setMaler, id, avbrytRedigering) => {
	malerApi
		.endreMalNavn(id, nyttMalnavn)
		.then(() =>
			setMaler((maler) =>
				maler.map((mal) => ({ ...mal, malNavn: mal.id === id ? nyttMalnavn : mal.malNavn }))
			)
		)
		.then(avbrytRedigering(id))
}
